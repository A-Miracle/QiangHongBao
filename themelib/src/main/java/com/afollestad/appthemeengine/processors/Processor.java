package com.afollestad.appthemeengine.processors;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author Aidan Follestad (afollestad)
 */
public interface Processor<T extends View, E> {

    void process(@NonNull Context context, @Nullable String key, @Nullable T target, @Nullable E extra);
}