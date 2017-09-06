package com.hou.lock.listener;

/**
 * Created by Zxl on 2017/9/4
 */

public interface OnLockListener {
    /**
     * 没有密码时，第一次录入密码触发器
     */
    void onTypeInOnce(String input);

    /**
     * 已经录入第一次密码，录入第二次密码触发器
     */
    void onTypeInTwice(String input, boolean isSuccess);

    /**
     * 验证密码触发器
     */
    void onUnLock(String input, boolean isSuccess);

    /**
     * 密码长度不够
     */
    void onError();

}
