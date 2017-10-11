package com.ctao.qhb.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.ctao.baselib.utils.LogUtils;
import com.ctao.qhb.App;

/**
 * Created by A Miracle on 2017/8/31.
 */
public class PackageUtils {

    public static PackageInfo getPackageInfo(String packageName){
        try {
            return App.getApp().getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.e(e);
        }
        return null;
    }
}
