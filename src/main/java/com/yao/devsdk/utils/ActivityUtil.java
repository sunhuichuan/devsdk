package com.yao.devsdk.utils;

import android.app.Activity;
import android.content.Intent;

import com.yao.devsdk.R;
import com.yao.devsdk.components.model.ActivityIntent;
import com.yao.devsdk.ui.SDKBaseActivity;

/**
 * Activity跳转工具类
 */
public class ActivityUtil {

    /**
     * 默认动画开启Activity
     * @param intent
     */
    public static void startActivityWithDefaultAnimation(ActivityIntent intent){
        startActivityWithDefaultAnimation(intent,-1);
    }

    /**
     * 默认动画开启Activity
     * @param activityIntent
     */
    public static void startActivityWithDefaultAnimation(ActivityIntent activityIntent, int requestCode){
        if (activityIntent!=null){
            Intent intent = activityIntent.getIntent();
            Activity activity = activityIntent.getContext();
            activity.startActivityForResult(intent, requestCode);
        }
    }


    /**
     * 平移动画开启Activity
     * @param intent
     */
    public static void startActivityWithTranslationAnimation(ActivityIntent intent){
        startActivityWithTranslationAnimation(intent,-1);
    }
    /**
     * 平移动画开启Activity
     * @param activityIntent
     */
    public static void startActivityWithTranslationAnimation(ActivityIntent activityIntent, int requestCode){
        if (activityIntent!=null){
            int inAnim = R.anim.activity_translation_start_in;
            int outAnim = R.anim.activity_translation_start_out;
            Intent intent = activityIntent.getIntent();
            Activity activity = activityIntent.getContext();
            activity.startActivityForResult(intent, requestCode);
            activity.overridePendingTransition(inAnim,
                    outAnim);
        }
    }

    /**
     * 平移动画关闭Activity
     * @param context
     */
    public static void finishActivityWithTranslationAnimation(SDKBaseActivity context){
        if (context!=null){
            int inAnim = R.anim.activity_translation_finish_in;
            int outAnim = R.anim.activity_translation_finish_out;
            context.finishPage();
            context.overridePendingTransition(inAnim,
                    outAnim);
        }
    }





    /**
     * 渐入渐出动画开启Activity
     * @param intent
     */
    public static void startActivityWithFadeAnimation(ActivityIntent intent){
        startActivityWithFadeAnimation(intent,-1);
    }
    /**
     * 渐入渐出动画开启Activity
     */
    public static void startActivityWithFadeAnimation(ActivityIntent activityIntent,int requestCode){
        if (activityIntent!=null){
            int inAnim = R.anim.activity_fade_in;
            int outAnim = R.anim.activity_fade_out;
            Intent intent = activityIntent.getIntent();
            Activity activity = activityIntent.getContext();
            activity.startActivityForResult(intent,requestCode);
            activity.overridePendingTransition(inAnim,
                    outAnim);
        }
    }
    /**
     * 渐入渐出动画开启Activity
     * @param context
     */
    public static void finishActivityWithFadeAnimation(SDKBaseActivity context){
        if (context!=null){
            int outAnim = R.anim.activity_fade_out;
            context.finishPage();
            context.overridePendingTransition(0,
                    outAnim);
        }
    }



    /**
     * 从下向上动画开启Activity
     * @param intent
     */
    public static void startActivityWithUpAnimation(ActivityIntent intent){
        startActivityWithUpAnimation(intent,-1);
    }
    /**
     * 从下向上动画开启Activity
     */
    public static void startActivityWithUpAnimation(ActivityIntent activityIntent,int requestCode){
        if (activityIntent!=null){
            int inAnim = R.anim.activity_up_in;
            Intent intent = activityIntent.getIntent();
            Activity activity = activityIntent.getContext();
            activity.startActivityForResult(intent,requestCode);
            //加上fake_anim是为了避免背景变黑色
            activity.overridePendingTransition(inAnim,R.anim.fake_anim);
        }
    }
    /**
     * 渐入渐出动画开启Activity
     * @param context
     */
    public static void finishActivityWithDownAnimation(SDKBaseActivity context){
        if (context!=null){
            int outAnim = R.anim.activity_up_out;
            context.finishPage();
            context.overridePendingTransition(0,
                    outAnim);
        }
    }
}
