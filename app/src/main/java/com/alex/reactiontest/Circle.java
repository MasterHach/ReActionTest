package com.alex.reactiontest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class Circle extends View {
    private Paint mPaint;

    public Circle(Context context) {
        super(context);
        init();
    }

    public Circle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        int radius = (int) getResources().getDimension(R.dimen.dot_size) / 4;
        canvas.drawCircle(cx, cy, radius, mPaint);
    }
}
