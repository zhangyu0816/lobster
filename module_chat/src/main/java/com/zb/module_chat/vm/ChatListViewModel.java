package com.zb.module_chat.vm;

import android.content.Context;
import android.content.Intent;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.ChatListDb;
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
import io.realm.Realm;

public class ChatListViewModel extends BaseViewModel implements ChatListVMInterface, OnRefreshListener {
    public ChatAdapter adapter;
    private List<ChatList> chatMsgList = new ArrayList<>();
    private ChatListFragmentBinding mBinding;
    private ChatListDb chatListDb;
    private BaseReceiver updateChatReceiver;
    private BaseReceiver relieveReceiver;
    private SimpleItemTouchHelperCallback callback;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        chatListDb = new ChatListDb(Realm.getDefaultInstance());
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
                    activity.sendBroadcast(new Intent("lobster_updateRed"));
                } else {
                    int position = -1;
                    if (chatMsgList.size() > 0) {
                        for (int i = 0; i < chatMsgList.size(); i++) {
                            if (chatMsgList.get(i) != null && chatMsgList.get(i).getUserId() == userId) {
                                position = i;
                                break;
                            }
                        }
                    }
                    ChatList chatList = chatListDb.getChatMsg(userId, userId == BaseActivity.dynUserId ? 5 : 4);
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
                        }
                        chatMsgList.add(0, chatList);
                        adapter.notifyDataSetChanged();
                        activity.sendBroadcast(new Intent("lobster_updateRed"));
                    }
                }
                mBinding.refresh.finishRefresh();
            }
        };
        relieveReceiver = new BaseReceiver(activity, "lobster_relieve") {
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
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        activity.sendBroadcast(new Intent("lobster_updateRed"));
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
}
