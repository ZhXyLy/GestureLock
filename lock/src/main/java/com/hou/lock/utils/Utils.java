package com.hou.lock.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.hou.lock.widget.StatusBarView;

/**
 * Created by Zxl on 2017/9/4
 */

public class Utils {
    public static int dp2px(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (density * dp + 0.5f);
    }

    public static float cos(float angle) {
        return (float) Math.cos(angle / 180f * Math.PI);
    }

    public static float sin(float angle) {
        return (float) Math.sin(angle / 180f * Math.PI);
    }

    /**
     * 计算两点连线的角度
     *
     * @param firstX
     * @param firstY
     * @param secondX
     * @param secondY
     * @return
     */
    public static float computeAngle(float firstX, float firstY, float secondX, float secondY) {
        // 两点的x、y值
        float x = secondX - firstX;
        float y = secondY - firstY;
        double hypotenuse = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        // 斜边长度
        double cos = x / hypotenuse;
        double radian = Math.acos(cos);
        // 求出弧度
        float angle = (float) (180 / (Math.PI / radian));
        // 用弧度算出角度
        if (y < 0) {
            angle = 180 + (180 - angle);
        } else if ((y == 0) && (x < 0)) {
            angle = 180;
        } else if (x == 0 && y == 0) {
            angle = 0;
        }
        return angle;
    }


    public static float computeDistance(float firstX, float firstY, float secondX, float secondY) {
        // 两点的x、y值
        float x = secondX - firstX;
        float y = secondY - firstY;
        return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    public static void setStatusBarColor(Activity activity, int color, int statusBarAlpha) {
        if (isOver23()) {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            activity.getWindow().setStatusBarColor(color);
        } else if (isOver21()) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //注意要清除 FLAG_TRANSLUCENT_STATUS flag
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().setStatusBarColor(color);
        } else if (isOver19()) {
            //获取windowphone下的decorView
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            int count = decorView.getChildCount();
            //判断是否已经添加了statusBarView
            if (count > 0 && decorView.getChildAt(count - 1) instanceof StatusBarView) {
                decorView.getChildAt(count - 1).setBackgroundColor(calculateStatusColor(color, statusBarAlpha));
            } else {
                //新建一个和状态栏高宽的view
                StatusBarView statusView = createStatusBarView(activity, color, statusBarAlpha);
                decorView.addView(statusView);
            }
            ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
            //rootview不会为状态栏留出状态栏空间
            ViewCompat.setFitsSystemWindows(rootView, true);
            rootView.setClipToPadding(true);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    private static StatusBarView createStatusBarView(Activity activity, int color, int alpha) {
        // 绘制一个和状态栏一样高的矩形
        StatusBarView statusBarView = new StatusBarView(activity);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity));
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(calculateStatusColor(color, alpha));
        return statusBarView;
    }

    private static int calculateStatusColor(int color, int alpha) {
        float v = alpha / 255f;
        int red = (int) (Color.red(color) * v);
        int green = (int) (Color.green(color) * v);
        int blue = (int) (Color.blue(color) * v);

        return Color.rgb(red, green, blue);
    }

    private static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 是否大于等于23
     */
    private static boolean isOver23() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 是否大于等于21
     */
    private static boolean isOver21() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * 是否大于等于19
     */
    private static boolean isOver19() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }
}
