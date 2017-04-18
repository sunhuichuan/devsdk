package com.yao.devsdk;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.yao.devsdk.constants.FolderPath;
import com.yao.devsdk.imageloader.ImageLoaderManager;
import com.yao.devsdk.log.CrashLogManager;
import com.yao.devsdk.log.CustomCrashHandler;
import com.yao.devsdk.log.LoggerUtil;
import com.yao.devsdk.constants.SdkConst;
import com.yao.devsdk.prefs.PrefsSDKUser;
import com.yao.devsdk.utils.DisplayUtil;
import com.yao.devsdk.utils.SdkUtil;
import com.yao.devsdk.utils.UniqueDeviceId;

public class SdkConfig {
    private static final String TAG = "SdkConfig";

    private static Context appContext;

    //版本号
    public static int versionCode;
    //版本名
    public static String versionName;

    public static String OSVersionName;



    public static DisplayMetrics mDisplayMetrics;

    //设备屏幕宽度
    public static int mScreenWidth;
    //设备屏幕高度
    public static int mScreenHeight;
    // DEVICE_ID 默认为15个零，每台机器都不一样。
    public static String deviceId = "000000000000000";


    public static float density;
    public static int densityDpi;

    public static String networkType;
    public static String networkSubtype;
    public static String networkOperator;
    public static String mac;
    public static String iccid;
    public static String imsi;
    public static String IMEI;
    public static String deviceName;
    private static String ipAddress;
    public static String userAgent = "android";
    public static int heapSize;

    public static String token;
    public static String uid;
    public static long token_expiredTime;

    /**
     * 初始化sdk
     * @param context
     * @param isDebug 是否开启debug模式
     * @param SDFolderPath sd卡上文件存储的path
     */
    public static void initBaseConfig(Context context,boolean isDebug,String SDFolderPath) {
        appContext = context;
        //这里要判断是否有 " / ",否则保存目录会错误！！！
        FolderPath.PATH_APP_ROOT = (SDFolderPath.indexOf(0)=='/')?SDFolderPath:'/'+SDFolderPath;
        SdkConst.DEBUG = isDebug;
        initVersionInfo(context);
        initDeviceInfo(context);

        //初始化DisplayUtils
        DisplayUtil.init(context);
        //初始化ImageLoader
        ImageLoaderManager.initImageLoader(context);
        //设置UncaughtExceptionHandler
        CustomCrashHandler.getInstance().setCustomCrashHanler(context);
        //清理crash文件夹
        CrashLogManager.getInstance().cleanCrashLogFolder();
    }

    /**
     * 获取appContext的引用,供sdk上层module调用
     * @return
     */
    public static Context getAppContext(){
        return appContext;
    }

    /**
     * 获取设备硬件相关信息
     *
     * @param context
     */
    private static void initDeviceInfo(Context context) {
        initDisplay(context);
        initNetwork(context);
        initMac(context);
        initDeviceName(context);
        initDeviceUserAgent(context);
        initIpAddress(context);
        initHeapSize(context);
        initPhoneState(context);

//        mArmArchitecture = getCpuArchitecture();
        deviceId = UniqueDeviceId.getDeviceId(context);

    }

//    public static Object[] getCpuArchitecture(){
//        return CpuUtils.getCpuArchitecture();
//    }


    private static void initVersionInfo(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
            versionName = pi.versionName;
            versionCode = pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            LoggerUtil.e(TAG, "获取版本号异常", e);
        }
    }

    private static void initDisplay(Context context) {
        mDisplayMetrics = context.getResources().getDisplayMetrics();
        density = mDisplayMetrics.density;
        densityDpi = mDisplayMetrics.densityDpi;
        mScreenWidth = mDisplayMetrics.widthPixels;
        mScreenHeight = mDisplayMetrics.heightPixels;
        if (mScreenWidth > mScreenHeight) {
            //宽高取得有问题,调换宽高
            mScreenWidth = mDisplayMetrics.heightPixels;
            mScreenHeight = mDisplayMetrics.widthPixels;
        }
    }




    private static void initNetwork(Context context) {
        NetworkInfo ni = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        networkType = ni == null ? "" : ni.getTypeName();
        networkSubtype = ni == null ? "" : ni.getSubtypeName();
    }

    /**
     * 获取网卡mac地址,过滤掉":"
     *
     * @return
     */
    private static void initMac(Context context) {
        try {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();

        mac = info.getMacAddress();
//        if (mac != null) {
//            mac = mac.replaceAll(":", "").toUpperCase();
//        }
    } catch (Exception e) {
        LoggerUtil.e(TAG,"获取PhoneState相关权限异常",e);
    }
    }
    /**
     * 获取设备app堆大小
     */
    private static void initHeapSize(Context context){
        try {
            ActivityManager activityManager= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            heapSize=activityManager.getMemoryClass();
        } catch (Exception e) {
            LoggerUtil.e(TAG, "获取heapSize异常", e);
        }
    }
    /**
     * 获取手机型号名
     */
    private static void initDeviceName(Context context) {
        deviceName = Build.MODEL;
        OSVersionName = Build.VERSION.RELEASE;//系统版本号，例如 4.4.2
    }

    /**
     * 获取手机UA
     */
    private static void initDeviceUserAgent(Context context) {
        userAgent = getCurrentUserAgent(context);
    }
    /**
     * 获取ip地址
     */
    private static void initIpAddress(Context context) {
        try {
            ipAddress = SdkUtil.getLocalIpAddress(context);
        } catch (Exception e) {
            LoggerUtil.e(TAG, "获取ip地址异常", e);
        }
        LoggerUtil.i(TAG,"ip地址是："+ipAddress);
    }


    /**
     * 获取浏览器的UA
     */
    public static String getCurrentUserAgent(Context context) {
        try {
            if (TextUtils.equals(userAgent, "android")) {
                WebView webview = new WebView(context);
                //webview.layout(0, 0, 0, 0);
                WebSettings settings = webview.getSettings();
                userAgent = settings.getUserAgentString();
            }
            if (TextUtils.isEmpty(userAgent)){
                userAgent = "error";
            }
        } catch (Exception e) {
            LoggerUtil.e(TAG, "获取userAgent异常", e);
        }
        return userAgent;
    }


    /**
     * 初始化PhoneState权限相关的值
     * @param context
     */
    public static void initPhoneState(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            iccid = tm.getSimSerialNumber();
            imsi = tm.getSubscriberId();
            IMEI = tm.getDeviceId();
            networkOperator = tm.getNetworkOperatorName();
        } catch (SecurityException e) {
            LoggerUtil.i(TAG,"获取PhoneState相关权限异常",e);
        } catch (Exception e) {
            LoggerUtil.e(TAG,"获取IMEI异常",e);
        }
    }


    /**获取ip地址*/
    public static String getIpAddress() {
        if (TextUtils.isEmpty(ipAddress)){
            initIpAddress(getAppContext());
        }
        return ipAddress;
    }

    /**
     * 是否已经登录
     */
    public static boolean isLogin() {
        boolean result = !TextUtils.isEmpty(SdkConfig.token) && !TextUtils.isEmpty(SdkConfig.uid);
        return result;
    }




    /**
     * 初始化uid和token
     */
    public static void initUidToken() {
        PrefsSDKUser prefsUser = PrefsSDKUser.getInstance();
        String uid = prefsUser.login_uid.getVal();
        String token = prefsUser.login_token.getVal();
        SdkConfig.uid = uid;
        SdkConfig.token = token;
    }

    /**
     * 保存uid和token
     * @param uid
     * @param token
     */
    public static void saveUidToken(String uid,String token){
        PrefsSDKUser prefsUser = PrefsSDKUser.getInstance();
        prefsUser.login_uid.setVal(uid).commit();
        prefsUser.login_token.setVal(token).commit();
        SdkConfig.uid = uid;
        SdkConfig.token = token;
    }


    /**
     * 清除uidtoken，算是退出登录
     */
    public static void clearUidToken() {
        PrefsSDKUser prefsUser = PrefsSDKUser.getInstance();
        prefsUser.login_uid.setVal("").commit();
        prefsUser.login_token.setVal("").commit();
        SdkConfig.uid = "";
        SdkConfig.token = "";
    }




}
