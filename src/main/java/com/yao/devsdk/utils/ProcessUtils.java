package com.yao.devsdk.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

import com.yao.devsdk.log.LoggerUtil;

import java.util.List;

/**
 * 进程相关Utils
 * Created by huichuan on 16/9/5.
 */
public class ProcessUtils {

    private static final String TAG = "ProcessUtils";

    /**
     * 当前进程名字
     * @param context
     * @return
     */
    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    /**
     * 是否是主进程
     * //TODO 是不是写的有点啰嗦？待修改？
     * @param context
     * @return
     */
    public static boolean isMainProcess(Context context) {
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfo = am.getRunningAppProcesses();
        String mainProcessName = context.getPackageName();
        int myPid = android.os.Process.myPid();
        LoggerUtil.i(TAG, "进程pid:" + myPid);
        for (ActivityManager.RunningAppProcessInfo info : processInfo) {
            if (info.pid == myPid){
                LoggerUtil.i(TAG, "进程名：" + info.processName);
                if (TextUtils.equals(mainProcessName,info.processName)){
                    return true;
                }else{
                    return false;
                }
            }
        }
        return false;
    }


}
