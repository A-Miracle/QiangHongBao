package com.ctao.qhb;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ctao.baselib.Global;
import com.ctao.baselib.utils.LogUtils;
import com.ctao.baselib.utils.SPUtils;

/**
 * Created by A Miracle on 2017/8/17.
 */
public class Config {
    public static final String FILE_LOG = "log";
    public static final String FILE_IMG = "img";
    public static final String FILE_APK = "apk";
    public static final String FILE_CACHE = "cache";

    public static final String PACKAGE_NAME_WX = "com.tencent.mm";
    public static final String PACKAGE_NAME_QQ = "com.tencent.mobileqq";
    public static final String PACKAGE_NAME_TIM = "com.tencent.tim";

    public static final String SP_LATEST_DATE  = "SP_LATEST_DATE"; // 最新时间
    public static final String SP_LATEST_CODE  = "SP_UPDATE_PACKAGE"; // 最新包Code
    public static final String SP_AGREEMENT  = "SP_AGREEMENT"; // 免责声明
    public static final String SP_WX_MODE  = "SP_WX_MODE"; // 微信抢模式
    public static final String SP_WX_DELAY_TIME  = "SP_WX_DELAY_TIME"; // 微信拆延时

    public static final String SP_QQ_MODE  = "SP_QQ_MODE"; // QQ抢模式
    public static final String SP_QQ_DELAY_TIME  = "SP_QQ_DELAY_TIME"; // QQ拆延时

    public static final String SP_ENABLE_WE_CHAT = "platform_WeChat";
    public static final String SP_ENABLE_QQ = "platform_QQ";
    public static final String SP_ENABLE_TIM = "platform_TIM";

    public static final String SP_SMART_BACK_WE_CHAT = "smart_back_wx"; // 微信智能返回
    public static final String SP_SMART_BACK_QQ = "SP_SMART_BACK_QQ"; // QQ智能返回

    public static final String SP_WORD_QQ = "qq_word_setting"; // QQ口令红包

    public static final String SP_LOCK_SCREEN_ROB = "lock_screen_automatic_grab"; // 锁屏自动抢
    public static final String SP_VOICE = "voice"; // 声音
    public static final String SP_VIBRATION = "vibration"; // 震动
    public static final String SP_NIGHT_NOT_DISTURB = "night_not_disturb"; // 夜间免打扰


    public static final int WX_MODE_0 = 0;//自动抢
    public static final int WX_MODE_1 = 1;//抢群聊, 包括自己发的;
    public static final int WX_MODE_2 = 2;//抢群聊, 不包括自己发的
    public static final int WX_MODE_3 = 3;//通知手动抢

    private boolean isEnableWeChat;
    private boolean isEnableQQ;
    private boolean isEnableTIM;
    private boolean isSmartBackWeChat, isSmartBackQQ;
    private boolean isLockScreenRob;
    private boolean isVoice;
    private boolean isVibration;
    private boolean isNightNotDisturb;
    private boolean isWordQQ;

    private int wxMode, qqMode;
    private float wxDelayTime, qqDelayTime;

    private Config(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Global.getContext());
        isEnableWeChat = sp.getBoolean(SP_ENABLE_WE_CHAT, true);
        isEnableQQ = sp.getBoolean(SP_ENABLE_QQ, true);
        isEnableTIM = sp.getBoolean(SP_ENABLE_TIM, true);

        isSmartBackWeChat = sp.getBoolean(SP_SMART_BACK_WE_CHAT, true);
        isSmartBackQQ = SPUtils.getBoolean(SP_SMART_BACK_QQ, false);

        isLockScreenRob = sp.getBoolean(SP_LOCK_SCREEN_ROB, false);
        isVoice = sp.getBoolean(SP_VOICE, true);
        isVibration = sp.getBoolean(SP_VIBRATION, true);
        isNightNotDisturb = sp.getBoolean(SP_NIGHT_NOT_DISTURB, false);

        wxMode = SPUtils.getInt(SP_WX_MODE, WX_MODE_0);
        wxDelayTime = SPUtils.getFloat(SP_WX_DELAY_TIME, 0);

        qqMode = SPUtils.getInt(SP_QQ_MODE, WX_MODE_0);
        qqDelayTime = SPUtils.getFloat(SP_QQ_DELAY_TIME, 0);
        isWordQQ = sp.getBoolean(SP_WORD_QQ, true);
    }
    
    private static class Single {
        static Config Instance = new Config();
    }
    public static Config getInstance() {
        return Config.Single.Instance;
    }

    /** is Enable */
    public void changeEnable(String key) {
        switch (key){
            case SP_ENABLE_WE_CHAT:
                isEnableWeChat = !isEnableWeChat;
                break;
            case SP_ENABLE_QQ:
                isEnableQQ = !isEnableQQ;
                break;
            case SP_ENABLE_TIM:
                isEnableTIM = !isEnableTIM;
                break;
        }
        LogUtils.printOut(isEnableWeChat+", "+isEnableQQ+", "+isEnableTIM);
    }

    /** is SmartBack */
    public void changeSmartBack(String key) {
        switch (key){
            case SP_ENABLE_WE_CHAT:
                isSmartBackWeChat = !isSmartBackWeChat;
                break;
            case SP_ENABLE_QQ:
                isSmartBackQQ = !isSmartBackQQ;
                SPUtils.putObject(SP_SMART_BACK_QQ, isSmartBackQQ); // 要自己保存
                break;
            case SP_ENABLE_TIM:
                break;
        }
        LogUtils.printOut(isSmartBackWeChat+", "+isSmartBackQQ);
    }

    public void changeWordQQ(){
        isWordQQ = !isWordQQ;
    }

    /** is Global */
    public void changeGlobal(String key){
        switch (key){
            case SP_LOCK_SCREEN_ROB:
                isLockScreenRob = !isLockScreenRob;
                break;
            case SP_VOICE:
                isVoice = !isVoice;
                break;
            case SP_VIBRATION:
                isVibration = !isVibration;
                break;
            case SP_NIGHT_NOT_DISTURB:
                isNightNotDisturb = !isNightNotDisturb;
                break;
        }
    }

    /** WX Mode */
    public void setWXMode(int mode){
        SPUtils.putObject(SP_WX_MODE, mode);
        wxMode = mode;
    }

    /** WX DelayTime */
    public void setWXDelayTime(float delayTime){
        SPUtils.putObject(SP_WX_DELAY_TIME, delayTime);
        wxDelayTime = delayTime;
    }

    /** WX Mode */
    public void setQQMode(int mode){
        SPUtils.putObject(SP_QQ_MODE, mode);
        qqMode = mode;
    }

    /** WX DelayTime */
    public void setQQDelayTime(float delayTime){
        SPUtils.putObject(SP_QQ_DELAY_TIME, delayTime);
        qqDelayTime = delayTime;
    }

    /** 是否启动微信抢红包*/
    public boolean isEnableWeChat() {
        return isEnableWeChat;
    }

    /** 是否启动QQ抢红包*/
    public boolean isEnableQQ() {
        return isEnableQQ;
    }
    
    /** 是否启动TIM抢红包*/
    public boolean isEnableTIM() {
        return isEnableTIM;
    }

    /** 微信抢红包模式 */
    public int getWXMode(){
        return wxMode;
    }

    /** 微信打开红包后延时时间 s*/
    public float getWXDelayTime() {
        return wxDelayTime;
    }

    /** QQ抢红包模式 */
    public int getQQMode(){
        return qqMode;
    }

    /** QQ打开红包后延时时间 s*/
    public float getQQDelayTime() {
        return qqDelayTime;
    }

    /** 微信打开红包后延时时间 ms*/
    public int getQQOpenDelayTime() {
        return (int) (qqDelayTime * 1000);
    }

    /** 微信打开红包后延时时间 ms*/
    public int getWeChatOpenDelayTime() {
        return (int) (wxDelayTime * 1000);
    }

    /** 微信智能返回*/
    public boolean isSmartBackWeChat(){
        return isSmartBackWeChat;
    }

    /** QQ智能返回*/
    public boolean isSmartBackQQ(){
        return isSmartBackQQ;
    }

    /** 锁屏自动抢 */
    public boolean isLockScreenRob(){
        return isLockScreenRob;
    }

    /** 声音 */
    public boolean isVoice(){
        return isVoice;
    }

    /** 震动 */
    public boolean isVibration(){
        return isVibration;
    }

    /** 夜间免打扰 */
    public boolean isNightNotDisturb(){
        return isNightNotDisturb;
    }

    /** QQ口令红包 */
    public boolean isWordQQ(){
        return isWordQQ;
    }
}
