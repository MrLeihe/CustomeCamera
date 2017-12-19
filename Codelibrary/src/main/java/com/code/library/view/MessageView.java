package com.code.library.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.code.library.R;
import com.code.library.utils.SystemUtils;

/**
 * Created by yue on 15/11/2.
 * 显示提示信息的自定义view
 */
public class MessageView extends RelativeLayout implements View.OnClickListener {
    private Context context;
    private TextView textView;
    private int textSize = 13;
    private int padding = 9;
    private int time = 3000;
    private boolean isshow = false;

    private Animation showAnim, hideAnim;

    public MessageView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public MessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    private void initView() {
        showAnim = AnimationUtils.loadAnimation(context, R.anim.alpha_message_show);
        hideAnim = AnimationUtils.loadAnimation(context, R.anim.alpha_message_hide);

        this.setClickable(true);
        this.setOnClickListener(this);
        textView = new TextView(context);
        textView.setTextColor(context.getResources().getColor(R.color.White));
        textView.setTextSize(SystemUtils.px2sp(context, SystemUtils.dp2px(context, textSize)));
        textView.setPadding(SystemUtils.dp2px(context, padding), SystemUtils.dp2px(context, padding), SystemUtils.dp2px(context, padding), SystemUtils.dp2px(context, padding));
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, TRUE);
        textView.setLayoutParams(layoutParams);
        textView.setGravity(Gravity.CENTER);
        addView(textView);
        this.setVisibility(View.INVISIBLE);
    }

    public void showMessage(String message) {
        showMessage(message, false);
    }

    /**
     * 显示警告信息
     *
     * @param message   显示的文本
     * @param isWarning 是否为警告
     */
    public void showMessage(String message, boolean isWarning) {
        if (isshow) {
            return;
        }
        isshow = true;
        if (isWarning) {
            setBackgroundColor(context.getResources().getColor(R.color.Message_gray));
        } else {
            setBackgroundColor(context.getResources().getColor(R.color.Message_Green));
        }
        getBackground().setAlpha(229);
        textView.setText(message);
        this.setVisibility(View.VISIBLE);
        this.startAnimation(showAnim);
        handler.sendEmptyMessageDelayed(HIDE, time);
    }

    private void hide() {
        this.startAnimation(hideAnim);
        MessageView.this.setVisibility(View.INVISIBLE);
        isshow = false;
    }

    private static final int HIDE = 1;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HIDE:
                    hide();
                    break;
            }
        }
    };

    public void setTextSize(int textSize) {
        textView.setTextSize(SystemUtils.px2sp(context, SystemUtils.dp2px(context, textSize)));
    }

    public void setPadding(int padding) {
        textView.setPadding(SystemUtils.dp2px(context, padding), SystemUtils.dp2px(context, padding), SystemUtils.dp2px(context, padding), SystemUtils.dp2px(context, padding));
    }

    public void setTime(int time) {
        this.time = time;
    }


    @Override
    public void onClick(View v) {
        if (isshow) {
            handler.removeMessages(HIDE);
            hide();
        }
    }
}
