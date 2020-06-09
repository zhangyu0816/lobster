package com.zb.module_chat.vm;

import android.content.Context;
import android.content.Intent;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.likeMeListApi;
import com.zb.lib_base.api.noReadBottleNumApi;
import com.zb.lib_base.api.pairListApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.ChatListDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.ChatList;
import com.zb.lib_base.model.LikeMe;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.SCToastUtil;
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
    private int pageNo = 1;
    private ChatListDb chatListDb;
    private ChatPairFragmentBinding mBinding;
    private MineInfo mineInfo;
    private BaseReceiver pairListReceiver;
    private BaseReceiver updateContactNumReceiver;
    private BaseReceiver relieveReceiver;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        chatListDb = new ChatListDb(Realm.getDefaultInstance());
        mineInfo = mineInfoDb.getMineInfo();
        mBinding = (ChatPairFragmentBinding) binding;
        pairListReceiver = new BaseReceiver(activity, "lobster_pairList") {
            @Override
            public void onReceive(Context context, Intent intent) {
                onRefresh(mBinding.refresh);
            }
        };
        updateContactNumReceiver = new BaseReceiver(activity, "lobster_updateContactNum") {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (MineApp.contactNum.getBeLikeCount() > 0) {
                    if (chatMsgList.size() > 0) {
                        ChatList chatList = chatMsgList.get(0);
                        if (chatList.getChatType() == 1) {
                            if (chatList.getNoReadNum() != MineApp.contactNum.getBeLikeCount()) {
                                chatList.setNoReadNum(MineApp.contactNum.getBeLikeCount());
                                adapter.notifyItemChanged(0);
                            }
                        } else {
                            ChatList chatList1 = new ChatList();
                            chatList1.setImage("be_like_logo_icon");
                            chatList1.setNick("查看谁喜欢我");
                            chatList1.setMsgType(1);
                            chatList1.setStanza("小姐姐们正在焦急等待你们的回应！");
                            chatList1.setNoReadNum(MineApp.contactNum.getBeLikeCount());
                            chatList1.setChatType(1);
                            chatMsgList.add(0, chatList);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        };
        relieveReceiver = new BaseReceiver(activity, "lobster_relieve") {
            @Override
            public void onReceive(Context context, Intent intent) {
                long otherUserId = intent.getLongExtra("otherUserId", 0);
                for (int i = 0; i < chatMsgList.size(); i++) {
                    if (chatMsgList.get(i).getChatType() == 4 && chatMsgList.get(i).getUserId() == otherUserId) {
                        adapter.notifyItemRemoved(i);
                        chatMsgList.remove(i);
                        break;
                    }
                }
            }
        };
        setAdapter();
    }

    public void onDestroy() {
        pairListReceiver.unregisterReceiver();
        updateContactNumReceiver.unregisterReceiver();
        relieveReceiver.unregisterReceiver();
    }

    @Override
    public void setAdapter() {
        adapter = new ChatAdapter<>(activity, R.layout.item_chat_pair, chatMsgList, this);
        contactNum();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        pageNo++;
        pairList();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        // 下拉刷新
        mBinding.refresh.setEnableLoadMore(true);
        pageNo = 1;
        chatMsgList.clear();
        adapter.notifyDataSetChanged();
        contactNum();
    }

    @Override
    public void selectIndex(int position) {
        ChatList chatList = chatMsgList.get(position);
        if (chatList.getChatType() == 1) {
            // 喜欢我
            if (mineInfo.getMemberType() == 2) {
                ActivityUtils.getMineFCL(2);
                return;
            }
            SCToastUtil.showToastBlack(activity, "查看被喜欢的人为VIP用户专享功能");
        } else if (chatList.getChatType() == 2) {
            // 漂流瓶
            ActivityUtils.getBottleList();
        } else if (chatList.getChatType() == 3) {
            // 超级喜欢
            ActivityUtils.getCardMemberDetail(chatList.getUserId());
        } else if (chatList.getChatType() == 4) {
            // 匹配-聊天
            ActivityUtils.getChatActivity(chatList.getUserId());
        }
    }

    @Override
    public void contactNum() {
        if (MineApp.contactNum.getBeLikeCount() > 0) {
            ChatList chatList = new ChatList();
            chatList.setImage("be_like_logo_icon");
            chatList.setNick("查看谁喜欢我");
            chatList.setMsgType(1);
            chatList.setStanza("小姐姐们正在焦急等待你们的回应！");
            chatList.setNoReadNum(MineApp.contactNum.getBeLikeCount());
            chatList.setChatType(1);
            chatMsgList.add(chatList);
        }
        adapter.notifyDataSetChanged();
        noReadBottleNum();
    }

    @Override
    public void noReadBottleNum() {
        noReadBottleNumApi api = new noReadBottleNumApi(new HttpOnNextListener<Integer>() {
            @Override
            public void onNext(Integer o) {
                ChatList chatList = new ChatList();
                chatList.setImage("bottle_logo_icon");
                chatList.setNick("漂流瓶");
                chatList.setMsgType(1);
                chatList.setStanza(o == 0 ? "漂流瓶很安静啊~" : "您有新消息");
                chatList.setNoReadNum(o);
                chatList.setChatType(2);
                chatMsgList.add(chatList);
                adapter.notifyDataSetChanged();
                likeMeList();
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void likeMeList() {
        likeMeListApi api = new likeMeListApi(new HttpOnNextListener<List<LikeMe>>() {
            @Override
            public void onNext(List<LikeMe> o) {
                int start = chatMsgList.size();
                for (LikeMe likeMe : o) {
                    ChatList chatList = new ChatList();
                    chatList.setUserId(likeMe.getUserId());
                    chatList.setImage(likeMe.getHeadImage());
                    chatList.setNick(likeMe.getNick());
                    chatList.setMsgType(1);
                    chatList.setStanza("超级喜欢你！");
                    chatList.setNoReadNum(0);
                    chatList.setChatType(3);
                    chatList.setCreationDate(likeMe.getModifyTime());
                    chatMsgList.add(chatList);
                }
                adapter.notifyItemRangeChanged(start, chatMsgList.size());
                pairList();
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    pairList();
                }
            }
        }, activity).setPageNo(0).setLikeOtherStatus(2);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void pairList() {
        pairListApi api = new pairListApi(new HttpOnNextListener<List<LikeMe>>() {
            @Override
            public void onNext(List<LikeMe> o) {
                int start = chatMsgList.size();
                for (LikeMe likeMe : o) {
                    ChatList chatMsg = chatListDb.getChatMsg(likeMe.getOtherUserId());
                    ChatList chatList = new ChatList();
                    chatList.setUserId(likeMe.getUserId());
                    chatList.setImage(likeMe.getHeadImage());
                    chatList.setNick(likeMe.getNick());
                    chatList.setMsgType(chatMsg != null ? chatMsg.getMsgType() : 1);
                    chatList.setStanza(chatMsg != null ? chatMsg.getStanza() : "匹配于" + (likeMe.getPairTime().isEmpty() ? likeMe.getPairTime() : likeMe.getPairTime().substring(5, 10)));
                    chatList.setNoReadNum(chatMsg != null ? chatMsg.getNoReadNum() : 0);
                    chatList.setChatType(4);
                    chatList.setCreationDate(chatMsg != null ? chatMsg.getCreationDate() : likeMe.getModifyTime());
                    chatMsgList.add(chatList);
                }
                adapter.notifyItemRangeChanged(start, chatMsgList.size());
                mBinding.refresh.finishRefresh();
                mBinding.refresh.finishLoadMore();
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    mBinding.refresh.finishRefresh();
                    mBinding.refresh.finishLoadMore();
                    mBinding.refresh.setEnableLoadMore(false);
                }
            }
        }, activity).setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
