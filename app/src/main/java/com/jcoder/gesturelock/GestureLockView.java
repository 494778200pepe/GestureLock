package com.jcoder.gesturelock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * @author wang
 * @date 2018/7/24.
 */

public class GestureLockView extends View implements View.OnTouchListener {

    private ArrayList<Circle> circles = new ArrayList<>();
    private ArrayList<Circle> selectedCircles = new ArrayList<>();
    private static final int RADIUS = 60;
    private static final int LINE_WIDTH = 18;
    private int mWidth;
    private int mHeight;

    private Paint mPaint;

    public GestureLockView(Context context) {
        super(context);
        setOnTouchListener(this);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(4);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getWidth();
        mHeight = getHeight();
        int width;
        int height;
        int offsetX = 0;
        int offsetY = 0;
        if (mHeight > mWidth) {
            width = height = mWidth / 4;
            offsetY = (mHeight - mWidth) / 2;
        } else {
            width = height = mHeight / 4;
            offsetX = (mWidth - mHeight) / 2;
        }

        circles.add(new Circle(offsetX + width, offsetY + height, RADIUS));
        circles.add(new Circle(offsetX + width * 2, offsetY + height, RADIUS));
        circles.add(new Circle(offsetX + width * 3, offsetY + height, RADIUS));

        circles.add(new Circle(offsetX + width, offsetY + height * 2, RADIUS));
        circles.add(new Circle(offsetX + width * 2, offsetY + height * 2, RADIUS));
        circles.add(new Circle(offsetX + width * 3, offsetY + height * 2, RADIUS));

        circles.add(new Circle(offsetX + width, offsetY + height * 3, RADIUS));
        circles.add(new Circle(offsetX + width * 2, offsetY + height * 3, RADIUS));
        circles.add(new Circle(offsetX + width * 3, offsetY + height * 3, RADIUS));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCircles(canvas);
        drawCircleLines(canvas);
        drawMoveLine(canvas);
    }

    private void drawCircles(Canvas canvas) {
        for (Circle circle : circles) {
            if (circle.isSelected()) {
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(Color.GREEN);
                canvas.drawCircle(circle.x, circle.y, circle.radius, mPaint);
            }
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(Color.BLUE);
            canvas.drawCircle(circle.x, circle.y, circle.radius / 3, mPaint);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(circle.x, circle.y, circle.radius, mPaint);
        }
    }

    private void drawCircleLines(Canvas canvas) {
        if (selectedCircles.size() > 2) {
            Circle firstCircle;
            Circle secondCircle;
            for (int i = 1; i < selectedCircles.size(); i++) {
                firstCircle = selectedCircles.get(i - 1);
                secondCircle = selectedCircles.get(i);
                canvas.drawLine(firstCircle.x, firstCircle.y, secondCircle.x, secondCircle.y, mPaint);
            }
        }
    }

    private void drawMoveLine(Canvas canvas) {
        if (moveX > 0 && moveY > 0) {
            Circle lastCircle = selectedCircles.get(selectedCircles.size() - 1);
            canvas.drawLine(lastCircle.x, lastCircle.y, moveX, moveY, mPaint);
        }
    }

    int moveX = -1;
    int moveY = -1;

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        moveX = -1;
        moveY = -1;
        Circle circle = null;
        boolean handleEvent = false;
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                circle = checkPointsSelected(x, y);
                if (circle != null) {
                    handleEvent = true;
                    postInvalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                checkPointsSelected(x, y);
                moveX = x;
                moveY = y;
                postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
                moveX = 0;
                moveY = 0;
                selectedCircles.clear();
                break;
            default:
                break;
        }
        return handleEvent;
    }

    private class Circle {
        int x;
        int y;
        int radius;
        boolean isSelected = false;

        public Circle(int x, int y, int radius) {
            this.x = x;
            this.y = y;
            this.radius = radius;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            this.isSelected = selected;
        }
    }

    private Circle checkPointsSelected(int x, int y) {
        for (Circle circle : circles) {
            if (checkSelected(circle, x, y)) {
                circle.setSelected(true);
                if (!selectedCircles.contains(circle)) {
                    selectedCircles.add(circle);
                }
                return circle;
            }
        }
        return null;
    }

    public static boolean checkSelected(Circle circle, int x, int y) {
        int distance = (int) Math.sqrt((circle.x - x) * (circle.x - x) + (circle.y - y) * (circle.y - y));
        return distance < circle.radius;
    }

}
