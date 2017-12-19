package com.code.library.share.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.code.library.share.AppParam;
import com.code.library.utils.LogUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownLoadHelper {
    private OnDownLoadListener onDownLoadListener = null;
    private String path;
    private int nowsize = 0;// 当前下载大小
    private int filesize = 0;// 文件总大小

    public interface OnDownLoadListener {
        void onDownLoadResult(String res);
    }

    public DownLoadHelper(Context context, OnDownLoadListener onDownLoadListener) {
        this.onDownLoadListener = onDownLoadListener;
        // 处理下载路径
        String basePath = DirectoryUtils.getDefaultStoragePath(context);
        path = basePath + File.separator + AppParam.DOWNLOAD_FOLDER;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    public void download(String url) {
        new DownLoadTask().execute(url);
    }

    private void returnResult(String res) {
        if (onDownLoadListener != null) {
            onDownLoadListener.onDownLoadResult(res);
        }
    }

    private class DownLoadTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... params) {
            LogUtils.e("开始下载:");
            String imgPath = path + File.separator + System.currentTimeMillis();
            HttpURLConnection conn = null;
            FileOutputStream fileOutputStream = null;
            BufferedInputStream bufferedInputStream = null;
            byte[] Buffer = new byte[2048];
            int size;
            try {
                URL url = new URL(params[0]); // URL对象
                conn = (HttpURLConnection) url.openConnection();
//                conn.setDoInput(true); // 允许输入流，即允许下载
//                conn.setUseCaches(false); // 不使用缓冲
                conn.setRequestMethod("GET"); // 使用get请求
                conn.setConnectTimeout(10000);// 设置10秒超时
                conn.setReadTimeout(10000);// 设置10秒超时
                bufferedInputStream = new BufferedInputStream(
                        conn.getInputStream());// 获取buffer输入流
                filesize = conn.getContentLength();// 得到下载文件大小
                File downloadFile = new File(imgPath);
                fileOutputStream = new FileOutputStream(downloadFile);// 得到一个文件输出流
                // 保存文件
                while ((size = bufferedInputStream.read(Buffer)) != -1) {
                    fileOutputStream.write(Buffer, 0, size);
                    nowsize = nowsize + size;
                    // 更新UI
                    publishProgress();
                }
            } catch (Exception e) {
                LogUtils.e("下载任务失败:" + e.toString());
                e.printStackTrace();
                imgPath = null;
            } finally {
                LogUtils.e("下载任务结束");
                //关闭连接的操作
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (bufferedInputStream != null) {
                    try {
                        bufferedInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                conn.disconnect();
            }
            return imgPath;
        }

        protected void onPostExecute(String result) {
            LogUtils.i("result===" + result);
            returnResult(result);
        }
    }
}
