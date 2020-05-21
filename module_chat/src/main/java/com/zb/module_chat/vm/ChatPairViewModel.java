package com.zb.module_chat.vm;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.api.contactNumApi;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.ChatList;
import com.zb.lib_base.model.ContactNum;
import com.zb.lib_base.utils.PreferenceUtil;
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
    private List<ChatList> chatMsgList = new ArrayList<>();
    private int pageNo = 1;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        setAdapter();
    }

    @Override
    public void setAdapter() {
        adapter = new ChatAdapter<>(activity, R.layout.item_chat_pair, chatMsgList, this);
        contactNum();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        contactNum();
    }

    @Override
    public void selectIndex(int position) {

    }

    @Override
    public void contactNum() {
        contactNumApi api = new contactNumApi(new HttpOnNextListener<ContactNum>() {
            @Override
            public void onNext(ContactNum o) {
                ChatList chatList = new ChatList();
                chatList.setImage("be_like_logo_icon");
                chatList.setNick("查看谁喜欢我");
                chatList.setMsgType(1);
                chatList.setStanza("小姐姐们正在焦急等待你们的回应！");
                chatList.setNoReadNum(o.getBeLikeCount());
                chatMsgList.add(chatList);

                ChatList chatList1 = new ChatList();
                chatList1.setImage("bottle_logo_icon");
                chatList1.setNick("漂流瓶");
                chatList1.setMsgType(1);
                chatList1.setStanza("您有新消息");
                chatList1.setNoReadNum(1);
                chatMsgList.add(chatList1);

                adapter.notifyDataSetChanged();
            }
        }, activity).setOtherUserId(BaseActivity.userId);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
