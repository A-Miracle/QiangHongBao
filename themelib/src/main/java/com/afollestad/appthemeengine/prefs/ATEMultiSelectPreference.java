package com.afollestad.appthemeengine.prefs;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.View;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.appthemeengine.Config;
import com.afollestad.appthemeengine.R;

/**
 * @author Aidan Follestad (afollestad)
 */
public class ATEMultiSelectPreference extends ListPreference {

    public ATEMultiSelectPreference(Context context) {
        super(context);
        init(context, null);
    }

    public ATEMultiSelectPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ATEMultiSelectPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public ATEMultiSelectPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private String mKey;

    private void init(Context context, AttributeSet attrs) {
        setLayoutResource(R.layout.ate_preference_custom);

        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ATEMultiSelectPreference, 0, 0);
            try {
                mKey = a.getString(R.styleable.ATEMultiSelectPreference_ateKey_pref_multiSelect);
            } finally {
                a.recycle();
            }
        }

        if (!Config.usingMaterialDialogs(context, mKey)) {
            ATE.config(context, mKey)
                    .usingMaterialDialogs(true)
                    .commit();
        }
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        ATE.apply(view, mKey);
    }
}
