package com.ctao.qhb.job;

import android.view.accessibility.AccessibilityEvent;

import com.ctao.qhb.service.QHBService;

/**
 * Created by A Miracle on 2017/8/17.
 */
public interface IAccessibilityJob {
    String getPackageName();
    void onCreate(QHBService service);
    void onReceive(AccessibilityEvent event);
    void onDestroy();
    boolean isEnable();
}
