package com.ctao.qhb.job;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Parcelable;
import android.os.SystemClock;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.ctao.baselib.Global;
import com.ctao.baselib.utils.LogUtils;
import com.ctao.qhb.Config;
import com.ctao.qhb.Statistics;
import com.ctao.qhb.job.config.WeChatConfig;
import com.ctao.qhb.service.QHBService;
import com.ctao.qhb.utils.AccessibilityUtils;
import com.ctao.qhb.utils.NotifyUtils;

import java.util.List;

/**
 * Created by A Miracle on 2017/8/17.
 */
public class WeChatAccessibilityJob extends BaseAccessibilityJob {

    private static final String TAG = "WeChatAccessibilityJob";

    /** 微信的包名 */
    public static final String PACKAGE_NAME = "com.tencent.mm";

    /** 红包消息的关键字 */
    private static final String KEY = "[微信红包]";
    private static final String KEY_SEARCH = "领取红包";
    private static final String KEY_SEARCH_SELF = "查看红包";

    private static final String CLASS_NAME_BUTTON = "android.widget.Button";
    private static final String CLASS_NAME_LISTVIEW = "android.widget.ListView";
    private static final String CLASS_NAME_TEXTVIEW = "android.widget.TextView";

    private static final int APP_STATE_BACKGROUND = -1;
    private static final int APP_STATE_FOREGROUND = 1;

    private static final int WINDOW_LAUNCHER_UI = 1; // CLASSNAME_1
    private static final int WINDOW_LUCKY_MONEY_OPEN = 2; // CLASSNAME_2
    private static final int WINDOW_LUCKY_MONEY_DETAILUI = 3; // CLASSNAME_3
    private static final int WINDOW_OTHER = -1;

    private static final int SILENCE_TIME = 1300;

    private int mAppState = APP_STATE_FOREGROUND;
    private int mCurrentWindow = WINDOW_OTHER;
    private boolean isReceived; // 收到红包
    private boolean isSilence; // 沉默, 防止没抢到红包而反复点

    // 微信6.5.10; versionCode = 1080;-----------------------------------
    private final static String CLASSNAME_1 = "com.tencent.mm.ui.LauncherUI"; // 打开红包
    private final static String CLASSNAME_2 = "com.tencent.mm.plugin.luckymoney.ui.En_fba4b94f"; // 拆红包
    private final static String CLASSNAME_3 = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI"; // 红包领取详情

    private final static String LABEL_1 = "发了一个红包";
    private final static String LABEL_2 = "给你发了一个红包";
    private final static String LABEL_3 = "发了一个红包，金额随机";

    private static String ID_BUTTON_OPEN = "com.tencent.mm:id/bnr"; // '开'
    private static String ID_GROUP_NAME = "com.tencent.mm:id/gs"; // 聊天标题

    private static String ID_LIST_MSG_ITEM = "com.tencent.mm:id/aie"; // 聊天记录列表中Item
    private static String ID_LIST_MSG_RED = "com.tencent.mm:id/i8"; // 聊天记录列表中Item小圆点
    private static String ID_LIST_MSG_LABEL = "com.tencent.mm:id/aii"; // 聊天记录列表中Item最新消息
    private static String ID_LIST_MSG = "com.tencent.mm:id/bpl"; // 聊天记录列表

    private static String ID_LIST_CHAT = "com.tencent.mm:id/a4l"; // 当前会话列表
    private static String ID_LIST_CHAT_ITEM = "com.tencent.mm:id/q"; // ItemView, clickable=false;
    private static String ID_LIST_CHAT_ITEM_VIEW = "com.tencent.mm:id/a7i"; // 红包ItemView, clickable=true;
    private static String ID_LIST_CHAT_ITEM_TEXT = "com.tencent.mm:id/ij"; // 文本ItemView, clickable=true;

    // 微信6.5.13; versionCode = 1100;-----------------------------------

    @Override
    public boolean isEnable() {
        return config().isEnableWeChat();
    }

    @Override
    public String getPackageName() {
        return PACKAGE_NAME;
    }

    @Override
    public void onCreate(QHBService service) {
        super.onCreate(service);
        updateConfig();
    }

    /** 根据对应版本配置 */
    private void updateConfig() {
        WeChatConfig config = new WeChatConfig();
        int versionCode = mService.getWeChatPackageInfo().versionCode;
        WeChatConfig.Value value = null;

        if(versionCode >= WeChatConfig.V_1100){
            value = config.getVersion(WeChatConfig.V_1100);
        }else if(versionCode >= WeChatConfig.V_1080){
            value = config.getVersion(WeChatConfig.V_1080);
        }

        if(value == null){
            return;
        }
        ID_BUTTON_OPEN = value.ID_BUTTON_OPEN;
        ID_GROUP_NAME = value.ID_GROUP_NAME;

        ID_LIST_MSG_ITEM = value.ID_LIST_MSG_ITEM;
        ID_LIST_MSG_RED = value.ID_LIST_MSG_RED;
        ID_LIST_MSG_LABEL = value.ID_LIST_MSG_LABEL;
        ID_LIST_MSG = value.ID_LIST_MSG;

        ID_LIST_CHAT = value.ID_LIST_CHAT;
        ID_LIST_CHAT_ITEM = value.ID_LIST_CHAT_ITEM;
        ID_LIST_CHAT_ITEM_VIEW = value.ID_LIST_CHAT_ITEM_VIEW;
        ID_LIST_CHAT_ITEM_TEXT = value.ID_LIST_CHAT_ITEM_TEXT;
    }

    /** 微信是否运行在前台 */
    private boolean isRunningForeground(Context context){
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if(!tasks.isEmpty()){
            String packageName = tasks.get(0).topActivity.getPackageName();
            if(PACKAGE_NAME.equals(packageName)){
                return true ;
            }
        }
        return false ;
    }

    /* 场景一: 从通知栏抢红包
        TYPE_NOTIFICATION_STATE_CHANGED  Text: [联系人: [微信红包]恭喜发财，大吉大利]; ParcelableData: Notification;
        TYPE_WINDOW_STATE_CHANGED  ClassName: com.tencent.mm.ui.LauncherUI; 点红包
        TYPE_WINDOW_STATE_CHANGED  ClassName: com.tencent.mm.plugin.luckymoney.ui.En_fba4b94f; 拆红包
        TYPE_WINDOW_STATE_CHANGED  ClassName: com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI; 红包详情了

       场景二: 从消息记录列表抢红包

       场景三: 当前会话列表抢红包

       场景四: 其他会话列表抢红包

    */

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

    /** 处理通知栏事件 */
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
                        NotifyUtils.send(pendingIntent); // 打开微信
                    }else{
                        NotifyUtils.showNotify(context(), String.valueOf(nf.tickerText), pendingIntent); // 显示有红包通知
                    }
                } else {
                    NotifyUtils.send(pendingIntent); // 打开微信
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
                mCurrentWindow = WINDOW_LAUNCHER_UI;
                break;
            case CLASSNAME_2:
                mCurrentWindow = WINDOW_LUCKY_MONEY_OPEN;
                break;
            case CLASSNAME_3:
                mCurrentWindow = WINDOW_LUCKY_MONEY_DETAILUI;
                break;
           default:
               mCurrentWindow = WINDOW_OTHER;
                break;
        }

        if(!isReceived) { // 未收到红包, 下面操作不执行
            return;
        }

        switch (mCurrentWindow){
            case WINDOW_LAUNCHER_UI:
                clickRedPacket(); // 在聊天界面, 去点中红包
                break;
            case WINDOW_LUCKY_MONEY_OPEN:
                openRedPacket(); // 点中了红包, 下一步就是去拆红包
                break;
            case WINDOW_LUCKY_MONEY_DETAILUI:
                detailsRedPacket(); // 拆完红包后看详细的纪录界面
                break;
        }
    }

    /** 窗口内容变化 */
    private void windowContentEvent(AccessibilityEvent event) {
        if(mCurrentWindow != WINDOW_LAUNCHER_UI){ // //不在聊天界面或聊天列表，不处理
            return;
        }

        AccessibilityNodeInfo nodeInfo = mService.getRootInActiveWindow();
        if(nodeInfo == null){
            return;
        }

        // 直接去获取当前会话的最后一条Item, 不为null, 则是当前会话列表
        AccessibilityNodeInfo item = AccessibilityUtils.findNodeInfosByIdLast(nodeInfo, ID_LIST_CHAT_ITEM);
        if(item != null){
            if(isSilence && mService.getWeChatPackageInfo().versionCode < WeChatConfig.V_1100){ // 沉默中, return
                return;
            }
            clickLastMsg(nodeInfo);
            return;
        }

        // 直接去获取聊天记录的第一条Item, 不为null, 则是聊天记录列表
        item = AccessibilityUtils.findNodeInfosById(nodeInfo, ID_LIST_MSG_ITEM); //第一条消息
        if(item != null){
            AccessibilityNodeInfo red = AccessibilityUtils.findNodeInfosById(item, ID_LIST_MSG_RED);
            if(red != null){ // 有小圆点, 说明有未读消息
                AccessibilityNodeInfo label = AccessibilityUtils.findNodeInfosById(item, ID_LIST_MSG_LABEL);
                if(label != null){
                    String text = String.valueOf(label.getText());
                    LogUtils.printOut(TAG, text);
                    int index = text.lastIndexOf(":");
                    if(index != -1) {
                        text = text.substring(index + 1);
                    }
                    if(text.contains(KEY)) {
                        isReceived = true;
                        // 有红包, 点开item
                        AccessibilityUtils.performClick(label);
                    }
                }
            }
            return;
        }
    }

    /** 通知栏进来-点红包 */
    private void clickRedPacket() {
        AccessibilityNodeInfo nodeInfo = mService.getRootInActiveWindow();
        if(nodeInfo == null) {
            LogUtils.printErr(TAG, "rootWindow为空");
            return;
        }

        if(!clickLastMsg(nodeInfo)){ // 通知栏进入, 可能消息发送过快, 错过红包在最新消息位置
            AccessibilityNodeInfo real = AccessibilityUtils.findNodeInfosByIdLast(nodeInfo, ID_LIST_CHAT_ITEM_VIEW);
            if(real != null){ // 找历史最新的红包
                if(clickRedPacket(nodeInfo, real)){
                    isReceived = true;
                }
            }else{
                isReceived = false;
                LogUtils.printOut("------这可能真的是个假红包------");
                back(2);
            }
        }
    }

    /** 抢红包模式 */
    private boolean clickRedPacket(AccessibilityNodeInfo nodeInfo, AccessibilityNodeInfo real) {
        boolean flag = false;
        int wxMode = config().getWXMode();
        if (wxMode == Config.WX_MODE_0){
            if(isMemberChatUi(nodeInfo)){ // 群聊, 可以抢自己的
                flag = true;
                AccessibilityUtils.performClick(real);
            }else{ // 单聊, 不可以抢自己的
                List<AccessibilityNodeInfo> other = real.findAccessibilityNodeInfosByText(KEY_SEARCH);
                if(other != null && !other.isEmpty()){
                    flag = true;
                    AccessibilityUtils.performClick(real);
                }
            }
        }else if(wxMode == Config.WX_MODE_1){
            if(isMemberChatUi(nodeInfo)){ // 群聊, 可以抢自己的
                flag = true;
                AccessibilityUtils.performClick(real);
            }
        }else if(wxMode == Config.WX_MODE_2){
            if(isMemberChatUi(nodeInfo)){ // 群聊, 不可以抢自己的
                List<AccessibilityNodeInfo> other = real.findAccessibilityNodeInfosByText(KEY_SEARCH);
                if(other != null && !other.isEmpty()){
                    flag = true;
                    AccessibilityUtils.performClick(real);
                }
            }
        }else if(wxMode == Config.WX_MODE_3){
            // 通知手动抢
            NotifyUtils.playEffect(context(), config());
        }
        return flag;
    }

    /** 点最新消息 */
    private boolean clickLastMsg(AccessibilityNodeInfo nodeInfo) {
        boolean isClick = false;
        AccessibilityNodeInfo listView = AccessibilityUtils.findNodeInfosById(nodeInfo, ID_LIST_CHAT);
        if(listView == null){
            return isClick;
        }
        int childCount = listView.getChildCount();
        if(childCount <= 0){
            return isClick;
        }
        AccessibilityNodeInfo item = listView.getChild(childCount - 1);
        if(item != null){ // 每一条新消息都试着点红包
            AccessibilityNodeInfo real = AccessibilityUtils.findNodeInfosById(item, ID_LIST_CHAT_ITEM_VIEW);
            if(real != null) { // 真红包

                // 新版本后, 1100(包括)以上, 能判断红包是否已经领取
                if(mService.getWeChatPackageInfo().versionCode >= WeChatConfig.V_1100){
                    AccessibilityNodeInfo realToo = AccessibilityUtils.findNodeInfosByTexts(real, KEY_SEARCH, KEY_SEARCH_SELF);
                    if(realToo == null){
                        return isClick;
                    }
                }

                if(clickRedPacket(nodeInfo, real)){
                    isReceived = isClick = true;
                }
            }else{
                isClick = false;
                LogUtils.printOut("------非红包------");
            }
        }
        return isClick;
    }

    /** 拆红包 */
    private void openRedPacket() {
        AccessibilityNodeInfo nodeInfo = mService.getRootInActiveWindow();
        if(nodeInfo == null) {
            LogUtils.printErr(TAG, "rootWindow为空");
            return;
        }

        AccessibilityNodeInfo button_open = AccessibilityUtils.findNodeInfosById(nodeInfo, ID_BUTTON_OPEN);

        if (button_open == null) { // 分别对应固定金额的红包 拼手气红包
            AccessibilityNodeInfo textNode = AccessibilityUtils.findNodeInfosByTexts(nodeInfo, LABEL_1, LABEL_2, LABEL_3);
            if (textNode != null) {

                // 有两层父容器
                AccessibilityNodeInfo parent = textNode.getParent().getParent();
                for (int i = 0; i < parent.getChildCount(); i++) {
                    AccessibilityNodeInfo node = parent.getChild(i);
                    if (CLASS_NAME_BUTTON.equals(node.getClassName())) {
                        button_open = node;
                        break;
                    }
                }
            }
        }

        if(button_open != null) {
            final AccessibilityNodeInfo n = button_open;
            long sDelayTime = config().getWeChatOpenDelayTime(); // 延时时间
            if(sDelayTime <= 0) {
                Global.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AccessibilityUtils.performClick(n);
                    }
                }, sDelayTime);
            } else {
                AccessibilityUtils.performClick(n);
            }
            Statistics.event(Statistics.OPEN_HONGBAO);
        }else{
            // 没有 '开' , 没抢到
            isReceived = false;
            LogUtils.printOut("------没抢到------");
            back();
        }
    }

    /** 领取详情 */
    private void detailsRedPacket() {
        // 到这, 领取流程算是完了
        isReceived = false;
        back();
    }

    /** 返回 */
    private void back() {
        back(-1);
    }

    /** 返回 */
    private void back(int count) {
        silence(); // 沉默

        if(!config().isSmartBackWeChat()){
            return;
        }

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
                SystemClock.sleep(666);// 需要个时间差
            }
        }
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

    /** 是否为群聊天 */
    private boolean isMemberChatUi(AccessibilityNodeInfo nodeInfo) {
        if(nodeInfo == null) {
            return false;
        }

        AccessibilityNodeInfo label = AccessibilityUtils.findNodeInfosById(nodeInfo, ID_GROUP_NAME);
        if(label != null) {
            String title = String.valueOf(label.getText());
            LogUtils.printOut("title", title);
            if(title != null && title.endsWith(")")) {
                return true;
            }
        }
        return false;
    }
}
