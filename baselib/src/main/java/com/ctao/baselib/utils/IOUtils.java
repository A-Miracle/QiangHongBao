package com.ctao.baselib.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by A Miracle on 2016/9/26.
 */
public class IOUtils {

    /** 关闭流 */
    public static boolean close(Closeable io) {
        if (io != null) {
            try {
                io.close();
            } catch (IOException e) {
                LogUtils.e(IOUtils.class.getSimpleName(), e);
            }
        }
        return true;
    }
}
