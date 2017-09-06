package com.hou.lock.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hou.lock.R;
import com.hou.lock.listener.LockView;
import com.hou.lock.listener.OnLockNumListener;
import com.hou.lock.listener.OnUnLockCallback;
import com.hou.lock.utils.ErrorEvent;
import com.hou.lock.utils.LockBus;
import com.hou.lock.utils.Sp;
import com.hou.lock.widget.GestureLockIndicator;
import com.hou.lock.widget.GestureLockView;

/**
 * 手势密码验证
 */
public class LockActivity extends AppCompatActivity {

    private GestureLockView mLockView;
    private GestureLockIndicator mLockIndicator;
    private TextView tvLockExplain;//提示说明

    private int errorTimes;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        mLockView = (GestureLockView) findViewById(R.id.gesture_lock_view);
        mLockIndicator = (GestureLockIndicator) findViewById(R.id.gesture_lock_indicator);
        tvLockExplain = (TextView) findViewById(R.id.tv_lock_explain);
        //显示-忘记手势密码
        TextView tvForgetGesturePassword = (TextView) findViewById(R.id.tv_forget_gesture_password);
        tvForgetGesturePassword.setVisibility(View.VISIBLE);
        //验证密码，隐藏Title
        RelativeLayout rlTitle = (RelativeLayout) findViewById(R.id.rl_title);
        rlTitle.setVisibility(View.INVISIBLE);

        tvLockExplain.setText(R.string.draw_unlock);

        //取出缓存中错误的次数
        errorTimes = Sp.getDefault(this).getInt(LockView.ERROR_TIMES, 5);

        //取出本地
        final String lockP = Sp.getDefault(this).getString(LockView.LOCK_P);
        mLockView.setOnLockNumListener(new OnLockNumListener() {
            @Override
            public void onLock(String password) {

                if (password.equals(lockP)) {
                    Sp.getDefault(LockActivity.this).putInt(LockView.ERROR_TIMES, 5);

                    setResult(RESULT_OK);
                    finish();
                } else {
                    errorTimes--;
                    if (errorTimes > 0) {
                        Sp.getDefault(LockActivity.this).putInt(LockView.ERROR_TIMES, errorTimes);

                        tvLockExplain.setTextColor(getResources().getColor(R.color.lock_error_color));
                        tvLockExplain.setText(String.format(getString(R.string.times_explain), errorTimes));

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
}
