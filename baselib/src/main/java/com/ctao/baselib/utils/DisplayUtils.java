package com.ctao.baselib.utils;

import android.content.res.Resources;

import com.ctao.baselib.Global;

/**
 * Created by A Miracle on 2016/9/26.
 */
public class DisplayUtils {

    public static final int width = Resources.getSystem().getDisplayMetrics().widthPixels;
    public static final int height = Resources.getSystem().getDisplayMetrics().heightPixels + ViewUtils.getNavigationBar(Global.getContext());

    private static final float DENSITY = Resources.getSystem().getDisplayMetrics().densityDpi / 160.0f;

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int converDip2px(float dpValue) {
        return Math.round(dpValue * DENSITY);
    }

    /**
     * 根据手机的分辨率从 px(像素)的单位 转成为dp
     */
    public static int converPx2dip(float pxValue) {
        return Math.round(pxValue / DENSITY);
    }
}
