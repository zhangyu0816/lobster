package com.zb.module_chat.vm;

import android.content.Context;
import android.content.Intent;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.clearAllHistoryMsgApi;
import com.zb.lib_base.api.thirdReadChatApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.ChatListDb;
import com.zb.lib_base.db.HistoryMsgDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.imcore.ImUtils;
import com.zb.lib_base.model.ChatList;
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

public class ChatListViewModel extends BaseViewModel implements ChatListVMInterface, OnRefreshListener {
    public ChatAdapter adapter;
    private List<ChatList> chatMsgList = new ArrayList<>();
    private ChatListFragmentBinding mBinding;
    private BaseReceiver updateChatReceiver;
    private BaseReceiver relieveReceiver;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
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
                    chatMsgList.addAll(ChatListDb.getInstance().getChatList(5));
                    chatMsgList.addAll(ChatListDb.getInstance().getChatList(4));
                    adapter.notifyDataSetChanged();
                    activity.sendBroadcast(new Intent("lobster_updateRed"));
                } else {
                    int position = -1;
                    if (chatMsgList.size() > 0) {
                        for (int i = 0; i < chatMsgList.size(); i++) {
                            if (chatMsgList.get(i) != null)
                                if (chatMsgList.get(i).getUserId() == userId) {
                                    position = i;
                                    break;
                                }
                        }
                    }
                    ChatList chatList = ChatListDb.getInstance().getChatMsg(userId, userId == BaseActivity.dynUserId ? 5 : 4);
                    if (chatList == null) {
                        mBinding.refresh.finishRefresh();
                        return;
                    }
                    if (updateImage) {
                        if (position != -1) {
                            chatMsgList.set(position, chatList);
                            adapter.notifyItemChanged(position);
                            activity.sendBroadcast(new Intent("lobster_updateRed"));
                        }
                    } else {
                        if (position != -1) {
                            adapter.notifyItemRemoved(position);
                            chatMsgList.remove(position);
                            adapter.notifyDataSetChanged();
                        }
                        chatMsgList.add(0, chatList);
                        adapter.notifyDataSetChanged();
                        activity.sendBroadcast(new Intent("lobster_updateRed"));
                    }
                }
                mBinding.refresh.finishRefresh();
            }
        };
        relieveReceiver = new BaseReceiver(activity, "lobster_relieve_1") {
            @Override
            public void onReceive(Context context, Intent intent) {
                long otherUserId = intent.getLongExtra("otherUserId", 0);
                int position = -1;
                try {
                    if (chatMsgList.size() > 0) {
                        for (int i = 0; i < chatMsgList.size(); i++) {
                            ChatList item = chatMsgList.get(i);
                            if (item != null && item.getUserId() == otherUserId) {
                                position = i;
                                break;
                            }
                        }
                    }
                    if (position != -1) {
                        adapter.notifyItemRemoved(position);
                        chatMsgList.remove(position);
                        adapter.notifyDataSetChanged();
                        activity.sendBroadcast(new Intent("lobster_updateRed"));
                        HistoryMsgDb.getInstance().deleteHistoryMsg(otherUserId, 1, 0);
                        ChatListDb.getInstance().deleteChatMsg(otherUserId);
                        ImUtils.getInstance().setOtherUserId(otherUserId);
                        ImUtils.getInstance().setDelete(true, activity);
                        clearAllHistoryMsg(otherUserId);
                        thirdReadChat(otherUserId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public void onDestroy() {
        chatMsgList.clear();
        try {
            updateChatReceiver.unregisterReceiver();
            relieveReceiver.unregisterReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setAdapter() {
        chatMsgList.addAll(ChatListDb.getInstance().getChatList(5));
        for (ChatList item : ChatListDb.getInstance().getChatList(4)) {
            boolean has = false;
            for (LikeMe likeMe : MineApp.pairList) {
                if (item.getUserId() == likeMe.getOtherUserId()) {
                    has = true;
                    break;
                }
            }
            if (has)
                chatMsgList.add(item);
        }
        adapter = new ChatAdapter<>(activity, R.layout.item_chat_list, chatMsgList, this);
        activity.sendBroadcast(new Intent("lobster_updateRed"));
        SimpleItemTouchHelperCallback callback = new SimpleItemTouchHelperCallback(adapter);
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
        ActivityUtils.getChatActivity(chatMsgList.get(position).getUserId(),false);
    }

    @Override
    public void deleteItem(int position) {
        new TextPW(activity, mBinding.getRoot(), "清除聊天记录", "清除记录后，将无法找回",
                "清除", false, new TextPW.CallBack() {
            @Override
            public void sure() {
                long otherUserId = chatMsgList.get(position).getUserId();
                Intent data = new Intent("lobster_relieve");
                data.putExtra("otherUserId", otherUserId);
                data.putExtra("isRelieve", false);
                activity.sendBroadcast(data);
            }

            @Override
            public void cancel() {
                adapter.notifyItemChanged(position);
                activity.sendBroadcast(new Intent("lobster_updateRed"));
            }
        });
    }

    private void clearAllHistoryMsg(long otherUserId) {
        clearAllHistoryMsgApi api = new clearAllHistoryMsgApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                activity.sendBroadcast(new Intent("lobster_unReadCount"));
            }
        }, activity).setOtherUserId(otherUserId);
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
