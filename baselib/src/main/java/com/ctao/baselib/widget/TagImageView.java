package com.ctao.baselib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.ctao.baselib.R;
import com.ctao.baselib.utils.DisplayUtils;

public class TagImageView extends RoundedImageView {

	private static final int LEFT_TOP = 0x00;
	private static final int RIGHT_TOP = 0x01;
	private static final int LEFT_BOTTOM = 0x02;
	private static final int RIGHT_BOTTOM = 0x03;
	private static final float THE_SQUARE_ROOT_OF_2 = (float) Math.sqrt(2);

	private int mTagOrientation = LEFT_TOP;

	private Paint mTagBgPaint;
	private int mTagBackgroundColor = 0x9F27CDC0;
	private float mCornerDistance = DisplayUtils.converDip2px(20);
	private float mTagWidth = DisplayUtils.converDip2px(21);

	private Paint mTagTextPaint;
	private String mTagText = "冠军";
	private float mTagTextSize = DisplayUtils.converDip2px(14);
	private int mTagTextColor = 0xFFFFFFFF;
	private float mTagTextDownX;
	private int mTagTextRotate;
	private Rect mTagTextBound;

	private Point mStartPoint;
	private Point mEndPoint;
	private Path mTagPath;
	private boolean isEnable;

	public TagImageView(Context context) {
		this(context, null);
	}

	public TagImageView(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TagImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TagImageView);
		mTagOrientation = ta.getInteger(R.styleable.TagImageView_tagOrientation, 0);
		mTagBackgroundColor = ta.getColor(R.styleable.TagImageView_tagBackgroundColor, mTagBackgroundColor);
		mCornerDistance = ta.getDimension(R.styleable.TagImageView_tagCornerDistance, mCornerDistance);
		mTagWidth = ta.getDimension(R.styleable.TagImageView_tagWidth, mTagWidth);
		String tagText = ta.getString(R.styleable.TagImageView_tagText);
		if (!TextUtils.isEmpty(tagText)) {
			mTagText = tagText;
		}
		mTagTextSize = ta.getDimension(R.styleable.TagImageView_tagTextSize, mTagTextSize);
		mTagTextColor = ta.getColor(R.styleable.TagImageView_tagTextColor, mTagTextColor);
		mTagTextDownX = ta.getDimension(R.styleable.TagImageView_tagTextDownX, mTagTextDownX);
		mTagTextRotate = ta.getInteger(R.styleable.TagImageView_tagTextRotate, mTagTextRotate);
		isEnable = ta.getBoolean(R.styleable.TagImageView_tagEnable, isEnable);
		ta.recycle();

		mStartPoint = new Point();
		mEndPoint = new Point();
		mTagTextBound = new Rect();

		mTagPath = new Path();

		mTagBgPaint = new Paint();
		mTagBgPaint.setAntiAlias(true);
		mTagBgPaint.setDither(true);
		mTagBgPaint.setStyle(Paint.Style.STROKE);
		mTagBgPaint.setStrokeJoin(Paint.Join.ROUND);
		mTagBgPaint.setStrokeCap(Paint.Cap.SQUARE);

		mTagTextPaint = new Paint();
		mTagBgPaint.setAntiAlias(true);
		mTagTextPaint.setDither(false);
	}

	protected Bitmap getBitmapFromDrawable(Drawable drawable){
		Bitmap bitmap = super.getBitmapFromDrawable(drawable);
		if (!isEnable) {
			return bitmap;
		}

		if(!bitmap.isMutable()){
			bitmap = bitmap.copy(bitmap.getConfig(), true);
		}

		Canvas canvas = new Canvas(bitmap);

		// draw background
		float distance = mCornerDistance + mTagWidth / 2;
		initStartAndEndPoint(distance);
		mTagBgPaint.setColor(mTagBackgroundColor);
		mTagBgPaint.setStrokeWidth(mTagWidth);
		mTagPath.reset();
		mTagPath.moveTo(mStartPoint.x, mStartPoint.y);
		mTagPath.lineTo(mEndPoint.x, mEndPoint.y);
		canvas.drawPath(mTagPath, mTagBgPaint);

		// draw text
		mTagTextPaint.setTextSize(mTagTextSize);
		mTagTextPaint.getTextBounds(mTagText, 0, mTagText.length(), mTagTextBound);
		mTagTextPaint.setColor(mTagTextColor);
		float hypotenuse = THE_SQUARE_ROOT_OF_2 * distance;
		float x = hypotenuse / 2 - mTagTextBound.width() / 2;
		float y = mTagTextBound.height() / 2 + mTagTextDownX;
		if (mTagTextRotate != 0) {
			canvas.save();
			float measureText = mTagTextPaint.measureText(mTagText);
			canvas.rotate(mTagTextRotate, x + measureText / 2, y + mTagTextSize / 2);
		}
		canvas.drawTextOnPath(mTagText, mTagPath, x, y, mTagTextPaint);
		if (mTagTextRotate != 0) {
			canvas.restore();
		}

		return bitmap;
	}

	private void initStartAndEndPoint(float distance) {
		int width = getMeasuredWidth();
		int height = getMeasuredHeight();
		switch (mTagOrientation) {
			case LEFT_TOP:
				mStartPoint.x = 0;
				mStartPoint.y = distance;
				mEndPoint.x = distance;
				mEndPoint.y = 0;
				break;
			case RIGHT_TOP:
				mStartPoint.x = width - distance;
				mStartPoint.y = 0;
				mEndPoint.x = width;
				mEndPoint.y = distance;
				break;
			case LEFT_BOTTOM:
				mStartPoint.x = width - distance;
				mStartPoint.y = height;
				mEndPoint.x = width;
				mEndPoint.y = height - distance;
				break;
			case RIGHT_BOTTOM:
				mStartPoint.x = 0;
				mStartPoint.y = height - distance;
				mEndPoint.x = distance;
				mEndPoint.y = height;
				break;
		}
	}

	private class Point {
		float x;
		float y;
	}

	public boolean isEnable() {
		return isEnable;
	}

	public void setEnable(boolean enable) {
		isEnable = enable;
	}

	public String getTagText() {
		return mTagText;
	}

	public void setTagText(String tagText) {
		this.mTagText = tagText;
	}
}
