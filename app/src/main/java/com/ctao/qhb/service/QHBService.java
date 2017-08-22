package com.ctao.qhb.service;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

import com.ctao.baselib.utils.LogUtils;
import com.ctao.baselib.utils.ToastUtils;
import com.ctao.qhb.App;
import com.ctao.qhb.event.MessageEvent;
import com.ctao.qhb.job.IAccessibilityJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;

/**
 * Created by A Miracle on 2017/8/15.
 * 抢红包外挂服务
 */
public final class QHBService extends AccessibilityService {

    private static final String TAG = "QHBService";

    private static boolean isRun;
    private List<IAccessibilityJob> mAccessibilityJobs;
    private HashMap<String, IAccessibilityJob> mPkgAccessibilityJobMap;
    private QHBNotificationManager mQHBNotificationManager;

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
        super.onCreate();
        App.getApp().initJobs(this);
        mAccessibilityJobs = App.getApp().getAccessibilityJobs();
        mPkgAccessibilityJobMap = App.getApp().getPkgAccessibilityJobMap();
        mQHBNotificationManager = new QHBNotificationManager(this);
        mQHBNotificationManager.startNotification();
    }

    @Override
    public void onDestroy() {
        isRun = false;
        EventBus.getDefault().post(new MessageEvent(MessageEvent.QHB_SERVICE_STATE, false));
        EventBus.getDefault().unregister(this);
        mQHBNotificationManager.stopNotification();
        super.onDestroy();
        LogUtils.printOut(TAG, "QHBService onDestroy");
        if(mPkgAccessibilityJobMap != null) {
            mPkgAccessibilityJobMap.clear();
        }
        if(mAccessibilityJobs != null && !mAccessibilityJobs.isEmpty()) {
            for (IAccessibilityJob job : mAccessibilityJobs) {
                job.onDestroy();
            }
            mAccessibilityJobs.clear();
        }

        mAccessibilityJobs = null;
        mPkgAccessibilityJobMap = null;
    }

    @Override
    public void onInterrupt() { // 服务中断，如授权关闭或者将服务杀死
        ToastUtils.show("抢红包服务中断");
        LogUtils.printErr("抢红包服务中断");
        isRun = false;
        EventBus.getDefault().post(new MessageEvent(MessageEvent.QHB_SERVICE_STATE, false));
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected(); // 连接服务后,一般是在授权成功后会接收到
        ToastUtils.show("已连接抢红包服务");
        LogUtils.printOut("已连接抢红包服务");
        isRun = true;
        EventBus.getDefault().post(new MessageEvent(MessageEvent.QHB_SERVICE_STATE, true));
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // 接收事件, 如触发了通知栏变化、界面变化等
        LogUtils.printOut(TAG, "event >>>" + eventToString(event) + "\n");
        String pkn = String.valueOf(event.getPackageName());
        if(mAccessibilityJobs != null) {
            for (IAccessibilityJob job : mAccessibilityJobs) {
                if(pkn.equals(job.getPackageName()) && job.isEnable()) {
                    job.onReceive(event);
                }
            }
        }
    }

    public static boolean isRun() {
        return isRun;
    }

    private String eventToString(AccessibilityEvent event) {
        StringBuilder builder = new StringBuilder();
        String eventType = "";
        switch (event.getEventType()){
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED: // 通知栏状态变化
                eventType = "TYPE_NOTIFICATION_STATE_CHANGED";
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED: // 窗口状态变化
                eventType = "TYPE_WINDOW_STATE_CHANGED";
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:  // 窗口内容变化
                eventType = "TYPE_WINDOW_CONTENT_CHANGED";
                break;
        }
        builder.append("EventType: ").append(eventType);
        builder.append("; ClassName: ").append(event.getClassName());
        builder.append("; Text: ").append(event.getText());
        builder.append("; ContentDescription: ").append(event.getContentDescription());
        builder.append("; ItemCount: ").append(event.getItemCount());
        builder.append("; ParcelableData: ").append(event.getParcelableData());
        return builder.toString();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {

    }
}
