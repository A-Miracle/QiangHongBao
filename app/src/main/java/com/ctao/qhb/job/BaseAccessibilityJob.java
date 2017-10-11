package com.ctao.qhb.job;

import android.content.Context;

import com.ctao.baselib.Global;
import com.ctao.qhb.Config;
import com.ctao.qhb.service.QHBService;

/**
 * Created by A Miracle on 2017/8/17.
 */
public abstract class BaseAccessibilityJob implements IAccessibilityJob {

    protected QHBService mService;

    @Override
    public void onCreate(QHBService service) {
        mService = service;
    }

    public Config config() {
        return Config.getInstance();
    }

    public Context context() {
        return Global.getContext();
    }

    @Override
    public void onDestroy() { }
}
