package com.code.library.http;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.code.library.utils.LogUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Wxcily on 16/1/12.
 */
public class DownloadHelper {

    private static DownloadHelper instance;
    private OkHttpClient okHttpClient;


    private DownloadHelper() {
        okHttpClient = OkHttpUtils.getInstance().getOkHttpClient();
    }

    public static DownloadHelper getInstance() {
        if (instance == null) {
            synchronized (DownloadHelper.class) {
                if (instance == null) {
                    instance = new DownloadHelper();
                }
            }
        }
        return instance;
    }

    public interface OnDownloadListener {
        void onSuccess(String path);

        void onError();

        void onProgress(float progress);
    }

    public interface OnIdolDownloadListener {
        void onBefore();

        void onSuccess(String path);

        void onError();

        void onProgress(int progress);
    }

    /**
     * 下载文件
     *
     * @param url
     * @param savePath
     * @param onDownloadListener
     */
    public void download(final String url, final String savePath, final OnDownloadListener onDownloadListener) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        final Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onDownloadListener.onError();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream inputStream = null;
                FileOutputStream fileOutputStream = null;
                byte[] buffer = new byte[1024];
                int length;
                long nowLength = 0;//现在已经下载的长度
                long totalLength;//文件的总长度
                try {
                    totalLength = response.body().contentLength();
                    inputStream = response.body().byteStream();
                    File file = new File(savePath);
                    fileOutputStream = new FileOutputStream(file);
                    while ((length = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, length);
                        nowLength += length;
                        final long finalTotalLength = totalLength;
                        final long finalNowLength = nowLength;
                        onDownloadListener.onProgress(finalNowLength * 1.0f / finalTotalLength);
                    }
                    fileOutputStream.flush();
                    onDownloadListener.onSuccess(savePath);
                } catch (Exception e) {
                    onDownloadListener.onError();
                } finally {
                    try {
                        if (inputStream != null) inputStream.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fileOutputStream != null) fileOutputStream.close();
                    } catch (IOException e) {
                    }
                }
            }

        });
    }

    public void downLoadUrlList(List<String> urlList, String filepath, String prefix, final OnIdolDownloadListener onIdolDownloadListener) {
        new DownLoadTask(urlList, filepath, prefix, onIdolDownloadListener).execute();
    }


    class DownLoadTask extends AsyncTask<Void, Integer, Void> {
        public List<String> urlList;
        public ProgressDialog progressBar;
        private int totalsize;
        private OnIdolDownloadListener onIdolDownloadListener;
        private String filepath;
        private String prefix;

        public DownLoadTask(List<String> urlList, String filepath, String prefix, OnIdolDownloadListener onIdolDownloadListener) {
            this.urlList = urlList;
            this.filepath = filepath;
            this.prefix = prefix;
            this.onIdolDownloadListener = onIdolDownloadListener;
            totalsize = urlList.size();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            onIdolDownloadListener.onBefore();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                for (int i = 0; i < urlList.size(); i++) {
                    int size = downLoadUrl(urlList.get(i),
                            filepath, prefix + i + ".png");
                    int value = (i  * 100)/ totalsize;
                    publishProgress(value);
                }
            } catch (Exception e) {
                onIdolDownloadListener.onError();
            }


            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            onIdolDownloadListener.onProgress(values[0]);
            LogUtils.i("value==" + values[0]);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            onIdolDownloadListener.onSuccess(filepath);
            super.onPostExecute(aVoid);
        }
    }

    /**
     * 下载贴纸
     *
     * @param url
     * @return 返回进度
     */
    public static int downLoadUrl(String url, String filepath, String fileName) {
        File tempFile = new File(filepath);
        File[] files = null;

        if (!tempFile.exists()) {
            tempFile.mkdir();
        }

        URL myFileUrl = null;
        BufferedInputStream bufferedInputStream = null;
        FileOutputStream fileOutputStream = null;
        HttpURLConnection conn = null;

        byte[] Buffer = new byte[32768];// 32kb的缓存
        int size = 0;// 本次下载大小
        int nowsize = 0;// 当前下载大小
        int totalsize = 0;// 文件总大小
        int now = 0;
        try {
            myFileUrl = new URL(url);

            conn = (HttpURLConnection) myFileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bufferedInputStream = new BufferedInputStream(
                    conn.getInputStream());// 获取buffer输入流
            totalsize = conn.getContentLength();
            File downloadFile = new File(filepath, fileName);

            fileOutputStream = new FileOutputStream(downloadFile);// 得到一个文件输出流
            // 保存文件
            while ((size = bufferedInputStream.read(Buffer)) != -1) {
                fileOutputStream.write(Buffer, 0, size);
            }
            files = tempFile.listFiles();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭连接的操作
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
        return files.length;
    }
}
