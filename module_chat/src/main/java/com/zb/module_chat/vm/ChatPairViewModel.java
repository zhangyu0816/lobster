package com.zb.module_chat.vm;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.likeMeListApi;
import com.zb.lib_base.api.pairListApi;
import com.zb.lib_base.api.personOtherDynApi;
import com.zb.lib_base.api.relievePairApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.ChatListDb;
import com.zb.lib_base.db.LikeDb;
import com.zb.lib_base.db.LikeTypeDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.ChatList;
import com.zb.lib_base.model.CollectID;
import com.zb.lib_base.model.DiscoverInfo;
import com.zb.lib_base.model.LikeMe;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DateUtil;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.utils.SimpleItemTouchHelperCallback;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.SelectorPW;
import com.zb.lib_base.windows.TextPW;
import com.zb.lib_base.windows.VipAdPW;
import com.zb.module_chat.R;
import com.zb.module_chat.adapter.ChatAdapter;
import com.zb.module_chat.databinding.ChatPairFragmentBinding;
import com.zb.module_chat.iv.ChatPairVMInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.ItemTouchHelper;

public class ChatPairViewModel extends BaseViewModel implements ChatPairVMInterface, OnRefreshListener {

    public ChatAdapter adapter;
    public List<ChatList> chatMsgList = new ArrayList<>();
    private ChatPairFragmentBinding mBinding;
    private BaseReceiver pairListReceiver;
    private BaseReceiver updateChatTypeReceiver;
    private BaseReceiver relieveReceiver;
    private BaseReceiver updateChatReceiver;
    private BaseReceiver publishReceiver;
    private List<ChatList> chatType4List = new ArrayList<>();
    private List<String> selectorList = new ArrayList<>();

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (ChatPairFragmentBinding) binding;
        selectorList.add("发布照片");
        selectorList.add("发布小视频");
        pairListReceiver = new BaseReceiver(activity, "lobster_pairList") {
            @Override
            public void onReceive(Context context, Intent intent) {
                onRefresh(mBinding.refresh);
            }
        };
        publishReceiver = new BaseReceiver(activity, "lobster_publish") {
            @Override
            public void onReceive(Context context, Intent intent) {
                onRefresh(mBinding.refresh);
            }
        };
        updateChatTypeReceiver = new BaseReceiver(activity, "lobster_updateChatType") {
            @Override
            public void onReceive(Context context, Intent intent) {
                // chatType  1：被喜欢  2：漂流瓶  3：被超级喜欢  4：匹配  5：动态
                int chatType = intent.getIntExtra("chatType", 0);
                boolean isUpdate = intent.getBooleanExtra("isUpdate", false);
                try {
                    if (isUpdate) {
                        if (chatMsgList.size() != 0) {
                            chatMsgList.set(chatType == 1 ? 1 : 0, ChatListDb.getInstance().getChatList(chatType).get(0));
                            adapter.notifyItemChanged(chatType == 1 ? 1 : 0);
                        }
                    }
                } catch (Exception ignored) {
                }

            }
        };
        relieveReceiver = new BaseReceiver(activity, "lobster_relieve") {
            @Override
            public void onReceive(Context context, Intent intent) {
                long otherUserId = intent.getLongExtra("otherUserId", 0);
                boolean isRelieve = intent.getBooleanExtra("isRelieve", false);
                try {
                    if (isRelieve) {
                        LikeDb.getInstance().deleteLike(otherUserId);
                        LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_contactNum"));
                    }

                    Intent data = new Intent("lobster_relieve_1");
                    data.putExtra("otherUserId", otherUserId);
                    data.putExtra("isRelieve", false);
                    LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                onRefresh(mBinding.refresh);
            }
        };
        updateChatReceiver = new BaseReceiver(activity, "lobster_updateChat") {
            @Override
            public void onReceive(Context context, Intent intent) {
                long userId = intent.getLongExtra("userId", 0);
                long flashTalkId = intent.getLongExtra("flashTalkId", 0);
                if (flashTalkId != 0) return;
                if (userId != 0) {
                    for (int i = 0; i < chatMsgList.size(); i++) {
                        if (chatMsgList.get(i) != null)
                            if (chatMsgList.get(i).getChatType() == 4 && chatMsgList.get(i).getUserId() == userId) {
                                chatMsgList.set(i, ChatListDb.getInstance().getChatMsg(userId, 4));
                                adapter.notifyItemChanged(i);
                                break;
                            }
                    }
                }
            }
        };
        setAdapter();
    }

    public void onDestroy() {
        try {
            pairListReceiver.unregisterReceiver();
            updateChatTypeReceiver.unregisterReceiver();
            relieveReceiver.unregisterReceiver();
            updateChatReceiver.unregisterReceiver();
            publishReceiver.unregisterReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setAdapter() {
        adapter = new ChatAdapter<>(activity, R.layout.item_chat_pair, chatMsgList, this);
        SimpleItemTouchHelperCallback callback = new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mBinding.pairList);
        callback.setSort(false);
        callback.setSwipeEnabled(true);
        callback.setSwipeFlags(ItemTouchHelper.START | ItemTouchHelper.END);
        callback.setDragFlags(0);
        mBinding.refresh.setEnableLoadMore(false);

        MineApp.getApp().getFixedThreadPool().execute(() -> {
            SystemClock.sleep(500);
            activity.runOnUiThread(() -> onRefresh(mBinding.refresh));
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        // 下拉刷新
        chatType4List.clear();
        chatMsgList.clear();
        adapter.notifyDataSetChanged();
        chatMsgList.addAll(ChatListDb.getInstance().getChatList(2));
        chatMsgList.addAll(ChatListDb.getInstance().getChatList(1));
        personOtherDyn();
    }

    @Override
    public void selectIndex(int position) {
        ChatList chatList = chatMsgList.get(position);
        if (chatList.getChatType() == 1) {
            // 喜欢我
            if (MineApp.mineInfo.getMemberType() == 2) {
                PreferenceUtil.saveIntValue(activity, "beLikeCount" + BaseActivity.userId, MineApp.contactNum.getBeLikeCount());
                ChatListDb.getInstance().setHasNewBeLike(false);
                adapter.notifyItemChanged(1);
                ActivityUtils.getMineFCL(2, 0);
                return;
            }
            new VipAdPW(mBinding.getRoot(), 4, "");
        } else if (chatList.getChatType() == 2) {
            // 漂流瓶
            ActivityUtils.getBottleList();
        } else if (chatList.getChatType() == 3) {
            // 超级喜欢
            ActivityUtils.getCardMemberDetail(chatList.getUserId(), false);
        } else if (chatList.getChatType() == 4) {
            // 匹配-聊天
            ActivityUtils.getChatActivity(chatList.getUserId(), false);
        } else if (chatList.getChatType() == 10) {
            if (checkPermissionGranted(activity, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)) {
                setPermissions();
            } else {
                if (PreferenceUtil.readIntValue(activity, "publishPermission") == 0) {
                    PreferenceUtil.saveIntValue(activity, "publishPermission", 1);
                    new TextPW(activity, mBinding.getRoot(), "权限说明",
                            "在使用发布动态功能，包括图文、视频时，我们将会申请相机、存储、麦克风权限：" +
                                    "\n 1、申请相机权限--发布动态时获取拍摄照片，录制视频功能，" +
                                    "\n 2、申请存储权限--发布动态时获取保存和读取图片、视频，" +
                                    "\n 3、申请麦克风权限--发布视频动态时获取录制视频音频功能，" +
                                    "\n 4、若您点击“同意”按钮，我们方可正式申请上述权限，以便正常发布图文动态、视频动态，" +
                                    "\n 5、若您点击“拒绝”按钮，我们将不再主动弹出该提示，您也无法使用发布动态功能，不影响使用其他的虾菇功能/服务，" +
                                    "\n 6、您也可以通过“手机设置--应用--虾菇--权限”或app内“我的--设置--权限管理--权限”，手动开启或关闭相机、存储、麦克风权限。",
                            "同意", false, true, this::getPermissions);
                } else {
                    if (!checkPermissionGranted(activity, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        SCToastUtil.showToast(activity, "你未开启存储权限，请前往我的--设置--权限管理--权限进行设置", true);
                    } else if (!checkPermissionGranted(activity, Manifest.permission.CAMERA)) {
                        SCToastUtil.showToast(activity, "你未开启相机权限，请前往我的--设置--权限管理--权限进行设置", true);
                    } else if (!checkPermissionGranted(activity, Manifest.permission.RECORD_AUDIO)) {
                        SCToastUtil.showToast(activity, "你未开启麦克风权限，请前往我的--设置--权限管理--权限进行设置", true);
                    }
                }
            }
        }
    }

    @Override
    public void deleteItem(int position) {
        if (chatMsgList.get(position).getChatType() == 4) {
            new TextPW(mBinding.getRoot(), "解除匹配关系", "解除匹配关系后，将对方移除匹配列表及聊天列表。",
                    "解除", false, new TextPW.CallBack() {
                @Override
                public void sure() {
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

    private void personOtherDyn() {
        personOtherDynApi api = new personOtherDynApi(new HttpOnNextListener<List<DiscoverInfo>>() {
            @Override
            public void onNext(List<DiscoverInfo> o) {
                int a = DateUtil.getDateCount(DateUtil.getNow(DateUtil.yyyy_MM_dd_HH_mm_ss), o.get(0).getCreateTime(), DateUtil.yyyy_MM_dd_HH_mm_ss, 1000f * 3600f * 24f);
                if (DateUtil.getDateCount(DateUtil.getNow(DateUtil.yyyy_MM_dd_HH_mm_ss), o.get(0).getCreateTime(), DateUtil.yyyy_MM_dd_HH_mm_ss, 1000f * 3600f * 24f) >= 2) {
                    int start = chatMsgList.size();
                    ChatList chatList = new ChatList();
                    chatList.setUserId(BaseActivity.systemUserId);
                    chatList.setImage("ic_chat_xiagu");
                    chatList.setNick("虾菇");
                    chatList.setMsgType(1);
                    chatList.setStanza("好久没发布了，期待着你更新照片和视频哦！");
                    chatList.setNoReadNum(0);
                    chatList.setChatType(10);
                    chatList.setCreationDate(DateUtil.getNow(DateUtil.yyyy_MM_dd_HH_mm_ss));
                    chatMsgList.add(chatList);
                    adapter.notifyItemRangeChanged(start, chatMsgList.size());
                }
                MineApp.pairList.clear();
                beSuperLikeList();
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    int start = chatMsgList.size();
                    ChatList chatList = new ChatList();
                    chatList.setUserId(BaseActivity.systemUserId);
                    chatList.setImage("ic_chat_xiagu");
                    chatList.setNick("虾菇");
                    chatList.setMsgType(1);
                    chatList.setStanza("好久没发布了，期待着你更新照片和视频哦！");
                    chatList.setNoReadNum(0);
                    chatList.setChatType(10);
                    chatList.setCreationDate(DateUtil.getNow(DateUtil.yyyy_MM_dd_HH_mm_ss));
                    chatMsgList.add(chatList);
                    adapter.notifyItemRangeChanged(start, chatMsgList.size());
                    MineApp.pairList.clear();
                    beSuperLikeList();
                }
            }
        }, activity)
                .setDynType(0)
                .setOtherUserId(BaseActivity.userId)
                .setPageNo(1);
        HttpManager.getInstance().doHttpDeal(api);
    }

    /**
     * 权限
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            performCodeWithPermission("虾菇需要访问存储、相机、麦克风权限", new BaseActivity.PermissionCallback() {
                        @Override
                        public void hasPermission() {
                            setPermissions();
                        }

                        @Override
                        public void noPermission() {
                            PreferenceUtil.saveIntValue(activity, "publishPermission", 1);
                        }
                    }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO);
        } else {
            setPermissions();
        }
    }

    private void setPermissions() {
        MineApp.toPublish = true;
        MineApp.toContinue = false;
        new SelectorPW(mBinding.getRoot(), selectorList, position1 -> {
            if (position1 == 0) {
                ActivityUtils.getCameraMain(activity, true, true, false);
            } else {
                ActivityUtils.getCameraVideo(false);
            }
        });
    }

    @Override
    public void beSuperLikeList() {
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
                pairList(1);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    pairList(1);
                }
            }
        }, activity).setPageNo(0).setLikeOtherStatus(2);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void relievePair(long otherUserId) {
        relievePairApi api = new relievePairApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                LikeTypeDb.getInstance().deleteLikeType(otherUserId);
                Intent data = new Intent("lobster_relieve");
                data.putExtra("otherUserId", otherUserId);
                data.putExtra("isRelieve", true);
                LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(data);
            }
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void pairList(int pageNo) {
        pairListApi api = new pairListApi(new HttpOnNextListener<List<LikeMe>>() {
            @Override
            public void onNext(List<LikeMe> o) {
                for (LikeMe likeMe : o) {
                    LikeDb.getInstance().saveLike(new CollectID(likeMe.getOtherUserId()));
                    ChatList chatMsg = ChatListDb.getInstance().getChatMsg(likeMe.getOtherUserId(), 4);
                    ChatList chatList = new ChatList();
                    chatList.setUserId(likeMe.getOtherUserId());
                    chatList.setImage(likeMe.getHeadImage());
                    chatList.setNick(likeMe.getNick());
                    chatList.setMsgType(chatMsg != null ? chatMsg.getMsgType() : 1);
                    chatList.setStanza(chatMsg != null ? chatMsg.getStanza() : "匹配于" + (likeMe.getPairTime().isEmpty() ? DateUtil.getNow(DateUtil.yyyy_MM_dd_HH_mm_ss).substring(5, 10) : likeMe.getPairTime().substring(5, 10)));
                    chatList.setNoReadNum(chatMsg != null ? chatMsg.getNoReadNum() : 0);
                    chatList.setChatType(4);
                    chatList.setCreationDate(chatMsg != null ? chatMsg.getCreationDate() : likeMe.getPairTime());
                    chatType4List.add(chatList);
                }
                MineApp.pairList.addAll(o);
                pairList(pageNo + 1);
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    mBinding.refresh.finishRefresh();
                    LikeMeComparator comparator = new LikeMeComparator();
                    Collections.sort(chatType4List, comparator);
                    chatMsgList.addAll(chatType4List);
                    adapter.notifyDataSetChanged();
                }
            }
        }, activity).setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    public static class LikeMeComparator implements Comparator<ChatList> {
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
