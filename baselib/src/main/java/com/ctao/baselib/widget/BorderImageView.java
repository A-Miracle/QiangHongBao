package com.ctao.baselib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.ctao.baselib.R;

public class BorderImageView extends TagImageView {

	@ColorInt
	protected int mBorderColor;
	protected float mBorderSize;
	protected Paint mBorderPaint;
	
	public BorderImageView(Context context) {
		this(context, null);
	}

	public BorderImageView(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public BorderImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BorderImageView);
		mBorderColor = ta.getColor(R.styleable.BorderImageView_borderColor, Color.parseColor("#303F9F"));
		mBorderSize = ta.getDimension(R.styleable.BorderImageView_borderSize, 0);
		ta.recycle();

		mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mBorderPaint.setDither(false);
		mBorderPaint.setStyle(Paint.Style.STROKE);

		if(mRadius == null){
			mRadius = new float[]{0f, 0f, 0f, 0f};
		}
	}

	@Override
	protected Bitmap getBitmapFromDrawable(Drawable drawable) {
		Bitmap bitmap = super.getBitmapFromDrawable(drawable);
		if (mBorderSize <= 0) {
			return bitmap;
		}

		if(!bitmap.isMutable()){
			bitmap = bitmap.copy(bitmap.getConfig(), true);
		}

		Canvas canvas = new Canvas(bitmap);

		// draw border
		mBorderPaint.setColor(mBorderColor);
		mBorderPaint.setStrokeWidth(mBorderSize + 2);
		mPath.reset();
		mPathFactory.planRoundPath(mRadius, getRectF(), mPath, mBorderSize / 2 - 1);
		canvas.drawPath(mPath, mBorderPaint);

		return bitmap;
	}
}
