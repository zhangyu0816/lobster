package com.zb.module_chat.vm;

import android.content.Context;
import android.content.Intent;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.chatListApi;
import com.zb.lib_base.db.ChatListDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.ChatList;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_chat.R;
import com.zb.module_chat.adapter.ChatAdapter;
import com.zb.module_chat.databinding.ChatListFragmentBinding;
import com.zb.module_chat.iv.ChatListVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import io.realm.Realm;

public class ChatListViewModel extends BaseViewModel implements ChatListVMInterface, OnRefreshListener {
    public ChatAdapter adapter;
    private List<ChatList> chatMsgList = new ArrayList<>();
    private ChatListFragmentBinding mBinding;
    private ChatListDb chatListDb;
    private BaseReceiver updateChatReceiver;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        chatListDb = new ChatListDb(Realm.getDefaultInstance());
        mBinding = (ChatListFragmentBinding) binding;
        setAdapter();
        updateChatReceiver = new BaseReceiver(activity, "lobster_updateChat") {
            @Override
            public void onReceive(Context context, Intent intent) {
                chatMsgList.clear();
                adapter.notifyDataSetChanged();
                chatMsgList.addAll(chatListDb.getChatList());
                adapter.notifyDataSetChanged();
                mBinding.refresh.finishRefresh();
            }
        };
    }

    @Override
    public void setAdapter() {
        chatMsgList.addAll(chatListDb.getChatList());
        adapter = new ChatAdapter<>(activity, R.layout.item_chat_list, chatMsgList, this);
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
}
