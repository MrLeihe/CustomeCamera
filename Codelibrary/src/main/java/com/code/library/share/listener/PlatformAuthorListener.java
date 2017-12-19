package com.code.library.share.listener;

import com.code.library.share.APPShare;

import org.json.JSONObject;

/**
 * Created by yue on 16/3/14.
 */
public interface PlatformAuthorListener {

    void onComplete(APPShare.Platform platform, JSONObject jsonObject, String avatar);

    void onError();

    void onCancel();

}
