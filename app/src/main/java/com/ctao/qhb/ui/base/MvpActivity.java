package com.ctao.qhb.ui.base;

import android.support.annotation.Nullable;

import com.ctao.baselib.ui.BaseActivity;
import com.ctao.qhb.R;
import com.ctao.qhb.event.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by A Miracle on 2017/6/24.
 */
public abstract class MvpActivity extends BaseActivity{

    private Unbinder unbinder;

    @Override
    protected void bindButterKnife() {
        unbinder = ButterKnife.bind(this);
    }

    @Override
    protected void unbindButterKnife() {
        unbinder.unbind();
    }

    @Override
    protected void registerEventBus() {
        EventBus.getDefault().register(this);
    }

    @Override
    protected void unregisterEventBus() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initData() {
        super.initData();
        themeColor = getResources().getColor(R.color.colorPrimaryDark);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {

    }

    @Nullable
    @Override
    public String getATEKey() {
        return "default_theme";
    }
}
