package com.ctao.qhb.utils;

import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.PowerManager;
import android.os.Vibrator;

import com.ctao.baselib.utils.LogUtils;
import com.ctao.qhb.App;
import com.ctao.qhb.Config;
import com.ctao.qhb.R;

import java.util.Calendar;

/**
 * Created by A Miracle on 2017/8/17.
 */
public class NotifyUtils {

    private static Vibrator sVibrator;
    private static KeyguardManager sKeyguardManager; // 键盘锁管理器对象
    private static PowerManager sPowerManager; // 电源管理器对象

    /** 是否为锁屏或黑屏状态*/
    public static boolean isLockScreen() {
        KeyguardManager km = getKeyguardManager();
        boolean b = km.inKeyguardRestrictedInputMode();
        boolean screenOn = !isScreenOn();
        LogUtils.printOut(b+", "+screenOn);
        return b || screenOn;
    }

    public static void wakeAndUnlock() {
        //获取电源管理器对象
        PowerManager pm = getPowerManager();

        //获取PowerManager.WakeLock对象，后面的参数|表示同时传入两个值，最后的是调试用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");

        //点亮屏幕
        wl.acquire(1000);

        //得到键盘锁管理器对象
        KeyguardManager km = getKeyguardManager();
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");

        //解锁
        kl.disableKeyguard();
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
    public static boolean isScreenOn() {
        PowerManager pm = getPowerManager();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            return pm.isInteractive();
        } else {
            return pm.isScreenOn();
        }
    }

    /** 播放效果、声音与震动*/
    public static void playEffect(Context context, Config config) {
        // 夜间模式，不处理
        if(isNightTime() && config.isNightNotDisturb()) {
            return;
        }

        if(config.isVoice()) {
            try {
                MediaPlayer player = MediaPlayer.create(App.getApp(), R.raw.prompt);
                if(player != null){
                    player.start();
                }
            } catch (Exception e) {
                LogUtils.e(e);
            }
        }
        if(config.isVibration()) {
            if(sVibrator == null) {
                sVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            }
            sVibrator.vibrate(new long[]{100, 10, 100, 1000}, -1);
        }
    }

    /** 是否为夜间*/
    public static  boolean isNightTime() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if(hour >= 23 || hour < 7) {
            return true;
        }
        return false;
    }

    public static PowerManager getPowerManager() {
        if(sPowerManager == null) {
            sPowerManager = (PowerManager) App.getApp().getSystemService(Context.POWER_SERVICE);
        }
        return sPowerManager;
    }

    public static KeyguardManager getKeyguardManager() {
        if(sKeyguardManager == null) {
            sKeyguardManager = (KeyguardManager) App.getApp().getSystemService(Context.KEYGUARD_SERVICE);
        }
        return sKeyguardManager;
    }
}
