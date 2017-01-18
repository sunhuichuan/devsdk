package com.yao.devsdk.imageloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.yao.devsdk.R;

import java.io.File;

public class ImageOption {

    private static DisplayImageOptions smallOptions;
    private static DisplayImageOptions bigOptions;

    public static DisplayImageOptions avatarOptions = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.default_pic_middle)
            .showImageForEmptyUri(R.drawable.default_pic_middle)
            .showImageOnFail(R.drawable.default_pic_middle)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    private static DisplayImageOptions emptyOptions;

    public static DisplayImageOptions getSmallOptions() {
        if (smallOptions == null){
            smallOptions = getSmallOptionsBuilder().build();
        }
        return smallOptions;
    }

    public static DisplayImageOptions getBigOptions() {
        if (bigOptions == null){
            bigOptions = getBigOptionsBuilder().build();
        }
        return bigOptions;
    }


    public static DisplayImageOptions getEmptyOptions() {
        if (emptyOptions == null){
            emptyOptions = getEmptyOptionsBuilder().build();
        }
        return emptyOptions;
    }

    public static DisplayImageOptions.Builder getSmallOptionsBuilder(){
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_pic_middle)
                .showImageForEmptyUri(R.drawable.default_pic_middle)
                .showImageOnFail(R.drawable.default_pic_middle)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new FadeInBitmapDisplayer(200));//图片加载好后渐入的动画时间;
        return builder;
    }
    public static DisplayImageOptions.Builder getBigOptionsBuilder(){
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_pic_large)
                .showImageForEmptyUri(R.drawable.default_pic_large)
                .showImageOnFail(R.drawable.default_pic_large)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new FadeInBitmapDisplayer(200));//图片加载好后渐入的动画时间
        return builder;
    }


    public static DisplayImageOptions.Builder getEmptyOptionsBuilder(){
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder()
                .cacheInMemory(false)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565);
        return builder;
    }



    /**
     * 获取分享图片的Options
     */
    public static Bitmap getSharePicBitmap(String url) {

        Bitmap bitmap = null;
        File file = ImageLoaderManager.getInstance().getImageCacheFile(url);
        if (file != null) {
            bitmap = transImage(file.getAbsolutePath(), 200, 200);
        }
        return bitmap;

    }


    public static Bitmap transImage(String fromFile, int width, int height) {
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(fromFile, options);
            int bitmapWidth = options.outWidth;
            int bitmapHeight = options.outHeight;
            // 缩放图片的尺寸
            float scaleWidth = (float) bitmapWidth / width;
            float scaleHeight = (float) bitmapHeight / width;
//            Matrix matrix = new Matrix();
//            matrix.postScale(scaleWidth, scaleHeight);

            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inSampleSize = scaleHeight > scaleWidth ? Math.round(scaleHeight) : Math.round(scaleHeight);
            bitmap = BitmapFactory.decodeFile(fromFile, options);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return bitmap;
    }

}
