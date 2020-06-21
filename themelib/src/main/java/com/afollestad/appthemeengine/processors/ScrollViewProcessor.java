package com.afollestad.appthemeengine.processors;

import android.content.Context;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.afollestad.appthemeengine.Config;
import com.afollestad.appthemeengine.util.EdgeGlowUtil;

/**
 * @author Aidan Follestad (afollestad)
 */
public class ScrollViewProcessor implements Processor<ScrollView, Void> {

    @Override
    public void process(@NonNull Context context, @Nullable String key, @Nullable ScrollView target, @Nullable Void extra) {
        if (target == null) return;
        EdgeGlowUtil.setEdgeGlowColor(target, Config.accentColor(context, key));
    }
}
