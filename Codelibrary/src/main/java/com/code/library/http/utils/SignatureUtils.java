package com.code.library.http.utils;

import com.code.library.utils.MD5Utils;

/**
 * Created by Wxcily on 16/1/11.
 */
public class SignatureUtils {

    public static final int GET = 1;
    public static final int POST = 2;
    public static final int DELETE = 3;

    /**
     * 用于给URL签名
     *
     * @param method
     * @param url
     * @return
     */
    public static String signature(int method, String url, String key) {
        return url + getSignature(method, url, key);
    }


    /**
     * 返回签名
     *
     * @param method
     * @param url
     * @return
     */
    public static String getSignature(int method, String url, String key) {
        String methodString = "";
        switch (method) {
            case 1:
                methodString = "GET";
                break;
            case 2:
                methodString = "POST";
                break;
            case 3:
                methodString = "DELETE";
                break;
        }
        String base_url;
        if (url.indexOf("?") >= 0) {
            base_url = url.substring(0, url.indexOf("?"));
        } else {
            base_url = url;
        }
        long time = System.currentTimeMillis() / 1000L;
        String signature = MD5Utils.Md5(methodString + base_url + time + key);
        String result;
        if (url.contains("?")) {
            result = "&sign=" + signature + "&_t=" + time;
        } else {
            result = "?sign=" + signature + "&_t=" + time;
        }
        return result;
    }
}
