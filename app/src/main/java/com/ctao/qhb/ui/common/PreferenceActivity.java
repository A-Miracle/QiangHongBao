package com.ctao.qhb.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import com.ctao.baselib.utils.LogUtils;
import com.ctao.qhb.R;
import com.ctao.qhb.ui.base.MvpActivity;

import java.io.Serializable;

import butterknife.BindView;

/**
 * Created by A Miracle on 2017/8/24.
 */
public class PreferenceActivity extends MvpActivity{
    public static final String TYPE_FRAGMENT = "TYPE_FRAGMENT";
    public static final String TYPE_TITLE = "TYPE_TITLE";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private String title;
    private PreferenceFragment mFragment;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_common;
    }

    @Override
    protected void onAfterSetContentLayout(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if(intent != null){
            title = intent.getStringExtra(TYPE_TITLE);
            Serializable extra = intent.getSerializableExtra(TYPE_FRAGMENT);
            if(extra instanceof Class){
                Class<PreferenceFragment> clazz = (Class<PreferenceFragment>) extra;
                try {
                    mFragment = clazz.newInstance();
                } catch (InstantiationException e) {
                    LogUtils.e(e);
                } catch (IllegalAccessException e) {
                    LogUtils.e(e);
                }
            }
        }

        if(mFragment == null){
            return;
        }
        getFragmentManager().beginTransaction().replace(R.id.fl_container, mFragment, mFragment.getClass().getSimpleName()).commit();

        if(!TextUtils.isEmpty(title)){
            mToolbar.setTitle(title);
        }else{
            mToolbar.setTitle(mFragment.getTitle());
        }

        initMenu();
    }

    protected void initMenu() {
        if(mFragment != null){
            mFragment.initMenu(getContentView());
        }
    }

    @Override
    protected void initOtherOnCreateInLast() {
        if(mFragment != null){
            mFragment.initOtherOnCreateInLast(getContentView());
        }
    }


    @Override
    protected Toolbar getBackToolBar() {
        return mToolbar;
    }

    @Override
    public void finish() {
        if(mFragment != null){
            mFragment.onFinish();
        }
        LogUtils.printOut("CommonActivity.finish : mFragment == " + mFragment);
        super.finish();
    }
}
