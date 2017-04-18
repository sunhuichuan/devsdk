package com.yao.devsdk.components.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * 可以保存Activity的引用的Intent
 * Created by huichuan on 2017/2/22.
 */
public class ActivityIntent {


    private Intent mInnerIntent;
    private Activity mActivity;


    public ActivityIntent(Context context,Class<?> clazz){
        if (context instanceof Activity){
            mInnerIntent = new Intent(context,clazz);
            mActivity = (Activity) context;
        }else{
            throw new RuntimeException("Context 必须是Activity");
        }
    }

    public Intent getIntent() {
        return mInnerIntent;
    }

    public Activity getContext() {
        return mActivity;
    }
}
