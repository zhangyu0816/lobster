package com.zb.module_chat.vm;

import android.content.Context;
import android.content.Intent;

import com.alibaba.mobileim.conversation.IYWConversationService;
import com.alibaba.mobileim.conversation.YWConversation;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.clearAllHistoryMsgApi;
import com.zb.lib_base.api.otherImAccountInfoApi;
import com.zb.lib_base.api.thirdReadChatApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.ChatListDb;
import com.zb.lib_base.db.HistoryMsgDb;
import com.zb.lib_base.db.LikeDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.imcore.LoginSampleHelper;
import com.zb.lib_base.model.ChatList;
import com.zb.lib_base.model.ImAccount;
import com.zb.lib_base.model.LikeMe;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.SimpleItemTouchHelperCallback;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.TextPW;
import com.zb.module_chat.R;
import com.zb.module_chat.adapter.ChatAdapter;
import com.zb.module_chat.databinding.ChatListFragmentBinding;
import com.zb.module_chat.iv.ChatListVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.ItemTouchHelper;
import io.realm.Realm;

public class ChatListViewModel extends BaseViewModel implements ChatListVMInterface, OnRefreshListener {
    public ChatAdapter adapter;
    private List<ChatList> chatMsgList = new ArrayList<>();
    private ChatListFragmentBinding mBinding;
    private ChatListDb chatListDb;
    private BaseReceiver updateChatReceiver;
    private BaseReceiver relieveReceiver;
    private SimpleItemTouchHelperCallback callback;
    private LikeDb likeDb;
    private HistoryMsgDb historyMsgDb;
    private int prePosition = -1;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        chatListDb = new ChatListDb(Realm.getDefaultInstance());
        historyMsgDb = new HistoryMsgDb(Realm.getDefaultInstance());
        likeDb = new LikeDb(Realm.getDefaultInstance());
        mBinding = (ChatListFragmentBinding) binding;
        setAdapter();
        updateChatReceiver = new BaseReceiver(activity, "lobster_updateChat") {
            @Override
            public void onReceive(Context context, Intent intent) {
                long userId = intent.getLongExtra("userId", 0);
                boolean updateImage = intent.getBooleanExtra("updateImage", false);
                if (userId == 0) {
                    chatMsgList.clear();
                    adapter.notifyDataSetChanged();
                    chatMsgList.addAll(chatListDb.getChatList(5));
                    chatMsgList.addAll(chatListDb.getChatList(4));
                    adapter.notifyDataSetChanged();
                } else {
                    int position = -1;
                    for (int i = 0; i < chatMsgList.size(); i++) {
                        if (chatMsgList.get(i) != null)
                            if (chatMsgList.get(i).getUserId() == userId) {
                                position = i;
                                break;
                            }
                    }

                    if (updateImage) {
                        if (position != -1) {
                            chatMsgList.set(position, chatListDb.getChatMsg(userId, userId == BaseActivity.dynUserId ? 5 : 4));
                            adapter.notifyItemChanged(position);
                        }
                    } else {
                        if (position != -1) {
                            adapter.notifyItemRemoved(position);
                            chatMsgList.remove(position);
                        }
                        chatMsgList.add(0, chatListDb.getChatMsg(userId, userId == BaseActivity.dynUserId ? 5 : 4));
                        adapter.notifyDataSetChanged();
                    }
                }
                mBinding.refresh.finishRefresh();
            }
        };
        relieveReceiver = new BaseReceiver(activity, "lobster_relieve") {
            @Override
            public void onReceive(Context context, Intent intent) {
                long otherUserId = intent.getLongExtra("otherUserId", 0);
                if (chatMsgList.size() == 0)
                    return;
                if (prePosition == -1) {
                    for (int i = 0; i < chatMsgList.size(); i++) {
                        if (chatMsgList.get(i).getUserId() == otherUserId) {
                            prePosition = i;
                            break;
                        }
                    }
                }
                if (prePosition != -1) {
                    adapter.notifyItemRemoved(prePosition);
                    chatMsgList.remove(prePosition);
                    chatListDb.deleteChatMsg(otherUserId);
                    prePosition = -1;
                }
                otherImAccountInfoApi(otherUserId);
                clearAllHistoryMsg(otherUserId);
                thirdReadChat(otherUserId);
            }
        };
    }

    public void onDestroy() {
        chatMsgList.clear();
        updateChatReceiver.unregisterReceiver();
        relieveReceiver.unregisterReceiver();
    }

    @Override
    public void setAdapter() {
        chatMsgList.addAll(chatListDb.getChatList(5));

        for (ChatList item : chatListDb.getChatList(4)) {
            boolean has = false;
            for (LikeMe likeMe : MineApp.pairList) {
                if (item.getUserId() == likeMe.getOtherUserId()) {
                    has = true;
                }
            }
            if (has)
                chatMsgList.add(item);
        }
        adapter = new ChatAdapter<>(activity, R.layout.item_chat_list, chatMsgList, this);
        callback = new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mBinding.chatList);
        callback.setSort(false);
        callback.setSwipeEnabled(true);
        callback.setSwipeFlags(ItemTouchHelper.START | ItemTouchHelper.END);
        callback.setDragFlags(0);
        mBinding.refresh.setEnableLoadMore(false);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        activity.sendBroadcast(new Intent("lobster_chatList"));
    }

    @Override
    public void selectIndex(int position) {
        ActivityUtils.getChatActivity(chatMsgList.get(position).getUserId());
    }

    @Override
    public void deleteItem(int position) {
        new TextPW(activity, mBinding.getRoot(), "清除聊天记录", "清除记录后，将无法找回",
                "清除", false, new TextPW.CallBack() {
            @Override
            public void sure() {
                prePosition = position;
                long otherUserId = chatMsgList.get(position).getUserId();
                historyMsgDb.deleteHistoryMsg(otherUserId, 1, 0);
                adapter.notifyItemRemoved(prePosition);
                chatMsgList.remove(prePosition);
                chatListDb.deleteChatMsg(otherUserId);
                otherImAccountInfoApi(otherUserId);
                clearAllHistoryMsg(otherUserId);
                thirdReadChat(otherUserId);
                activity.sendBroadcast(new Intent("lobster_pairList"));
            }

            @Override
            public void cancel() {
                adapter.notifyItemChanged(position);
            }
        });
    }

    /**
     * 对方的阿里百川账号
     */
    private void otherImAccountInfoApi(long otherUserId) {
        otherImAccountInfoApi api = new otherImAccountInfoApi(new HttpOnNextListener<ImAccount>() {
            @Override
            public void onNext(ImAccount o) {
                try {
                    IYWConversationService mConversationService = LoginSampleHelper.imCore
                            .getConversationService();
                    YWConversation conversation = mConversationService
                            .getConversationByUserId(o.getImUserId(), LoginSampleHelper.APP_KEY);
                    // 删除所有聊天记录
                    conversation.getMessageLoader().deleteAllMessage();
                } catch (Exception e) {
                }
            }
        }, activity);
        api.setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void clearAllHistoryMsg(long otherUserId) {
        clearAllHistoryMsgApi api = new clearAllHistoryMsgApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {

            }
        }, activity).setOtherUserId(otherUserId).setMsgChannelType(1).setDriftBottleId(0);
        HttpManager.getInstance().doHttpDeal(api);
    }

    /**
     * 清除未读数量
     */
    private void thirdReadChat(long otherUserId) {
        thirdReadChatApi api = new thirdReadChatApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {

            }
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
