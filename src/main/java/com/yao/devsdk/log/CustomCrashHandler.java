package com.yao.devsdk.log;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.yao.devsdk.constants.SdkConst;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

public class CustomCrashHandler implements UncaughtExceptionHandler {
    private static final String TAG = "CustomCrashHandler";
    private Context mContext;
    private static CustomCrashHandler mInstance = new CustomCrashHandler();

    private CustomCrashHandler() {
    }

    public static CustomCrashHandler getInstance() {
        return mInstance;
    }

    /**
     * 异常发生时，系统回调的函数，我们在这里处理一些操作
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        // 将一些信息保存到SDcard中 //TODO 需要修改逻辑，把同一天的crash log放在一个文件中
        CrashLogManager.getInstance().saveInfoToSD(mContext, ex);
        Log.e(TAG, "Fatal异常崩溃", ex);
        // 提示用户程序即将退出
        showToast(mContext, "很抱歉，程序遭遇异常，即将退出！");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);

    }

    /**
     * 为我们的应用程序设置自定义Crash处理
     */
    public void setCustomCrashHanler(Context context) {
        mContext = context;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 显示提示信息，需要在线程中显示Toast
     *
     * @param context
     * @param msg
     */
    private void showToast(final Context context, final String msg) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }).start();
    }





    /**
     * 获取异常的stack信息
     *
     */
    public static String getExceptionStackInfo(Throwable e){
        StringWriter mStringWriter = new StringWriter();
        PrintWriter mPrintWriter = new PrintWriter(mStringWriter);
        e.printStackTrace(mPrintWriter);
        mPrintWriter.close();
        String cause = mStringWriter.toString();
        String msg = e.getMessage();
        String stackString = "。。。\n"+msg+"。。。\n"+cause;
        return stackString;
    }
    /**
     * 存储异常Log
     * 抛出去不就崩溃了，即使界面显示成屎，也比一点就崩溃强一点不是
     * 现在修改切debug时抛，为了发现问题，线上不抛
     */
    public static void saveExceptionLog(Context context, String exceptionStr, Exception e){
        String msg = getExceptionStackInfo(e);
        IllegalStateException throwable = new IllegalStateException(exceptionStr+"###\n"+msg);
        CrashLogManager crashLogManager = CrashLogManager.getInstance();
        if (SdkConst.DEBUG){
            //debug时，要把throwable抛出去
            crashLogManager.saveInfoToSD(context,throwable);
            throw throwable;
        }else{
            crashLogManager.saveInfoToSD(context, throwable);
        }
    }
}
