package com.zb.module_chat.vm;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.api.chatListApi;
import com.zb.lib_base.db.ChatListDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.ChatMsg;
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
    private List<ChatMsg> chatMsgList = new ArrayList<>();
    private ChatListFragmentBinding mBinding;
    private ChatListDb chatListDb;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        chatListDb = new ChatListDb(Realm.getDefaultInstance());
        mBinding = (ChatListFragmentBinding) binding;
        setAdapter();
    }

    @Override
    public void setAdapter() {
        adapter = new ChatAdapter<>(activity, R.layout.item_chat_list, chatMsgList, this);
        mBinding.refresh.setEnableLoadMore(false);
        chatList();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        chatList();
    }

    @Override
    public void chatList() {
        chatListApi api = new chatListApi(new HttpOnNextListener<List<ChatMsg>>() {
            @Override
            public void onNext(List<ChatMsg> o) {
                for (ChatMsg chatMsg : o) {
                    chatListDb.saveChatList(chatMsg);
                }
                chatMsgList.clear();
                chatMsgList.addAll(chatListDb.getChatList());
                adapter.notifyDataSetChanged();
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void selectIndex(int position) {

    }
}
