package com.ctao.baselib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.ctao.baselib.R;
import com.ctao.baselib.utils.BitmapUtils;
import com.ctao.baselib.utils.PathFactory;

/**
 * Created by A Miracle on 2017/6/27.
 * https://github.com/A-Miracle/CustomView
 * 圆角ImageView
 */
public class RoundedImageView extends android.support.v7.widget.AppCompatImageView {

    protected PathFactory mPathFactory;

    protected boolean isCircle;
    protected float[] mRadius; // [LeftTop, RightTop, RightBottom, LeftBottom]
    protected Paint mBitmapPaint;
    protected Path mPath;

    public RoundedImageView(Context context) {
        this(context, null);
    }

    public RoundedImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundedImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RoundedImageView);
        float radius = ta.getDimension(R.styleable.RoundedImageView_roundRadius, 0);
        float radiusLeftTop = ta.getDimension(R.styleable.RoundedImageView_roundRadiusLeftTop, 0);
        float radiusRightTop = ta.getDimension(R.styleable.RoundedImageView_roundRadiusRightTop, 0);
        float radiusRightBottom = ta.getDimension(R.styleable.RoundedImageView_roundRadiusRightBottom, 0);
        float radiusLeftBottom = ta.getDimension(R.styleable.RoundedImageView_roundRadiusLeftBottom, 0);
        isCircle = ta.getBoolean(R.styleable.RoundedImageView_roundRadiusIsCircle, false);
        ta.recycle();

        if (radius != 0) {
            mRadius = new float[] { radius, radius, radius, radius };
        }

        boolean falg = (radiusLeftTop != 0) ? true : false;
        falg = (radiusRightTop != 0) ? true : falg;
        falg = (radiusRightBottom != 0) ? true : falg;
        falg = (radiusLeftBottom != 0) ? true : falg;

        if (falg) {
            mRadius = new float[] { radiusLeftTop, radiusRightTop, radiusRightBottom, radiusLeftBottom };
        }

        mPathFactory = new PathFactory();
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBitmapPaint.setDither(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(isCircle){
            int measuredWidth = getMeasuredWidth();
            int measuredHeight = getMeasuredHeight();

            if (measuredWidth != 0 || measuredHeight != 0) {
                int size = measuredWidth > measuredHeight ? measuredHeight : measuredWidth;
                float radius = size / 2.0f;
                if (radius != 0) {
                    mRadius = new float[]{radius, radius, radius, radius};
                }
                setMeasuredDimension(size, size);
            }
        }
    }

    public float[] getRadius() {
        return mRadius;
    }

    /** [LeftTop, RightTop, RightBottom, LeftBottom] */
    public void setRadius(float[] radius) {
        setRadius(radius, true);
    }

    /** [LeftTop, RightTop, RightBottom, LeftBottom] */
    protected void setRadius(float[] radius, boolean invalidate) {
        mRadius = radius;
        if(invalidate){
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mRadius == null) {
            super.onDraw(canvas);
        }else{
            drawRounded(canvas);
        }
    }

    private void drawRounded(Canvas canvas) {
        if (!setupBitmapPaint()){
            return;
        }

        RectF rect = getRectF();
        if (mRadius[0] == mRadius[1] && mRadius[0] == mRadius[2] && mRadius[0] == mRadius[3]) {
            canvas.drawRoundRect(rect, mRadius[0], mRadius[0], mBitmapPaint);
        }else{
            mPath.reset();
            mPathFactory.planRoundPath(mRadius, rect, mPath, 0);
            canvas.drawPath(mPath, mBitmapPaint);
        }

        mBitmapPaint.setShader(null);
    }

    protected boolean setupBitmapPaint() {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return false;
        }

        Bitmap bitmap = getBitmapFromDrawable(drawable);
        if(bitmap == null){
            return false;
        }

        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        bitmapShader.setLocalMatrix(getImageMatrix());
        mBitmapPaint.setShader(bitmapShader);
        return true;
    }

    protected RectF getRectF(){
        return new RectF(getPaddingLeft(), getPaddingTop(), getRight() - getLeft() - getPaddingRight(),
                getBottom() - getTop() - getPaddingBottom());
    }

    protected Bitmap getBitmapFromDrawable(Drawable drawable){
        return BitmapUtils.getBitmapFromDrawable(drawable);
    }
}
