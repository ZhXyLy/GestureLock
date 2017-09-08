package com.hou.lock;

import android.app.Application;

import com.hou.lock.utils.Sp;

/**
 * Created by Zxl on 2017/9/8
 */

public class GestureApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Sp.init(this);
    }
}
