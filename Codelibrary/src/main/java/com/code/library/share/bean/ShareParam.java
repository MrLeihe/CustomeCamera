package com.code.library.share.bean;

import com.code.library.share.AppParam;

/**
 * Created by yue on 16/3/16.
 */
public class ShareParam {

    private String title;
    private String text;
    private String url;
    private String imgUrl;
    private String imgPath;
    private String appName;

    public ShareParam() {
        appName = AppParam.SHARE_APP_NAME;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
