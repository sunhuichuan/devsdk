package com.yao.devsdk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;

public class DisplayUtil {




    //liaohuqiu demo 中的DisplayUtils
    public static int SCREEN_WIDTH_PIXELS;
    public static int SCREEN_HEIGHT_PIXELS;
    public static float SCREEN_DENSITY;
    public static int SCREEN_WIDTH_DP;
    public static int SCREEN_HEIGHT_DP;
    private static boolean sInitialed = false;

    public static void init(Context context) {
        if (sInitialed || context == null) {
            return;
        }
        sInitialed = true;
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        SCREEN_WIDTH_PIXELS = dm.widthPixels;
        SCREEN_HEIGHT_PIXELS = dm.heightPixels;
        SCREEN_DENSITY = dm.density;
        SCREEN_WIDTH_DP = (int) (SCREEN_WIDTH_PIXELS / dm.density);
        SCREEN_HEIGHT_DP = (int) (SCREEN_HEIGHT_PIXELS / dm.density);
    }

    public static int dp2px(float dp) {
        final float scale = SCREEN_DENSITY;
        return (int) (dp * scale + 0.5f);
    }

    public static int designedDP2px(float designedDp) {
        if (SCREEN_WIDTH_DP != 320) {
            designedDp = designedDp * SCREEN_WIDTH_DP / 320f;
        }
        return dp2px(designedDp);
    }







    /**
     * 获取手机屏幕高度,以px为单位
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    /**
     * 获取手机屏幕宽度，以px为单位
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    /**
     * 返回程序window宽度
     *
     * @return
     */
    public static int getWindowWidth(Activity activity) {
        return activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getWidth();
    }

    /**
     * 返回程序window高度，不包括通知栏和标题栏
     *
     * @return
     */
    public static int getWindowContentHeight(Activity activity) {
        return activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT)
                .getHeight();
    }

    /**
     * 返回程序window高度，不包括通知栏
     *
     * @return
     */
    public static int getWindowHeight(Activity activity) {
        return getScreenHeight(activity) - getStatusBarHeight(activity);
    }

    /**
     * 返回屏幕像素密度
     *
     * @param context
     * @return
     */
    public static float getPixelDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * 返回状态栏高度
     *
     * @param activity
     * @return
     */
    public static int getStatusBarHeight(Activity activity) {
        Rect outRect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        return outRect.top;
    }



    /**
     * 利用反射获取状态栏的高度。
     *
     * @return 返回状态栏高度的像素值。
     */
    public static int getStatusBarHeightWithReflect(Context context) {
        int statusBarHeight = 0;
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = context.getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        return statusBarHeight;
    }




    public static int getTitleBarHeight(Activity activity) {
        return getScreenHeight(activity) - getWindowContentHeight(activity)
                - getStatusBarHeight(activity);
    }

    /**
     * 单位转换，将dip转换为px
     *
     * @param dp
     * @param context
     * @return
     */
    public static int dip2px(Context context,float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * 单位转换，将px转换为dip
     *
     * @param px
     * @param context
     * @return
     */
    public static int px2dip(Context context, float px) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }


    /**
     * 活的ActionBar的高度
     * @param activity
     * @return
     */
    public static int getActionBarHeight(Activity activity) {
        TypedValue mTypedValue = new TypedValue();
        activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, mTypedValue, true);
        int actionBarHeight = TypedValue.complexToDimensionPixelOffset(mTypedValue.data, activity.getResources().getDisplayMetrics());
        return actionBarHeight;
    }


    /**
     * 获取dimens值
     * @param context
     * @param dimensResource
     * @return
     */
    public static int getDimens(Context context,int dimensResource){
        int dimens = context.getResources().getDimensionPixelOffset(dimensResource);
        return dimens;
    }

    public static int getNavBarHeight(Context c) {
        int result = 0;
        boolean hasMenuKey = ViewConfiguration.get(c).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

        if(!hasMenuKey && !hasBackKey) {
            //The device has a navigation bar
            Resources resources = c.getResources();

            int orientation = resources.getConfiguration().orientation;
            int resourceId;
            if (isTablet(c)){
                resourceId = resources.getIdentifier(orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_height_landscape", "dimen", "android");
            }  else {
                resourceId = resources.getIdentifier(orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_width", "dimen", "android");
            }

            if (resourceId > 0) {
                return resources.getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }


    static boolean isTablet(Context c) {
        return (c.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }


    /**
     * 取得resource颜色id
     * @param context
     * @param resourceId
     * @return
     */
    public static int getColor(Context context,int resourceId){
        int color = context.getResources().getColor(resourceId);
        return color;
    }

    /**
     * 取得带有状态的颜色资源id
     * @param context
     * @param resourceId
     * @return
     */
    public static ColorStateList getColorStateList(Context context,int resourceId){
        ColorStateList colorStateList = context.getResources().getColorStateList(resourceId);
        return colorStateList;
    }

    /**
     * 取得resource drawable
     * @param context
     * @param resourceId
     * @return
     */
    public static Drawable getDrawable(Context context,int resourceId){
        Drawable drawable = context.getResources().getDrawable(resourceId);
        return drawable;
    }

}
