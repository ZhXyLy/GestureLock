package com.hou.lock.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.hou.lock.listener.LockView;

/**
 * Created by Zxl on 2017/9/5
 */

public class Sp {
    private static final String TAG = "Sp";
    private static String userId = "default";

    public static void setAlias(String id) {
        userId = id;
    }

    private static Sp Instance;
    private final SharedPreferences preferences;

    public static void init(Context appContext) {
        Instance = new Sp(appContext);
    }

    public static Sp getDefault() {
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

    public void remove(String key) {
        preferences.edit().remove(key).apply();
    }

    //------------------------------

    public void setLock(String lock) {
        Log.e(TAG, "setLock: 保存：" + userId);
        Sp.getDefault().putString(LockView.LOCK_P + userId, lock);
    }

    public String getLock() {
        Log.e(TAG, "setLock: 获取：" + userId);
        return Sp.getDefault().getString(LockView.LOCK_P + userId);
    }

    public void clearLock() {
        Log.e(TAG, "setLock: 删除：" + userId);
        Sp.getDefault().remove(LockView.LOCK_P + userId);
    }
}
