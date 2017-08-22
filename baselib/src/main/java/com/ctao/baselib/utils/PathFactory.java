package com.ctao.baselib.utils;

import android.graphics.Path;
import android.graphics.RectF;

/**
 * Created by A Miracle on 2016/9/29.
 */
public class PathFactory {

	/**
	 * 规划圆角Path
	 * @param radius 圆角半径[LeftTop, RightTop, RightBottom, LeftBottom]
	 * @param rect RectF
	 * @param path Path
	 * @param padding padding
	 */
	public void planRoundPath(float[] radius, RectF rect, Path path, float padding) {
		if (radius == null || rect == null && path == null) {
			return;
		}
		if (radius.length < 4) {
			return;
		}
		path.moveTo(rect.left + radius[0], rect.top + padding);
		path.lineTo(rect.right - radius[1], rect.top + padding);
		path.arcTo(new RectF(rect.right - radius[1] * 2 + padding, rect.top + padding, rect.right - padding,
				radius[1] * 2 + rect.top - padding), 270, 90);

		path.lineTo(rect.right - padding, rect.top + radius[1]);
		path.lineTo(rect.right - padding, rect.bottom - radius[2]);
		path.arcTo(new RectF(rect.right - radius[2] * 2 + padding, rect.bottom - radius[2] * 2 + padding,
				rect.right - padding, rect.bottom - padding), 0, 90);

		path.lineTo(rect.right - radius[2], rect.bottom - padding);
		path.lineTo(rect.left + radius[3], rect.bottom - padding);
		path.arcTo(new RectF(rect.left + padding, rect.bottom - radius[3] * 2 + padding,
				rect.left + radius[3] * 2 - padding, rect.bottom - padding), 90, 90);

		path.lineTo(rect.left + padding, rect.bottom - radius[3]);
		path.lineTo(rect.left + padding, rect.top + radius[0]);
		path.arcTo(new RectF(rect.left + padding, rect.top + padding, rect.left + radius[0] * 2 - padding,
				rect.top + radius[0] * 2 - padding), 180, 90);

		path.close();
	}
}
