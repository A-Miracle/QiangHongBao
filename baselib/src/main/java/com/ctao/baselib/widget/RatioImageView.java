package com.ctao.baselib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import com.ctao.baselib.R;

/**
 * Created by A Miracle on 2016/1/29.
 * 宽高成一定比例的ImageView
 */
public class RatioImageView extends TagImageView {

    private float ratio_w_h = 1;

    public RatioImageView(Context context) {
        this(context, null);
    }

    public RatioImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RatioImageView);
        ratio_w_h = ta.getFloat(R.styleable.RatioImageView_ratio_w_h, ratio_w_h);
        ta.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        if (View.MeasureSpec.EXACTLY == widthMode) {
            setMeasuredDimension(getMeasuredWidth(), (int) (getMeasuredWidth() / ratio_w_h + 0.5f));
        } else if (View.MeasureSpec.EXACTLY == heightMode) {
            setMeasuredDimension((int) (getMeasuredHeight() * ratio_w_h + 0.5f), getMeasuredHeight());
        }
    }
}
