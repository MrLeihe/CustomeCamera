package com.code.library.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.code.library.R;
import com.code.library.share.bean.Oauth2AccessToken;
import com.code.library.share.bean.ShareParam;
import com.code.library.share.listener.PlatformAuthorListener;
import com.code.library.share.listener.ShareListener;
import com.code.library.share.utils.AccessTokenKeeper;
import com.code.library.share.utils.BitmapUtils;
import com.code.library.share.utils.DownLoadHelper;
import com.code.library.share.utils.HttpHelper;
import com.code.library.utils.LogUtils;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXEmojiObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Wxcily on 16/3/14.
 * Funny分享SDK
 */
public class APPShare {

    private static APPShare instance;
    private Context context;
    private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
    private static final String TRANSACTION_LOGIN = "TRANSACTION_LOGIN";
    private static final String TRANSACTION_SHARE = "TRANSACTION_SHARE";

    //各平台实例
    private Tencent tencent;
    private IWXAPI iwxapi;
    private AuthInfo authInfo;
    private SsoHandler ssoHandler;
    private IWeiboShareAPI weiboShareAPI;

    //私有化构造方法
    private APPShare() {
    }

    //平台
    public enum Platform {
        QQ, QZONE, WECHAT, WECHAT_FRIENDS, WEIBO, FACEBOOK
    }

    //单例实例
    public static APPShare getInstance() {
        if (instance == null) {
            synchronized (APPShare.class) {
                if (instance == null) {
                    instance = new APPShare();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化方法,必须先调用此接口再执行其他操作!!
     *
     * @param context 全局的ApplicationContext
     */
    public void init(Context context) {
        this.context = context;
        tencent = Tencent.createInstance(AppParam.QQ_APPID, context);
        iwxapi = WXAPIFactory.createWXAPI(context, AppParam.WECHAT_APPID, AppParam.WECHAT_SIGNATURE);
        iwxapi.registerApp(AppParam.WECHAT_APPID);
        authInfo = new AuthInfo(context, AppParam.WEIBO_APPKEY,
                AppParam.WEIBO_REDIRECT_URL, AppParam.WEIBO_SCOPE);
        weiboShareAPI = WeiboShareSDK.createWeiboAPI(context, AppParam.WEIBO_APPKEY);
        weiboShareAPI.registerApp();
    }

    /**
     * 第三方登陆的方法
     *
     * @param platform 第三方登陆的平台
     */
    public void login(Activity activity, Platform platform, PlatformAuthorListener platformListener) {
        switch (platform) {
            case QQ:
                QQLogin(activity, platformListener);
                break;
            case WECHAT:
                WechatLogin(activity, platformListener);
                break;
            case WEIBO:
                WeiBoLogin(activity, platformListener);
                break;
            default:
                LogUtils.e("您传入的平台不支持第三方登陆");
                platformListener.onError();
                break;
        }
    }

    /********************** QQ登陆部分 **********************/

    /**
     * QQ登陆逻辑
     *
     * @param platformListener
     */
    private void QQLogin(Activity activity, final PlatformAuthorListener platformListener) {
        if (tencent.isSessionValid()) {
            tencent.logout(context);
        }
        tencent.login(activity, "get_simple_userinfo,get_user_info", new IUiListener() {
            @Override
            public void onComplete(Object response) {
                if (response == null) {
                    platformListener.onError();
                    return;
                }
                JSONObject jsonResponse = (JSONObject) response;
                if (null == jsonResponse || jsonResponse.length() == 0) {
                    platformListener.onError();
                    return;
                }
                if (!initOpenidAndToken(jsonResponse)) {
                    platformListener.onError();
                    return;
                }
                getQQUserInfo(platformListener);
            }

            @Override
            public void onError(UiError uiError) {
                platformListener.onError();
            }

            @Override
            public void onCancel() {
                platformListener.onCancel();
            }
        });
    }

    /**
     * 用于数据回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     * @param platformListener
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data, final PlatformAuthorListener platformListener) {
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        if (requestCode == Constants.REQUEST_LOGIN) {
            Tencent.onActivityResultData(requestCode, resultCode, data, new IUiListener() {
                @Override
                public void onComplete(Object response) {
                    if (response == null) {
                        platformListener.onError();
                        return;
                    }
                    JSONObject jsonResponse = (JSONObject) response;
                    if (null == jsonResponse || jsonResponse.length() == 0) {
                        platformListener.onError();
                        return;
                    }
                    if (!initOpenidAndToken(jsonResponse)) {
                        platformListener.onError();
                        return;
                    }
                    getQQUserInfo(platformListener);
                }

                @Override
                public void onError(UiError uiError) {
                    platformListener.onError();
                }

                @Override
                public void onCancel() {
                    platformListener.onCancel();
                }
            });
        }
    }

    /**
     * 用于数据回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     * @param shareListener
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data, final ShareListener shareListener) {
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        if (requestCode == Constants.REQUEST_QQ_SHARE || requestCode == Constants.REQUEST_QZONE_SHARE) {
            Tencent.onActivityResultData(requestCode, resultCode, data, new IUiListener() {
                @Override
                public void onComplete(Object response) {
                    shareListener.onComplete();
                }

                @Override
                public void onError(UiError uiError) {
                    shareListener.onError();
                }

                @Override
                public void onCancel() {
                    shareListener.onCancel();
                }
            });
        }
    }

    /**
     * QQ登陆成功后设置Token和OpenId
     *
     * @param jsonObject
     */
    private boolean initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                tencent.setAccessToken(token, expires);
                tencent.setOpenId(openId);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 登陆成功后获取用户信息
     *
     * @param platformListener
     */
    private void getQQUserInfo(final PlatformAuthorListener platformListener) {
        IUiListener getInfoListener = new IUiListener() {

            @Override
            public void onComplete(final Object response) {
                if (response == null) {
                    platformListener.onError();
                    return;
                }
                JSONObject jsonResponse = (JSONObject) response;
                if (null == jsonResponse || jsonResponse.length() == 0) {
                    platformListener.onError();
                    return;
                }
                String avatar;
                try {
                    jsonResponse.put("uid", tencent.getOpenId());
                    avatar = jsonResponse.getString("figureurl_qq_2");
                } catch (JSONException e) {
                    e.printStackTrace();
                    platformListener.onError();
                    return;
                }
                platformListener.onComplete(Platform.QQ, jsonResponse, avatar);
            }

            @Override
            public void onError(UiError e) {
                platformListener.onError();
            }

            @Override
            public void onCancel() {
                platformListener.onError();
            }
        };
        UserInfo userInfo = new UserInfo(context, tencent.getQQToken());
        userInfo.getUserInfo(getInfoListener);
    }

    /************************  微信登陆部分  **********************/

    /**
     * 返回Wxapi
     *
     * @return
     */
    public IWXAPI getWxapi() {
        return iwxapi;
    }

    /**
     * 使用微信登陆
     *
     * @param platformListener
     */
    private void WechatLogin(Activity activity, PlatformAuthorListener platformListener) {
        if (!iwxapi.isWXAppInstalled()) {
            Toast.makeText(activity, "您还没有安装微信客户端", Toast.LENGTH_SHORT).show();
            return;
        }
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "funny_share";
        req.transaction = TRANSACTION_LOGIN;
        wxPlatformListener = platformListener;
        iwxapi.sendReq(req);
    }

    /**
     * 微信登陆回调接口,回调后置空
     */
    private PlatformAuthorListener wxPlatformListener = null;

    /**
     * 用于接收微信回调的数据
     *
     * @param baseResp
     */
    public void onResp(BaseResp baseResp) {
        String transaction = baseResp.transaction;
        if (transaction.equals(TRANSACTION_LOGIN)) {
            if (wxPlatformListener == null) {
                return;
            }
            switch (baseResp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    String code = ((SendAuth.Resp) baseResp).code;
                    getAccessToken(code);
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    wxPlatformListener.onError();
                    wxPlatformListener = null;
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    wxPlatformListener.onCancel();
                    wxPlatformListener = null;
                    break;
            }
        } else {
            if (wxShareListener == null) {
                return;
            }
            switch (baseResp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    wxShareListener.onComplete();
                    wxShareListener = null;
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    wxShareListener.onError();
                    wxShareListener = null;
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    wxShareListener.onCancel();
                    wxShareListener = null;
                    break;
            }
        }
    }

    /**
     * 同过登陆的code 获取token
     *
     * @param code
     */
    private void getAccessToken(String code) {
        String url = String.format(AppParam.WECHAT_LOGIN_URL, AppParam.WECHAT_APPID, AppParam.WECHAT_APPSECRET, code);
        new HttpHelper(new HttpHelper.OnHttpHelperListener() {
            @Override
            public void onData(String data) {
                if (data == null || data.length() == 0) {
                    wxPlatformListener.onError();
                    wxPlatformListener = null;
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    String openid = jsonObject.getString("openid");
                    String token = jsonObject.getString("access_token");
                    getWeChatUserInfo(openid, token);
                } catch (JSONException e) {
                    e.printStackTrace();
                    wxPlatformListener.onError();
                    wxPlatformListener = null;
                }
            }
        }).GetResult(url);
    }

    /**
     * 通过token获取用户数据
     *
     * @param openid
     * @param token
     */
    private void getWeChatUserInfo(String openid, String token) {
        String url = String.format(AppParam.WECHAT_USERINFO_URL, token, openid);
        new HttpHelper(new HttpHelper.OnHttpHelperListener() {
            @Override
            public void onData(String data) {
                if (data == null || data.length() == 0) {
                    wxPlatformListener.onError();
                    wxPlatformListener = null;
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    String avatar = jsonObject.getString("headimgurl");
                    wxPlatformListener.onComplete(Platform.WECHAT, jsonObject, avatar);
                } catch (JSONException e) {
                    e.printStackTrace();
                    wxPlatformListener.onError();
                }
                wxPlatformListener = null;
            }
        }).GetResult(url);
    }

    /*************************
     * 微博登陆部分
     **********************/

    private void WeiBoLogin(final Activity activity, final PlatformAuthorListener platformListener) {
        ssoHandler = new SsoHandler(activity, authInfo);
        ssoHandler.authorize(new WeiboAuthListener() {
            @Override
            public void onComplete(Bundle bundle) {
                Oauth2AccessToken token = Oauth2AccessToken.parseAccessToken(bundle); // 从 Bundle 中解析 Token
                if (token.isSessionValid()) {
                    AccessTokenKeeper.save(context, token);
                    getWeiBoUserInfo(token, platformListener);
                } else {
                    String code = bundle.getString("code", "");
                    LogUtils.e("ErrorCode:" + code);
                    platformListener.onError();
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                platformListener.onError();
            }

            @Override
            public void onCancel() {
                platformListener.onCancel();
            }
        });
    }

    private void getWeiBoUserInfo(Oauth2AccessToken token, final PlatformAuthorListener platformListener) {
        String url = String.format(AppParam.WEIBO_USERINFO_URL, token.getToken(), token.getUid());
        new HttpHelper(new HttpHelper.OnHttpHelperListener() {
            @Override
            public void onData(String data) {
                if (data == null || data.length() == 0) {
                    platformListener.onError();
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    String avatar = jsonObject.getString("avatar_large");
                    platformListener.onComplete(Platform.WEIBO, jsonObject, avatar);
                } catch (JSONException e) {
                    e.printStackTrace();
                    platformListener.onError();
                }
            }
        }).GetResult(url);
    }

    /******************************************************
     * ****************** 以下为分享部分 *********************
     ******************************************************/

    public void share(Activity activity, Platform platform, ShareParam shareParam, ShareListener shareListener) {
        switch (platform) {
            case QQ:
                shareToQQ(activity, shareParam, shareListener, false);
                break;
            case QZONE:
                shareToQQ(activity, shareParam, shareListener, true);
                break;
            case WECHAT:
                shareToWechat(shareParam, shareListener, false);
                break;
            case WECHAT_FRIENDS:
                shareToWechat(shareParam, shareListener, true);
                break;
            case WEIBO:
                shareToWeibo(activity, shareParam, shareListener);
                break;
        }
    }


    private void shareToQQ(Activity activity, ShareParam shareParam, final ShareListener shareListener, boolean qzone) {
        if (shareParam.getImgUrl() != null && shareParam.getImgUrl() != "") {
            shareWebImageForQQ(activity, shareParam, shareListener, qzone);
        } else {
            Bundle params = new Bundle();
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
            params.putString(QQShare.SHARE_TO_QQ_APP_NAME, shareParam.getAppName());
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, shareParam.getImgPath());
            if (qzone) {
                params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
                params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareParam.getUrl());
            } else {
                params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
            }
            tencent.shareToQQ(activity, params, new IUiListener() {
                @Override
                public void onComplete(Object o) {
                    shareListener.onComplete();
                }

                @Override
                public void onError(UiError uiError) {
                    shareListener.onError();
                }

                @Override
                public void onCancel() {
                    shareListener.onCancel();
                }
            });
        }
    }

    private void shareWebImageForQQ(final Activity activity, final ShareParam shareParam, final ShareListener shareListener, final boolean qzone) {
        new DownLoadHelper(context, new DownLoadHelper.OnDownLoadListener() {
            @Override
            public void onDownLoadResult(String res) {
                if (res == null || res.length() == 0) {
                    shareListener.onError();
                    return;
                }
                Bundle params = new Bundle();
                params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);

                params.putString(QQShare.SHARE_TO_QQ_TITLE, AppParam.SHARE_APP_TITLE);
                params.putString(QQShare.SHARE_TO_QQ_SUMMARY, AppParam.SHARE_APP_SUMMARY);
                params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, AppParam.SHARE_APP_URL);
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, AppParam.SHARE_APP_IMAGE);
                params.putString(QQShare.SHARE_TO_QQ_APP_NAME, AppParam.SHARE_APP_NAME);
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, res);

                /*
                params.putString(QQShare.SHARE_TO_QQ_APP_NAME, shareParam.getAppName());
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, res);*/
                if (qzone) {
                    params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
                    params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareParam.getUrl());
                } else {
                    params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
                }
                tencent.shareToQQ(activity, params, new IUiListener() {
                    @Override
                    public void onComplete(Object o) {
                        shareListener.onComplete();
                    }

                    @Override
                    public void onError(UiError uiError) {
                        shareListener.onError();
                    }

                    @Override
                    public void onCancel() {
                        shareListener.onCancel();
                    }
                });
            }
        }).download(shareParam.getImgUrl());
    }

    private ShareListener wxShareListener;

    private void shareToWechat(final ShareParam shareParam, final ShareListener shareListener, final boolean timeLine) {
        String imgUrl = shareParam.getImgUrl();
        wxShareListener = shareListener;
        if (imgUrl != null && imgUrl != "") {
            new DownLoadHelper(context, new DownLoadHelper.OnDownLoadListener() {
                @Override
                public void onDownLoadResult(String res) {
                    if (res == null || res.length() == 0) {
                        wxShareListener.onError();
                        wxShareListener = null;
                        return;
                    }
                    shareLocalImage(res, timeLine);
                }
            }).download(imgUrl);
        } else {
            shareLocalImage(shareParam.getImgPath(), timeLine);
        }
    }

    private void shareLocalImage(String imgPath, boolean timeLine) {
        Bitmap bitmap;
        try {
            bitmap = BitmapUtils.getRightSzieBitmap(imgPath, BitmapUtils.Config_720P);
        } catch (Exception ex) {
            ex.printStackTrace();
            wxShareListener.onError();
            wxShareListener = null;
            return;
        }

        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = AppParam.SHARE_APP_URL;

        //设置分享图片
//        WXImageObject wxImageObject = new WXImageObject();
        WXMediaMessage wxMediaMessage = new WXMediaMessage(webpage);
//        wxMediaMessage.mediaObject = wxImageObject;
        //Cutie和Funny不同 Funny只分享图片有二维码
        wxMediaMessage.title = AppParam.SHARE_APP_TITLE;
        wxMediaMessage.description = AppParam.SHARE_APP_SUMMARY;

        //设置缩略图 微信限制缩略图为32KB
        wxMediaMessage.thumbData = BitmapUtils.bmpToByteArray(bitmap, 30);

//        wxMediaMessage.setThumbImage();

        bitmap.recycle();
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = TRANSACTION_SHARE;
        req.message = wxMediaMessage;

        if (timeLine) {
            int wxSdkVersion = iwxapi.getWXAppSupportAPI();
            if (wxSdkVersion >= TIMELINE_SUPPORTED_VERSION) {
                req.scene = SendMessageToWX.Req.WXSceneTimeline;
            } else {
                req.scene = SendMessageToWX.Req.WXSceneSession;
            }
        } else {
            req.scene = SendMessageToWX.Req.WXSceneSession;
        }
        iwxapi.sendReq(req);
    }

    private void shareToWeibo(final Activity activity, final ShareParam shareParam, final ShareListener shareListener) {
        if (shareParam.getImgUrl() != null && shareParam.getImgUrl() != "") {
            new DownLoadHelper(activity, new DownLoadHelper.OnDownLoadListener() {
                @Override
                public void onDownLoadResult(String res) {
                    if (res == null || res.length() == 0) {
                        shareListener.onError();
                        return;
                    }
                    shareParam.setImgPath(res);
                    shareToWeiBoForLocalImage(activity, shareParam, shareListener);
                }
            }).download(shareParam.getImgUrl());
        } else {
            shareToWeiBoForLocalImage(activity, shareParam, shareListener);
        }
    }

    private ShareListener weiboShareListener = null;

    private void shareToWeiBoForLocalImage(Activity activity, ShareParam shareParam, final ShareListener shareListener) {
        File file = new File(shareParam.getImgPath());
        if (!file.exists()) {
            shareListener.onError();
            return;
        }
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeFile(shareParam.getImgPath());
        } catch (Exception e) {
            e.printStackTrace();
            shareListener.onError();
            return;
        }
        weiboShareListener = shareListener;
        TextObject textObject = new TextObject();
        textObject.text = shareParam.getText()+" ";
        ImageObject imageObject = new ImageObject();
        imageObject.setImageObject(bitmap);
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();//初始化微博的分享消息
        weiboMessage.textObject = textObject;
        weiboMessage.imageObject = imageObject;
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
        weiboShareAPI.sendRequest(activity, request);
    }

    public IWeiboShareAPI getWeiboShareAPI() {
        return weiboShareAPI;
    }

    public void onResponse(BaseResponse baseResponse) {
        if (weiboShareListener == null) {
            return;
        }
        switch (baseResponse.errCode) {
            case WBConstants.ErrorCode.ERR_OK:
                weiboShareListener.onComplete();
                weiboShareListener = null;
                break;
            case WBConstants.ErrorCode.ERR_CANCEL:
                weiboShareListener.onCancel();
                weiboShareListener = null;
                break;
            case WBConstants.ErrorCode.ERR_FAIL:
                weiboShareListener.onError();
                weiboShareListener = null;
                break;
        }
    }

    /************************
     * 分享应用
     ************************/

    public void shareApp(Activity activity, Platform platform, ShareListener shareListener) {
        switch (platform) {
            case QQ:
                shareAppToQQ(activity, shareListener, false);
                break;
            case QZONE:
                shareAppToQQ(activity, shareListener, true);
                break;
            case WECHAT:
                shareAppToWechat(activity, shareListener);
                break;
            case WECHAT_FRIENDS:
                ShareParam wechat = new ShareParam();

                /*params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
                params.putString(QQShare.SHARE_TO_QQ_TITLE, AppParam.SHARE_APP_TITLE);
                params.putString(QQShare.SHARE_TO_QQ_SUMMARY, AppParam.SHARE_APP_SUMMARY);
                params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, AppParam.SHARE_APP_URL);
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, AppParam.SHARE_APP_IMAGE);
                params.putString(QQShare.SHARE_TO_QQ_APP_NAME, AppParam.SHARE_APP_NAME);*/
                wechat.setTitle(AppParam.SHARE_APP_TITLE);
                wechat.setText(AppParam.SHARE_APP_SUMMARY);
                wechat.setImgUrl(AppParam.SHARE_APP_IMAGE);
                APPShare.getInstance().share(activity, Platform.WECHAT_FRIENDS, wechat, shareListener);
                break;
            case WEIBO:
                ShareParam weibo = new ShareParam();
                weibo.setImgUrl(AppParam.SHARE_APP_IMAGE);
                weibo.setText(AppParam.SHARE_APP_SUMMARY + " @Funny菌 " + AppParam.SHARE_APP_URL);
                shareToWeibo(activity, weibo, shareListener);
                break;

            case FACEBOOK:
                //跳转到Facebook客户端
                /*if(AppUtil.isInstalled(activity, AppParam.FACEBOOK_PAC)){
                    AppUtil.openCLD(AppParam.FACEBOOK_PAC, activity);
                }*/

                break;
        }
    }

    private void shareAppToQQ(Activity activity, final ShareListener shareListener, boolean qzone) {
        if (qzone) {
            ShareParam shareParam = new ShareParam();
            shareParam.setImgUrl(AppParam.SHARE_APP_IMAGE);
            shareParam.setUrl(AppParam.SHARE_APP_URL);
            shareWebImageForQQ(activity, shareParam, shareListener, qzone);
        } else {
            Bundle params = new Bundle();
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
            params.putString(QQShare.SHARE_TO_QQ_TITLE, AppParam.SHARE_APP_TITLE);
            params.putString(QQShare.SHARE_TO_QQ_SUMMARY, AppParam.SHARE_APP_SUMMARY);
            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, AppParam.SHARE_APP_URL);
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, AppParam.SHARE_APP_IMAGE);
            params.putString(QQShare.SHARE_TO_QQ_APP_NAME, AppParam.SHARE_APP_NAME);
            params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
            tencent.shareToQQ(activity, params, new IUiListener() {
                @Override
                public void onComplete(Object o) {
                    shareListener.onComplete();
                }

                @Override
                public void onError(UiError uiError) {
                    shareListener.onError();
                }

                @Override
                public void onCancel() {
                    shareListener.onCancel();
                }
            });
        }
    }

    private void shareAppToWechat(Activity activity, final ShareListener shareListener) {
        wxShareListener = shareListener;
        //设置分享图片
        WXWebpageObject wxWebpageObject = new WXWebpageObject();
        wxWebpageObject.webpageUrl = AppParam.SHARE_APP_URL;
        WXMediaMessage wxMediaMessage = new WXMediaMessage();
        wxMediaMessage.mediaObject = wxWebpageObject;
        wxMediaMessage.title = AppParam.SHARE_APP_TITLE;
        wxMediaMessage.description = AppParam.SHARE_APP_SUMMARY;
        //设置缩略图 微信限制缩略图为32KB
        // TODO: 2016/7/14

        Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.logo);
        wxMediaMessage.thumbData = BitmapUtils.bmpToByteArray(bitmap, 32);
        bitmap.recycle();
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = TRANSACTION_SHARE;
        req.message = wxMediaMessage;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        iwxapi.sendReq(req);
    }

    public void shareEmojiToWechat(String path, ShareListener shareListener) {
        wxShareListener = shareListener;
        //设置分享图片
        WXEmojiObject wxEmojiObject = new WXEmojiObject(file2byte(path));
        WXMediaMessage wxMediaMessage = new WXMediaMessage();
        wxMediaMessage.thumbData = BitmapUtils.bmpToByteArray(BitmapFactory.decodeFile(path), 32);
        wxMediaMessage.mediaObject = wxEmojiObject;
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = TRANSACTION_SHARE;
        req.message = wxMediaMessage;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        iwxapi.sendReq(req);
    }

    public void shareEmojiToWechat(Bitmap bitmap, ShareListener shareListener) {
        wxShareListener = shareListener;
        //设置分享图片
        WXEmojiObject wxEmojiObject = new WXEmojiObject(BitmapUtils.bmpToByteArray(bitmap, 10485760));
        WXMediaMessage wxMediaMessage = new WXMediaMessage();
        wxMediaMessage.thumbData = BitmapUtils.bmpToByteArray(bitmap, 32);
        wxMediaMessage.mediaObject = wxEmojiObject;
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = TRANSACTION_SHARE;
        req.message = wxMediaMessage;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        iwxapi.sendReq(req);
    }

    public void shareEmojiToWechat(Context context, String url, final ShareListener shareListener) {
        new DownLoadHelper(context, new DownLoadHelper.OnDownLoadListener() {
            @Override
            public void onDownLoadResult(String res) {
                if (res == null || res == "") {
                    shareListener.onError();
                    return;
                }
                shareEmojiToWechat(res, shareListener);
            }
        }).download(url);
    }

    public static byte[] file2byte(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

}

