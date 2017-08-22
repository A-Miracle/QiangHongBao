package com.ctao.baselib.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;

import com.ctao.baselib.Global;

/**
 * Created by A Miracle on 2017/1/9.
 * 资源工具类
 */
public class ResourcesUtils {

    public static int getValueOfColorAttr(int style, int defaultColor, int attr) {
        return getValueOfColorAttr(style, defaultColor, new int[]{attr})[0];
    }

    public static int[] getValueOfColorAttr(int style, int defaultColor, int... attrs) {
        if (attrs.length == 0) {
            return attrs;
        }
        int[] colors = new int[attrs.length];
        TypedArray array = Global.getContext().obtainStyledAttributes(style, attrs);
        for (int i = 0; i < attrs.length; i++) {
            colors[i] = array.getColor(i, defaultColor);
        }
        array.recycle();
        return colors;
    }

    public static int getValueOfColorAttr(Context context, int defaultColor, int attr) {
        return getValueOfColorAttr(context, defaultColor, new int[]{attr})[0];
    }

    public static int[] getValueOfColorAttr(Context context, int defaultColor, int... attrs) {
        if (attrs.length == 0) {
            return attrs;
        }
        int[] colors = new int[attrs.length];
        TypedArray array = context.obtainStyledAttributes(attrs);
        for (int i = 0; i < attrs.length; i++) {
            colors[i] = array.getColor(i, defaultColor);
        }
        array.recycle();
        return colors;
    }

    /**
     * 摘录于android-UniversalMusicPlayer-master, 其实跟我实现的一样[这个好像好用些]
     * Get a color value from a theme attribute.
     * @param context used for getting the color.
     * @param attribute theme attribute.
     * @param defaultColor default to use.
     * @return color value
     */
    public static int getThemeColor(Context context, int attribute, int defaultColor) {
        int themeColor = 0;
        String packageName = context.getPackageName();
        try {
            Context packageContext = context.createPackageContext(packageName, 0);
            ApplicationInfo applicationInfo =
                    context.getPackageManager().getApplicationInfo(packageName, 0);
            packageContext.setTheme(applicationInfo.theme);
            Resources.Theme theme = packageContext.getTheme();
            TypedArray ta = theme.obtainStyledAttributes(new int[] {attribute});
            themeColor = ta.getColor(0, defaultColor);
            ta.recycle();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return themeColor;
    }

    /** [这个好像好用些] */
    public static int getThemeColor(Context context, int themeRes, int attribute, int defaultColor) {
        int themeColor = 0;
        String packageName = context.getPackageName();
        try {
            Context packageContext = context.createPackageContext(packageName, 0);
            packageContext.setTheme(themeRes);
            Resources.Theme theme = packageContext.getTheme();
            TypedArray ta = theme.obtainStyledAttributes(new int[] {attribute});
            themeColor = ta.getColor(0, defaultColor);
            ta.recycle();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return themeColor;
    }
}
