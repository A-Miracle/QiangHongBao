package com.ctao.qhb.interact.contract;


import com.ctao.qhb.interact.IBasePresenter;
import com.ctao.qhb.interact.IBaseView;
import com.ctao.qhb.interact.model.Update;
import com.ctao.qhb.interact.view.ILoadingView;

import java.io.File;

/**
 * View 与 Presenter 之间的 Contract
 */
public interface IUpdateContract {
    interface View extends IBaseView<Presenter>, ILoadingView {
        void checkUpdate(Update update);
        void downloadComplete(File file);
        void downloadProgress(int progress);
    }

    interface Presenter extends IBasePresenter {
        void checkUpdate();
        void downloadApk(String fileName);
    }
}
