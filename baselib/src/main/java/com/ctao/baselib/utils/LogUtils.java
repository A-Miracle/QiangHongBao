package com.ctao.baselib.utils;


import android.text.TextUtils;

import com.ctao.baselib.lib.logger.LogAdapter;
import com.ctao.baselib.lib.logger.Logger;
import com.ctao.baselib.lib.logger.Settings;

/**
 * Created by A Miracle on 2016/9/19.
 */
public class LogUtils {
    public static String LOGTAG = "Admin";
    public static boolean LOG_DEBUG = true;

    /**
     * 初始化Log
     * @param isDebug 是否Debug环境
     * @param tag 全局的统一默认Tag
     * @param logAdapter 正式环境下 , LogAdapter
     */
    public static void init(boolean isDebug, String tag, LogAdapter logAdapter){
        LOG_DEBUG = isDebug;
        if(!TextUtils.isEmpty(tag)){
            LOGTAG = tag;
        }
        Settings settings = Logger.init(LOGTAG);
        if(!LOG_DEBUG && null != logAdapter){ //正式环境, 将Log写入文件
            settings.logAdapter(logAdapter); //default AndroidLogAdapter
        }
    }

    public static void v(String message) {
        Logger.v(message);
    }

    public static void d(String message) {
        Logger.d(message);
    }

    public static void d(Object object) {
        Logger.d(object);
    }

    public static void i(String message) {
        Logger.i(message);
    }

    public static void w(String message) {
        Logger.w(message);
    }

    public static void wtf(String message) {
        Logger.wtf(message);
    }

    public static void e(String message) {
        Logger.e(message);
    }

    public static void e(Throwable throwable) {
        Logger.e(throwable, "");
    }

    public static void e(String message, Throwable throwable) {
        Logger.e(throwable, message);
    }

    public static void v(String tag, String message) {
        Logger.t(tag).v(message);
    }

    public static void d(String tag, String message) {
        Logger.t(tag).d(message);
    }

    public static void d(String tag, Object object) {
        Logger.t(tag).d(object);
    }

    public static void i(String tag, String message) {
        Logger.t(tag).i(message);
    }

    public static void w(String tag, String message) {
        Logger.t(tag).w(message);
    }

    public static void wtf(String tag, String message) {
        Logger.t(tag).wtf(message);
    }

    public static void e(String tag, String message) {
        Logger.t(tag).e(message);
    }

    public static void e(String tag, String message, Throwable throwable) {
        Logger.t(tag).e(throwable, message);
    }

    public static void printOut(String message){
        if(LOG_DEBUG){
            System.out.println(">>> "+message);
        }
    }
    public static void printErr(String message){
        if(LOG_DEBUG){
            System.err.println(">>> "+message);
        }
    }

    public static void printOut(String tag, String message) {
        printOut(tag + " : " + message);
    }

    public static void printErr(String tag, String message) {
        printErr(tag + " : " + message);
    }

}
