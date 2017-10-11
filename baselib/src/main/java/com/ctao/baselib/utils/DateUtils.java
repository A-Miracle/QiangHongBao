package com.ctao.baselib.utils;

import android.text.TextUtils;

import java.text.SimpleDateFormat;

/**
 * Created by A Miracle on 2017/7/7.
 */
public class DateUtils {

    public static String formatTime(long time, String format){
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(time);
    }

    public static String getNotNull(String str){
        if(TextUtils.isEmpty(str) || "null".equals(str)){
            return "";
        }
        return str;
    }

    public static int getIntStr(String str){
        if(!TextUtils.isEmpty(str) && TextUtils.isDigitsOnly(str)){
            return Integer.valueOf(str);
        }
        return 0;
    }

    public static long getLongStr(String str){
        if(!TextUtils.isEmpty(str) && TextUtils.isDigitsOnly(str)){
            return Long.valueOf(str);
        }
        return 0l;
    }

    public static boolean getBooleanStr(String str){
        if("true".equals(str)){
            return true;
        }
        return false;
    }
}
