package com.yao.devsdk.factory;

import android.content.Context;
import android.media.Image;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yao.devsdk.R;
import com.yao.devsdk.SdkConfig;
import com.yao.devsdk.utils.DisplayUtil;

/**
 * View的创建工厂
 * 此类只是为了和style对照，写在sdk中不一定合适
 * Created by huichuan on 16/9/22.
 */
public class ViewFactory {

    /**
     * 创建普通的TextView
     * @param context
     * @param text
     * @return
     */
    public static TextView createTextView(Context context,CharSequence text){
        Context appContext = SdkConfig.getAppContext();
        TextView tv_text = new TextView(context);
        tv_text.setTextSize(15);
        tv_text.setSingleLine();
        tv_text.setEllipsize(TextUtils.TruncateAt.END);
        tv_text.setGravity(Gravity.CENTER);
        tv_text.setTextColor(DisplayUtil.getColor(appContext,R.color.gray_color_545454));
        tv_text.setText(text);
        return tv_text;
    }

    /**
     * 创建普通的ImageView
     * @param context
     * @return
     */
    public static ImageView createImageView(Context context,int defaultResId){
        Context appContext = SdkConfig.getAppContext();
        ImageView iv_image = new ImageView(context);
        iv_image.setImageResource(defaultResId);
        iv_image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return iv_image;
    }



}
