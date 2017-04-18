package com.yao.devsdk.net;

import com.yao.devsdk.SdkConfig;
import com.yao.devsdk.log.LoggerUtil;
import com.yao.devsdk.net.event.NeedLoginEvent;
import com.yao.devsdk.net.exception.ApiException;
import com.yao.devsdk.net.response.HttpResult;

import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 *
 * Created by huichuan on 16/4/14.
 */
public class HttpClient<T> {

    private static final String TAG = "HttpClient";

    public static String BASE_URL;

    //10秒链接超时
    private static final int CONNECT_TIMEOUT = 10;
    //20秒读超时
    private static final int READ_TIMEOUT = 100;

    Retrofit retrofit;

    T requestService;



    private static HttpClient ourInstance;

    //获取单例
    public static <T> HttpClient<T> getInstance(String baseUrl,Class<T> apiService){
        if (ourInstance == null){
            synchronized (HttpResult.class){
                if (ourInstance == null){
                    BASE_URL = baseUrl;
                    ourInstance = new HttpClient<>(apiService,BASE_URL,CONNECT_TIMEOUT);
                }
            }
        }
        return ourInstance;
    }


    /**
     * 获取apiService
     * @return
     */
    public T getRequestService() {
        return requestService;
    }

    public HttpClient(Class<T> apiService, String baseUrl, int connectTimeOut){
        //手动创建一个OkHttpClient并设置超时时间
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(connectTimeOut, TimeUnit.SECONDS);
        httpClientBuilder.readTimeout(READ_TIMEOUT,TimeUnit.SECONDS);
        httpClientBuilder.addInterceptor(new ClientInterceptor());
        OkHttpClient okHttpClient = httpClientBuilder.build();

        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(baseUrl)
                .build();

        requestService = retrofit.create(apiService);
    }






    /**
     * 配置client所在线程的transformer
     */
    public static class ConfigTransformer<T> implements Observable.Transformer<T, T> {


        @Override
        public Observable<T> call(Observable<T> observable) {
            LoggerUtil.i(TAG,"当前设定的线程是："+Thread.currentThread().getId());
            return observable
                    .subscribeOn(Schedulers.io())
//                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    }


    /**
     * 用来统一处理Http的resultCode,并将HttpResult的Data部分剥离出来返回给subscriber
     *
     */
    public static class HttpResultFunc<T> implements Func1<HttpResult<T>, T> {
        @Override
        public T call(HttpResult<T> httpResult) {
            int statusCode = httpResult.getStatus();
            if (statusCode != ApiException.OK) {
                checkLogin(statusCode);
                throw new ApiException(httpResult.getStatus(),httpResult.getMessage());
            }
            return httpResult.getData();
        }
    }



    private static void checkLogin(int stateCode){
        if (stateCode == ApiException.LOGIN_REQUIRED){
            //需要登录,先清空本地的token
            SdkConfig.clearUidToken();
            EventBus.getDefault().post(new NeedLoginEvent());
        }
    }



}
