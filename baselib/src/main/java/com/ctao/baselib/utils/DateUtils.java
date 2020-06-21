package com.ctao.baselib.utils;

import java.text.SimpleDateFormat;

/**
 * Created by A Miracle on 2017/7/7.
 */
public class DateUtils {

    public static String formatTime(long time, String format){
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(time);
    }
}
