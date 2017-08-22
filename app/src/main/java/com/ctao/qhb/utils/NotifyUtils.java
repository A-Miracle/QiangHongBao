package com.ctao.qhb.utils;

import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.os.PowerManager;

import com.ctao.baselib.utils.LogUtils;

/**
 * Created by A Miracle on 2017/8/17.
 */
public class NotifyUtils {
    private static KeyguardManager sKeyguardManager;
    private static PowerManager sPowerManager;

    /** 是否为锁屏或黑屏状态*/
    public static boolean isLockScreen(Context context) {
        KeyguardManager km = getKeyguardManager(context);
        return km.inKeyguardRestrictedInputMode() || !isScreenOn(context);
    }

    /** 执行PendingIntent事件*/
    public static void send(PendingIntent pendingIntent) {
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            LogUtils.e(e);
        }
    }

    /** 显示通知*/
    public static void showNotify(Context context, String title, PendingIntent pendingIntent) {
        /*NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setUsesChronometer(true)
                .setContentIntent(pendingIntent)
                .setContentTitle(title);

        // 播放持续时间, 不显示了
        builder.setWhen(0)
                .setShowWhen(false)
                .setUsesChronometer(false);

        // 确保通知可以被用户当我们不玩:
        builder.setOngoing(false);

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build());*/

        // 没想到较好的办法清除 notify
    }

    /** 屏幕是否处于活动状态 */
    public static boolean isScreenOn(Context context) {
        PowerManager pm = getPowerManager(context);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            return pm.isInteractive();
        } else {
            return pm.isScreenOn();
        }
    }

    public static PowerManager getPowerManager(Context context) {
        if(sPowerManager == null) {
            sPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        }
        return sPowerManager;
    }

    public static KeyguardManager getKeyguardManager(Context context) {
        if(sKeyguardManager == null) {
            sKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        }
        return sKeyguardManager;
    }
}
