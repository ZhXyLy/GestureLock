package com.hou.lock.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
 *
 * @author zhaoxl
 */
public class LockSettingActivity extends AppCompatActivity {
    public static final String STEP = "step";
    public static final String TITLE_BACKGROUND_COLOR = "title_background_color";
    public static final String LIGHT_STATUS_BAR = "is_light_status_bar";
    private Sp sp;

    public interface Step {
        int SET = 1;
        int CONFIRM = 2;
        int UPDATE = 0;
    }

    public int step = 1;

    private GestureLockView mLockView;
    private GestureLockIndicator mLockIndicator;
    private TextView tvLockExplain;//提示说明
    private TextView tvReset;

    private int errorTimes;
    private String firstPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        //固定屏幕-竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        sp = Sp.getDefault();

        mLockView = (GestureLockView) findViewById(R.id.gesture_lock_view);
        mLockIndicator = (GestureLockIndicator) findViewById(R.id.gesture_lock_indicator);
        tvLockExplain = (TextView) findViewById(R.id.tv_lock_explain);
        ImageView ivBack = (ImageView) findViewById(R.id.iv_back);
        tvReset = (TextView) findViewById(R.id.tv_reset);
        TextView tvForgetGesturePassword = (TextView) findViewById(R.id.tv_forget_gesture_password);
        tvForgetGesturePassword.setVisibility(View.INVISIBLE);

        RelativeLayout rlTitle = (RelativeLayout) findViewById(R.id.rl_title);
        rlTitle.setVisibility(View.VISIBLE);
        int titleBgColor = getIntent().getIntExtra(LockSettingActivity.TITLE_BACKGROUND_COLOR,
                getResources().getColor(R.color.lock_title_background_color));
        rlTitle.setBackgroundColor(titleBgColor);

        Utils.setStatusBar(this);
        boolean lightStatusBar = getIntent().getBooleanExtra(LockSettingActivity.LIGHT_STATUS_BAR, false);
        Utils.switchLightOrDarkStatusBar(this, lightStatusBar);

        step = getIntent().getIntExtra(LockSettingActivity.STEP, Step.SET);

        tvReset.setVisibility(View.INVISIBLE);
        tvReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvReset.setVisibility(View.INVISIBLE);
                step = Step.SET;
                tvLockExplain.setTextColor(getResources().getColor(R.color.lock_explain_color));
                tvLockExplain.setText(R.string.lock_draw_unlock);
                mLockIndicator.setIndicator("");
            }
        });

        if (step == Step.UPDATE) {
            ivBack.setVisibility(View.VISIBLE);
            ivBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            tvLockExplain.setText(R.string.lock_confirm_unlock);
            errorTimes = Sp.getDefault().getInt(LockView.ERROR_TIMES, 5);
        } else {
            ivBack.setVisibility(View.INVISIBLE);
            tvLockExplain.setText(R.string.lock_draw_unlock);
        }

        //取出缓存中错误的次数
        errorTimes = sp.getInt(LockView.ERROR_TIMES, 5);

        mLockView.setOnLockNumListener(new OnLockNumListener() {
            @Override
            public void onLock(String password) {
                switch (step) {
                    case Step.UPDATE:
                        //取出本地

                        String lockPassword = sp.getLock();
                        if (lockPassword.equals(password)) {
                            step = Step.SET;
                            sp.putInt(LockView.ERROR_TIMES, 5);
                            clear(10);
                            tvLockExplain.setTextColor(getResources().getColor(R.color.lock_explain_color));
                            tvLockExplain.setText(R.string.lock_draw_new_unlock);
                        } else {
                            errorTimes--;
                            if (errorTimes > 0) {
                                sp.putInt(LockView.ERROR_TIMES, errorTimes);

                                tvLockExplain.setTextColor(getResources().getColor(R.color.lock_error_color));
                                tvLockExplain.setText(String.format(getString(R.string.lock_times_explain), errorTimes));
                                clear(10);
                            } else {
                                resetLockPassword();
                            }
                        }
                        break;
                    case Step.SET:
                        if (password.length() < 4) {
                            tvLockExplain.setTextColor(getResources().getColor(R.color.lock_error_color));
                            tvLockExplain.setText(String.format(getString(R.string.lock_point_count_explain), 4));
                            clear(10);
                        } else {
                            firstPassword = password;
                            clear(10);
                            mLockIndicator.setIndicator(password);
                            tvLockExplain.setTextColor(getResources().getColor(R.color.lock_explain_color));
                            tvLockExplain.setText(R.string.lock_confirm_unlock);
                            step = Step.CONFIRM;
                        }
                        break;
                    case Step.CONFIRM:
                        if (!password.equals(firstPassword)) {
                            tvReset.setVisibility(View.VISIBLE);
                            tvLockExplain.setTextColor(getResources().getColor(R.color.lock_error_color));
                            tvLockExplain.setText(R.string.lock_not_same_with_last);
                            clear(10);
                        } else {
                            sp.setLock(password);

                            setResult(RESULT_OK);
                            finish();
                        }
                        break;
                }
            }
        });
    }

    private void resetLockPassword() {
        LockBus.getDefault().send(new ErrorEvent());

        sp.putInt(LockView.ERROR_TIMES, 5);

        setResult(OnUnLockCallback.TIMES_OF_ERROR_EXCEED_LIMIT_RESULT);
        finish();
    }

    private void clear(long mills) {
        mLockView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mLockView.clear();
            }
        }, mills);
    }

    @Override
    public void onBackPressed() {
        if (step == Step.UPDATE) {
            super.onBackPressed();
        } else {
            // 返回键不结束activity，而是将应用退到后台
            moveTaskToBack(true);
        }
    }
}
