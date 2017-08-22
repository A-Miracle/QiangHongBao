package com.ctao.qhb;

import android.app.Application;

import com.afollestad.appthemeengine.ATE;
import com.ctao.baselib.Global;
import com.ctao.baselib.utils.LogUtils;
import com.ctao.qhb.job.IAccessibilityJob;
import com.ctao.qhb.job.WeChatAccessibilityJob;
import com.ctao.qhb.service.QHBService;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by A Miracle on 2017/8/17.
 */
public final class App extends Application{
    private static App sApp;

    private static final Class[] ACCESSIBILITY_JOBS = {
            WeChatAccessibilityJob.class,
    };

    private List<IAccessibilityJob> mAccessibilityJobs;
    private HashMap<String, IAccessibilityJob> mPkgAccessibilityJobMap;

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
        Global.init(this);
        LogUtils.init(true, "qhb", null);
        initTheme();

        // 初始化 Bugly [全开]
        CrashReport.initCrashReport(this, "APP ID", false);
    }

    private void initTheme() {
        if (!ATE.config(this, "default_theme").isConfigured(1)) {
            ATE.config(this, "default_theme")
                    .activityTheme(R.style.AppTheme)
                    .primaryColorRes(R.color.colorPrimary)
                    .autoGeneratePrimaryDark(true) //自动生成 colorPrimaryDark
                    .accentColorRes(R.color.colorPrimaryDark)
                    .textColorPrimaryRes(R.color.text_color_primary) //?android:textColorPrimary
                    .textColorSecondaryRes(R.color.text_color_secondary) //?android:textColorSecondary
                    .usingMaterialDialogs(true)
                    .commit();
        }
    }

    public static App getApp(){
        return sApp;
    }

    public void initJobs(QHBService service){
        if(mAccessibilityJobs != null && mPkgAccessibilityJobMap != null){
            return;
        }

        mAccessibilityJobs = new ArrayList<>();
        mPkgAccessibilityJobMap = new HashMap<>();

        //初始化辅助插件工作
        for(Class clazz : ACCESSIBILITY_JOBS) {
            try {
                Object object = clazz.newInstance();
                if(object instanceof IAccessibilityJob) {
                    IAccessibilityJob job = (IAccessibilityJob) object;
                    job.onCreate(service);
                    mAccessibilityJobs.add(job);
                    mPkgAccessibilityJobMap.put(job.getPackageName(), job);
                }
            } catch (Exception e) {
                LogUtils.e(e);
            }
        }
    }

    public List<IAccessibilityJob> getAccessibilityJobs() {
        return mAccessibilityJobs;
    }

    public HashMap<String, IAccessibilityJob> getPkgAccessibilityJobMap() {
        return mPkgAccessibilityJobMap;
    }
}
