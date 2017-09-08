package com.hou.lock.utils;

/**
 * Created by Zxl on 2017/9/8
 */

public class GestureLock {
    /**
     * 通过用户Id来区分用户和单独保存密码
     *
     * @param userId
     */
    public static void setAlias(String userId) {
        Sp.init(userId);
    }
}
