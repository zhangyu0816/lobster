package com.zb.lib_base.db;

import android.text.TextUtils;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.model.ChatList;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class ChatListDb extends BaseDao {
    public volatile static ChatListDb INSTANCE;

    public ChatListDb(Realm realm) {
        super(realm);
    }

    //获取单例
    public static ChatListDb getInstance() {
        if (INSTANCE == null) {
            synchronized (ChatListDb.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ChatListDb(Realm.getDefaultInstance());
                }
            }
        }
        return INSTANCE;
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
        RealmResults<ChatList> results = realm.where(ChatList.class).notEqualTo("userId", 399583).equalTo("chatType", chatType).equalTo("mainUserId", BaseActivity.userId).findAllSorted("creationDate", Sort.DESCENDING);
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


    public void updateChatMsg(long otherUserId, String creationDate, String stanza, int msgType, int otherChatCount, CallBack callBack) {
        beginTransaction();
        ChatList chatList = realm.where(ChatList.class).equalTo("userId", otherUserId).equalTo("mainUserId", BaseActivity.userId).findFirst();
        if (chatList == null) {
            callBack.fail();
        } else {
            chatList.setCreationDate(creationDate);
            chatList.setStanza(stanza);
            chatList.setMsgType(msgType);
            chatList.setNoReadNum(chatList.getNoReadNum() + 1);
            if (chatList.getChatType() == 6)
                chatList.setOtherChatCount(otherChatCount + chatList.getNoReadNum());
            callBack.success();
        }
        commitTransaction();
    }

    public boolean hasFlashChat(ChatList chatMsg, int otherChatCount) {
        beginTransaction();
        ChatList chatList = realm.where(ChatList.class).equalTo("userId", chatMsg.getUserId()).equalTo("mainUserId", BaseActivity.userId).equalTo("flashTalkId", chatMsg.getFlashTalkId()).findFirst();
        if (chatList != null) {
            chatList.setCreationDate(chatMsg.getCreationDate());
            chatList.setStanza(chatMsg.getStanza());
            chatList.setMsgType(chatMsg.getMsgType());
            chatList.setNoReadNum(chatMsg.getNoReadNum());
            chatList.setOtherChatCount(otherChatCount + chatMsg.getNoReadNum());
        }
        commitTransaction();
        return chatList != null;
    }

    public void updateMember(long otherUserId, String image, String nick, int chatType, CallBack callBack) {
        beginTransaction();
        ChatList chatList = realm.where(ChatList.class).equalTo("chatType", chatType).equalTo("userId", otherUserId).equalTo("mainUserId", BaseActivity.userId).findFirst();
        if (chatList != null) {
            if (!image.isEmpty())
                chatList.setImage(image);
            if (!nick.isEmpty())
                chatList.setNick(nick);
            chatList.setNoReadNum(0);
            if (TextUtils.equals("每人发10句可以解锁资料哦~", chatList.getStanza()))
                callBack.fail();
            else
                callBack.success();
        } else {
            callBack.fail();
        }
        commitTransaction();
    }

    public void updateMemberForFlash(long otherUserId, String image, String nick, long flashTalkId, int myChatCount, int otherChatCount, CallBack callBack) {
        beginTransaction();
        ChatList chatList = realm.where(ChatList.class).equalTo("flashTalkId", flashTalkId).equalTo("chatType", 6).equalTo("userId", otherUserId).equalTo("mainUserId", BaseActivity.userId).findFirst();
        if (chatList != null) {
            if (!image.isEmpty())
                chatList.setImage(image);
            if (!nick.isEmpty())
                chatList.setNick(nick);
            chatList.setNoReadNum(0);
            chatList.setMyChatCount(myChatCount);
            chatList.setOtherChatCount(otherChatCount);
            if (TextUtils.equals("每人发10句可以解锁资料哦~", chatList.getStanza()))
                callBack.fail();
            else
                callBack.success();
        } else {
            callBack.fail();
        }
        commitTransaction();
    }

    @FunctionalInterface
    public interface CallBack {
        void success();

        default void fail() {
        }
    }

    public int getAllUnReadNum() {
        beginTransaction();
        int unReadNum = 0;
        RealmResults<ChatList> results = realm.where(ChatList.class).notEqualTo("noReadNum", 0).notEqualTo("chatType", 1).equalTo("mainUserId", BaseActivity.userId).findAll();
        if (results.size() > 0) {
            for (ChatList chatList : results) {
                unReadNum += chatList.getNoReadNum();
            }
        }
        commitTransaction();
        return unReadNum;
    }

    public int getChatTabRed() {
        beginTransaction();
        int unReadNum = 0;
        RealmResults<ChatList> results = realm.where(ChatList.class).notEqualTo("noReadNum", 0).greaterThanOrEqualTo("chatType", 4).equalTo("mainUserId", BaseActivity.userId).findAll();
        if (results.size() > 0) {
            for (ChatList chatList : results) {
                unReadNum += chatList.getNoReadNum();
            }
        }
        commitTransaction();
        return unReadNum;
    }

    public void setHasNewBeLike(boolean hasNewBeLike) {
        beginTransaction();
        ChatList chatList = realm.where(ChatList.class).equalTo("chatType", 1).equalTo("userId", BaseActivity.likeUserId).equalTo("mainUserId", BaseActivity.userId).findFirst();
        if (chatList != null)
            chatList.setHasNewBeLike(hasNewBeLike);
        commitTransaction();
    }

}
