package com.ctao.baselib.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by A Miracle on 2016/9/29.
 */
public class BitmapUtils {
	private static final Canvas sCanvas = new Canvas();
	private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
	private static final int COLORDRAWABLE_DIMENSION = 1;// 颜色可拉维度
	
	public static Bitmap getBitmapFromDrawable(Drawable drawable) {
		if (drawable == null) {
			return null;
		}
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}
		try {
			Bitmap bitmap;
			if (drawable instanceof ColorDrawable) {
				bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
			} else {
				bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
						BITMAP_CONFIG);
			}
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
			drawable.draw(canvas);
			return bitmap;
		} catch (OutOfMemoryError e) {
			return null;
		}
	}

	/** 读取图片(图片已经过合理缩放) */
	public static Bitmap readFile2BitmapZoom(File file) {
		if (file.exists()) {// 若该文件存在
			FileDescriptor fd;
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
				fd = fis.getFD();

				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeFileDescriptor(fd, null, options);
				int imgHeight = options.outHeight;
				int imgWidth = options.outWidth;

				int windowWidth = DisplayUtils.width;
				int windowHeitht = DisplayUtils.height;
				if(imgWidth > imgHeight){
					int tmp = imgWidth;
					imgWidth = imgHeight;
					imgHeight = tmp;
				}
				int scaleX = imgWidth/windowWidth;
				int scaleY = imgHeight/windowHeitht;
				int scale = 1;
				if (scaleX >= scaleY && scaleX > 1) {
					scale = scaleX - (scaleX - scaleY) / 4;
				} else if (scaleY > scaleX && scaleY > 1) {
					scale = scaleY - (scaleY - scaleX) / 4;
				}

				options.inSampleSize = scale;

				options.inJustDecodeBounds = false;
				options.inDither = false;
				options.inPurgeable = true;
				options.inInputShareable = true;
				Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fd, null, options);

				if(bitmap != null){
					LogUtils.printOut("readFile2BitmapZoom: "+bitmap.getWidth()+", "+bitmap.getHeight());
				}
				return bitmap;
			} catch (FileNotFoundException e) {
				LogUtils.e(BitmapUtils.class.getSimpleName(), e);
			} catch (IOException e) {
				LogUtils.e(BitmapUtils.class.getSimpleName(), e);
			} finally {
				IOUtils.close(fis);
			}
		}
		return null;
	}

	/**
	 * 从视图创建位图
	 * @param view
	 * @return
	 */
	public static Bitmap createBitmapFromView(View view) {
		if (view instanceof ImageView) {
			Drawable drawable = ((ImageView) view).getDrawable();
			if (drawable != null && drawable instanceof BitmapDrawable) {
				return ((BitmapDrawable) drawable).getBitmap();
			}
		}
		view.clearFocus();
		Bitmap bitmap = createBitmapSafely(view.getWidth(),
				view.getHeight(), Bitmap.Config.ARGB_8888, 1);
		if (bitmap != null) {
			synchronized (sCanvas) {
				Canvas canvas = sCanvas;
				canvas.setBitmap(bitmap);
				view.draw(canvas);
				canvas.setBitmap(null);
			}
		}
		return bitmap;
	}

	/**
	 * 一个安全的Bitmap.createBitmap(width, height, config)
	 * @param width
	 * @param height
	 * @param config
	 * @param retryCount 重试次数
	 * @return
	 */
	public static Bitmap createBitmapSafely(int width, int height, Bitmap.Config config, int retryCount) {
		try {
			return Bitmap.createBitmap(width, height, config);
		} catch (OutOfMemoryError e) {
			LogUtils.e(e);
			if (retryCount > 0) {
				System.gc();
				return createBitmapSafely(width, height, config, retryCount - 1);
			}
			return null;
		}
	}

	/** 缩放图片 */
	public static Bitmap zoomBitmapScale(Bitmap bitmap, float sx, float sy) {
		if (sx <= 0 || sy <= 0) {
			return bitmap;
		}
		if(bitmap == null){
			return null;
		}
		Matrix matrix = new Matrix();
		matrix.postScale(sx, sx);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	}
}
