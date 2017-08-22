package com.ctao.baselib.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ctao.baselib.R;
import com.ctao.baselib.utils.LogUtils;
import com.ctao.baselib.utils.ViewUtils;

/**
 * Created by A Miracle on 2017/6/24.
 * 模板方法模式
 */
public abstract class BaseFragment extends Fragment {
    protected View rootView;

    @Override
    public void onDestroyView() {
        unregisterEventBus();
        super.onDestroyView();
        LogUtils.printOut("leak : " + getClass().getSimpleName() + ".onDestroyView() >>> ");
    }

    @Override
    public void onDestroy() {
        unbindButterKnife();
        super.onDestroy();
        LogUtils.printOut("leak : " + getClass().getSimpleName() + ".onDestroy() >>> ");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        registerEventBus();
        if (rootView == null){
            rootView = createView();
            if(rootView == null){
                rootView = inflater.inflate(getLayoutId(), null, false);
            }

            bindButterKnife(); // 通过注解绑定控件
            initView();
            initListener();
        }else{
            ViewUtils.removeSelfFromParent(rootView);
        }
        return rootView;
    }

    /**注册EventBus*/
    protected void registerEventBus() {}

    /**反注册EventBus*/
    protected void unregisterEventBus() {}

    /**获取视图返回View*/
    protected View createView(){ return null; }

    /**获取 Layout Id*/
    protected abstract int getLayoutId();

    /**通过注解绑定控件, 在使用ButterKnife可能的情况下*/
    protected void bindButterKnife() { }

    /**通过注解绑定控件, 在使用ButterKnife可能的情况下*/
    protected void unbindButterKnife() { }

    /**初始化View*/
    protected abstract void initView();

    protected void initListener() { }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
        getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
}
