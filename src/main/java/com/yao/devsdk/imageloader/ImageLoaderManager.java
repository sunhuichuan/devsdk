package com.yao.devsdk.imageloader;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;

/**
 * 图片加载管理者
 * Created by YAO on 15/7/16.
 */
public class ImageLoaderManager {


    private static ImageLoaderManager instance = new ImageLoaderManager();

    private ImageLoaderManager() {
    }

    /**
     * 单例获取ImageCacheManager
     */
    public static ImageLoaderManager getInstance() {
        return instance;
    }



    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .memoryCacheSizePercentage(20)
                .threadPoolSize(Runtime.getRuntime().availableProcessors() * 2 + 1)
                .build();
        ImageLoader.getInstance().init(config);
    }


    /**
     * 获取UIL实例
     * @return
     */
    public ImageLoader getUILInstance(){
        return ImageLoader.getInstance();
    }


    /**
     * 获取缓存的图片文件
     */
    public File getImageCacheFile(String url) {
        File imageFile = null;
        if (!TextUtils.isEmpty(url)) {
            // url可能会为空
            imageFile = ImageLoader.getInstance().getDiskCache().get(url);
        }
        return imageFile;
    }





    public void displayImage(String url,ImageView iv,DisplayImageOptions options){
        displayImage(url, iv, options,null);
    }
    /**
     * 加载图片
     * @param url
     * @param iv
     * @param options
     */
    public void displayImage(String url,ImageView iv,DisplayImageOptions options,ImageLoadingListener listener){
        ImageLoader.getInstance().displayImage(url, iv, options,listener);
    }


}
