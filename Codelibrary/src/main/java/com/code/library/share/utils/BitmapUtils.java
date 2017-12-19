package com.code.library.share.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.io.ByteArrayOutputStream;

/**
 * Created by yue on 16/3/17.
 */
public class BitmapUtils {

    public static final int Config_480P = 1;// 800*480
    public static final int Config_720P = 2;// 1280*720
    public static final int Config_1080P = 3;// 1920*1080
    public static final int Config_2K = 4;// 2560*1440

    private static int getSize(int config) {
        int size = 0;
        switch (config) {
            case Config_480P:
                size = 480;
                break;
            case Config_720P:
                size = 720;
                break;
            case Config_1080P:
                size = 1080;
                break;
            case Config_2K:
                size = 1440;
                break;
        }
        return size;
    }

    /**
     * 返回适应屏幕尺寸的位图
     *
     * @param bit
     * @param config
     */
    public static Bitmap getRightSzieBitmap(Bitmap bit, int config) {
        // 得到理想宽度
        int ww = getSize(config);
        // 获取图片宽度
        int w = bit.getWidth();
        // 计算缩放率
        float rate = 1f;
        if (w > ww) {
            rate = (float) ww / (float) w;
        }
        // 重新绘图
        Bitmap bitmap = Bitmap.createBitmap((int) (bit.getWidth() * rate),
                (int) (bit.getHeight() * rate), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Rect rect = new Rect(0, 0, (int) (bit.getWidth() * rate),
                (int) (bit.getHeight() * rate));
        canvas.drawBitmap(bit, null, rect, null);
        return bitmap;
    }

    /**
     * 返回适应屏幕的位图 更节省内存
     *
     * @param fileName
     * @param config
     * @return
     */
    public static Bitmap getRightSzieBitmap(String fileName, int config) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileName, options);
        options.inJustDecodeBounds = false;
        int w = options.outWidth;
        int ww = getSize(config);
        if ((ww * 2) < w) {
            options.inSampleSize = 2;
        }
        // 重新绘图
        Bitmap bitmap = BitmapFactory.decodeFile(fileName, options);
        return getRightSzieBitmap(bitmap, config);
    }

    /**
     * 将原图转为缩略图并转为Byte数组
     * @param image 原始图片
     * @param fileSzie 限制的大小 KB
     * @return
     */
    public static byte[] bmpToByteArray(Bitmap image, int fileSzie) {
        int width=150;
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        Bitmap bitmap=getBitmapForWidth(image, width);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, data);
        while ((data.toByteArray().length/1024)>fileSzie){
            width-=10;
            data.reset();
            bitmap=getBitmapForWidth(image,width);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, data);
        }
        bitmap.recycle();
        byte[] result = data.toByteArray();
        try {
            data.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 返回适应屏幕尺寸的位图
     *
     * @param bit
     * @param width
     */
    public static Bitmap getBitmapForWidth(Bitmap bit, int width) {
        // 得到理想宽度
        int ww = width;
        // 获取图片宽度
        int w = bit.getWidth();
        // 计算缩放率
        float rate = 1f;
        if (w > ww) {
            rate = (float) ww / (float) w;
        }
        // 重新绘图
        Bitmap bitmap = Bitmap.createScaledBitmap(bit, (int) (bit.getWidth() * rate), (int) (bit.getHeight() * rate), true);
        return bitmap;
    }
}
