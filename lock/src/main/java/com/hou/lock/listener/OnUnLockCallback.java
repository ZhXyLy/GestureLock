package com.hou.lock.listener;

/**
 * Created by Zxl on 2017/9/6
 */

public interface OnUnLockCallback {
    //当次数超过限制时的回调
    int TIMES_OF_ERROR_EXCEED_LIMIT_RESULT = 1111;

    void unLock();
}
