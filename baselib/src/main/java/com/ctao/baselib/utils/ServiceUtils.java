package com.ctao.baselib.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by A Miracle on 2017/6/27.
 */
public class ServiceUtils {
    /**
     * 判断服务是否正在运行
     * @param context
     * @param serviceName  服务的全类名
     * @return
     */
    public static boolean isRunning(Context context, String serviceName) {
        // ActivityManager 进程的管理者
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(1000); // 参数
        for (ActivityManager.RunningServiceInfo info : runningServices) {
            String className = info.service.getClassName();
            if (serviceName.equals(className)) {
                return true;
            }
        }
        return false;
    }
}
