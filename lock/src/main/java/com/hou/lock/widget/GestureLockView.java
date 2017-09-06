package com.hou.lock.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.hou.lock.R;
import com.hou.lock.listener.OnLockNumListener;
import com.hou.lock.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zxl on 2017/9/4
 */

public class GestureLockView extends View {
    private static final String TAG = "GestureLockView";

    public static final int CAL = 3;
    public static final int ROW = 3;

    /**
     * 所有元素集合
     */
    private List<CellView> cellViews = new ArrayList<>(ROW * CAL);
    /**
     * 所选元素
     */
    private List<CellView> selectCellViews = new ArrayList<>();

    private Paint mPaint;
    //触摸点x,y坐标
    private float x;
    private float y;

    /**
     * 密码收集
     */
    private StringBuilder passwordSb = new StringBuilder();
    private boolean isFinish;
    private int lastCell = -1;
    private float cellRadius;

    public GestureLockView(Context context) {
        this(context, null);
    }

    public GestureLockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GestureLockView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GestureLockView);
        float cellWidth = a.getDimension(R.styleable.GestureLockView_lock_cell_width, 3);
        cellRadius = a.getDimension(R.styleable.GestureLockView_lock_cell_radius, 0);
        float cellCenterRadius = a.getDimension(R.styleable.GestureLockView_lock_cell_center_radius, Utils.dp2px(context, 8));
        int normalColor = a.getColor(R.styleable.GestureLockView_lock_cell_normal_color, getResources().getColor(R.color.lock_normal_color));
        int selectedColor = a.getColor(R.styleable.GestureLockView_lock_cell_selected_color, getResources().getColor(R.color.lock_selected_color));
        int errorColor = a.getColor(R.styleable.GestureLockView_lock_cell_error_color, getResources().getColor(R.color.lock_error_color));
        a.recycle();

        for (int i = 0; i < ROW * CAL; i++) {
            CellView cv = new CellView();
            cv.setNum(i + 1);
            cv.setCircleWidth(cellWidth);
            cv.setCenterRadius(cellCenterRadius);
            cv.setNormalColor(normalColor);
            cv.setSelectedColor(selectedColor);
            cv.setErrorColor(errorColor);
            cellViews.add(i, cv);
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

    /**
     * 计算坐标添加CellView到集合中
     *
     * @param width  实际能绘制的宽度
     * @param height 实际能绘制的高度
     */
    private void computeCellAttr(int width, int height) {
        for (int i = 0; i < cellViews.size(); i++) {
            CellView cv = cellViews.get(i);
            float x = getPaddingLeft() + width / CAL * (i % CAL) + width / CAL / 2;
            cv.setCenterX(x);
            float y = getPaddingTop() + height / ROW * (i / ROW) + height / ROW / 2;
            cv.setCenterY(y);
            float r = cellRadius > 0 && cellRadius > cv.getCenterRadius() ? cellRadius : width / 5.0f / 6 * 3;
            cv.setRadius(r);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制所选点之间的连线
        for (int i = 1; i < selectCellViews.size(); i++) {
            CellView firstCell = selectCellViews.get(i - 1);
            CellView secondCell = selectCellViews.get(i);

            float angle = Utils.computeAngle(firstCell.getCenterX(), firstCell.getCenterY(),
                    secondCell.getCenterX(), secondCell.getCenterY());
            firstCell.setAngle(angle);

            mPaint.setColor(firstCell.getColor());
            mPaint.setStrokeWidth(firstCell.getCircleWidth());
            canvas.drawLine(firstCell.getCenterX() + (firstCell.getRadius() * Utils.cos(angle)),
                    firstCell.getCenterY() + (firstCell.getRadius() * Utils.sin(angle)),
                    secondCell.getCenterX() - (secondCell.getRadius() * Utils.cos(angle)),
                    secondCell.getCenterY() - (secondCell.getRadius() * Utils.sin(angle)), mPaint);
        }
        //绘制最后点和手之间的连线
        if (selectCellViews.size() > 0 && !isFinish) {
            CellView cell = selectCellViews.get(selectCellViews.size() - 1);
            //大于半径时才绘制
            float distance = Utils.computeDistance(cell.getCenterX(), cell.getCenterY(), x, y);
            if (distance > cell.getRadius()) {
                float angle = Utils.computeAngle(cell.getCenterX(), cell.getCenterY(), x, y);
                mPaint.setColor(cell.getColor());
                mPaint.setStrokeWidth(cell.getCircleWidth());
                canvas.drawLine(cell.getCenterX() + (cell.getRadius() * Utils.cos(angle)),
                        cell.getCenterY() + (cell.getRadius() * Utils.sin(angle)),
                        x,
                        y, mPaint);
            }
        }

        //绘制Cell
        for (CellView cellView : cellViews) {

            cellView.setLastCell(lastCell);
            cellView.draw(canvas, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isFinish) {
            isFinish = false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                x = event.getX();
                y = event.getY();
                CellView cell = getCellAt(x, y);
                //触摸点在Cell范围内，并且此点没有被选中，就添加入选中集合中
                if (cell != null && !cell.isSelected()) {
                    cell.setSelected(true);
                    cell.setStatus(CellView.Status.SELECTED);
                    lastCell = cell.getNum();
                    selectCellViews.add(cell);//加入选中集合
                }
                //如果有选中的Cell，则重新绘制
                if (selectCellViews.size() > 0) {

                    invalidate();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (selectCellViews.size() > 0) {

                    passwordSb.setLength(0);
                    for (CellView cv : selectCellViews) {
                        passwordSb.append(cv.getNum());
                    }
                    isFinish = true;
                    invalidate();

                    mCallback.onLock(passwordSb.toString());
                }
                break;
        }
        return true;
    }

    private CellView getCellAt(float x, float y) {
        for (CellView cv : cellViews) {
            if (!(x >= cv.getCenterX() - cv.getRadius() && x <= cv.getCenterX() + cv.getRadius())) {
                continue;
            }

            if (!(y >= cv.getCenterY() - cv.getRadius() && y <= cv.getCenterY() + cv.getRadius())) {
                continue;
            }
            return cv;
        }
        return null;
    }

    public void clear() {

        // 清除状态
        selectCellViews.clear();
        for (CellView cellView : cellViews) {
            cellView.clear();
        }
        // 通知重绘
        invalidate();
    }

    public void error() {
        for (CellView cellView : selectCellViews) {
            cellView.setStatus(CellView.Status.ERROR);
        }
        // 通知重绘
        invalidate();
    }

    private OnLockNumListener mCallback = new OnLockNumListener() {
        @Override
        public void onLock(String password) {

        }
    };

    public void setOnLockNumListener(OnLockNumListener listener) {
        this.mCallback = listener;
    }
}