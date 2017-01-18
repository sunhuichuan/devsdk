package com.yao.devsdk.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.TextView;

import com.yao.devsdk.SdkConfig;
import com.yao.devsdk.constants.SdkConst;
import com.yao.devsdk.log.LogUtil;
import com.yao.devsdk.model.SchemeMap;
import com.yao.devsdk.widget.HandlerToast;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 常用工具类
 */
public class SdkUtil {


    private static final String TAG = "CommonUtils";
//    public static Context mAppContext;


    public static boolean isAccelerometerOpen(Context ctx) {
        int screenchange = 0;
        try {
            screenchange = Settings.System.getInt(ctx.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);
            if (1 == screenchange) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;

    }


    // 获取url域名
    public static String getDomain(String url) {
        try {
            Pattern p = Pattern.compile("(?<=http://|\\.)[^.]*?\\.(com|cn|net|org|biz|info|cc|tv)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = p.matcher(url);
            matcher.find();
            return matcher.group();
        } catch (Exception e) {
            return url;
        }


    }


    /**
     * 程序是否在前台运行
     *
     * @return
     */
    public static boolean isAppOnForeground(Context ctx) {
        ActivityManager activityManager = (ActivityManager) ctx
                .getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = ctx.getPackageName();

        List<RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }

    /**
     * 根据URL获取图片在文件系统中的文件名
     *
     * @param url
     * @return
     */
    public static String getTempFileName(String url) {

        String tempFileName = "";
        try {
            if (null != url && !"".equals(url.trim())) {
                url = url.replace("_2.jpg", "_1.jpg");
                // http://r1.sinaimg.cn/10260/2011/0329/4f/0/5037778/60x60x100x0x0x1.jpg
                // change to
                // http___r1.sinaimg.cn_10260_2011_0329_4f_0_5037778_60x60x100x0x0x1.jpg
                tempFileName = url.replace('/', '_').replace(':', '_')
                        .replace(',', '_').replace('?', '_');
                if (!tempFileName.contains(".jpg")
                        && !tempFileName.contains(".png")
                        && !tempFileName.contains(".gif")) {
                    tempFileName += ".jpg";
                }
            }
            if (tempFileName.length() > 130) {
                int index = tempFileName.lastIndexOf(".");
                String suffix = tempFileName.substring(index,
                        tempFileName.length());
                tempFileName = MD5.hexdigest(tempFileName);
                tempFileName += suffix;
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e.getCause());
        }
        return tempFileName;
    }

    /*
     * 获取手机的Ip地址
     */
    public static String getLocalIpAddress(Context context) throws SocketException {
        // 检查Wifi状态
        if (isWifi(context)) {
            WifiManager wm = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo wi = wm.getConnectionInfo();
            // 获取32位整型IP地址
            int ipAdd = wi.getIpAddress();
            // 把整型地址转换成“*.*.*.*”地址
            String ip = intToIp(ipAdd);
            LogUtil.d("ipwifi", ip);
            return ip;

        } else {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        }
        return null;

    }

    static String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
                + "." + (i >> 24 & 0xFF);
    }


    @SuppressWarnings("rawtypes")
    public static boolean isEmpty(Object object) {
        if (object == null)
            return true;
        if (object instanceof Uri)
            return (object.toString()).length() == 0;
        if (object instanceof CharSequence)
            return ((CharSequence) object).length() == 0;
        if (object instanceof Collection)
            return ((Collection) object).isEmpty();
        if (object instanceof Map)
            return ((Map) object).isEmpty();
        if (object.getClass().isArray())
            return java.lang.reflect.Array.getLength(object) == 0;
        return false;
    }

    public static boolean isNotEmpty(Object object) {
        return !isEmpty(object);
    }


    public static String getMetaDataFromManifest(Context context, String key) {
        String ret = null;
        try {
            ApplicationInfo appinfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            ret = appinfo.metaData.getString(key);
        } catch (Exception e) {
            LogUtil.e(TAG, "获取meta异常", e);
        }
        return ret;
    }

    //从assets里读取 不进行数盟渠道防作弊的渠道列表
    public static String getStringFromAssets(Context ctx, String fileName) {
        String result = "";
        try {
            InputStreamReader inputReader = new InputStreamReader(ctx.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line;

            while ((line = bufReader.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean isServiceWorkedByClassName(Context context, String classname) {
        // #debug
        LogUtil.d(TAG, "isServiceWorkedByClassName(): start the "
                + classname);
        ActivityManager myManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager
                .getRunningServices(50);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().equals(classname)) {
                // #debug
                LogUtil.d(TAG, "isServiceWorkedByClassName(): get the "
                        + classname);
                return true;
            }
        }
        return false;
    }


    public static void showDebugToast(Context context, String text) {
        if (SdkConst.DEBUG) {
            HandlerToast.getInstance(context).showToast(text);
        }
    }


    public static void showToast(Context context, String text) {
        HandlerToast.getInstance(context).showToast(text);
    }


    public static int getAdjustAlignmentForImage(TextView view) {
        int ret = ImageSpan.ALIGN_BASELINE;
        if (view.getText().length() == 0) {
            return ImageSpan.ALIGN_BOTTOM;
        } else {
            try {
                String text = view.getText().toString();
                if (text.trim().length() == 0) {
                    return ImageSpan.ALIGN_BOTTOM;
                }
            } catch (Exception ignored) {

            }
        }
        return ret;
    }

    public static boolean isScreenLocked(Context context) {
        KeyguardManager mKeyguardManager = (KeyguardManager) context
                .getSystemService(Context.KEYGUARD_SERVICE);
        return mKeyguardManager.inKeyguardRestrictedInputMode();
    }


    /**
     * 获取圆角bitmap
     *
     * @param bitmap
     * @param roundPX
     * @return
     */
    public static Bitmap getRCB(Bitmap bitmap, float roundPX)
    // RCB means Rounded Corner Bitmap
    {
        Bitmap dstbmp = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(dstbmp);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPX, roundPX, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return dstbmp;
    }

    /**
     * 判断是否移动网络
     *
     * @param context
     * @return
     */
    public static boolean is2gOr3g(Context context) {
        try {
            ConnectivityManager conMan = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            // mobile 3G Data Network
            State mobile = State.UNKNOWN;
            NetworkInfo networkInfo = conMan
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (networkInfo != null) {
                mobile = networkInfo.getState();
            }
            return mobile == State.CONNECTED || mobile == State.CONNECTING;
        } catch (Exception e) {
            LogUtil.e(TAG, "判断网络异常", e);
        }
        return false;
    }

    /**
     * 判断是否3G网络
     *
     * @return
     */
    public static boolean is3gNet(Context context) {
        try {
            ConnectivityManager conMan = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            // mobile 3G Data Network
            NetworkInfo networkInfo = conMan
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (networkInfo != null) {
                int subType = networkInfo.getSubtype();

                switch (subType) {
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                        return false; // ~ 50-100 kbps
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                        return false; // ~ 14-64 kbps
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                        return false; // ~ 50-100 kbps
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        return true; // ~ 400-1000 kbps
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        return true; // ~ 600-1400 kbps
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                        return false; // ~ 100 kbps
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                        return true; // ~ 2-14 Mbps
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                        return true; // ~ 700-1700 kbps
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                        return true; // ~ 1-23 Mbps
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                        return true; // ~ 400-7000 kbps
                    // Unknown
                    case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                        return false;
                    default:
                        return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "判断网络异常", e);
        }
        return false;
    }

    /**
     * 判断是否wifi连接
     *
     * @param context
     * @return
     */
    public static boolean isWifi(Context context) {
        try {
            ConnectivityManager conMan = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            State wifi = State.UNKNOWN;
            NetworkInfo networkInfo = conMan
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (networkInfo != null) {
                wifi = networkInfo.getState();
            }
            return wifi == State.CONNECTED || wifi == State.CONNECTING;
        } catch (Exception e) {
            LogUtil.e(TAG, "判断网络异常", e);
        }
        return false;
    }

    /**
     * 判断网络连接状况
     *
     * @param aContext
     * @return
     */
    public static boolean isNetworkConnected(Context aContext) {
        boolean flag = false;
        try {
            if (aContext == null) {
                return false;
            }
            ConnectivityManager conMan = (ConnectivityManager) aContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            // mobile 3G Data Network
            State mobile = State.UNKNOWN;
            State wifi = State.UNKNOWN;
            NetworkInfo networkInfo = conMan
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (networkInfo != null) {
                mobile = networkInfo.getState();
            }
            networkInfo = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (networkInfo != null) {
                wifi = networkInfo.getState();
            }
            if (mobile == State.CONNECTED || mobile == State.CONNECTING) {
                flag = true;

            }
            if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
                flag = true;

            }
        } catch (Exception e) {
            LogUtil.e(TAG, "判断网络异常", e);
        }
        return flag;
    }


    /**
     * 判断是否安装某应用程序
     */

    public static boolean appIsInstalled(Context context, String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            LogUtil.i(TAG, "packageInfo:" + packageInfo);
        } catch (Exception e) {
            //Caused by: android.os.TransactionTooLargeException
            LogUtil.i(TAG, "判断安装异常了", e);
            return false;
        }
        return true;

    }


    /**
     * 向本地写数据feed流顺序表,以追加形式
     */
    public static String getPackageSignatureMD5(Context context, String pkgname) {
        try {
            PackageManager manager = context.getPackageManager();
            //通过包管理器获得指定包名包含签名的包信息
            PackageInfo packageInfo = manager.getPackageInfo(pkgname, PackageManager.GET_SIGNATURES);
            //通过返回的包信息获得签名数组
            Signature[] signatures = packageInfo.signatures;
            LogUtil.i(TAG, "签名个数：" + signatures.length);
            byte[] signatureArray = null;
            //循环遍历签名数组拼接应用签名
            for (Signature signature : signatures) {
                signatureArray = signature.toByteArray();
                LogUtil.i(TAG, "签名:" + signatureArray);
                LogUtil.i(TAG, "MD5签名后:" + MD5.hexdigest(signatureArray));
            }
            LogUtil.i(TAG, "获取到的签名为：" + signatureArray);
            String signatureMd5 = MD5.hexdigest(signatureArray);
            LogUtil.i(TAG, "获取到的签名md5为：" + signatureMd5);
            return signatureMd5;
        } catch (Exception e) {
            LogUtil.e(TAG, "获取签名异常", e);
        }
        return "";
    }


    /**
     * 转换文件大小
     *
     * @param fileS
     * @return B/KB/MB/GB
     */
    public static String formatFileSize(long fileS) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.0");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 清除缓存目录
     *
     * @param dir     目录
     * @param curTime 当前系统时间
     * @return
     */
    public static int clearCacheFolder(File dir, long curTime) {
        int deletedFiles = 0;
        if (dir != null && dir.isDirectory()) {
            try {
                for (File child : dir.listFiles()) {
                    if (child.isDirectory()) {
                        deletedFiles += clearCacheFolder(child, curTime);
                    }
                    if (child.lastModified() < curTime) {
                        String a = child.toString();
                        int index = a.lastIndexOf("/");
                        a = a.substring(index + 1);
                        if (!"volley".endsWith(a) && child.delete()) {
                            deletedFiles++;
                        }
                    }
                }
            } catch (Exception e) {
                LogUtil.e(TAG, e.getMessage(), e.getCause());
            }
        }
        return deletedFiles;
    }

    // 获取SD卡剩余空间
    public static long getRemainSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    // 判断文件是否存在
    public static boolean isFileExist(String path) {
        File file = new File(Environment.getExternalStorageDirectory() + path);
        return file.exists();
    }

    // 删除Editable中指定位置的元素，可能为字符，也可能为ImageSpan
    public static Editable deleteElement(Editable editText, int position) {
        ImageSpan[] imageSpans = editText
                .getSpans(0, position, ImageSpan.class);
        if (imageSpans.length > 0) {
            ImageSpan lastImageSpan = null;
            int end = 0;
            for (int i = imageSpans.length - 1; i >= 0; i--) {
                lastImageSpan = imageSpans[i];
                end = editText.getSpanEnd(lastImageSpan);
                if (end == position) {
                    break;
                }
            }
            if (end == position) {
                int start = editText.getSpanStart(lastImageSpan);
                editText.delete(start, end);
            } else {
                editText.delete(position - 1, position);
            }
        } else {
            editText.delete(position - 1, position);
        }
        return editText;
    }


    /**
     * 获取联网类型
     */
    public static String getLinkType(Context context) {
        String type;
        if (is3gNet(context)) {
            type = "3g";
        } else if (is2gOr3g(context)) {
            type = "2g";
        } else {
            type = "wifi";
        }
        return type;
    }


    /**
     * 扩大View的触摸和点击响应范围,最大不超过其父View范围
     *
     * @param view
     * @param top
     * @param bottom
     * @param left
     * @param right
     */
    public static void expandViewTouchDelegate(final View view, final int top,
                                               final int bottom, final int left, final int right) {

        ((View) view.getParent()).post(new Runnable() {
            @Override
            public void run() {
                Rect bounds = new Rect();
                view.setEnabled(true);
                view.getHitRect(bounds);

                bounds.top -= top;
                bounds.bottom += bottom;
                bounds.left -= left;
                bounds.right += right;

                TouchDelegate touchDelegate = new TouchDelegate(bounds, view);

                if (View.class.isInstance(view.getParent())) {
                    ((View) view.getParent()).setTouchDelegate(touchDelegate);
                }
            }
        });
    }


    /**
     * 添加图片标签到文字的开头
     */
    public static SpannableString addLabelImgToTextFront(final Context context, int resId, String text) {

        try {
            String LABEL = "[label]";
            String separatorInLabel = " ";
            if (text.startsWith("【")) {
                // 以【 开头的文字，标签前不再加一个空格分割，否则分割距离过大，因为【字符签名自带一段空白距离
                separatorInLabel = "";
            }
            SpannableString ss = new SpannableString(LABEL + separatorInLabel + text);// 标志和文字保持一定间距
            // 得到drawable对象，即所要插入的图片
            Drawable d = SdkConfig.getAppContext().getResources().getDrawable(resId);
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            // 用这个drawable对象代替字符串LABEL
            ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE) {
                @Override
                public void draw(Canvas canvas, CharSequence text, int start, int end, float x,
                                 int top, int y, int bottom, Paint paint) {
                    Drawable b = getDrawable();
                    canvas.save();

                    final int transY = top + (bottom - top - b.getBounds().bottom) / 2;

                    canvas.translate(x, transY + DisplayUtil.dip2px(context, 2f));
                    b.draw(canvas);
                    canvas.restore();
                }
            };
            // 包括0但是不包括
            // LABEL.length()即：7。[0,7)。值得注意的是当我们复制这个图片的时候，实际是复制了[label]这个字符串。
            ss.setSpan(span, 0, LABEL.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            return ss;
        } catch (Exception e) {
            LogUtil.e(TAG, "添加图片标签异常", e);
        }
        return new SpannableString(text);
    }

    /**
     * 添加图片标签到文字的结尾
     */
    public static SpannableString addLabelImgToTextTail(final Context context, int resId, String text) {
        try {
            String LABEL = "[label]";
            SpannableString ss = new SpannableString(text + " " + LABEL);
            Drawable d = SdkConfig.getAppContext().getResources().getDrawable(resId);
            assert d != null;
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE) {
                @Override
                public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x,
                                 int top, int y, int bottom, @NonNull Paint paint) {
                    Drawable b = getDrawable();
                    canvas.save();
                    final int transY = top + (bottom - top - b.getBounds().bottom) / 2;
                    canvas.translate(x, transY + DisplayUtil.dip2px(context, 1));
                    b.draw(canvas);
                    canvas.restore();
                }
            };
            ss.setSpan(span, ss.length() - LABEL.length(), ss.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            return ss;
        } catch (Exception e) {
            LogUtil.e(TAG, "添加图片标签异常", e);
        }
        return new SpannableString(text);
    }


    /**
     * 获取通过scheme跳转的
     */
    public static SchemeMap getSchemeParams(String schemeStr) {
        try {
            HashMap<String, String> paramsMap = new HashMap<String, String>();
            String paramsStr = schemeStr.substring(schemeStr.indexOf("?") + 1);
            String[] paramsArray = paramsStr.split("&");
            //	参数不为空，遍历添加到map中
            for (int i = 0; i < paramsArray.length; i++) {
                String[] param = paramsArray[i].split("=");
                if (param.length == 2) {
                    paramsMap.put(URLDecoder.decode(param[0], "utf-8"), URLDecoder.decode(param[1], "utf-8"));
                }
            }
            return new SchemeMap(paramsMap);
        } catch (Exception e) {
            LogUtil.e(TAG, "scheme解析异常", e);
        }
        return null;

    }


    /**
     * 是否安装了SIM卡
     */
    public static boolean isEquipSIM(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String iccid = tm.getSimSerialNumber();
        String imsi = tm.getSubscriberId();
        return !TextUtils.isEmpty(iccid) && !TextUtils.isEmpty(imsi);
    }


    /**
     * 第一次安装时在桌面创建快捷方式
     *
     * @param context
     * @param appName             appName
     * @param ic_launcher_res_id
     * @param splashActivityClass 欢迎页面的class
     */
    public static void createShortCut(Context context, String appName, int ic_launcher_res_id, Class splashActivityClass) {
        final String ACTION_ADD_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
        final String EXTRA_DUPLICATE = "duplicate";

        Intent intent = new Intent(ACTION_ADD_SHORTCUT);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, appName);
        // 是否可以有多个快捷方式的副本，参数如果是true就可以生成多个快捷方式，如果是false就不会重复添加
        // 好像无效哦
        intent.putExtra(EXTRA_DUPLICATE, false);
        Intent intent2 = new Intent(Intent.ACTION_MAIN);
        intent2.addCategory(Intent.CATEGORY_LAUNCHER);
        intent2.setClass(context, splashActivityClass);
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent2);
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(context,
                        ic_launcher_res_id));
        context.sendBroadcast(intent);
    }

}
