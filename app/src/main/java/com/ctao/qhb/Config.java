package com.ctao.qhb;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ctao.baselib.Global;
import com.ctao.baselib.utils.LogUtils;

/**
 * Created by A Miracle on 2017/8/17.
 */
public class Config {
    public static final String SP_FIRST  = "SP_FIRST";

    public static final String SP_ENABLE_WE_CHAT = "platform_WeChat";
    public static final String SP_ENABLE_QQ = "platform_QQ";
    public static final String SP_ENABLE_TIM = "platform_TIM";

    private boolean isEnableWeChat;
    private boolean isEnableQQ;
    private boolean isEnableTIM;

    private Config(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Global.getContext());
        isEnableWeChat = sp.getBoolean(SP_ENABLE_WE_CHAT, true);
        isEnableQQ = sp.getBoolean(SP_ENABLE_QQ, true);
        isEnableTIM = sp.getBoolean(SP_ENABLE_TIM, true);
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

    /** 微信打开红包后延时时间*/
    public int getWeChatOpenDelayTime() {
        return 0;
    }

    /** 是否抢自己发的, 针对群聊 */
    public boolean isRobSelf(){
        return true;
    }

    /** 当前不在微信内, 返回桌面 */
    public boolean isRobCompleteToHome(){
        return true;
    }
}
