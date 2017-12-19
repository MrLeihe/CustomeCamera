package com.code.library.update;

/**
 * Created by yue on 16/2/2.
 */
public class UpateInfo {

    /**
     * VersionCode : 27
     * VersionName : 3.1.1
     * FileSize : 20.9 M
     * Content : 1.Funny 相机全面升级，采用了白白的五颜六色的萌萌哒的设计 2.新增逗萌情景相机功能，进入相机页面就可以看到，今年过年用自拍拜年吧！ 3.社区频道改版，图片如瀑布倾泻而下一览无遗，为小主您筛选好热门精华横屏自动播放。 4.我们还新增了会动的文字表情，真的会动哦！创作自己的专属表情，发送到微信或者QQ去，让别人羡慕吧（傲娇脸）！！ 5.最后的最后，我们还更新了很多可爱的水印！
     * APKURL : http://openbox.mobilem.360.cn/index/d/sid/1964545
     */

    private String VersionCode;
    private String VersionName;
    private String FileSize;
    private String Content;
    private String APKURL;

    public void setVersionCode(String VersionCode) {
        this.VersionCode = VersionCode;
    }

    public void setVersionName(String VersionName) {
        this.VersionName = VersionName;
    }

    public void setFileSize(String FileSize) {
        this.FileSize = FileSize;
    }

    public void setContent(String Content) {
        this.Content = Content;
    }

    public void setAPKURL(String APKURL) {
        this.APKURL = APKURL;
    }

    public String getVersionCode() {
        return VersionCode;
    }

    public String getVersionName() {
        return VersionName;
    }

    public String getFileSize() {
        return FileSize;
    }

    public String getContent() {
        return Content;
    }

    public String getAPKURL() {
        return APKURL;
    }
}
