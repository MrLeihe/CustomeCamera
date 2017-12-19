package com.code.library.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.code.library.R;

/**
 * Created by yue on 23/5/5.
 */
public abstract class BaseFragment extends Fragment {
    protected FragmentActivity activity;
    protected Context context;
    protected View layout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        activity = getActivity();
        context = getActivity();
        onBefore();
        super.onCreate(savedInstanceState);
        initialize();
        initView();
        initData();
        onAfter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return layout;
    }

    /**
     * onCreate 执行之前的操作
     */
    protected void onBefore() {
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

    protected void onAfter() {
    }

    protected void initLayout(int layoutid) {
        layout = LayoutInflater.from(activity).inflate(layoutid, null);
    }

    protected View findView(int viewid) {
        return layout.findViewById(viewid);
    }


    protected ImageButton titleBack;
    protected TextView titleText, titleAction;
    protected ImageButton titleActionImg;

    protected void getTitleBar() {
        titleBack = (ImageButton) findView(R.id.title_back);
        titleText = (TextView) findView(R.id.title_text);
        titleAction = (TextView) findView(R.id.title_action);
    }

    protected void getImgTitleBar() {
        titleBack = (ImageButton) findView(R.id.title_back);
        titleText = (TextView) findView(R.id.title_text);
        titleActionImg = (ImageButton) findView(R.id.title_action);
    }

    protected void startRefreshing(final SwipeRefreshLayout swipeRefreshLayout) {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    protected void stopRefreshing(final SwipeRefreshLayout swipeRefreshLayout) {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }
}
