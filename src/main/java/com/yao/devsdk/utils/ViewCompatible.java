package com.yao.devsdk.utils;

import android.view.View;

/**
 *
 * Created by huichuan on 16/5/8.
 */
public class ViewCompatible {

    /**
     * 设置View背景色
     * @param v
     * @param colorId
     */
    public static void setBackgroundColor(View v, int colorId) {
        v.setBackgroundColor(v.getContext().getResources().getColor(colorId));
    }
}
