package com.alex.reactiontest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Random;

public class Circle extends View {
    private Paint mPaint;
    public int[] colors = {Color.CYAN, Color.BLUE, Color.GREEN, Color.YELLOW, Color.BLACK};
    private Paint mPaintBack;
    public GradientDrawable drawable;
    public int cx = getWidth() / 2;
    public int cy = getHeight() / 2;

    int radius = (int) getResources().getDimension(R.dimen.dot_size) / 4;

    public Circle(Context context) {
        super(context);
        init();
    }

    public Circle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setRandom();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBack = new Paint(Paint.ANTI_ALIAS_FLAG);
        Shader shader = setGrad(cx, cy, radius);
        mPaintBack.setColor(Color.GRAY);
        mPaintBack.setAlpha(90);
        mPaint.setShader(shader);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Log.d("lolkek", String.valueOf(radius));
        canvas.drawCircle(cx + 5, cy +  5, radius, mPaintBack);
        canvas.drawCircle(cx, cy, radius, mPaint);
    }

    public void setRandom() {
        Random random = new Random();
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        cx = random.nextInt(screenWidth - 380 - getWidth()) + 100;
        cy = random.nextInt(screenHeight - 668 - getHeight()) + 300;

//        Log.d("lolkek", String.valueOf(cx));
//        Log.d("lolkek", String.valueOf(cy));
    }

    public void makeBeautiful() {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        gradientDrawable.setColors(new int[]{Color.RED, Color.BLUE});
        //gradientDrawable.setStroke(4, Color.GRAY, 4, 4);
        //setBackground(gradientDrawable);

    }

    public Shader setGrad(int x, int y, int rad) {
        int randomColor = colors[new Random().nextInt(colors.length)];
        Shader shader = new LinearGradient(cx - rad,cy - rad,cx + rad,cy + rad, Color.RED, randomColor, Shader.TileMode.CLAMP);
        //mPaint.setShader(shader);
        return shader;
    }
}
