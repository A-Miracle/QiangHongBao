package com.ctao.baselib;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

/**
 * Created by A Miracle on 2017/6/24.
 * BaseLib初始化配置介绍:
 *  1. Global.init
 *  2. LogUtils.init
 */
public class Global {
    private static Context mContext;
    private static int mMainThreadId = -1;
    private static Handler mMainThreadHandler;

    private Global(){}

    public static void init(Application app){
        mContext = app;
        mMainThreadHandler = new Handler();
        mMainThreadId = android.os.Process.myTid();
    }

    public static Context getContext(){
        return mContext;
    }

    public static Handler getHandler() {
        return mMainThreadHandler;
    }

    public static int getThreadId() {
        return mMainThreadId;
    }
}
