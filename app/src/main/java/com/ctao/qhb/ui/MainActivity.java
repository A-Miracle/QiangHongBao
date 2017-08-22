package com.ctao.qhb.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ctao.baselib.manager.AppManager;
import com.ctao.baselib.utils.LogUtils;
import com.ctao.baselib.utils.ToastUtils;
import com.ctao.qhb.Config;
import com.ctao.qhb.R;
import com.ctao.qhb.event.MessageEvent;
import com.ctao.qhb.service.QHBService;
import com.ctao.qhb.ui.base.MvpActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by A Miracle on 2017/8/15.
 */
public class MainActivity extends MvpActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.bt_state) Button bt_state;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onAfterSetContentLayout(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        changeState();
        getFragmentManager().beginTransaction().replace(R.id.fl_container, new MainFragment()).commit();
    }

    @OnClick(R.id.bt_state)
    public void onClick(View view){
        if(!QHBService.isRun()){
            openAccessibilityServiceSettings();
        }
    }

    @Override
    public void onMessageEvent(MessageEvent event) {
        super.onMessageEvent(event);
        switch (event.getType()){
            case MessageEvent.QHB_SERVICE_STATE:
                changeState();
                break;
        }
    }

    /** 打开辅助服务的设置 */
    private void openAccessibilityServiceSettings() {
        try {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
            ToastUtils.show("找到[快手抢红包], 然后开启服务即可");
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    private void changeState() {
        if(QHBService.isRun()){
            bt_state.setText("已连接抢红包服务");
        }else{
            bt_state.setText("抢红包服务中断, 点击开启");
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
    }

    public static class MainFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference_main);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            invalidateSettings();
        }

        private void invalidateSettings() {

            // 微信开关
            findPreference("platform_WeChat").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                   Config.getInstance().changeEnable(Config.SP_ENABLE_WE_CHAT);
                    return true;
                }
            });

            // 微信设置
            findPreference("platform_WeChat_setting").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    return true;
                }
            });

            // 其他设置
            findPreference("setting").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    return true;
                }
            });

            // 退出服务
            findPreference("exit").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if(!QHBService.isRun()){
                        exit();
                        return true;
                    }
                    new MaterialDialog.Builder(getActivity())
                            .title("退出服务")
                            .content("找到[快手抢红包], 然后关闭服务再退出即可")
                            .negativeText("取消")
                            .positiveText("确认")
                            .onAny(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    switch (which) {
                                        case POSITIVE:
                                            getActivity().startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                                            break;
                                    }

                                }
                            })
                            .show();
                    return true;
                }
            });

            // 关于
            findPreference("about").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new MaterialDialog.Builder(getActivity())
                            .title("关于")
                            .content("作者很懒, 什么也没留下!")
                            .positiveText("确认")
                            .show();
                    return true;
                }
            });
        }

        private void exit(){
            AppManager.getInstance().exitApp(true);
        }
    }
}
