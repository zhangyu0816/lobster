package com.zb.module_chat.vm;

import android.content.Context;
import android.content.Intent;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.pairListApi;
import com.zb.lib_base.api.relievePairApi;
import com.zb.lib_base.db.ChatListDb;
import com.zb.lib_base.db.HistoryMsgDb;
import com.zb.lib_base.db.LikeDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.ChatList;
import com.zb.lib_base.model.CollectID;
import com.zb.lib_base.model.LikeMe;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.SimpleItemTouchHelperCallback;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.TextPW;
import com.zb.lib_base.windows.VipAdPW;
import com.zb.module_chat.R;
import com.zb.module_chat.adapter.ChatAdapter;
import com.zb.module_chat.databinding.ChatPairFragmentBinding;
import com.zb.module_chat.iv.ChatPairVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.ItemTouchHelper;
import io.realm.Realm;

public class ChatPairViewModel extends BaseViewModel implements ChatPairVMInterface, OnRefreshListener, OnLoadMoreListener {

    public ChatAdapter adapter;
    private LikeDb likeDb;
    public List<ChatList> chatMsgList = new ArrayList<>();
    public int pageNo = 1;
    private ChatListDb chatListDb;
    private HistoryMsgDb historyMsgDb;
    private ChatPairFragmentBinding mBinding;
    private MineInfo mineInfo;
    private BaseReceiver pairListReceiver;
    private BaseReceiver updateContactNumReceiver;
    private BaseReceiver relieveReceiver;
    private BaseReceiver finishRefreshReceiver;
    private SimpleItemTouchHelperCallback callback;
    private int prePosition = -1;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        likeDb = new LikeDb(Realm.getDefaultInstance());
        chatListDb = new ChatListDb(Realm.getDefaultInstance());
        historyMsgDb = new HistoryMsgDb(Realm.getDefaultInstance());
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
                int chatType = intent.getIntExtra("chatType", 0);
                boolean isUpdate = intent.getBooleanExtra("isUpdate", false);
                if (isUpdate) {
                    if (chatMsgList.size() != 0) {
                        chatMsgList.set(chatType - 1, chatListDb.getChatList(chatType).get(0));
                        adapter.notifyItemChanged(chatType - 1);
                    }
                } else {
                    if (chatType == 1) {
                        chatMsgList.clear();
                        adapter.notifyDataSetChanged();
                    }
                    chatMsgList.addAll(chatListDb.getChatList(chatType));
                    adapter.notifyItemChanged(chatType - 1);
                    if (chatType == 3) {
                        pairList();
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
                        if (chatMsgList.get(i).getChatType() == 4 && chatMsgList.get(i).getUserId() == otherUserId) {
                            prePosition = i;
                            break;
                        }
                    }
                }
                adapter.notifyItemRemoved(prePosition);
                chatMsgList.remove(prePosition);
                prePosition = -1;
            }
        };
        finishRefreshReceiver = new BaseReceiver(activity, "lobster_finishRefresh") {
            @Override
            public void onReceive(Context context, Intent intent) {
                mBinding.refresh.finishRefresh();
                mBinding.refresh.finishLoadMore();
            }
        };

        setAdapter();
    }

    public void onDestroy() {
        pairListReceiver.unregisterReceiver();
        updateContactNumReceiver.unregisterReceiver();
        relieveReceiver.unregisterReceiver();
        finishRefreshReceiver.unregisterReceiver();
    }

    @Override
    public void setAdapter() {
        adapter = new ChatAdapter<>(activity, R.layout.item_chat_pair, chatMsgList, this);
        callback = new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mBinding.pairList);
        callback.setSort(false);
        callback.setSwipeEnabled(true);
        callback.setSwipeFlags(ItemTouchHelper.START | ItemTouchHelper.END);
        callback.setDragFlags(0);
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
        chatMsgList.addAll(chatListDb.getChatList(1));
        chatMsgList.addAll(chatListDb.getChatList(2));
        chatMsgList.addAll(chatListDb.getChatList(3));
        pairList();
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
            new VipAdPW(activity, mBinding.getRoot(), false, 4);
        } else if (chatList.getChatType() == 2) {
            // 漂流瓶
            ActivityUtils.getBottleList();
        } else if (chatList.getChatType() == 3) {
            // 超级喜欢
            ActivityUtils.getCardMemberDetail(chatList.getUserId(), false);
        } else if (chatList.getChatType() == 4) {
            // 匹配-聊天
            ActivityUtils.getChatActivity(chatList.getUserId());
        }
    }

    @Override
    public void deleteItem(int position) {
        if (chatMsgList.get(position).getChatType() == 4) {
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
        } else {
            adapter.notifyItemChanged(position);
        }
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

    @Override
    public void pairList() {
        pairListApi api = new pairListApi(new HttpOnNextListener<List<LikeMe>>() {
            @Override
            public void onNext(List<LikeMe> o) {
                int start = chatMsgList.size();
                for (LikeMe likeMe : o) {
                    likeDb.saveLike(new CollectID(likeMe.getOtherUserId()));
                    ChatList chatMsg = chatListDb.getChatMsg(likeMe.getOtherUserId(), 4);
                    ChatList chatList = new ChatList();
                    chatList.setUserId(likeMe.getOtherUserId());
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
