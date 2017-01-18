package com.yao.devsdk.utils;

import android.content.Intent;

import com.yao.devsdk.R;
import com.yao.devsdk.ui.SDKBaseActivity;

/**
 * Activity跳转工具类
 */
public class ActivityUtil {


    /**
     * 平移动画开启Activity
     * @param context
     * @param intent
     */
    public static void startActivityWithTranslationAnimation(SDKBaseActivity context,Intent intent){
        startActivityWithTranslationAnimation(context,intent,-1);
    }
    /**
     * 平移动画开启Activity
     * @param context
     * @param intent
     */
    public static void startActivityWithTranslationAnimation(SDKBaseActivity context,Intent intent,int requestCode){
        if (intent!=null && context!=null){
            int inAnim = R.anim.activity_translation_start_in;
            int outAnim = R.anim.activity_translation_start_out;
            context.startActivityForResult(intent, requestCode);
            context.overridePendingTransition(inAnim,
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
     * @param context
     * @param intent
     */
    public static void startActivityWithFadeAnimation(SDKBaseActivity context,Intent intent){
        startActivityWithFadeAnimation(context,intent,-1);
    }
    /**
     * 渐入渐出动画开启Activity
     * @param context
     * @param intent
     */
    public static void startActivityWithFadeAnimation(SDKBaseActivity context,Intent intent,int requestCode){
        if (intent!=null){
            int inAnim = R.anim.activity_fade_in;
            int outAnim = R.anim.activity_fade_out;
            context.startActivityForResult(intent,requestCode);
            context.overridePendingTransition(inAnim,
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
     * @param context
     * @param intent
     */
    public static void startActivityWithUpAnimation(SDKBaseActivity context,Intent intent){
        startActivityWithUpAnimation(context,intent,-1);
    }
    /**
     * 从下向上动画开启Activity
     * @param context
     * @param intent
     */
    public static void startActivityWithUpAnimation(SDKBaseActivity context,Intent intent,int requestCode){
        if (intent!=null){
            int inAnim = R.anim.activity_up_in;
            context.startActivityForResult(intent,requestCode);
            //加上fake_anim是为了避免背景变黑色
            context.overridePendingTransition(inAnim,R.anim.fake_anim);
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
