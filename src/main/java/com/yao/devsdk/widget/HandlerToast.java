package com.yao.devsdk.widget;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.widget.Toast;


/**
 * 自带HanderThread的Toast,避免在子线程中调用Toast.makeToast.show()时报looper没有准备完成异常
 *
 * @author shc
 * @since 15/10/8
 */
public class HandlerToast {
    private static HandlerToast ourInstance;
    private HandlerThread toastHandlerThread;
    private Handler toastHandler;
    private Context context;

    public static HandlerToast getInstance(Context context) {
        if (ourInstance == null){
            synchronized (HandlerToast.class){
                ourInstance = new HandlerToast(context);
            }
        }
        return ourInstance;
    }

    private HandlerToast(final Context context) {
        this.context = context;
        toastHandlerThread = new HandlerThread("toasthandler");
        toastHandlerThread.start();
        toastHandler = new Handler(toastHandlerThread.getLooper());
    }


    /**
     * 显示系统toast
     * @param str
     */
    public void showToast(final String str){
        if (TextUtils.isEmpty(str)){
            return;
        }

        toastHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,str,Toast.LENGTH_SHORT).show();
            }
        });

    }


}
