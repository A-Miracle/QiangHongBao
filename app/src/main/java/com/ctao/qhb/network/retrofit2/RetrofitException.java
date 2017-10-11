package com.ctao.qhb.network.retrofit2;

/**
 * Created by A Miracle on 2017/5/16.
 */
public class RetrofitException extends Exception {
    public RetrofitException() {
    }

    public RetrofitException(String message) {
        super(message);
    }

    public RetrofitException(String message, Throwable cause) {
        super(message, cause);
    }

    public RetrofitException(Throwable cause) {
        super(cause);
    }
}
