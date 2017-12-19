package com.code.library.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * Created by yue on 15/10/29.
 * SharedPreferences 封装
 */
public class SharedConfig {
	private SharedConfig() {
	}

	private static SharedConfig instance;
	private SharedPreferences sharedPreferences;
	private Editor editor;

	private SharedConfig(Context context) {
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		editor = sharedPreferences.edit();
	}

	public static SharedConfig getInstance(Context context) {
		if (instance == null) {
			instance = new SharedConfig(context);
		}
		return instance;
	}

	public boolean writeData(String key, String value) {
		boolean result = false;
		editor.putString(key, value);
		result = editor.commit();
		return result;
	}

	public boolean writeData(String key, float value) {
		boolean result = false;
		editor.putFloat(key, value);
		result = editor.commit();
		return result;
	}

	public boolean writeData(String key, int value) {
		boolean result = false;
		editor.putInt(key, value);
		result = editor.commit();
		return result;
	}

	public boolean writeData(String key, long value) {
		boolean result = false;
		editor.putLong(key, value);
		result = editor.commit();
		return result;
	}

	public boolean writeData(String key, boolean value) {
		boolean result = false;
		editor.putBoolean(key, value);
		result = editor.commit();
		return result;
	}

	public String readString(String key, String def) {
		String result = sharedPreferences.getString(key, def);
		return result;
	}

	public float readFloat(String key, float def) {
		float result = sharedPreferences.getFloat(key, def);
		return result;
	}

	public int readInt(String key, int def) {
		int result = sharedPreferences.getInt(key, def);
		return result;
	}

	public long readLong(String key, long def) {
		long result = sharedPreferences.getLong(key, def);
		return result;
	}

	public boolean readBoolean(String key, boolean def) {
		boolean result = sharedPreferences.getBoolean(key, def);
		return result;
	}

	public boolean deleteItem(String item) {
		boolean result = false;
		editor.remove(item);
		result = editor.commit();
		return result;
	}

	public boolean isExist(String item) {
		boolean result = false;
		result = sharedPreferences.contains(item);
		return result;
	}

	public void clearConfig() {
		editor.clear().commit();
	}
}
