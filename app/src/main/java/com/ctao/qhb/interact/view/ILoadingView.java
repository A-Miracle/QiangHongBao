package com.ctao.qhb.interact.view;

/**
 * Created by A Miracle on 2016/11/24.
 */
public interface ILoadingView {
    void showProgress();
    void hideProgress();
    void showFailure(String msg, String... tag);
}

