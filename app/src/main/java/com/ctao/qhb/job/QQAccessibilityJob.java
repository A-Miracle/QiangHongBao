package com.ctao.qhb.job;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.ctao.baselib.Global;
import com.ctao.baselib.utils.LogUtils;
import com.ctao.qhb.Config;
import com.ctao.qhb.utils.AccessibilityUtils;
import com.ctao.qhb.utils.NotifyUtils;

import java.util.List;

/**
 * Created by A Miracle on 2017/8/30.
 */
public class QQAccessibilityJob extends BaseAccessibilityJob{
    private static final String TAG = "QQAccessibilityJob";
    private static final long SILENCE_TIME = 999;

    /** 红包消息的关键字 */
    private static final String KEY = "[QQ红包]";
    private static final int APP_STATE_BACKGROUND = -1;

    private static final int APP_STATE_FOREGROUND = 1;
    private static final int WINDOW_SPLASH = 1; // CLASSNAME_1
    private static final int WINDOW_QWALLET_PLUGIN_PROXY = 2; // CLASSNAME_2
    private static final int WINDOW_OTHER = -1;

    private int mAppState = APP_STATE_FOREGROUND;
    private int mCurrentWindow = WINDOW_OTHER;
    private boolean isReceived; // 收到红包

    // QQ7.1.8 versionCode = 718;-----------------------------------
    private final static String CONTENT_DESC = "点击查看详情"; // 非口令红包
    private final static String CONTENT_DESC_WORD = "点击领取口令"; // 口令红包
    private final static String SEARCH = "点击拆开"; // 非口令红包
    private final static String SEARCH_WORD = "口令红包"; // 口令红包
    private final static String SEARCH_WORD_OPENED = "口令红包已拆开"; // 口令红包
    private final static String KEY_WORD = "口令:";
    private final static String KEY_LOOK = "查看领取详情";

    private final static String CLASSNAME_1 = "com.tencent.mobileqq.activity.SplashActivity"; // 聊天页
    private final static String CLASSNAME_2 = "cooperation.qwallet.plugin.QWalletPluginProxyActivity"; // 已拆红包

    private static String ID_SUB_NAME = "com.tencent.mobileqq:id/title_sub"; // 子标题(有子标题才是单聊)
    private static String ID_CALL_NAME = "com.tencent.mobileqq:id/ivTitleBtnRightCall"; // 电话(有电话可能是单聊或讨论组)
    private static String ID_MSG_BOX = "com.tencent.mobileqq:id/msgbox"; // msg box

    private static String ID_LIST_MSG_ITEM = "com.tencent.mobileqq:id/relativeItem"; // 聊天记录列表中Item
    private static String ID_LIST_MSG_RED = "com.tencent.mobileqq:id/unreadmsg"; // 聊天记录列表中Item小圆点
    private static String ID_LIST_MSG_LABEL = "com.tencent.mobileqq:id/name"; // 聊天记录列表中Item最新消息(还好这里只有一个name的Id)
    private static String ID_LIST_MSG = "com.tencent.mobileqq:id/recent_chat_list"; // 聊天记录列表

    private static String ID_LIST_CHAT = "com.tencent.mobileqq:id/listView1"; // 当前会话列表
    private static String ID_LIST_CHAT_ITEM = "com.tencent.mobileqq:id/chat_item_content_layout"; // ItemView, clickable=false; content-desc;

    private static String ID_LIST_CHAT_INPUT = "com.tencent.mobileqq:id/input"; // EditText input
    private static String ID_LIST_CHAT_SEND = "com.tencent.mobileqq:id/fun_btn"; // Button send
    private boolean isSilence;
    // QQ7.1.8 versionCode = 718;-----------------------------------

    @Override
    public String getPackageName() {
        return Config.PACKAGE_NAME_QQ;
    }

    @Override
    public boolean isEnable() {
        return config().isEnableQQ();
    }

    /** QQ是否运行在前台 */
    private boolean isRunningForeground(Context context){
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if(!tasks.isEmpty()){
            String packageName = tasks.get(0).topActivity.getPackageName();
            if(Config.PACKAGE_NAME_QQ.equals(packageName)){
                return true ;
            }
        }
        return false ;
    }

    @Override
    public void onReceive(AccessibilityEvent event) {
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED: // 通知栏状态变化
                notificationEvent(event);
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED: // 窗口状态变化
                windowStateEvent(event);
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:  // 窗口内容变化
                windowContentEvent(event);
                break;
        }
    }

    /** 通知栏状态变化 */
    private void notificationEvent(AccessibilityEvent event) {
        // 小细节, 抢完红包干啥
        if(!isRunningForeground(context())){
            mAppState = APP_STATE_BACKGROUND;
        }else {
            mAppState = APP_STATE_FOREGROUND;
        }

        Parcelable data = event.getParcelableData();
        if (data == null || !(data instanceof Notification)) {
            return;
        }

        List<CharSequence> texts = event.getText();
        if (!texts.isEmpty()) {
            String text = String.valueOf(texts.get(0));
            LogUtils.printOut(TAG, text);
            int index = text.lastIndexOf(":");
            if(index != -1) {
                text = text.substring(index + 1);
            }
            if(text.contains(KEY)) {
                isReceived = true;
                Notification nf = (Notification) data;
                PendingIntent pendingIntent = nf.contentIntent;
                if(NotifyUtils.isLockScreen()) {  // 是否为锁屏或黑屏状态
                    if(config().isLockScreenRob()){
                        NotifyUtils.wakeAndUnlock();
                        NotifyUtils.send(pendingIntent); // 打开QQ
                    }else{
                        NotifyUtils.showNotify(context(), String.valueOf(nf.tickerText), pendingIntent); // 显示有红包通知
                    }
                } else {
                    NotifyUtils.send(pendingIntent); // 打开QQ
                }

                // 播放声音和震动
                NotifyUtils.playEffect(context(), config());
            }
        }
    }

    /** 窗口状态变化 */
    private void windowStateEvent(AccessibilityEvent event) {
        CharSequence className = event.getClassName();
        if(className == null){
            return;
        }
        switch (className.toString()){
            case CLASSNAME_1:
                mCurrentWindow = WINDOW_SPLASH;
                break;
            case CLASSNAME_2:
                mCurrentWindow = WINDOW_QWALLET_PLUGIN_PROXY;
                break;
            default:
                mCurrentWindow = WINDOW_OTHER;
                break;
        }

        if(!isReceived) { // 未收到红包, 下面操作不执行
            return;
        }

        switch (mCurrentWindow){
            case WINDOW_SPLASH:
                clickRedPacket(); // 在聊天界面, 去点中红包
                break;
            case WINDOW_QWALLET_PLUGIN_PROXY:
                clickRedPacketAfter(); // 已拆红包, 干哈呢
                break;
        }
    }

    /** 查看领取详情或者返回 */
    private void clickRedPacketAfter() {
        // 到这, 领取流程算是完了
        isReceived = false;

        // 查看领取详情, 或者返回
        if(!config().isSmartBackQQ()){
            SystemClock.sleep(999);
            AccessibilityNodeInfo nodeInfo = mService.getRootInActiveWindow();
            if(nodeInfo == null) {
                LogUtils.printErr(TAG, "rootWindow为空");
                return;
            }

            AccessibilityNodeInfo look = AccessibilityUtils.findNodeInfosByText(nodeInfo, KEY_LOOK);
            if(look != null){
                AccessibilityUtils.performClick(look);
            }
        }else{
            back();
        }
    }

    /** 点红包 - 通知栏进来*/
    private void clickRedPacket() {
        AccessibilityNodeInfo nodeInfo = mService.getRootInActiveWindow();
        if(nodeInfo == null) {
            LogUtils.printErr(TAG, "rootWindow为空");
            return;
        }

        if(!clickLastMsg(nodeInfo)) { // 通知栏进入, 可能消息发送过快, 错过红包在最新消息位置
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(ID_LIST_CHAT_ITEM);
                if(list != null && !list.isEmpty()) {
                    for (int i = list.size() - 1, j = 0; i >= 0; i--) {
                        AccessibilityNodeInfo item = list.get(i);
                        CharSequence desc = item.getContentDescription();
                        if(TextUtils.isEmpty(desc)){
                            if (j++ >= 3){ // 查找历史以上三条
                                break;
                            }
                            continue;
                        }
                        isReceived = clickRedPacket(nodeInfo, item, desc);
                        break;
                    }
                }
            }else{
                AccessibilityNodeInfoCompat compat = new AccessibilityNodeInfoCompat(nodeInfo);
                List<AccessibilityNodeInfoCompat> list = compat.findAccessibilityNodeInfosByViewId(ID_LIST_CHAT_ITEM);
                if(list != null && !list.isEmpty()) {
                    for (int i = list.size() - 1, j = 0; i >= 0; i--) {
                        AccessibilityNodeInfoCompat item = list.get(i);
                        CharSequence desc = item.getContentDescription();
                        if(TextUtils.isEmpty(desc)){
                            if (j++ >= 3){ // 查找历史以上三条
                                break;
                            }
                            continue;
                        }
                        isReceived = clickRedPacket(nodeInfo, (AccessibilityNodeInfo) item.getInfo(), desc);
                        break;
                    }
                }
            }
        }

        if(!isReceived){
            LogUtils.printOut("------这可能真的是个假红包------");
            if(config().isSmartBackQQ()){ // 智能返回
                if(mAppState == APP_STATE_BACKGROUND){
                    back(2);
                }
            }
        }
    }

    /** 点最新消息 */
    private boolean clickLastMsg(AccessibilityNodeInfo nodeInfo) {
        boolean isClick = false;
        AccessibilityNodeInfo item = AccessibilityUtils.findNodeInfosByIdLast(nodeInfo, ID_LIST_CHAT_ITEM);
        if(item != null){
            CharSequence desc = item.getContentDescription();
            if(TextUtils.isEmpty(desc)){
                return isClick;
            }
            isClick = clickRedPacket(nodeInfo, item, desc);
        }
        return isClick;
    }

    /** 抢红包模式 */
    private boolean clickRedPacket(AccessibilityNodeInfo nodeInfo, AccessibilityNodeInfo item, CharSequence desc) {
        boolean isClick = false;
        String str = desc.toString();
        int index;
        if(str.startsWith(KEY_WORD)){ // 我去, 非口令中文标点, 口令英文标点
            index = str.lastIndexOf(",");
        }else{
            index = str.lastIndexOf("，");
        }
        if(index != -1) {
            str = str.substring(index + 1);
        }
        int qqMode = config().getQQMode();

        // 跟微信一样
        if(!isMemberChatUi(nodeInfo)){ // 单聊
            if(qqMode == Config.WX_MODE_1 || qqMode == Config.WX_MODE_2){
                return isClick;
            }
        }else if(qqMode == Config.WX_MODE_2){ // 抢群聊, 不包过自己发的
            AccessibilityNodeInfo look = AccessibilityUtils.findNodeInfosByText(item, KEY_LOOK);
            if(look != null){ // 自己发的
                return isClick;
            }
        }else if(qqMode == Config.WX_MODE_3){
            NotifyUtils.playEffect(context(), config());
            return isClick;
        }

        if(CONTENT_DESC.equals(str)){ // 非口令红包
            final AccessibilityNodeInfo real = AccessibilityUtils.findNodeInfosByText(item, SEARCH);
            if(real!= null){ // 红包没拆过
                isReceived = isClick = true;
                int delayTime = config().getQQOpenDelayTime();
                if(delayTime != 0){
                    Global.getHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            AccessibilityUtils.performClick(real);
                        }
                    }, delayTime);
                }else{
                    AccessibilityUtils.performClick(real);
                }
            }
        }else if(CONTENT_DESC_WORD.equals(str)){ // 口令红包
            if(!config().isWordQQ()){
                return isClick;
            }
            AccessibilityNodeInfo isOpen = AccessibilityUtils.findNodeInfosByText(item, SEARCH_WORD_OPENED);
            if(isOpen != null){
                return isClick;
            }
            AccessibilityNodeInfo real = AccessibilityUtils.findNodeInfosByText(item, SEARCH_WORD);
            if(real!= null){ // 红包没拆过
                isReceived = isClick = true;
                String word = desc.toString().substring(KEY_WORD.length(), index); // 口令
                AccessibilityNodeInfo input = AccessibilityUtils.findNodeInfosById(nodeInfo, ID_LIST_CHAT_INPUT);
                if(input != null){
                    if(isSilence){
                        return isClick; // 沉默, 防止发送多次口令
                    }
                    silence();
                    AccessibilityUtils.setText(input, word);
                }
                final AccessibilityNodeInfo send = AccessibilityUtils.findNodeInfosById(nodeInfo, ID_LIST_CHAT_SEND);
                if(send != null){
                    int delayTime = config().getQQOpenDelayTime();
                    if(delayTime != 0){
                        Global.getHandler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AccessibilityUtils.performClick(send);
                            }
                        }, delayTime);
                    }else{
                        AccessibilityUtils.performClick(send);
                    }
                }
            }
        }else{
            isClick = false;
            LogUtils.printOut("------非红包------");
        }
        return isClick;
    }

    /** 窗口内容变化 */
    private void windowContentEvent(AccessibilityEvent event) {
        if(mCurrentWindow != WINDOW_SPLASH){ // //不在聊天界面或聊天列表，不处理
            return;
        }

        AccessibilityNodeInfo nodeInfo = mService.getRootInActiveWindow();
        if(nodeInfo == null){
            return;
        }

        // 获取MsgBox
        AccessibilityNodeInfo msgBox = AccessibilityUtils.findNodeInfosById(nodeInfo, ID_MSG_BOX);
        if(msgBox != null){
            CharSequence text = msgBox.getText();
            if(!TextUtils.isEmpty(text)){
                String value = text.toString();
                int index = value.lastIndexOf(":");
                if(index != -1) {
                    value = value.substring(index + 1);
                }
                if(value.startsWith(KEY)) {
                    AccessibilityUtils.performClick(msgBox);
                    return;
                }
            }
        }

        // 直接去获取当前会话的最后一条Item, 不为null, 则是当前会话列表
        AccessibilityNodeInfo item = AccessibilityUtils.findNodeInfosByIdLast(nodeInfo, ID_LIST_CHAT_ITEM);
        if(item != null){
            CharSequence desc = item.getContentDescription();
            if(TextUtils.isEmpty(desc)){
                return;
            }
            clickRedPacket(nodeInfo, item, desc);
            return;
        }

        // 聊天记录的 ID_LIST_MSG_LABEL 是 View, 具体不知道怎么获取消息值
    }

    /** 返回 */
    private void back() {
        back(-1);
    }

    /** 返回 */
    private void back(int count) {
        LogUtils.printOut("AppState: " + mAppState);
        int backCount;
        if(mAppState == APP_STATE_BACKGROUND){
            mAppState = APP_STATE_FOREGROUND;
            backCount = 3;
        }else{
            backCount = 1;
        }

        if(count != -1){
            backCount = count;
        }

        for (int i = 0; i < backCount; i++) {
            AccessibilityUtils.performBack(mService);
            if(i < backCount - 1){
                SystemClock.sleep(66);// 需要个时间差
            }
        }
    }

    /**
     * 是否为群聊天
     */
    private boolean isMemberChatUi(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            return false;
        }
        AccessibilityNodeInfo label = AccessibilityUtils.findNodeInfosById(nodeInfo, ID_SUB_NAME);
        if (label == null) {
            return true;
        }
        return false;
    }

    /** 沉默 */
    private void silence() {
        isSilence = true; // 开启沉默
        Global.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isSilence = false;
            }
        }, SILENCE_TIME);
    }
}
