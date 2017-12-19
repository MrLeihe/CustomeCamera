package com.code.library.utils;

import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * Created by yue on 15/11/6.
 * 封装图片加载框架
 */
public class ImageLoadUtils {

    public static void loadImgUrl(String url, SimpleImageLoadingListener loadingListener) {
        ImageLoader.getInstance().loadImage(url, loadingListener);
    }

    public static void displayUrl(String url, ImageView imageView) {
        ImageLoader.getInstance().displayImage(url, imageView);
    }

    public static void displayUrl(String url, ImageView imageView, DisplayImageOptions options) {
        ImageLoader.getInstance().displayImage(url, imageView, options);
    }

    public static void loadFile(String path, ImageView imageView) {
        ImageLoader.getInstance().displayImage("file://" + path, imageView);
    }

    public static void loadAsset(String path, ImageView imageView) {
        ImageLoader.getInstance().displayImage("assets://" + path, imageView);
    }

    public static void loadDrawable(int id, ImageView imageView) {
        ImageLoader.getInstance().displayImage("drawable://" + id, imageView);
    }

    public static void loadDrawable(int id, ImageView imageView, DisplayImageOptions options) {
        ImageLoader.getInstance().displayImage("drawable://" + id, imageView, options);
    }

}
