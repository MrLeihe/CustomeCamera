package com.code.library.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageButton;
import android.widget.TextView;

import com.code.library.R;
import com.code.library.utils.ActivityManager;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by yue on 23/5/5.
 */
public abstract class BaseActivity extends FragmentActivity {

    protected Context context;
    protected Activity activity;

    protected ImageButton titleBack;
    protected TextView titleAction;
    protected TextView titleText;
    protected ImageButton titleActionImg;

    protected boolean addTask = true;

    protected void thisHome() {
        this.addTask = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        onBefore();
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initialize();
        initView();
        initData();
        onAfter();
        if (addTask)
            ActivityManager.getInstance().addActivity(this);
    }

    /**
     * onCreate 执行之前的操作
     */
    protected void onBefore() {
        this.context = this;
        this.activity = this;
    }

    /**
     * 用于初始化对象,获取Intent数据等操作
     */
    protected abstract void initialize();

    /**
     * 用于初始化视图,获取控件实例
     */
    protected abstract void initView();

    /**
     * 用于初始化数据,填充视图
     */
    protected void initData() {
    }

    /**
     * 用于执行数据填充完后的操作
     */
    protected void onAfter() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        //可初始化友盟统计
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (addTask)
            ActivityManager.getInstance().delActivity(this);
    }

    /**
     * 增加默认的界面切换动画
     */
    @Override
    public void startActivity(Intent intent) {
        startActivity(intent, true);
    }

    public void startActivity(Intent intent, boolean anim) {
        super.startActivity(intent);
        if (anim) overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode, true);
    }

    public void startActivityForResult(Intent intent, int requestCode, boolean anim) {
        super.startActivityForResult(intent, requestCode);
        if (anim) overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    @Override
    public void finish() {
        finish(true);
    }

    public void finish(boolean anim) {
        super.finish();
        if (anim) overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    protected void getTitleBar() {
        titleBack = (ImageButton) findViewById(R.id.title_back);
        titleText = (TextView) findViewById(R.id.title_text);
        titleAction = (TextView) findViewById(R.id.title_action);
    }

    /*protected void getImgTitleBar() {
        titleBack = (ImageButton) findViewById(R.id.title_back);
        titleText = (TextView) findViewById(R.id.title_text);
        titleActionImg = (TextView) findViewById(R.id.title_action);
    }*/

    /**
     * 监听返回键
     *
     * @param keyCode
     * @param event
     * @return
     */
    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (onPressBack()) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }*/

    /**
     * 复写返回键操作,返回true则不继续下发
     *
     * @return
     */
    protected boolean onPressBack() {
        return false;
    }
}
