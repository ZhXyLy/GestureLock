package com.hou.lock.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import com.hou.lock.utils.Utils;

/**
 * Created by Zxl on 2017/9/4
 */

public class CellView {
    private static final String TAG = "CellView";

    private int num;

    private float centerX;
    private float centerY;

    private float radius;
    private float centerRadius;

    private float circleWidth = 2;

    private int normalColor = Color.LTGRAY;
    private int selectedColor = Color.BLUE;
    private int errorColor = Color.RED;
    //随时改变的变量
    private boolean isSelected;
    private int lastCell = -1;//最后一个Cell的num
    private float angle;//小三角角度
    private Status status = Status.DEFAULT;
    private int color = Color.LTGRAY;

    private Path path;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public int getLastCell() {
        return lastCell;
    }

    public void setLastCell(int lastCell) {
        this.lastCell = lastCell;
    }

    public void clear() {
        angle = 0;
        isSelected = false;
        lastCell = -1;
        status = Status.DEFAULT;
        path.reset();
    }

    public int getColor() {
        return color;
    }

    public void setCircleWidth(float circleWidth) {
        this.circleWidth = circleWidth;
    }

    public float getCircleWidth() {
        return circleWidth;
    }

    public void setNormalColor(int normalColor) {
        this.normalColor = normalColor;
    }

    public void setSelectedColor(int selectedColor) {
        this.selectedColor = selectedColor;
    }

    public void setErrorColor(int errorColor) {
        this.errorColor = errorColor;
    }

    public enum Status {
        DEFAULT, SELECTED, ERROR;
    }

    public CellView() {
        path = new Path();
    }

    public void setCenterX(float x) {
        this.centerX = x;
    }

    public void setCenterY(float y) {
        this.centerY = y;
    }

    public void setRadius(float r) {
        this.radius = r;
    }

    public void setCenterRadius(float cr) {
        this.centerRadius = cr;
    }

    public float getCenterX() {
        return centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public float getRadius() {
        return radius;
    }

    public float getCenterRadius() {
        return centerRadius;
    }

    public void setStatus(Status status) {
        this.status = status;
        switch (status) {
            case SELECTED:
                color = selectedColor;
                break;
            case ERROR:
                color = errorColor;
                break;
            case DEFAULT:
            default:
                color = normalColor;
                break;
        }
    }

    public Status getStatus() {
        return status;
    }

    public void draw(Canvas canvas, Paint paint) {
        switch (status) {
            case SELECTED:
                color = selectedColor;
                paint.setColor(color);
                //绘制中心实心圆
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(centerX, centerY, centerRadius, paint);
                paint.setStrokeWidth(circleWidth);
                drawTriangle(canvas, paint);
                break;
            case ERROR:
                color = errorColor;
                paint.setColor(color);
                //绘制中心实心圆
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(centerX, centerY, centerRadius, paint);
                paint.setStrokeWidth(circleWidth);
                drawTriangle(canvas, paint);
                break;
            case DEFAULT:
            default:
                color = normalColor;
                paint.setColor(color);
                paint.setStrokeWidth(circleWidth);
                break;
        }
        //绘制空心圆
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(centerX, centerY, radius, paint);
    }

    private void drawTriangle(Canvas canvas, Paint paint) {
        if (num != lastCell) {
            path.reset();
            float triangleDistance = 20;//三角边长
            float distance = centerRadius + 20 + triangleDistance;//三角定点位置,间隔5
            float triangle = 15;//向两边的角度，计算三角两边位置
            path.moveTo(centerX + distance * Utils.cos(angle), centerY + distance * Utils.sin(angle));
            path.lineTo(centerX + (distance - triangleDistance) * Utils.cos(angle + triangle),
                    centerY + (distance - triangleDistance) * Utils.sin(angle + triangle));
            path.lineTo(centerX + (distance - triangleDistance) * Utils.cos(angle - triangle),
                    centerY + (distance - triangleDistance) * Utils.sin(angle - triangle));
            path.close();
            canvas.drawPath(path, paint);
        }
    }
}
