package com.afollestad.appthemeengine.customizers;


import androidx.annotation.ColorInt;

import com.afollestad.appthemeengine.Config;

/**
 * @author Aidan Follestad (afollestad)
 */
public interface ATEToolbarCustomizer {

    @Config.LightToolbarMode
    int getLightToolbarMode();

    @ColorInt
    int getToolbarColor();
}
