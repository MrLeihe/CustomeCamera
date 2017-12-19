package com.code.library.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.code.library.share.APPShare;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

/**
 * Created by Wxcily on 16/3/15.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler, IWeiboHandler.Response {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        APPShare.getInstance().getWxapi().handleIntent(getIntent(), this);
        APPShare.getInstance().getWeiboShareAPI().handleWeiboResponse(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        APPShare.getInstance().getWxapi().handleIntent(intent, this);
        APPShare.getInstance().getWeiboShareAPI().handleWeiboResponse(intent, this);
    }


    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        APPShare.getInstance().onResp(baseResp);
        this.finish();
    }

    @Override
    public void onResponse(BaseResponse baseResponse) {
        APPShare.getInstance().onResponse(baseResponse);
        this.finish();
    }
}
