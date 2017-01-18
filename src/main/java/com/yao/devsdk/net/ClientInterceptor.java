package com.yao.devsdk.net;

import android.text.TextUtils;

import com.yao.devsdk.SdkConfig;
import com.yao.devsdk.constants.SdkConst;
import com.yao.devsdk.log.LogUtil;
import com.yao.devsdk.utils.MD5;
import com.yao.devsdk.utils.ReflectionUtil;

import org.apache.commons.lang.StringEscapeUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 *
 * Created by huichuan on 16/4/14.
 */
public class ClientInterceptor implements Interceptor {
    private static final String TAG = "ClientInterceptor";

    /**
     * 是否显示服务器返回的字符串
     */
//    public static boolean isShowRawString = false;


    //拦截请求，增加公参
    @Override
    public Response intercept(Chain chain) throws IOException {
        final String KEY_ACTION = "action";
        //接口action
        String action = "";
        Request newRequest;

        String method = chain.request().method();

        switch (method) {
            case "POST": {
                //post  请求
                Request original = chain.request();
                Request.Builder newRequestBuilder = original.newBuilder();

                //请求体定制：统一添加token参数
                RequestBody body = original.body();
                if (body instanceof FormBody) {
                    FormBody oldFormBody = (FormBody) body;
                    FormBody.Builder newFormBody = new FormBody.Builder();

                    //转移老参数
                    for (int i = 0; i < oldFormBody.size(); i++) {
                        String encodedName = oldFormBody.encodedName(i);
                        String encodedValue = oldFormBody.encodedValue(i);
                        if (TextUtils.equals(encodedName, KEY_ACTION)) {
                            action = encodedValue;
                        }
                        newFormBody.addEncoded(encodedName, encodedValue);
                    }

                    Map<String, String> publicParams = getPublicParams(action);
                    //添加公参
                    Set<Map.Entry<String, String>> publicEntries = publicParams.entrySet();
                    for (Map.Entry<String, String> entry : publicEntries) {
                        newFormBody.addEncoded(entry.getKey(), entry.getValue());
                    }


                    FormBody formBody = newFormBody.build();
                    newRequestBuilder.method(original.method(), formBody);

                    if (SdkConst.DEBUG){
                        LogUtil.i(TAG,"请求的url为："+original.url().toString());
                        for (int i=0,size=formBody.size();i<size;i++){
                            LogUtil.i(TAG,"参数为："+formBody.name(i)+"="+formBody.value(i));
                        }

                    }

                } else if (body instanceof MultipartBody) {
                    //上传图片文件的body

                    MultipartBody oldFormBody = (MultipartBody) body;

                    MultipartBody.Builder newFormBodyBuilder = new MultipartBody.Builder(oldFormBody.boundary());
                    newFormBodyBuilder.setType(oldFormBody.contentType());

                    //转移老参数
                    List<MultipartBody.Part> partList = oldFormBody.parts();

                    for (int i = 0, size = oldFormBody.size(); i < size; i++) {
                        MultipartBody.Part part = partList.get(i);
                        Object bodyValue = ReflectionUtil.getFieldValue(part, "body");
                        if (ReflectionUtil.isInstance(bodyValue, StringRequestBody.class)) {
                            //是纯文本内容
                            StringRequestBody stringRequestBody = (StringRequestBody) bodyValue;
                            String name = stringRequestBody.getName();
                            String content = stringRequestBody.getContent();
                            LogUtil.i(TAG, "name是：" + name + ",content是:" + content);
                            if (TextUtils.equals(name, KEY_ACTION)) {
                                action = content;
                            }

                            newFormBodyBuilder.addFormDataPart(name,content);
                        }else{

                            newFormBodyBuilder.addPart(part);
                        }

                    }


                    //添加公参
                    Map<String, String> publicParams = getPublicParams(action);
                    Set<Map.Entry<String, String>> publicEntries = publicParams.entrySet();
                    for (Map.Entry<String, String> entry : publicEntries) {

//                        MultipartBody.Part formData = MultipartBody.Part.createFormData(entry.getKey(), entry.getValue());
//                        newFormBodyBuilder.addPart(formData);
                        newFormBodyBuilder.addFormDataPart(entry.getKey(),entry.getValue());

                    }

                    MultipartBody multipartBody = newFormBodyBuilder.build();
                    newRequestBuilder.method(original.method(), multipartBody);
                }

                newRequest = newRequestBuilder.build();

            }
            break;
            default: {
                //默认是GET请求
                HttpUrl httpUrl = chain.request().url();
                action = httpUrl.queryParameter(KEY_ACTION);
                Map<String, String> publicParams = getPublicParams(action);
                Set<Map.Entry<String, String>> publicEntries = publicParams.entrySet();
                HttpUrl.Builder builder = httpUrl.newBuilder();
                for (Map.Entry<String, String> entry : publicEntries) {
                    builder.addQueryParameter(entry.getKey(), entry.getValue());
                }
                HttpUrl newHttpUrl = builder.build();
                newRequest = chain.request().newBuilder().url(newHttpUrl).build();
            }
            break;

        }

        if (SdkConst.DEBUG) {
            LogUtil.i(TAG, "当前网络请求线程是：" + Thread.currentThread().getId());
            LogUtil.i(TAG, "请求的URL为：" + newRequest.url().toString());
        }

        //执行请求
        Response response = chain.proceed(newRequest);

        if (SdkConst.DEBUG) {
            //测试需要时打印返回数据格式
//            if (isShowRawString) {
//                String string = response.body().string();
//                String unescapeJava = StringEscapeUtils.unescapeJava(string);
//                LogUtil.i(TAG, "response:" + unescapeJava);
//
//            }
        }


        return response;
    }


    /**
     * 获取公共参数Map
     *
     * @return
     */
    public static Map<String, String> getPublicParams(String action) {

        final String uid = SdkConfig.uid;
        final String deviceId = SdkConfig.deviceId;
        final String deviceType = "5";
        final String source = "5";
        final String sourceToken = "UOuosUJG2sahVnmGnBLQ7R2hPWFwMYYB";


        Map<String, String> params = new ParamsMap();
        //接口版本 用于未来接口升级。当前值为1（该版本指的是后台接口版本，由服务器端指定，不是客户端产品版本）
        params.put("version", "1");
        //客户端标识id（udid），与device_type一起标示客户端唯一id，进行统计、分析等工作。
        params.put("device_id", deviceId);
        //服务器端指定的设备类型id。详情参见device_type
        params.put("device_type", deviceType);
        //用于传递客户端的版本号，例如 “1.0.1”，该字段仅用于日志统计，由客户端指定
        params.put("client_version", SdkConfig.versionName);
        //用于标示调用的接口名称，由各接口指定
//        params.put("action", action);
        //客户端接口调用id，建议每一个发布版本向后台申请一个唯一id，用于加密校验和数据加密
        //ios1.0 开发版本 的source  09QZ5ufUdU3pGJtm4KtrFIEK90jQP9yL
        //android 1.0 开发版本 的source  UOuosUJG2sahVnmGnBLQ7R2hPWFwMYYB
        params.put("source", source);
        //用于加密验证接口调用的合法性
        String skey = computeSkey(action, deviceType, deviceId, sourceToken, uid);
        params.put("skey", skey);
        //用于流量统计，市场渠道标识，默认为1
        params.put("from", "1");
        //客户端ip 用于流量统计
        params.put("ip", SdkConfig.getIpAddress());
        //客户端ua 包含设备品牌 型号等信息
        params.put("ua", SdkConfig.userAgent);
        //客户端的token （服务器叫session）
        params.put("session_key", SdkConfig.token);
        //客户端的uid
        params.put("uid", uid);
        return params;
    }


    /**
     * 参数的map
     */
    static class ParamsMap extends HashMap<String, String> {

        @Override
        public String put(String key, String value) {
            if (TextUtils.isEmpty(key)) {
                //key为空
                return null;
            }

            if (!TextUtils.isEmpty(value)) {
                return super.put(key, value);
            } else {
                return null;
            }
        }
    }


    static String computeSkey(String action, String deviceType, String deviceId, String sourceToken, String uid) {
        if (uid == null){
            //如果uid为null,会拼一个null字符串，所以要过滤掉
            uid = "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(deviceType).append(deviceId).append(sourceToken).append(action).append(uid);
        String hexdigest = MD5.hexdigest(sb.toString());

        sb = new StringBuilder();
        sb.append(hexdigest.charAt(23)).append(hexdigest.charAt(9)).append(hexdigest.charAt(5)).append(hexdigest.charAt(17)).append(hexdigest.charAt(30));
        String result = sb.toString();
        return result;

    }


}
