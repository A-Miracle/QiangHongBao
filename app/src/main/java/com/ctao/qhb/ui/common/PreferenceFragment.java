package com.ctao.qhb.ui.common;

import android.view.View;

/**
 * Created by A Miracle on 2017/8/24.
 */
public abstract class PreferenceFragment extends android.preference.PreferenceFragment{
    public abstract String getTitle();
    public void initMenu(View contentView) {}
    public void initOtherOnCreateInLast(View contentView) {}
    public void onFinish() {}
}
