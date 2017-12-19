package com.code.library.share.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.code.library.share.bean.Oauth2AccessToken;

/**
 * Created by yue on 16/3/16.
 */
public class AccessTokenKeeper {

    private static final String PREFERENCES_NAME = "funny_share_weibo_token";

    public static void save(Context context, Oauth2AccessToken token) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        Editor editor = pref.edit();
        editor.putString("token", token.getToken());
        editor.putString("phoneNum", token.getPhoneNum());
        editor.putString("refreshToken", token.getRefreshToken());
        editor.putString("uid", token.getUid());
        editor.putLong("expiresTime", token.getExpiresTime());
        editor.commit();
    }

    public static void clear(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

    public static Oauth2AccessToken read(Context context) {
        Oauth2AccessToken token = new Oauth2AccessToken();
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        token.setToken(pref.getString("token", ""));
        token.setUid(pref.getString("uid", ""));
        token.setExpiresTime(pref.getLong("expiresTime", 0));
        token.setRefreshToken(pref.getString("refreshToken", ""));
        token.setPhoneNum(pref.getString("phoneNum", ""));
        return token;
    }

}
