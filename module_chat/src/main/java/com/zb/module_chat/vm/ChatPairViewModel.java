package com.zb.module_chat.vm;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.api.contactNumApi;
import com.zb.lib_base.api.likeMeListApi;
import com.zb.lib_base.api.noReadBottleNumApi;
import com.zb.lib_base.db.ChatListDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.BottleNoRead;
import com.zb.lib_base.model.ChatList;
import com.zb.lib_base.model.ContactNum;
import com.zb.lib_base.model.LikeMe;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_chat.R;
import com.zb.module_chat.adapter.ChatAdapter;
import com.zb.module_chat.databinding.ChatPairFragmentBinding;
import com.zb.module_chat.iv.ChatPairVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import io.realm.Realm;

public class ChatPairViewModel extends BaseViewModel implements ChatPairVMInterface, OnRefreshListener, OnLoadMoreListener {

    public ChatAdapter adapter;
    private List<ChatList> chatMsgList = new ArrayList<>();
    private int pageNo = 0;
    private ChatListDb chatListDb;
    private ChatPairFragmentBinding mBinding;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        chatListDb = new ChatListDb(Realm.getDefaultInstance());
        mBinding = (ChatPairFragmentBinding) binding;
        setAdapter();
    }

    @Override
    public void setAdapter() {
        adapter = new ChatAdapter<>(activity, R.layout.item_chat_pair, chatMsgList, this);
        contactNum();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        pageNo++;
        likeMeList(1);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        // 下拉刷新
        mBinding.refresh.setEnableLoadMore(true);
        pageNo = 1;
        chatMsgList.clear();
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
                if (o.getBeLikeCount() > 0) {
                    ChatList chatList = new ChatList();
                    chatList.setImage("be_like_logo_icon");
                    chatList.setNick("查看谁喜欢我");
                    chatList.setMsgType(1);
                    chatList.setStanza("小姐姐们正在焦急等待你们的回应！");
                    chatList.setNoReadNum(o.getBeLikeCount());
                    chatList.setChatType(1);
                    chatMsgList.add(chatList);
                }
                adapter.notifyDataSetChanged();
                noReadBottleNum();
            }
        }, activity).setOtherUserId(BaseActivity.userId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void noReadBottleNum() {
        noReadBottleNumApi api = new noReadBottleNumApi(new HttpOnNextListener<BottleNoRead>() {
            @Override
            public void onNext(BottleNoRead o) {
                ChatList chatList = new ChatList();
                chatList.setImage("bottle_logo_icon");
                chatList.setNick("漂流瓶");
                chatList.setMsgType(1);
                chatList.setStanza(o.getNoReadNum() == 0 ? "漂流瓶很安静啊~" : "您有新消息");
                chatList.setNoReadNum(o.getNoReadNum());
                chatList.setChatType(2);
                chatMsgList.add(chatList);
                adapter.notifyDataSetChanged();
                likeMeList(2);
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void likeMeList(int likeOtherStatus) {
        likeMeListApi api = new likeMeListApi(new HttpOnNextListener<List<LikeMe>>() {
            @Override
            public void onNext(List<LikeMe> o) {
                int start = chatMsgList.size();
                for (LikeMe likeMe : o) {
                    ChatList chatMsg = chatListDb.getChatMsg(likeMe.getOtherUserId());
                    ChatList chatList = new ChatList();
                    chatList.setImage(likeMe.getHeadImage());
                    chatList.setNick(likeMe.getNick());
                    chatList.setMsgType(chatMsg != null ? chatMsg.getMsgType() : 1);
                    chatList.setStanza(likeMe.getLikeOtherStatus() == 2 ? "超级喜欢你！" : (chatMsg != null ? chatMsg.getStanza() : "匹配于" + likeMe.getModifyTime().substring(4, 10)));
                    chatList.setNoReadNum(chatMsg != null ? chatMsg.getNoReadNum() : 0);
                    chatList.setChatType(likeMe.getLikeOtherStatus() == 2 ? 3 : 4);
                    chatList.setCreationDate(chatMsg != null ? chatMsg.getCreationDate() : likeMe.getModifyTime());
                    chatMsgList.add(chatList);
                }
                adapter.notifyItemRangeChanged(start, chatMsgList.size());
                mBinding.refresh.finishRefresh();
                mBinding.refresh.finishLoadMore();

                if (pageNo == 0) {
                    pageNo++;
                    likeMeList(1);
                }
            }
        }, activity).setPageNo(pageNo).setLikeOtherStatus(likeOtherStatus);
        HttpManager.getInstance().doHttpDeal(api);
    }


}
