package com.afollestad.appthemeengine.customizers;


import androidx.annotation.StyleRes;

/**
 * @author Aidan Follestad (afollestad)
 */
public interface ATEActivityThemeCustomizer {

    @StyleRes
    int getActivityTheme();
}