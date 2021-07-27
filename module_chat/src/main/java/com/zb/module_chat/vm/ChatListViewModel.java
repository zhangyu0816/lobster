package com.zb.module_chat.vm;

import android.content.Context;
import android.content.Intent;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.clearAllHistoryMsgApi;
import com.zb.lib_base.api.flashClearHistoryMsgApi;
import com.zb.lib_base.api.thirdReadChatApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.ChatListDb;
import com.zb.lib_base.db.HistoryMsgDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.ChatList;
import com.zb.lib_base.model.LikeMe;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DateUtil;
import com.zb.lib_base.utils.SimpleItemTouchHelperCallback;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.TextPW;
import com.zb.module_chat.R;
import com.zb.module_chat.adapter.ChatAdapter;
import com.zb.module_chat.databinding.ChatListFragmentBinding;
import com.zb.module_chat.iv.ChatListVMInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
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
                long flashTalkId = intent.getLongExtra("flashTalkId", 0);
                if (userId == 0) {
                    chatMsgList.clear();
                    adapter.notifyDataSetChanged();
                    chatMsgList.addAll(ChatListDb.getInstance().getChatList(5));
                    chatMsgList.addAll(ChatListDb.getInstance().getChatList(6));
                    chatMsgList.addAll(ChatListDb.getInstance().getChatList(4));
                    Collections.sort(chatMsgList, new ChatComparator());
                    adapter.notifyDataSetChanged();
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
                    ChatList chatList = ChatListDb.getInstance().getChatMsg(userId, flashTalkId != 0 ? 6 : (userId == BaseActivity.dynUserId ? 5 : 4));
                    if (chatList == null) {
                        mBinding.refresh.finishRefresh();
                        return;
                    }

                    if (updateImage) {
                        if (position != -1) {
                            chatMsgList.set(position, chatList);
                            adapter.notifyItemChanged(position);
                        } else {
                            chatMsgList.add(0, chatList);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        if (position != -1) {
                            adapter.notifyItemRemoved(position);
                            chatMsgList.remove(position);
                            adapter.notifyDataSetChanged();
                        }
                        chatMsgList.add(0, chatList);
                        adapter.notifyDataSetChanged();
                    }
                }
                LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_updateRed"));
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
                        ChatList chatList = chatMsgList.remove(position);
                        adapter.notifyDataSetChanged();
                        LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_updateRed"));
                        HistoryMsgDb.getInstance().deleteHistoryMsg(otherUserId, chatList.getChatType() == 6 ? 3 : 1, 0, chatList.getFlashTalkId());
                        ChatListDb.getInstance().deleteChatMsg(otherUserId);

                        if (chatList.getFlashTalkId() == 0) {
                            clearAllHistoryMsg(otherUserId);
                            thirdReadChat(otherUserId);
                        } else {
                            flashClearHistoryMsg(otherUserId, chatList.getFlashTalkId());
                        }
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
        chatMsgList.addAll(ChatListDb.getInstance().getChatList(6));
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
        Collections.sort(chatMsgList, new ChatComparator());
        adapter = new ChatAdapter<>(activity, R.layout.item_chat_list, chatMsgList, this);
        LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_updateRed"));
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
        LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_chatList"));
    }

    @Override
    public void selectIndex(int position) {
        ChatList chatList = chatMsgList.get(position);
        if (chatList.getFlashTalkId() == 0)
            ActivityUtils.getChatActivity(chatList.getUserId(), false);
        else
            ActivityUtils.getFlashChatActivity(chatList.getUserId(), chatList.getFlashTalkId(), false);
    }

    @Override
    public void deleteItem(int position) {
        new TextPW(mBinding.getRoot(), "清除聊天记录", "清除记录后，将无法找回",
                "清除", false, new TextPW.CallBack() {
            @Override
            public void sure() {
                long otherUserId = chatMsgList.get(position).getUserId();
                Intent data = new Intent("lobster_relieve");
                data.putExtra("otherUserId", otherUserId);
                data.putExtra("isRelieve", false);
                LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(data);
            }

            @Override
            public void cancel() {
                adapter.notifyItemChanged(position);
                LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_updateRed"));
            }
        });
    }

    private void clearAllHistoryMsg(long otherUserId) {
        clearAllHistoryMsgApi api = new clearAllHistoryMsgApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_unReadCount"));
            }
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void flashClearHistoryMsg(long otherUserId, long flashTalkId) {
        flashClearHistoryMsgApi api = new flashClearHistoryMsgApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_unReadCount"));
            }
        }, activity).setOtherUserId(otherUserId).setFlashTalkId(flashTalkId);
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

    public static class ChatComparator implements Comparator<ChatList> {
        @Override
        public int compare(ChatList o1, ChatList o2) {
            if (o1 == null && o2 == null) {
                return 0;
            }
            if (o1 == null) {
                return -1;
            }
            if (o2 == null) {
                return 1;
            }
            if (o1.getCreationDate().isEmpty())
                return -1;
            if (o2.getCreationDate().isEmpty())
                return -1;
            return DateUtil.getDateCount(o2.getCreationDate(), o1.getCreationDate(), DateUtil.yyyy_MM_dd_HH_mm_ss, 1000f);
        }
    }
}
