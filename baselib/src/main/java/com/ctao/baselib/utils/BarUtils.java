package com.ctao.baselib.utils;

import android.app.Activity;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.ctao.baselib.R;


/**
 * Created by A Miracle on 2016/12/29.
 * 沉浸式状态栏
 */
public class BarUtils {

    public static void setStatusBarColor(Activity activity, int statusColor, int alpha) {
        setStatusBarColor(activity, calculateStatusBarColor(statusColor, alpha));
    }

    public static void setStatusBarColor(Activity activity, int statusColor){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 第一步, 透明状态栏
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // 第二步, 不区分 API19 和 API21 以上的差别, 获取到 ContentFrameLayout extends FrameLayout, 添加假状态栏
            ViewGroup mContentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);

            View mStatusBarView = mContentView.findViewById(R.id.status_bar);
            if (mStatusBarView != null) {
                // 如果已经添加假状态栏视图
                mStatusBarView.setBackgroundColor(statusColor);
            } else {
                int statusBarHeight = ViewUtils.getStatusBar(activity);

                // 给ContentView 添加 Margin 或 Padding
                View mContentChild = mContentView.getChildAt(mContentView.getChildCount() - 1);
                if (mContentChild != null) {
                    ViewCompat.setFitsSystemWindows(mContentChild, false);
                    FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mContentChild.getLayoutParams();
                    lp.topMargin += statusBarHeight;
                    mContentChild.setLayoutParams(lp);
                }

                // 添加假状态栏视图
                mStatusBarView = new View(activity);
                mStatusBarView.setId(R.id.status_bar);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
                layoutParams.gravity = Gravity.TOP;
                mStatusBarView.setLayoutParams(layoutParams);
                mStatusBarView.setBackgroundColor(statusColor);
                mContentView.addView(mStatusBarView, 0);
            }
        }
    }

    public static void setNavigationBarColor(Activity activity, int navColor){
        // 这个对状态栏有影响, 奇葩啊
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && ViewUtils.navigationBarExist2(activity)) {
            // 第一步, 透明导航栏
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

            // 第二步, 获取到 ContentFrameLayout extends FrameLayout, 添加导航栏占位
            ViewGroup mContentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);

            View mNavBarView = mContentView.findViewById(R.id.navigation_bar);
            if(mNavBarView != null){
                // 如果已经添加导航栏占位视图
                mNavBarView.setBackgroundColor(navColor);
            } else {
                int navBarHeight = ViewUtils.getNavigationBar(activity);

                // 给ContentView 添加 Margin 或 Padding
                View mContentChild = mContentView.getChildAt(mContentView.getChildCount() - 1);
                if (mContentChild != null) {
                    ViewCompat.setFitsSystemWindows(mContentChild, false);
                    FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mContentChild.getLayoutParams();
                    lp.bottomMargin += navBarHeight;
                    mContentChild.setLayoutParams(lp);
                }

                // 添加导航栏占位视图
                mNavBarView = new View(activity);
                mNavBarView.setId(R.id.navigation_bar);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, navBarHeight);
                layoutParams.gravity = Gravity.BOTTOM;
                mNavBarView.setLayoutParams(layoutParams);
                mNavBarView.setBackgroundColor(navColor);
                mContentView.addView(mNavBarView, 0);
            }
        }
    }

    // 将颜色进行透明处理
    private static int calculateStatusBarColor(int color, int alpha) {
        float a = 1 - alpha / 255f;
        int red = color >> 16 & 0xff;
        int green = color >> 8 & 0xff;
        int blue = color & 0xff;
        red = (int) (red * a + 0.5);
        green = (int) (green * a + 0.5);
        blue = (int) (blue * a + 0.5);
        return 0xff << 24 | red << 16 | green << 8 | blue;
    }
}
