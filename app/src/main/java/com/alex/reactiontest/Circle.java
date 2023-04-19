package com.alex.reactiontest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Random;

public class Circle extends View {
    private Paint mPaint;
    public int cx = getWidth() / 2;
    public int cy = getHeight() / 2;

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
        int radius = (int) getResources().getDimension(R.dimen.dot_size) / 4;
        canvas.drawCircle(cx, cy, radius, mPaint);
    }

    public void setRandom() {
        Random random = new Random();
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        cx = random.nextInt(screenWidth - 380 - getWidth()) + 100;
        cy = random.nextInt(screenHeight - 668 - getHeight()) + 300;

        Log.d("lolkek", String.valueOf(cx));
        Log.d("lolkek", String.valueOf(cy));
    }
}
