package com.ctao.qhb.ui.base;

import com.ctao.baselib.ui.BaseFragment;
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
public abstract class MvpFragment extends BaseFragment implements ILoadingView{
    private Unbinder unbinder;

    @Override
    protected void bindButterKnife() {
        unbinder = ButterKnife.bind(this, rootView);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {

    }

    @Override
    public void showProgress() {
        if(getActivity() instanceof MvpActivity){
            ((MvpActivity)getActivity()).showProgress();
        }
    }

    @Override
    public void hideProgress() {
        if(getActivity() instanceof MvpActivity){
            ((MvpActivity)getActivity()).hideProgress();
        }
    }

    @Override
    public void showFailure(String msg, String... tag) {
        if(getActivity() instanceof MvpActivity){
            ((MvpActivity)getActivity()).showFailure(msg, tag);
        }
    }
}
