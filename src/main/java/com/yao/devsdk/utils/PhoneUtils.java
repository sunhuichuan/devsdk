package com.yao.devsdk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 *
 * Created by huichuan on 16/9/22.
 */
public class PhoneUtils {

    /**
     * 跳转到拨号页面
     * @param context
     * @param phoneNum
     */
    public static void callPhoneNum(Context context, String phoneNum){
//        phoneno = Uri.encode("10086,,1,,1,#,2");
        String phoneNo = phoneNum;
        Intent intent=new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+phoneNo));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 打开桌面Launcher
     * @param context
     */
    public static void openLauncherActivity(Context context){
        Intent intent = new Intent();
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


}
