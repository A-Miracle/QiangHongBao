package com.ctao.qhb.network;

/**
 * Created by A Miracle on 2017/5/14.
 */
public interface Callable{
    interface Callback<Result> {
        void onSucceed(Result result);
        void onFailure(Throwable t);
    }
}
