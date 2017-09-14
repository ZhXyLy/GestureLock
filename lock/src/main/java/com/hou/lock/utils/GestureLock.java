package com.hou.lock.utils;

import android.content.Context;
import android.text.TextUtils;

/**
 * Created by Zxl on 2017/9/8
 */

public class GestureLock {
    /**
     * 通过用户Id来区分用户和单独保存密码
     */
    public static void setAlias(Context context, String userId) {
        Sp.init(context);
        Sp.setAlias(userId);
    }

    /**
     * 是否设置了密码
     *
     * @return true表示设置过
     */
    public static boolean isSetLockPassword() {
        return !TextUtils.isEmpty(Sp.getDefault().getLock());
    }
}
