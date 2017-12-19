package com.code.library.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.code.library.R;

/**
 * Created by yue on 15/11/4.
 * 从下方弹出的列表对话框
 */
public class BottomListDialog extends Dialog {

    private Context context;
    private static int width;
    private static int height;

    protected BottomListDialog(Context context, int width, int height) {
        super(context, R.style.BottomDialogStyle);
        this.context = context;
        this.width = width;
        this.height = height;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        window.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = width;
        params.height = height;
        window.setAttributes(params);
    }

    public static class Builder {
        private BottomListDialog dialog;
        private Context context;
        private OnCancelListener onCancelListener;
        private boolean cancelable;
        private boolean canceledOnTouchOutside;
        private BaseAdapter adapter;
        private OnItemClickListener onItemClickListener;

        public Builder(Context context, BaseAdapter adapter, int width, int height) {
            dialog = new BottomListDialog(context, width, height);
            this.context = context;
            this.adapter = adapter;
            cancelable = true;
            canceledOnTouchOutside = true;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Builder setCanceledOnTouchOutside(boolean canceled) {
            this.canceledOnTouchOutside = canceled;
            return this;
        }

        public Builder setOnCancelListener(OnCancelListener onCancelListener) {
            this.onCancelListener = onCancelListener;
            return this;
        }

        public Builder setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
            return this;
        }

        public BottomListDialog create() {
            if (dialog == null) {
                return null;
            }
            View view = LayoutInflater.from(context).inflate(
                    R.layout.layout_dialog_buttom_list, null);
            view.setMinimumWidth(width);
            ListView listview = (ListView) view.findViewById(R.id.listview);
            listview.setAdapter(adapter);
            if (onItemClickListener != null) {
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        onItemClickListener.onClick(dialog, position);
                    }
                });
            }
            if (onCancelListener != null) {
                dialog.setOnCancelListener(onCancelListener);
            }

            dialog.setCancelable(cancelable);
            dialog.setCanceledOnTouchOutside(canceledOnTouchOutside);

            dialog.setContentView(view);
            return dialog;
        }

        public BottomListDialog show() {
            create().show();
            return dialog;
        }

    }

    public interface OnItemClickListener {
        void onClick(Dialog dialog, int which);
    }


}
