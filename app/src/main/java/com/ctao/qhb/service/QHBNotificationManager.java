package com.ctao.qhb.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;

import com.ctao.qhb.R;
import com.ctao.qhb.event.MessageEvent;
import com.ctao.qhb.ui.MainActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by A Miracle on 2017/8/20.
 */
class QHBNotificationManager {
    private static final int NOTIFICATION_ID = 404;
    private static final int REQUEST_CODE = 100;
    private final QHBService mService;
    private final NotificationManagerCompat mNotificationManager;

    private boolean mStarted = false;
    public QHBNotificationManager(QHBService service) {
        mService = service;
        mNotificationManager = NotificationManagerCompat.from(service);

        //取消所有通知服务被杀和处理情况
        //重新启动系统。
        mNotificationManager.cancel(NOTIFICATION_ID);
    }

    /** 发布通知并开始跟踪会话保持更新。通知将自动删除如果会话被摧毁之前{ @link # stopNotification }。 */
    public void startNotification() {
        if (!mStarted) {
            EventBus.getDefault().register(this);
            // 通知必须更新后将开始设置为true
            Notification notification = createNotificationBuilder().build();
            if (notification != null) {
                mNotificationManager.notify(NOTIFICATION_ID, notification);
                mService.startForeground(NOTIFICATION_ID, notification);
                mStarted = true;
            }
        }
    }

    /**
     * 删除通知和停止跟踪会话。如果会话被摧毁这没有影响。
     */
    public void stopNotification() {
        if (mStarted) {
            EventBus.getDefault().unregister(this);
            mStarted = false;
            mNotificationManager.cancel(NOTIFICATION_ID);
            mService.stopForeground(true);
        }
    }


    private NotificationCompat.Builder createNotificationBuilder() {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mService);

        notificationBuilder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setUsesChronometer(true)
                .setContentIntent(createContentIntent())
                .setContentTitle("快手抢红包");

        // 播放持续时间, 不显示了
        notificationBuilder.setWhen(0)
                .setShowWhen(false)
                .setUsesChronometer(false);

        // 确保通知可以被用户当我们不玩:
        notificationBuilder.setOngoing(mService.isRun());

        return notificationBuilder;
    }

    private PendingIntent createContentIntent() {
        Intent openUI = new Intent(mService, MainActivity.class);
        openUI.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(mService, REQUEST_CODE, openUI,
                PendingIntent.FLAG_CANCEL_CURRENT);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.getType()){
            case MessageEvent.QHB_SERVICE_STATE:
                boolean param = (boolean) event.getParam();
                NotificationCompat.Builder builder = createNotificationBuilder();
                if(param){
                    builder.setContentText("已连接抢红包服务");
                }else {
                    builder.setContentText("抢红包服务中断");
                }
                mNotificationManager.notify(NOTIFICATION_ID, builder.build());
                break;
        }
    }
}
