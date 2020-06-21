package com.afollestad.appthemeengine.processors;

import android.content.Context;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.afollestad.appthemeengine.Config;
import com.afollestad.appthemeengine.util.EdgeGlowUtil;

/**
 * @author Aidan Follestad (afollestad)
 */
public class ListViewProcessor implements Processor<ListView, Void> {

    @Override
    public void process(@NonNull Context context, @Nullable String key, @Nullable ListView target, @Nullable Void extra) {
        if (target == null) return;
        EdgeGlowUtil.setEdgeGlowColor(target, Config.accentColor(context, key));
    }
}