package com.code.library.toast;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by yue on 15/10/29.
 * 防止重复创建的Toast工厂
 */
public class ToastFactory {
	private ToastFactory() {
	}

	private static Context context = null;
	private static Toast toast = null;

	public static Toast getToast(Context context, String text) {
		if (ToastFactory.context == context) {
			toast.setText(text);
			toast.setDuration(Toast.LENGTH_SHORT);
		} else {
			ToastFactory.context = context;
			toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		}
		return toast;
	}

	public static Toast getToast(Context context, int text) {
		if (ToastFactory.context == context) {
			toast.setText(text);
			toast.setDuration(Toast.LENGTH_SHORT);
		} else {
			ToastFactory.context = context;
			toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		}
		return toast;
	}

	public static Toast getLongToast(Context context, String text) {
		if (ToastFactory.context == context) {
			toast.setText(text);
			toast.setDuration(Toast.LENGTH_LONG);
		} else {
			ToastFactory.context = context;
			toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
		}
		return toast;
	}

	public static Toast getLongToast(Context context, int text) {
		if (ToastFactory.context == context) {
			toast.setText(text);
			toast.setDuration(Toast.LENGTH_LONG);
		} else {
			ToastFactory.context = context;
			toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
		}
		return toast;
	}

	public static void showLongToast(Context context, int text) {
		getLongToast(context, text).show();
	}

	public static void showLongToast(Context context, String text) {
		getLongToast(context, text).show();
	}

	public static void showToast(Context context, int text) {
		getToast(context, text).show();
	}

	public static void showToast(Context context, String text) {
		getToast(context, text).show();
	}

	public static void cancelToast() {
		if (toast != null) {
			toast.cancel();
		}
	}

}
