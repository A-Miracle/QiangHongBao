package com.ctao.qhb.ui.fragment;

import android.os.Bundle;
import android.preference.Preference;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ctao.baselib.utils.ToastUtils;
import com.ctao.qhb.App;
import com.ctao.qhb.Config;
import com.ctao.qhb.R;
import com.ctao.qhb.prefs.ATEPreference;
import com.ctao.qhb.ui.common.PreferenceFragment;

import java.math.BigDecimal;
import java.text.MessageFormat;

/**
 * Created by A Miracle on 2017/8/24.
 */
public class QQSettingFragment extends PreferenceFragment{

    @Override
    public String getTitle() {
        return App.getApp().getString(R.string.qq_setting);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_qq_setting);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        invalidateSettings();
    }

    private void invalidateSettings() {
        // 抢红包模式
        final ATEPreference model = (ATEPreference) findPreference("model");
        final String[] array = getActivity().getResources().getStringArray(R.array.qhb_mode);
        model.setSummary(array[Config.getInstance().getQQMode()]);
        model.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.qhb_mode)
                        .items(array)
                        .itemsCallbackSingleChoice(Config.getInstance().getQQMode(), new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                model.setSummary(array[which]);
                                Config.getInstance().setQQMode(which);
                                return true;
                            }
                        })
                        .positiveText(R.string.confirm)
                        .show();
                return true;
            }
        });

        // 延时抢
        final ATEPreference delay_time = (ATEPreference) findPreference("delay_time");
        final float delayTime = Config.getInstance().getQQDelayTime();
        final String qhb_delay_no = App.getApp().getString(R.string.qhb_delay_no);
        final String qhb_delay_time = App.getApp().getString(R.string.qhb_delay_time);
        String summary;
        if (new BigDecimal(delayTime).compareTo(new BigDecimal("0")) == 0) {
            summary = qhb_delay_no;
        } else {
            summary = MessageFormat.format(qhb_delay_time, delayTime);
        }
        delay_time.setSummary(summary);
        delay_time.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.qhb_delay_rob)
                        .content(R.string.qhb_delay_rob_hint)
                        .inputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL)
                        .input(null, Config.getInstance().getQQDelayTime() + "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                if(TextUtils.isEmpty(input) || ".".equals(input.toString())){
                                    input = "0";
                                }
                                String value = input.toString();
                                if(new BigDecimal(value).add(new BigDecimal(-10)).compareTo(new BigDecimal("0")) > 0){
                                    value = "10";
                                    ToastUtils.show("最大值不超过10s");
                                }
                                String summary;
                                if(new BigDecimal(value).compareTo(new BigDecimal("0")) == 0){
                                    summary = qhb_delay_no;
                                } else {
                                    summary = MessageFormat.format(qhb_delay_time, value);
                                }
                                delay_time.setSummary(summary);
                                Config.getInstance().setQQDelayTime(Float.parseFloat(value));
                            }
                        }).show();
                return true;
            }
        });

        // 智能返回
        final Preference smart_back_qq = findPreference("smart_back_qq");
        final String[] back = getActivity().getResources().getStringArray(R.array.qhb_back);
        boolean real = Config.getInstance().isSmartBackQQ();
        smart_back_qq.setSummary(back[real ? 1 : 0]);
        smart_back_qq.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.qq_receive_after)
                        .items(back)
                        .itemsCallbackSingleChoice(Config.getInstance().isSmartBackQQ() ? 1 : 0, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                smart_back_qq.setSummary(back[which]);
                                Config.getInstance().changeSmartBack(Config.SP_ENABLE_QQ);
                                return true;
                            }
                        })
                        .positiveText(R.string.confirm)
                        .show();
                return true;
            }
        });

        // 口令红包
        findPreference("qq_word_setting").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Config.getInstance().changeWordQQ();
                return true;
            }
        });
    }
}
