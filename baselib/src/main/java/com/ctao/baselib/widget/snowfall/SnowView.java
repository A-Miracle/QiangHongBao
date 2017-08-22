package com.ctao.baselib.widget.snowfall;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.ctao.baselib.lib.randomcolor.RandomColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//https://github.com/StylingAndroid/Snowfall
public class SnowView extends View {
    private final static int MAX_COUNT = 200;
    private static final int NUM_SNOWFLAKES = 150;
    private static final int DELAY = 16;

    private int mCount = NUM_SNOWFLAKES;
    private List<SnowFlake> snowflakes;
    private int width, height;
    private int[] colors;
    private java.util.Random random = new Random();
    private RandomColor randomColor = new RandomColor();

    public SnowView(Context context, int count, int... colors) {
        this(context);
        if(null != colors && colors.length > 0){
            this.colors = colors;
        }
        mCount = count > MAX_COUNT ? MAX_COUNT : count;
    }

    public void addSnow(int count){
        int actualAdd;
        if(mCount + count > MAX_COUNT){
            actualAdd = MAX_COUNT - mCount;
        }else {
            actualAdd = count;
        }
        if(actualAdd <= 0){
            return;
        }
        mCount += actualAdd;
        addSnows(width, height, actualAdd);
    }

    public void delSnow(int count){
        int actualDel;
        if(mCount - count >= 50){
            actualDel = count;
        }else{
            actualDel = mCount - 50;
        }
        if(actualDel <= 0){
            return;
        }
        mCount -= actualDel;
        for (int i = 0; i < actualDel; i++) {
            snowflakes.remove(0);
        }
    }

    public void setSnowCount(int count){
        mCount = count > MAX_COUNT ? MAX_COUNT : count;
        snowflakes.clear();
        addSnows(width, height, mCount);
    }

    public int getCount() {
        return mCount;
    }

    public SnowView(Context context) {
        super(context);
    }

    public SnowView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SnowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void resize(int width, int height) {
        snowflakes = new ArrayList<>();
        addSnows(width, height, mCount);
    }

    private void addSnows(int width, int height, int count) {
        if(width == 0 || height == 0){
            return;
        }

        SnowFlake snowFlake;
        for (int i = 0; i < count; i++) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            int color;
            if(null != colors){
                color = colors[random.nextInt(colors.length)];
            }else{
                color = randomColor.randomColor();
            }
            paint.setColor(color);
            paint.setStyle(Paint.Style.FILL);

            snowFlake = SnowFlake.create(width, height, paint);
            snowflakes.add(snowFlake);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw || h != oldh) {
            width = w;
            height = h;
            resize(w, h);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (SnowFlake snowFlake : snowflakes) {
            snowFlake.draw(canvas);
        }
        getHandler().postDelayed(runnable, DELAY);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };
}
