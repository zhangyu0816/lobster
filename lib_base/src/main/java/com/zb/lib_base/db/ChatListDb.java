package com.zb.lib_base.db;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.model.ChatMsg;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class ChatListDb extends BaseDao {

    public ChatListDb(Realm realm) {
        super(realm);
    }

    // 保存更新会话列表
    public void saveChatList(ChatMsg chatMsg) {
        beginTransaction();
        realm.insertOrUpdate(chatMsg);
        commitTransaction();
    }

    // 获取会话列表
    public List<ChatMsg> getChatList() {
        beginTransaction();
        List<ChatMsg> chatMsgList = new ArrayList<>();
        RealmResults<ChatMsg> results = realm.where(ChatMsg.class).equalTo("mainUserId", BaseActivity.userId).findAll();
        if (results.size() > 0) {
            chatMsgList.addAll(results);
        }
        commitTransaction();
        return chatMsgList;
    }

}
