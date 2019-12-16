package com.hou.lock.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.hou.lock.R;
import com.hou.lock.listener.LockView;
import com.hou.lock.listener.OnLockNumListener;
import com.hou.lock.listener.OnUnLockCallback;
import com.hou.lock.utils.ErrorEvent;
import com.hou.lock.utils.LockBus;
import com.hou.lock.utils.Sp;
import com.hou.lock.utils.Utils;
import com.hou.lock.widget.GestureLockIndicator;
import com.hou.lock.widget.GestureLockView;

/**
 * 手势密码验证
 */
public class LockActivity extends AppCompatActivity implements View.OnClickListener {

    private GestureLockView mLockView;
    private GestureLockIndicator mLockIndicator;
    private TextView tvLockExplain;//提示说明

    private int errorTimes;
    private AlertDialog forgetDialog;
    private Sp sp;
    private Activity activity;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        //固定屏幕-竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Utils.setStatusBarColor(this, Color.TRANSPARENT,0);
        Utils.switchLightOrDarkStatusBar(this, true);

        activity = this;
        sp = Sp.getDefault();

        mLockView = (GestureLockView) findViewById(R.id.gesture_lock_view);
        mLockIndicator = (GestureLockIndicator) findViewById(R.id.gesture_lock_indicator);
        tvLockExplain = (TextView) findViewById(R.id.tv_lock_explain);
        //显示-忘记手势密码
        TextView tvForgetGesturePassword = (TextView) findViewById(R.id.tv_forget_gesture_password);
        tvForgetGesturePassword.setVisibility(View.VISIBLE);
        tvForgetGesturePassword.setOnClickListener(this);

        tvLockExplain.setText(R.string.lock_draw_unlock);

        //取出缓存中错误的次数
        errorTimes = Sp.getDefault().getInt(LockView.ERROR_TIMES, 5);

        //取出本地
        final String lockP = sp.getLock();
        mLockView.setOnLockNumListener(new OnLockNumListener() {
            @Override
            public void onLock(String password) {

                if (password.equals(lockP)) {
                    sp.putInt(LockView.ERROR_TIMES, 5);

                    setResult(RESULT_OK);
                    finish();
                } else {
                    errorTimes--;
                    if (errorTimes > 0) {
                        sp.putInt(LockView.ERROR_TIMES, errorTimes);

                        tvLockExplain.setTextColor(getResources().getColor(R.color.lock_error_color));
                        tvLockExplain.setText(String.format(getString(R.string.lock_times_explain), errorTimes));

                    } else {
                        resetLockPassword();
                    }

                    mLockIndicator.setIndicator(password);
                    mLockView.error();
                    clear();
                }
            }
        });
    }

    private void resetLockPassword() {
        LockBus.getDefault().send(new ErrorEvent());

        sp.putInt(LockView.ERROR_TIMES, 5);
        sp.clearLock();

        setResult(OnUnLockCallback.TIMES_OF_ERROR_EXCEED_LIMIT_RESULT);
        finish();
    }

    private void clear() {
        mLockView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mLockView.clear();
            }
        }, 300);
    }

    @Override
    public void onBackPressed() {
        // 返回键不结束activity，而是将应用退到后台
        moveTaskToBack(true);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        //忘记密码
        if (i == R.id.tv_forget_gesture_password) {
            if (forgetDialog == null) {

                forgetDialog = new AlertDialog.Builder(activity)
                        .setTitle("提示")
                        .setMessage("忘记手势密码，需要重新登录\n是否现在登录？")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("重新登录", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sp.clearLock();
                                resetLockPassword();
                            }
                        })
                        .create();
            }
            if (forgetDialog != null && !forgetDialog.isShowing()) {

                forgetDialog.show();
            }
        }
    }
}
