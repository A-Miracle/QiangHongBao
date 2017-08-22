package com.ctao.baselib.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;

/**
 * Created by A Miracle on 2016/9/26.
 */
public class ViewUtils {

    /** 把自身从父View中移除 */
    public static void removeSelfFromParent(View view) {
        if (view != null) {
            ViewParent parent = view.getParent();
            if (parent != null && parent instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) parent;
                group.removeView(view);
            }
        }
    }

    /**
     * 获取状态栏高度
     * @param context
     * @return
     */
    public static int getStatusBar(Context context) {
        int result = 0;
        int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            result = context.getResources().getDimensionPixelOffset(resId);
        }
        LogUtils.printOut("StatusBarHeight : " + result);
        return result;
       /* Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;*/
    }

    /**
     * 获取导航栏高度
     * @param context
     * @return
     */
    public static int getNavigationBar(Context context) {
        int result = 0;
        int rid = context.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        if (rid > 0) {
            if (context.getResources().getBoolean(rid)){
                int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
                if(resourceId > 0){
                    result = context.getResources().getDimensionPixelSize(resourceId);
                }
            }
        }
        LogUtils.printOut("getNavigationBar : " + result);
        return result;
    }

    /**
     * 此方法在模拟器还是在真机都是完全正确
     * @param activity
     * @return
     */
    public static boolean navigationBarExist2(Activity activity) {
        WindowManager windowManager = activity.getWindowManager();
        Display d = windowManager.getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            d.getRealMetrics(realDisplayMetrics);
        }

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }

    /**
     * 获取View距离屏幕顶部距离
     * @param view
     * @return
     */
    public static int getViewToScreenTop(View view) {
        Rect frame = new Rect();
        view.getGlobalVisibleRect(frame);
        return frame.top;
    }

    /**
     * 子View 框取 父View的背景
     * @param root 一级父View
     * @param child 一级子View
     * @return Bitmap
     */
    public Bitmap getBoxTakeBitmap(View root, View child){
        root.buildDrawingCache();
        Bitmap bmp = root.getDrawingCache();

        Bitmap bitmap = Bitmap.createBitmap(child.getWidth(),
                child.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.translate(-child.getLeft(), -child.getTop());
        canvas.scale(1, 1);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bmp, 0, 0, paint);
        return bitmap;
    }

    /**
     * findViewById
     * @param rootView
     * @param viewId
     * @param <T>
     * @return
     */
    public static <T extends View> T getView(View rootView, int viewId) {
        SparseArray<View> views = null;

        Object tag = rootView.getTag();
        if (tag != null && tag instanceof SparseArray) {
            views = (SparseArray<View>)tag;
        }

        if(views == null){
            views = new SparseArray<>();
            rootView.setTag(views);
        }

        View view = views.get(viewId);
        if (view == null) {
            view = rootView.findViewById(viewId);
            views.put(viewId, view);
        }
        return (T) view;
    }
}
