package com.code.library.utils;

import android.content.Context;

import com.code.library.http.OkHttpUtils;

import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;

/**
 * Created by yue on 2015/11/28.
 */
public class XmlHttpHelper {

    private Context context;
    private OkHttpClient okHttpClient;

    public interface OnXmlHttpListener {
        void onError();
        void onSuccess(List<Map<String, String>> xml);

    }

    public XmlHttpHelper(Context context) {
        this.context = context;
        this.okHttpClient = OkHttpUtils.getInstance().getOkHttpClient();
    }

    public void get(String url, final OnXmlHttpListener onXmlHttpListener) {
        /*final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case AppConstant.WHAT.ERROR:
                        onXmlHttpListener.onError();
                        break;

                    case AppConstant.WHAT.SUCCESS:
                        onXmlHttpListener.onSuccess((List<Map<String, String>>) msg.obj);
                        break;
                }
            }
        };
        Request request = new Request.Builder().url(url).header("Content-type", "**///*").build();
        /*final Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                handler.sendEmptyMessage(AppConstant.WHAT.ERROR);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                switch (response.code()) {
                    case 200:
                        Message message = new Message();
                        message.what = AppConstant.WHAT.SUCCESS;
                        message.obj = XmlUtil.getXmlUtil().parseXMLWithPull(response.body().string());
                        handler.sendMessage(message);
                        break;
                    default:
                        handler.sendEmptyMessage(AppConstant.WHAT.SERVER_ERROR);
                        break;
                }
            }
        });*/
    }
}
