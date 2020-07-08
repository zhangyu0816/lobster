package com.zb.module_chat.vm;

import android.content.Context;
import android.content.Intent;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.relievePairApi;
import com.zb.lib_base.db.ChatListDb;
import com.zb.lib_base.db.HistoryMsgDb;
import com.zb.lib_base.db.LikeDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.ChatList;
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
                if (userId == 0) {
                    chatMsgList.clear();
                    adapter.notifyDataSetChanged();
                    chatMsgList.addAll(chatListDb.getChatList(4));
                    adapter.notifyDataSetChanged();
                    mBinding.refresh.finishRefresh();
                } else {
                    int position = -1;
                    for (int i = 0; i < chatMsgList.size(); i++) {
                        if (chatMsgList.get(i) != null)
                            if (chatMsgList.get(i).getUserId() == userId) {
                                position = i;
                                break;
                            }
                    }
                    if (position != -1) {
                        chatMsgList.set(position, chatListDb.getChatMsg(userId, 4));
                        adapter.notifyItemChanged(position);
                    } else {
                        chatMsgList.add(0, chatListDb.getChatMsg(userId, 4));
                        adapter.notifyDataSetChanged();
                    }
                }
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
                adapter.notifyItemRemoved(prePosition);
                chatMsgList.remove(prePosition);
                chatListDb.deleteChatMsg(otherUserId);
                prePosition = -1;
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
        chatMsgList.addAll(chatListDb.getChatList(4));
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
        new TextPW(activity, mBinding.getRoot(), "解除匹配关系", "解除匹配关系后，将对方移除匹配列表及聊天列表。",
                "解除", false, new TextPW.CallBack() {
            @Override
            public void sure() {
                prePosition = position;
                relievePair(chatMsgList.get(position).getUserId());
            }

            @Override
            public void cancel() {
                adapter.notifyItemChanged(position);
            }
        });
    }

    private void relievePair(long otherUserId) {
        relievePairApi api = new relievePairApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                likeDb.deleteLike(otherUserId);
                historyMsgDb.deleteHistoryMsg(otherUserId);
                Intent data = new Intent("lobster_relieve");
                data.putExtra("otherUserId", otherUserId);
                activity.sendBroadcast(data);
            }
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
