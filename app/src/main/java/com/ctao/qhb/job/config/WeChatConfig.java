package com.ctao.qhb.job.config;

import android.util.SparseArray;

/**
 * Created by A Miracle on 2017/8/25.
 */
public class WeChatConfig {
    public static final int V_1080 = 1080;
    public static final int V_1100 = 1100;

    private SparseArray<Value> mMap = new SparseArray<>();
    {
        // 微信6.5.10; versionCode = 1080;-----------------------------------
        mMap.put(1080, new Value(
                "com.tencent.mm:id/bnr", // '开'
                "com.tencent.mm:id/gs", // 聊天标题
                "com.tencent.mm:id/aie", // 聊天记录列表中Item
                "com.tencent.mm:id/i8", // 聊天记录列表中Item小圆点
                "com.tencent.mm:id/aii", // 聊天记录列表中Item最新消息
                "com.tencent.mm:id/bpl", // 聊天记录列表
                "com.tencent.mm:id/a4l", // 当前会话列表
                "com.tencent.mm:id/q", // ItemView, clickable=false;
                "com.tencent.mm:id/a7i", // 红包ItemView, clickable=true;
                "com.tencent.mm:id/ij" // 文本ItemView, clickable=true;
        ));

        // 微信6.5.13; versionCode = 1100;-----------------------------------
        mMap.put(1100, new Value(
                "com.tencent.mm:id/bp6", // '开'
                "com.tencent.mm:id/gz", // 聊天标题
                "com.tencent.mm:id/aja", // 聊天记录列表中Item
                "com.tencent.mm:id/ie", // 聊天记录列表中Item小圆点
                "com.tencent.mm:id/aje", // 聊天记录列表中Item最新消息
                "com.tencent.mm:id/bqc", // 聊天记录列表
                "com.tencent.mm:id/a5j", // 当前会话列表
                "com.tencent.mm:id/s", // ItemView, clickable=false;
                "com.tencent.mm:id/a8q", // 红包ItemView, clickable=true;
                "com.tencent.mm:id/iq" // 文本ItemView, clickable=true;
        ));
    }

    public Value getVersion(int versionCode) {
        return mMap.get(versionCode);
    }

    public static class Value{
        public Value(String ID_BUTTON_OPEN, String ID_GROUP_NAME, String ID_LIST_MSG_ITEM, String ID_LIST_MSG_RED, String ID_LIST_MSG_LABEL, String ID_LIST_MSG, String ID_LIST_CHAT, String ID_LIST_CHAT_ITEM, String ID_LIST_CHAT_ITEM_VIEW, String ID_LIST_CHAT_ITEM_TEXT) {
            this.ID_BUTTON_OPEN = ID_BUTTON_OPEN;
            this.ID_GROUP_NAME = ID_GROUP_NAME;
            this.ID_LIST_MSG_ITEM = ID_LIST_MSG_ITEM;
            this.ID_LIST_MSG_RED = ID_LIST_MSG_RED;
            this.ID_LIST_MSG_LABEL = ID_LIST_MSG_LABEL;
            this.ID_LIST_MSG = ID_LIST_MSG;
            this.ID_LIST_CHAT = ID_LIST_CHAT;
            this.ID_LIST_CHAT_ITEM = ID_LIST_CHAT_ITEM;
            this.ID_LIST_CHAT_ITEM_VIEW = ID_LIST_CHAT_ITEM_VIEW;
            this.ID_LIST_CHAT_ITEM_TEXT = ID_LIST_CHAT_ITEM_TEXT;
        }

        public String ID_BUTTON_OPEN = "com.tencent.mm:id/bnr"; // '开'
        public String ID_GROUP_NAME = "com.tencent.mm:id/gs"; // 聊天标题

        public String ID_LIST_MSG_ITEM = "com.tencent.mm:id/aie"; // 聊天记录列表中Item
        public String ID_LIST_MSG_RED = "com.tencent.mm:id/i8"; // 聊天记录列表中Item小圆点
        public String ID_LIST_MSG_LABEL = "com.tencent.mm:id/aii"; // 聊天记录列表中Item最新消息
        public String ID_LIST_MSG = "com.tencent.mm:id/bpl"; // 聊天记录列表

        public String ID_LIST_CHAT = "com.tencent.mm:id/a4l"; // 当前会话列表
        public String ID_LIST_CHAT_ITEM = "com.tencent.mm:id/q"; // ItemView, clickable=false;
        public String ID_LIST_CHAT_ITEM_VIEW = "com.tencent.mm:id/a7i"; // 红包ItemView, clickable=true;
        public String ID_LIST_CHAT_ITEM_TEXT = "com.tencent.mm:id/ij"; // 文本ItemView, clickable=true;
    }
}
