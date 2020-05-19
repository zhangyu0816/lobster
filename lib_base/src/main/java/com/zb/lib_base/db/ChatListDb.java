package com.zb.lib_base.db;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.model.ChatList;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class ChatListDb extends BaseDao {

    public ChatListDb(Realm realm) {
        super(realm);
    }

    // 保存更新会话列表
    public void saveChatList(ChatList chatList) {
        beginTransaction();
        realm.insertOrUpdate(chatList);
        commitTransaction();
    }

    // 获取会话列表
    public List<ChatList> getChatList() {
        beginTransaction();
        List<ChatList> chatMsgList = new ArrayList<>();
        RealmResults<ChatList> results = realm.where(ChatList.class).equalTo("mainUserId", BaseActivity.userId).findAll();
        if (results.size() > 0) {
            chatMsgList.addAll(results);
        }
        commitTransaction();
        return chatMsgList;
    }

}
