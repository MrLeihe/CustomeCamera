package com.code.library.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import java.io.File;

/**
 * Created by yue on 15/11/4.
 * 用于选择照片的工具
 */
public class SelectPhotoUtils {
    /**
     * 拍照选择图片
     *
     * @param activity
     * @param temp_path   临时文件路径
     * @param requestCode 请求码
     * @return 临时文件路径 失败返回Null
     */

    public static String photograph(Activity activity, String temp_path, int requestCode) {
        try {
            Intent intent = new Intent();
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri uri = Uri.fromFile(new File(temp_path));
            intent.putExtra("return-data", true);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            activity.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return temp_path;
    }
/*
    public static String photograph(Activity activity) {
        String temp_path = MyApplication.getInstance().getTempFileName();
        LogUtils.e("照片的临时保存路径:" + temp_path);
        return photograph(activity, temp_path, AppConstant.REQUEST_CODE.CAMERA);
    }*/

    /**
     * 通过相册选择图片
     *
     * @param activity
     */
    public static void album(Activity activity) {
        /*Intent intent = new Intent(activity, SelectPhotoActivity.class);
        activity.startActivityForResult(intent, AppConstant.REQUEST_CODE.ALBUM);
        activity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);*/
    }

    public static void albumNew(Activity activity) {
       /* Intent intent = new Intent(activity, SelectPhotoNewActivity.class);
        activity.startActivityForResult(intent, AppConstant.REQUEST_CODE.ALBUM);
        activity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);*/
    }

    public static void bgAlbum(Fragment fragment, Activity activity) {
        /*Intent intent = new Intent(activity, SelectPhotoActivity.class);
        fragment.startActivityForResult(intent, AppConstant.REQUEST_CODE.BACKGROUND);
        activity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);*/
    }

}
