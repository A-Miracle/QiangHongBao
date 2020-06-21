package com.ctao.baselib.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.ctao.baselib.R;
import com.ctao.baselib.utils.DisplayUtils;

/**
 * Created by A Miracle on 2017/7/23.
 */
public class BorderCircleView extends View{
    private Paint paint;
    private Paint paintBorder;
    private int borderWidth;
    private int backgroundColor;
    private int borderColor;

    public BorderCircleView(Context context) {
        super(context);
        init(context, null);
    }

    public BorderCircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BorderCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BorderCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        borderWidth = DisplayUtils.converDip2px(1);
        backgroundColor = Color.TRANSPARENT;
        borderColor = Color.BLACK;

        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BorderCircleView);
            borderWidth = ta.getDimensionPixelSize(R.styleable.BorderCircleView_borderWidth, borderWidth);
            backgroundColor = ta.getColor(R.styleable.BorderCircleView_backgroundColor, backgroundColor);
            borderColor = ta.getColor(R.styleable.BorderCircleView_borderCircleColor, borderColor);
            ta.recycle();
        }

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(backgroundColor);

        paintBorder = new Paint();
        paintBorder.setAntiAlias(true);
        paintBorder.setStyle(Paint.Style.STROKE);
        paintBorder.setColor(borderColor);

        setBackground(null);
    }

    @Override
    public void setBackgroundColor(int color) {
        paint.setColor(color);
        invalidate();
    }

    public void setBorderColor(int color) {
        paintBorder.setColor(color);
        invalidate();
    }
    public void setBorderWidth(int width) {
        borderWidth = width;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = width;
            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, MeasureSpec.getSize(heightMeasureSpec));
            }
            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int canvasSize = canvas.getWidth();
        if (canvas.getHeight() < canvasSize) {
            canvasSize = canvas.getHeight();
        }

        int circleCenter = (canvasSize - (borderWidth * 2)) / 2;
        canvas.drawCircle(circleCenter + borderWidth, circleCenter + borderWidth, ((canvasSize - (borderWidth * 2)) / 2) + borderWidth - 4.0f, paintBorder);
        canvas.drawCircle(circleCenter + borderWidth, circleCenter + borderWidth, ((canvasSize - (borderWidth * 2)) / 2) - 4.0f, paint);
    }
}
