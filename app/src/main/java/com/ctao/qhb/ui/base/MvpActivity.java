package com.ctao.qhb.ui.base;


import androidx.annotation.Nullable;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ctao.baselib.ui.BaseActivity;
import com.ctao.baselib.utils.ToastUtils;
import com.ctao.qhb.R;
import com.ctao.qhb.event.MessageEvent;
import com.ctao.qhb.interact.view.ILoadingView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by A Miracle on 2017/6/24.
 */
public abstract class MvpActivity extends BaseActivity implements ILoadingView{

    private MaterialDialog mDialog;
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

    @Override
    public void showProgress() {
        if (mDialog == null) {
            mDialog = new MaterialDialog.Builder(this)
                    .content("加载中...")
                    .progress(true, 0)
                    .cancelable(false)
                    .canceledOnTouchOutside(false)
                    .build();
        }
        mDialog.show();
    }

    @Override
    public void hideProgress() {
        if(mDialog != null){
            mDialog.dismiss();
            mDialog = null;
        }
    }

    @Override
    public void showFailure(String msg, String... tag) {
        ToastUtils.show(msg);
    }
}
