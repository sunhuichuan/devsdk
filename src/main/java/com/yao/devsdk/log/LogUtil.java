package com.yao.devsdk.log;

import com.yao.devsdk.constants.SdkConst;
import com.yao.devsdk.utils.file.FileUtil;

/**
 * sdk的log打印
 */
public class LogUtil {
    private static String TAG = "LogUtil";

    static boolean showLog() {
        return SdkConst.DEBUG;
    }

    public static void v(final String tag, final String msg) {
        if (showLog()) {
            android.util.Log.v(tag, msg);

        }
    }

    public static void v(final String tag, final String msg, Throwable throwable) {
        if (showLog()) {
            android.util.Log.v(tag, msg, throwable);

        }
    }

    public static void d(final String tag, final String msg) {
        if (showLog()) {

            android.util.Log.d(tag, msg);
        }
    }

    public static void d(final String tag, final String msg, Throwable throwable) {
        if (showLog()) {

            android.util.Log.d(tag, msg, throwable);
        }
    }

    public static void i(final String tag, final String msg) {
        if (showLog()) {

            android.util.Log.i(tag, msg);
        }
    }

    public static void i(final String tag, final String msg, Throwable throwable) {
        if (showLog()) {

            android.util.Log.i(tag, msg, throwable);
        }
    }

    public static void w(final String tag, final String msg) {
        if (showLog()) {

            android.util.Log.w(tag, msg);
        }
    }

    public static void w(final String tag, final String msg, Throwable throwable) {
        if (showLog()) {

            android.util.Log.w(tag, msg, throwable);
        }
    }

    public static void e(final String tag, final String msg) {
        if (showLog()) {

            android.util.Log.e(tag, msg);
        }
    }

    public static void e(final String tag, final String msg, Throwable throwable) {
        if (showLog()) {

            android.util.Log.e(tag, msg, throwable);
        }
    }

    /**
     * 打印 System.out.println 的 log
     *
     * @param msg
     */
    public static void println(String msg) {
        if (showLog()) {
            System.out.println(msg);
        }
    }

    public static void writeLog(String tag, final String msg, Throwable throwable) {
        if (showLog()) {
            android.util.Log.e(tag, msg, throwable);
            FileUtil.writeStringToSDFile("log.txt", msg);
        }
    }

    public static void logMEM() {
        // #mdebug
        if (SdkConst.DEBUG) {
            String mem = "********************************************" + "\n" + "nativeHeap = " +
                    android.os.Debug.getNativeHeapAllocatedSize() + " bytes\n" + "totalMemory = " +
                    Runtime.getRuntime().totalMemory() + " bytes\n" + "freeMemory = " +
                    Runtime.getRuntime().freeMemory() + " bytes\n" + "maxMemory = " + Runtime.getRuntime().maxMemory() +
                    " bytes\n" + "********************************************";
            LogUtil.v(TAG, mem);
        }
        // #enddebug
    }

}
