package com.code.library.update;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;

import com.code.library.R;
import com.code.library.dialog.PromptDialog;
import com.code.library.http.OkHttpUtils;
import com.code.library.http.callback.JsonCallBack;
import com.code.library.toast.ToastFactory;
import com.code.library.utils.LogUtils;
import com.code.library.utils.NetworkUtil;
import com.code.library.utils.SharedConfig;
import com.code.library.utils.SystemUtils;
import com.code.library.utils.VersionUtils;

import java.io.File;

import okhttp3.Call;

/**
 * Created by yue on 16/2/2.
 */
public class AppUpdate {

    private static AppUpdate instance;
    private Context context;
    private String server_url;
    private boolean must_wifi;
    private boolean isUpdateing = false;

    public interface Key {
        String UPDATE_IGNORE_VERSION = "UPDATE_IGNORE_VERSION";
        String UPDATE_HAVE_DOWNLOAD_VERSION = "UPDATE_HAVE_DOWNLOAD_VERSION";
        String UPDATE_HAVE_DOWNLOAD_PATH = "UPDATE_HAVE_DOWNLOAD_PATH";
    }

    public static AppUpdate getInstance() {
        if (instance == null) {
            synchronized (AppUpdate.class) {
                if (instance == null) {
                    instance = new AppUpdate();
                }
            }
        }
        return instance;
    }

    public void init(Context context, String server_url, boolean must_wifi) {
        this.context = context;
        this.server_url = server_url;
        this.must_wifi = must_wifi;
    }

    public void checkUpdate(Activity activity) {
        checkUpdate(activity, false);
    }

    public void checkUpdate(Activity activity, boolean active) {
        if (context == null || TextUtils.isEmpty(server_url)) {
            return;
        }
        synchronized (AppUpdate.class) {
            if (isUpdateing) {
                ToastFactory.showLongToast(context, context.getResources().getString(R.string.update_loading));
                return;
            } else {
                isUpdateing = true;
            }
        }
        if (NetworkUtil.isAvailable(context)) {
            if (active) {
                startUpdate(activity, active);
            } else {
                if (must_wifi) {
                    if (NetworkUtil.isWIFIActivate(context)) {
                        startUpdate(activity, active);
                    } else {
                        isUpdateing = false;
                        LogUtils.i("AppUpdate-非WIFI网络环境,应用忽略更新!");
                    }
                } else {
                    startUpdate(activity, active);
                }
            }
        } else {
            // 如果为主动更新当网络不可用时会提示连接网络
            if (active) {
                NetworkUtil.showPromptDialog(context);
            }
            isUpdateing = false;
        }
    }

    private void startUpdate(final Activity activity, final boolean active) {
        OkHttpUtils.getInstance()
                .get()
                .url(server_url)
                .build()
                .execute(new JsonCallBack<UpateInfo>() {
                    @Override
                    public void onError(Call call, Exception e) {
                        isUpdateing = false;
                        LogUtils.e("AppUpdate-请求失败请检查网络环境");
                    }

                    @Override
                    public void onResponse(UpateInfo response) {
                        int nowCode = VersionUtils.getVersionCode(context);
                        int serCode = Integer.parseInt(response.getVersionCode());
                        if (serCode > nowCode) {
                            if (!active) {
                                int IgnoreCode = Integer.parseInt(SharedConfig.getInstance(context).readString(
                                        AppUpdate.Key.UPDATE_IGNORE_VERSION, "0"));
                                if (IgnoreCode == serCode) {
                                    isUpdateing = false;
                                    LogUtils.i("AppUpdate-已忽略当前版本!");
                                    return;
                                }
                            }
                            showUpdateDialog(activity, response);
                        } else {
                            isUpdateing = false;
                            LogUtils.i("AppUpdate-已经是最新版本!");
                            if (active) {
                                ToastFactory.getToast(context, context.getResources().getString(R.string.app_updated)).show();
                            }
                        }
                    }
                });
    }

    // 显示更新对话框
    private void showUpdateDialog(Activity activity, final UpateInfo response) {
        String message = String.format(
                context.getResources().getString(
                        R.string.framework_faxianxinbanben),
                response.getVersionName(), response.getFileSize(), response.getContent());
        new PromptDialog.Builder(activity)
                .setTitle(R.string.framework_yingyonggengxin)
                .setMessage(message)
                .setViewStyle(PromptDialog.VIEW_STYLE_NORMAL)
                .setTitleBarColor(Color.parseColor("#4cc05f"))
                .setButton1(R.string.framework_xuxiao,
                        new PromptDialog.OnClickListener() {

                            @Override
                            public void onClick(Dialog dialog, int which) {
                                isUpdateing = false;
                                dialog.dismiss();
                            }
                        })
                .setButton2(R.string.framework_hulue,
                        new PromptDialog.OnClickListener() {

                            @Override
                            public void onClick(Dialog dialog, int which) {
                                isUpdateing = false;
                                SharedConfig.getInstance(context).writeData(
                                        AppUpdate.Key.UPDATE_IGNORE_VERSION,
                                        response.getVersionCode());
                                dialog.dismiss();
                            }
                        })
                .setButton3(R.string.framework_gengxin,
                        new PromptDialog.OnClickListener() {

                            @Override
                            public void onClick(Dialog dialog, int which) {
                                update(response);
                                dialog.dismiss();
                            }
                        }).show();

    }

    // 执行更新操作
    private void update(UpateInfo response) {
        int nowCode = Integer.parseInt(response.getVersionCode());
        int downCode = Integer.parseInt(SharedConfig.getInstance(context).readString(
                AppUpdate.Key.UPDATE_HAVE_DOWNLOAD_VERSION, "0"));
        // 如果已经下载该版本APK则直接更新
        if (nowCode == downCode) {
            String download_path = SharedConfig.getInstance(context)
                    .readString(AppUpdate.Key.UPDATE_HAVE_DOWNLOAD_PATH, "");
            File file = new File(download_path);
            if (file.exists()) {
                isUpdateing = false;
                LogUtils.i("AppUpdate-当前版本已下载直接安装!");
                SystemUtils.installAPK(context, download_path);
            } else {
                download(response);
            }
        } else {
            download(response);
        }
    }

    private void download(final UpateInfo response) {
        /*ToastFactory.showLongToast(context, context.getString(R.string.framework_zhengzaixiazaigengxin));
        String path = MyApplication.getInstance().getApkPath() + File.separator + System.currentTimeMillis() + ".apk";
        DownloadHelper.getInstance()
                .download(response.getAPKURL(), path, new DownloadHelper.OnDownloadListener() {
                    @Override
                    public void onSuccess(String path) {
                        isUpdateing = false;
                        // 更新配置文件
                        SharedConfig.getInstance(context).writeData(
                                AppUpdate.Key.UPDATE_HAVE_DOWNLOAD_VERSION, response.getVersionCode());
                        SharedConfig.getInstance(context).writeData(
                                AppUpdate.Key.UPDATE_HAVE_DOWNLOAD_PATH, path);
                        // 发出震动提示
                        long[] pattern = {0, 100, 200, 300};
                        Vibrator vibrator = (Vibrator) context
                                .getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(pattern, -1);
                        // 安装应用
                        SystemUtils.installAPK(context, path);
                    }

                    @Override
                    public void onError(String message) {
                        isUpdateing = false;
                        ToastFactory.showLongToast(context, context.getString(R.string.framework_xiazaishibai));
                        LogUtils.e("AppUpdate-下载失败!");
                    }

                    @Override
                    public void onProgress(float progress) {

                    }
                });*/
    }
}
