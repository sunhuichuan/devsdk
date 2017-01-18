package com.yao.devsdk.utils;

import android.content.Context;
import android.text.TextUtils;

import com.yao.devsdk.SdkConfig;

/**
 *
 * Created by huichuan on 16/4/15.
 */
public class StringKit {

    /**
     * 比较字符串和数字是否相等
     * @param str
     * @param num
     * @return
     */
    public static boolean isEquals(String str,int num){
        String numStr = String.valueOf(num);
        boolean result = TextUtils.equals(str,numStr);
        return result;
    }

    /**
     * 字符串
     * @param resId
     * @return
     */
    public static String getString(int resId){
        Context appContext = SdkConfig.getAppContext();
        String string = appContext.getResources().getString(resId);
        return string;
    }


}
