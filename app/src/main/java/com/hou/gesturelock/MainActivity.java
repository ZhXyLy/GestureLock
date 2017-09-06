package com.hou.gesturelock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.hou.lock.activity.LockActivity;
import com.hou.lock.activity.LockSettingActivity;
import com.hou.lock.listener.LockView;
import com.hou.lock.utils.ErrorEvent;
import com.hou.lock.utils.LockBus;
import com.hou.lock.utils.Sp;

import rx.Subscription;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

    private Subscription rxSubscribe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rxSubscribe = LockBus.getDefault().toObservable().subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (o instanceof ErrorEvent) {
                    Toast.makeText(MainActivity.this, "错误次数超过限制", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onGesture(View view) {
        Intent intent = new Intent(this, LockActivity.class);
        startActivity(intent);
    }

    public void onSetting(View view) {
        Intent intent = new Intent(this, LockSettingActivity.class);
        intent.putExtra(LockSettingActivity.STEP, LockSettingActivity.Step.SET);
        intent.putExtra(LockSettingActivity.TITLE_BACKGROUND_COLOR, getResources().getColor(R.color.colorPrimary));
        startActivity(intent);
    }

    public void onUpdate(View view) {
        Intent intent = new Intent(this, LockSettingActivity.class);
        intent.putExtra(LockSettingActivity.STEP, LockSettingActivity.Step.UPDATE);
        intent.putExtra(LockSettingActivity.TITLE_BACKGROUND_COLOR, getResources().getColor(R.color.colorPrimary));
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (rxSubscribe != null && rxSubscribe.isUnsubscribed()) {
            rxSubscribe.unsubscribe();
        }
    }
}
