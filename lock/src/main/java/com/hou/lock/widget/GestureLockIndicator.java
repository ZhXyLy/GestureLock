package com.hou.lock.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.View;

import com.hou.lock.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zxl on 2017/9/6
 */

public class GestureLockIndicator extends View {
    public static final int CAL = 3;
    public static final int ROW = 3;

    private Paint mPaint;
    private List<IndicatorCell> cells = new ArrayList<>();

    private String password = "";

    private int normalColor;
    private int selectedColor;


    public GestureLockIndicator(Context context) {
        this(context, null);
    }

    public GestureLockIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GestureLockIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GestureLockView);
        normalColor = a.getColor(R.styleable.GestureLockView_lock_cell_normal_color, getResources().getColor(R.color.lock_normal_color));
        selectedColor = a.getColor(R.styleable.GestureLockView_lock_cell_selected_color, getResources().getColor(R.color.lock_selected_color));
        a.recycle();

        for (int i = 0; i < ROW * CAL; i++) {
            IndicatorCell cell = new IndicatorCell();
            cell.setNum(i + 1);
            cells.add(i, cell);
        }
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.LTGRAY);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int innerW = width - getPaddingLeft() - getPaddingRight();
        int height;
        if (mode == MeasureSpec.AT_MOST) {
            height = innerW + getPaddingTop() + getPaddingBottom();
        } else {
            height = MeasureSpec.getSize(heightMeasureSpec);
        }
        setMeasuredDimension(width, height);
        computeCellAttr(innerW, innerW);
    }

    private void computeCellAttr(int w, int h) {
        for (int i = 0; i < cells.size(); i++) {
            IndicatorCell cv = cells.get(i);
            float x = getPaddingLeft() + w / CAL * (i % CAL) + w / CAL / 2;
            cv.setCenterX(x);
            float y = getPaddingTop() + h / ROW * (i / ROW) + h / ROW / 2;
            cv.setCenterY(y);
            float r = w / 5.0f / 6 * 3;
            cv.setRadius(r);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setStrokeWidth(3);
        //绘制Cell
        for (IndicatorCell ic : cells) {
            if (password.contains(String.valueOf(ic.getNum()))) {
                mPaint.setColor(selectedColor);
                mPaint.setStyle(Paint.Style.FILL);
            } else {
                mPaint.setColor(normalColor);
                mPaint.setStyle(Paint.Style.STROKE);
            }
            canvas.drawCircle(ic.getCenterX(), ic.getCenterY(), ic.getRadius(), mPaint);
        }
    }

    public void setIndicator(String password) {

        this.password = password;
        invalidate();
    }
}
