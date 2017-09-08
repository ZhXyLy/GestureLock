# GestureLock
简单手势密码实现

[![](https://jitpack.io/v/xiaohouzi456/GestureLock.svg)](https://jitpack.io/#xiaohouzi456/GestureLock)

```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

```
	dependencies {
	        compile 'com.github.xiaohouzi456:GestureLock:newVersion'
	}
```
 
使用 
 
```

public class MainActivity extends AppCompatActivity {

    private Subscription rxSubscribe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GestureLock.setAlias("8888");

        //只要在app主页来注册监听即可
        rxSubscribe = LockBus.getDefault().toObservable().subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (o instanceof ErrorEvent) {
                    //这里退出登录等操作
                    Toast.makeText(MainActivity.this, "退出登录，重新去登录", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (rxSubscribe != null && rxSubscribe.isUnsubscribed()) {
            rxSubscribe.unsubscribe();
        }
    }
```
验证手势
```
Intent intent = new Intent(this, LockActivity.class);
        startActivity(intent);
```
设置手势
```
Intent intent = new Intent(this, LockSettingActivity.class);
        intent.putExtra(LockSettingActivity.STEP, LockSettingActivity.Step.SET);
        intent.putExtra(LockSettingActivity.TITLE_BACKGROUND_COLOR, getResources().getColor(R.color.colorPrimary));
        startActivity(intent);
```
修改手势
```
Intent intent = new Intent(this, LockSettingActivity.class);
        intent.putExtra(LockSettingActivity.STEP, LockSettingActivity.Step.UPDATE);
        intent.putExtra(LockSettingActivity.TITLE_BACKGROUND_COLOR, getResources().getColor(R.color.colorPrimary));
        startActivity(intent);
```
