package com.zb.module_chat.vm;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.model.ChatMsg;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_chat.R;
import com.zb.module_chat.adapter.ChatAdapter;
import com.zb.module_chat.iv.ChatPairVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

public class ChatPairViewModel extends BaseViewModel implements ChatPairVMInterface , OnRefreshListener {

    public ChatAdapter adapter;
    private List<ChatMsg> chatMsgList = new ArrayList<>();

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        setAdapter();
    }

    @Override
    public void setAdapter() {
        adapter = new ChatAdapter<>(activity, R.layout.item_chat_pair, chatMsgList, this);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {

    }
}
