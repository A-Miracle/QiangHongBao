package com.afollestad.appthemeengine.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.appthemeengine.R;

/**
 * @author Aidan Follestad (afollestad)
 */
@PreMadeView
public class ATESecondaryTextView extends AppCompatTextView {

    public ATESecondaryTextView(Context context) {
        super(context);
        init(context, null);
    }

    public ATESecondaryTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ATESecondaryTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setTag("text_secondary");
        String key = null;
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ATESecondaryTextView, 0, 0);
            try {
                key = a.getString(R.styleable.ATESecondaryTextView_ateKey_secondaryTextView);
            } finally {
                a.recycle();
            }
        }
        ATE.apply(context, this, key);
    }
}
