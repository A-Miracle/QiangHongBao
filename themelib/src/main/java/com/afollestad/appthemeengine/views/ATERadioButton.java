package com.afollestad.appthemeengine.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatRadioButton;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.appthemeengine.R;

/**
 * @author Aidan Follestad (afollestad)
 */
@PreMadeView
public class ATERadioButton extends AppCompatRadioButton {

    public ATERadioButton(Context context) {
        super(context);
        init(context, null);
    }

    public ATERadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ATERadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setTag("tint_accent_color,text_primary");
        String key = null;
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ATERadioButton, 0, 0);
            try {
                key = a.getString(R.styleable.ATERadioButton_ateKey_radioButton);
            } finally {
                a.recycle();
            }
        }
        ATE.apply(context, this, key);
    }
}
