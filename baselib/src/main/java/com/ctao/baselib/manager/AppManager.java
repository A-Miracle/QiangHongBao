package com.ctao.baselib.manager;


import android.os.AsyncTask;
import android.os.SystemClock;

import com.ctao.baselib.ui.BaseActivity;

import java.util.ArrayList;
import java.util.Stack;

/**
 * activity 堆栈式管理
 * Created by A Miracle on 2016/9/19.
 */
public class AppManager {

    /** 堆栈列表 */
    private static Stack<BaseActivity> mActivitys;// LinkedList

    private static class Single {
        static AppManager Instance = new AppManager();
    }
    public static AppManager getInstance() {
        return AppManager.Single.Instance;
    }

    private AppManager() {
        mActivitys = new Stack<>();
    }

    /** 添加Activity到堆栈 */
    public void addActivity(BaseActivity activity) {
        mActivitys.add(activity);
    }

    /** 添加Activity到堆栈 */
    public void removeActivity(BaseActivity activity) {
        mActivitys.remove(activity);
    }

    /** 获取当前Activity（堆栈中最后一个压入的） */
    public BaseActivity getCurrentActivity() {
        return mActivitys.lastElement();
    }

    /** 获取某一个Activity（堆栈中的） */
    public BaseActivity getActivityByClass(Class<? extends BaseActivity> exceptClass) {
        for (BaseActivity activity : mActivitys) {
            if (activity.getClass().equals(exceptClass)) {
               return activity;
            }
        }
        return null;
    }

    /** 除了此Activity之外的所有Activity全部关闭 */
    public void finishExcept(BaseActivity except) {
        ArrayList<BaseActivity> copy;
        synchronized (mActivitys) {
            copy = new ArrayList<>(mActivitys);
        }
        for (BaseActivity activity : copy) {
            if (activity != except) {
                activity.finish();
            }
        }
    }

    /** 除了此Activity之外的所有Activity全部关闭 */
    public void finishExcept(Class<? extends BaseActivity> exceptClass) {
        ArrayList<BaseActivity> arrayList;
        synchronized (mActivitys) {
            arrayList = new ArrayList<>(mActivitys);
        }
        for (BaseActivity activity : arrayList) {
            if (!activity.getClass().equals(exceptClass)) {
                activity.finish();
            }
        }
    }

    /** 关闭所有Activity */
    public void finishAll() {
        ArrayList<BaseActivity> copy;
        synchronized (mActivitys) {
            copy = new ArrayList<>(mActivitys);
        }
        for (BaseActivity activity : copy) {
            activity.finish();
        }
    }

    /**  关闭从栈内某个Activity开始到某个Activity结束[start, end); */
    public void finishStartToEnd(Class<? extends BaseActivity> startClass, Class<? extends BaseActivity> endClass) {
        boolean isClose = false;
        ArrayList<BaseActivity> arrayList;
        synchronized (mActivitys) {
            arrayList = new ArrayList<>(mActivitys);
        }
        for (BaseActivity activity : arrayList) {
            if (activity.getClass() == startClass) {
                isClose = true;
            }
            if (activity.getClass() == endClass) {
                isClose = false;
            }
            if (isClose) {
                activity.finish();
            }
        }
    }

    /** 退出应用程序 */
    public void exitApp(boolean isBackground) {
        try {
            finishAll();
        } catch (Exception e) {
        }finally {
            if (isBackground) {
                new AsyncTask<String, Integer, String>() {
                    @Override
                    protected String doInBackground(String... strings) {
                        SystemClock.sleep(200); // 等待Activity动画执行完毕
                        return null;
                    }
                    @Override
                    protected void onPostExecute(String result) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(0);
                    }
                }.execute();
            }
        }
    }
}
