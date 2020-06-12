package com.zb.lib_base.db;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.model.ChatList;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class ChatListDb extends BaseDao {

    public ChatListDb(Realm realm) {
        super(realm);
    }

    // 保存更新会话列表
    public void saveChatList(ChatList chatList) {
        beginTransaction();
        chatList.setMainUserId(BaseActivity.userId);
        realm.insertOrUpdate(chatList);
        commitTransaction();
    }

    // 获取会话列表
    public List<ChatList> getChatList(int chatType) {
        beginTransaction();
        List<ChatList> chatMsgList = new ArrayList<>();
        RealmResults<ChatList> results = realm.where(ChatList.class).equalTo("chatType", chatType).equalTo("mainUserId", BaseActivity.userId).findAllSorted("creationDate", Sort.DESCENDING);
        if (results.size() > 0) {
            chatMsgList.addAll(results);
        }
        commitTransaction();
        return chatMsgList;
    }

    // 单个用户的最新聊天记录
    public ChatList getChatMsg(long otherUserId, int chatType) {
        beginTransaction();
        ChatList chatList = realm.where(ChatList.class).equalTo("chatType", chatType).equalTo("userId", otherUserId).equalTo("mainUserId", BaseActivity.userId).findFirst();
        commitTransaction();
        return chatList;
    }

    public void deleteChatMsg(long otherUserId) {
        beginTransaction();
        ChatList chatList = realm.where(ChatList.class).equalTo("userId", otherUserId).equalTo("mainUserId", BaseActivity.userId).findFirst();
        if (chatList != null) {
            chatList.deleteFromRealm();
        }
        commitTransaction();
    }


    public void updateChatMsg(long otherUserId, String creationDate, String stanza, int msgType, int noReadNum, CallBack callBack) {
        beginTransaction();
        ChatList chatList = realm.where(ChatList.class).equalTo("userId", otherUserId).equalTo("mainUserId", BaseActivity.userId).findFirst();
        if (chatList == null) {
            callBack.fail();
        } else {
            chatList.setCreationDate(creationDate);
            chatList.setStanza(stanza);
            chatList.setMsgType(msgType);
            chatList.setNoReadNum(noReadNum);
            callBack.success();
        }
        commitTransaction();
    }

    public interface CallBack {
        void success();

        void fail();
    }

    public int getAllUnReadNum() {
        beginTransaction();
        int unReadNum = 0;
        RealmResults<ChatList> results = realm.where(ChatList.class).notEqualTo("chatType",1).equalTo("mainUserId", BaseActivity.userId).findAll();
        if (results.size() > 0) {
            for (ChatList chatList : results) {
                unReadNum += chatList.getNoReadNum();
            }
        }
        commitTransaction();
        return unReadNum;
    }

}
