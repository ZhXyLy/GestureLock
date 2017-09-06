package com.hou.lock.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Zxl on 2017/9/5
 */

public class Sp {

    private static Sp Instance;
    private final SharedPreferences preferences;

    public static Sp getDefault(Context context) {
        if (Instance == null) {
            Instance = new Sp(context);
        }
        return Instance;
    }

    public Sp(Context context) {
        preferences = context.getSharedPreferences("gesture_lock", Context.MODE_PRIVATE);
    }

    public void putString(String key, String value) {
        preferences.edit().putString(key, value).apply();
    }

    public String getString(String key) {
        return preferences.getString(key, null);
    }

    public void putInt(String key, int value) {
        preferences.edit().putInt(key, value).apply();
    }

    public int getInt(String key, int defValue) {
        return preferences.getInt(key, defValue);
    }
}
