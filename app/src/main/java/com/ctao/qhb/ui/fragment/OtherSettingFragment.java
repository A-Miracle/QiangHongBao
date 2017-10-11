package com.ctao.qhb.ui.fragment;

import android.os.Bundle;
import android.preference.Preference;
import android.view.View;

import com.ctao.qhb.Config;
import com.ctao.qhb.R;
import com.ctao.qhb.ui.common.PreferenceFragment;

/**
 * Created by A Miracle on 2017/8/25.
 */
public class OtherSettingFragment extends PreferenceFragment{

    @Override
    public String getTitle() {
        return "全局设置";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_other_setting);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        invalidateSettings();
    }

    private void invalidateSettings() {

        // 锁屏自动抢
        findPreference("lock_screen_automatic_grab").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Config.getInstance().changeGlobal(Config.SP_LOCK_SCREEN_ROB);
                return true;
            }
        });

        // 声音
        findPreference("voice").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Config.getInstance().changeGlobal(Config.SP_VOICE);
                return true;
            }
        });

        // 震动
        findPreference("vibration").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Config.getInstance().changeGlobal(Config.SP_VIBRATION);
                return true;
            }
        });

        // 夜间免打扰
        findPreference("night_not_disturb").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Config.getInstance().changeGlobal(Config.SP_NIGHT_NOT_DISTURB);
                return true;
            }
        });
    }
}
