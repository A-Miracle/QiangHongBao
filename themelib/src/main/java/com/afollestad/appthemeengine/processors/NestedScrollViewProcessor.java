package com.afollestad.appthemeengine.processors;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import com.afollestad.appthemeengine.Config;
import com.afollestad.appthemeengine.util.EdgeGlowUtil;

/**
 * @author Aidan Follestad (afollestad)
 */
public class NestedScrollViewProcessor implements Processor<NestedScrollView, Void> {

    @Override
    public void process(@NonNull Context context, @Nullable String key, @Nullable NestedScrollView target, @Nullable Void extra) {
        if (target == null) return;
        EdgeGlowUtil.setEdgeGlowColor(target, Config.accentColor(context, key));
    }
}
