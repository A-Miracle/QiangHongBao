package com.ctao.qhb.event;

/**
 * Created by A Miracle on 2017/6/27.
 */
public class MessageEvent {
    private int type;
    private Object param;
    public MessageEvent(int type) {
        this.type = type;
    }
    public MessageEvent(int type, Object param) {
        this.type = type;
        this.param = param;
    }
    public int getType(){
        return this.type;
    }
    public Object getParam(){
        return this.param;
    }

    public static final int QHB_SERVICE_STATE = 0xA; // 抢红包服务状态 [boolean]
    public static final int QHB_PACKAGE_INFO_UPDATE = 0xB; // 微信、QQ、TIM等更新
}
