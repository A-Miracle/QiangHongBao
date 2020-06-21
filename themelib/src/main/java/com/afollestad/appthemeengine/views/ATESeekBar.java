package com.afollestad.appthemeengine.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatSeekBar;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.appthemeengine.R;

/**
 * @author Aidan Follestad (afollestad)
 */
@PreMadeView
public class ATESeekBar extends AppCompatSeekBar {

    public ATESeekBar(Context context) {
        super(context);
        init(context, null);
    }

    public ATESeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ATESeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setTag("tint_accent_color");
        String key = null;
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ATESeekBar, 0, 0);
            try {
                key = a.getString(R.styleable.ATESeekBar_ateKey_seekBar);
            } finally {
                a.recycle();
            }
        }
        ATE.apply(context, this, key);
    }
}
