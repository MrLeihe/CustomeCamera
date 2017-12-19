package com.code.library.thread;

import android.content.Context;

import com.code.library.utils.BitmapUtils;

import java.io.File;

/**
 * Created by yue on 15/11/5.
 * 用于清理临时文件
 */
public class ClearTempThread extends Thread {

    private Context context;
    private String path;

    public ClearTempThread(Context context, String path) {
        this.context = context;
        this.path = path;
    }

    @Override
    public void run() {
        super.run();
        File temp_dir = new File(path);
        File[] files = temp_dir.listFiles();
        if (files==null)
            return;
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.exists()) {
                file.delete();
            }
        }
        BitmapUtils.updateResources(context, path);
    }
}
