package com.yimi.rentme.vm;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.view.View;
import android.widget.RelativeLayout;

import com.yimi.rentme.databinding.AcMainBinding;
import com.yimi.rentme.iv.MainVMInterface;
import com.yimi.rentme.service.ForegroundLiveService;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.adapter.ViewPagerAdapter;
import com.zb.lib_base.api.chatListApi;
import com.zb.lib_base.api.contactNumApi;
import com.zb.lib_base.api.driftBottleChatListApi;
import com.zb.lib_base.api.firstOpenMemberPageApi;
import com.zb.lib_base.api.flashChatListApi;
import com.zb.lib_base.api.myImAccountInfoApi;
import com.zb.lib_base.api.myInfoApi;
import com.zb.lib_base.api.newDynMsgAllNumApi;
import com.zb.lib_base.api.openedMemberPriceListApi;
import com.zb.lib_base.api.otherInfoApi;
import com.zb.lib_base.api.pushGoodUserApi;
import com.zb.lib_base.api.systemChatApi;
import com.zb.lib_base.api.visitorBySeeMeCountApi;
import com.zb.lib_base.api.walletAndPopApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.BottleCacheDb;
import com.zb.lib_base.db.ChatListDb;
import com.zb.lib_base.db.FilmResourceDb;
import com.zb.lib_base.db.HistoryMsgDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.mimc.CustomMessageBody;
import com.zb.lib_base.mimc.UserManager;
import com.zb.lib_base.model.BottleCache;
import com.zb.lib_base.model.ChatList;
import com.zb.lib_base.model.ContactNum;
import com.zb.lib_base.model.ImAccount;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.model.MineNewsCount;
import com.zb.lib_base.model.SystemMsg;
import com.zb.lib_base.model.VipInfo;
import com.zb.lib_base.model.VisitorCount;
import com.zb.lib_base.model.WalletInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DateUtil;
import com.zb.lib_base.utils.FragmentUtils;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.utils.OpenNotice;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.FlashChatPW;
import com.zb.lib_base.windows.VipAdPW;
import com.zb.module_card.windows.GuidancePW;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import io.realm.Realm;

public class MainViewModel extends BaseViewModel implements MainVMInterface, UserManager.OnHandleMIMCMsgListener {
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private AcMainBinding mBinding;

    private int pageNo = 1;
    private int bottlePageNo = 1;
    private int nowIndex = -1;

    private BaseReceiver rechargeReceiver;
    private BaseReceiver chatListReceiver;
    private BaseReceiver bottleNumReceiver;
    private BaseReceiver newsCountReceiver;
    private BaseReceiver unReadCountReceiver;
    private BaseReceiver newDynMsgAllNumReceiver;
    private BaseReceiver contactNumReceiver;
    private BaseReceiver flashChatReceiver;

    private int time = 2 * 60 * 1000;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            handler.sendEmptyMessageDelayed(0, time);
            pushGoodUser();
            visitorBySeeMeCount();
            return false;
        }
    });
    private Vibrator vibrator;
    private Intent mIntent;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);

        mBinding = (AcMainBinding) binding;
        mBinding.tvTitle.setTypeface(MineApp.simplifiedType);
        mBinding.tvContent.setTypeface(MineApp.simplifiedType);
        mBinding.tvSubContent.setTypeface(MineApp.simplifiedType);
        mBinding.setUnReadCount(0);
        mBinding.setNewsCount(0);

        initFragments();

        vibrator = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);

        MineApp.cityName = PreferenceUtil.readStringValue(activity, "cityName");
        MineApp.sex = PreferenceUtil.readIntValue(activity, "mySex", -2) == -2 ? (MineApp.mineInfo.getSex() == 0 ? 1 : 0) : PreferenceUtil.readIntValue(activity, "mySex", -2);
        MineApp.minAge = PreferenceUtil.readIntValue(activity, "myMinAge", 18);
        MineApp.maxAge = PreferenceUtil.readIntValue(activity, "myMaxAge", 70);

        MineApp.sFilmResourceDb = new FilmResourceDb(Realm.getDefaultInstance());
        mIntent = new Intent(activity, ForegroundLiveService.class);
        activity.startService(mIntent);

        rechargeReceiver = new BaseReceiver(activity, "lobster_recharge") {
            @Override
            public void onReceive(Context context, Intent intent) {
                walletAndPop();
            }
        };

        chatListReceiver = new BaseReceiver(activity, "lobster_chatList") {
            @Override
            public void onReceive(Context context, Intent intent) {
                pageNo = 1;
                chatList();
                bottlePageNo = 1;
                driftBottleChatList();
            }
        };

        newDynMsgAllNumReceiver = new BaseReceiver(activity, "lobster_newDynMsgAllNum") {
            @Override
            public void onReceive(Context context, Intent intent) {
                newDynMsgAllNum(true);
            }
        };

        bottleNumReceiver = new BaseReceiver(activity, "lobster_bottleNum") {
            @Override
            public void onReceive(Context context, Intent intent) {
                noReadBottleNum(true);
            }
        };

        newsCountReceiver = new BaseReceiver(activity, "lobster_newsCount") {
            @Override
            public void onReceive(Context context, Intent intent) {
                mBinding.setNewsCount(MineApp.mineNewsCount.getFriendDynamicGiftNum() + MineApp.mineNewsCount.getFriendDynamicReviewNum() + MineApp.mineNewsCount.getFriendDynamicGoodNum() + MineApp.mineNewsCount.getSystemNewsNum());
                mBinding.setUnReadCount(ChatListDb.getInstance().getAllUnReadNum() + mBinding.getNewsCount());
            }
        };

        unReadCountReceiver = new BaseReceiver(activity, "lobster_unReadCount") {
            @Override
            public void onReceive(Context context, Intent intent) {
                mBinding.setNewsCount(MineApp.mineNewsCount.getFriendDynamicGiftNum() + MineApp.mineNewsCount.getFriendDynamicReviewNum() + MineApp.mineNewsCount.getFriendDynamicGoodNum() + MineApp.mineNewsCount.getSystemNewsNum());
                mBinding.setUnReadCount(ChatListDb.getInstance().getAllUnReadNum() + mBinding.getNewsCount());
            }
        };

        contactNumReceiver = new BaseReceiver(activity, "lobster_contactNum") {
            @Override
            public void onReceive(Context context, Intent intent) {
                contactNum(true);
            }
        };

        flashChatReceiver = new BaseReceiver(activity, "lobster_flashChat") {
            @Override
            public void onReceive(Context context, Intent intent) {
                new FlashChatPW(mBinding.getRoot());
            }
        };


        handler.sendEmptyMessageDelayed(0, time);
        createAnimator();

        MineApp.getApp().getFixedThreadPool().execute(() -> {
            SystemClock.sleep(1000);
            new OpenNotice(activity, mBinding.getRoot());
            activity.runOnUiThread(() -> {
                if (MineApp.mineInfo.getUserId() == 0)
                    myInfo();
                else
                    openedMemberPriceList();
            });
        });

        if (PreferenceUtil.readIntValue(activity, "showGuidance") == 0)
            MineApp.getApp().getFixedThreadPool().execute(() -> {
                SystemClock.sleep(500);
                activity.runOnUiThread(() -> new GuidancePW(mBinding.getRoot()));
            });
    }

    @Override
    public void onHandleMessage(CustomMessageBody body, String msgId) {
        activity.runOnUiThread(() -> {
            long otherUserId = body.getFromId() == BaseActivity.userId ? body.getToId() : body.getFromId();
            if (body.getDriftBottleId() != 0) {

                BottleCache dbData = BottleCacheDb.getInstance().getBottleCache(body.getDriftBottleId());
                BottleCache bottleCache = new BottleCache();
                bottleCache.setDriftBottleId(body.getDriftBottleId());
                bottleCache.setUserId(otherUserId);
                bottleCache.setCreationDate(DateUtil.getNow(DateUtil.yyyy_MM_dd_HH_mm_ss));
                bottleCache.setStanza(body.getStanza());
                bottleCache.setMsgType(body.getMsgType());
                bottleCache.setNoReadNum(dbData == null ? 1 : dbData.getNoReadNum() + 1);
                bottleCache.setMainUserId(BaseActivity.userId);
                BottleCacheDb.getInstance().saveBottleCache(bottleCache);

                // 更新会话列表
                Intent data = new Intent("lobster_singleBottleCache");
                data.putExtra("driftBottleId", body.getDriftBottleId());
                activity.sendBroadcast(data);

                // 更新对话页面
                Intent upMessage = new Intent("lobster_upMessage/driftBottleId=" + body.getDriftBottleId());
                upMessage.putExtra("customMessageBody", body);
                upMessage.putExtra("msgId", msgId);
                activity.sendBroadcast(upMessage);

                noReadBottleNum(true);

            } else if (body.getFlashTalkId() != 0) {
                int otherChatCount = Math.min(HistoryMsgDb.getInstance().getOtherChatCount(otherUserId, body.getFlashTalkId()), 10);
                ChatListDb.getInstance().updateChatMsg(otherUserId, DateUtil.getNow(DateUtil.yyyy_MM_dd_HH_mm_ss), body.getStanza(), body.getMsgType(), otherChatCount, new ChatListDb.CallBack() {
                    @Override
                    public void success() {
                        // 更新会话列表
                        mBinding.setUnReadCount(ChatListDb.getInstance().getAllUnReadNum());
                        Intent data = new Intent("lobster_updateChat");
                        data.putExtra("userId", otherUserId);
                        data.putExtra("flashTalkId", body.getFlashTalkId());
                        activity.sendBroadcast(data);

                        // 更新对话页面
                        Intent upMessage = new Intent("lobster_upMessage/flashTalkId=" + body.getFlashTalkId());
                        upMessage.putExtra("customMessageBody", body);
                        upMessage.putExtra("msgId", msgId);
                        activity.sendBroadcast(upMessage);
                    }

                    @Override
                    public void fail() {
                        otherInfo(otherUserId, body, msgId);
                    }
                });
            } else {
                if (otherUserId == BaseActivity.systemUserId) {
                    MineApp.mineNewsCount.setSystemNewsNum(MineApp.mineNewsCount.getSystemNewsNum() + 1);
                    activity.sendBroadcast(new Intent("lobster_newsCount"));
                } else {
                    ChatListDb.getInstance().updateChatMsg(otherUserId, DateUtil.getNow(DateUtil.yyyy_MM_dd_HH_mm_ss), body.getStanza(), body.getMsgType(), 0, new ChatListDb.CallBack() {
                        @Override
                        public void success() {
                            // 更新会话列表
                            mBinding.setUnReadCount(ChatListDb.getInstance().getAllUnReadNum());
                            Intent data = new Intent("lobster_updateChat");
                            data.putExtra("userId", otherUserId);
                            activity.sendBroadcast(data);

                            // 更新对话页面
                            Intent upMessage = new Intent("lobster_upMessage/friend=" + otherUserId);
                            upMessage.putExtra("customMessageBody", body);
                            upMessage.putExtra("msgId", msgId);
                            activity.sendBroadcast(upMessage);
                        }

                        @Override
                        public void fail() {
                            otherInfo(otherUserId, body, msgId);
                        }
                    });
                    if (body.getMsgType() == 112) {
                        newDynMsgAllNum(true);
                    }
                }
            }
        });
    }

    public void onDestroy() {
        try {
            rechargeReceiver.unregisterReceiver();
            chatListReceiver.unregisterReceiver();
            bottleNumReceiver.unregisterReceiver();
            newsCountReceiver.unregisterReceiver();
            unReadCountReceiver.unregisterReceiver();
            newDynMsgAllNumReceiver.unregisterReceiver();
            contactNumReceiver.unregisterReceiver();
            flashChatReceiver.unregisterReceiver();
//            activity.stopService(mIntent);
        } catch (Exception ignored) {
        }
    }

    public void onStart() {
        adapter.notifyItemChanged(mBinding.viewPage.getCurrentItem());
    }

    private ViewPagerAdapter adapter;

    private void initFragments() {
        fragments.clear();
        fragments.add(FragmentUtils.getHomeFragment());
        fragments.add(FragmentUtils.getCardFragment());
        fragments.add(FragmentUtils.getChatFragment());
        fragments.add(FragmentUtils.getMineFragment());

        adapter = new ViewPagerAdapter(activity, fragments);
        mBinding.viewPage.setUserInputEnabled(false);
        mBinding.viewPage.setSaveEnabled(false);
        mBinding.viewPage.setAdapter(adapter);
        mBinding.viewPage.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                nowIndex = position;
                if (nowIndex == 0 || nowIndex == 2) {
                    bottleTileStatus(true);
                    animatorStatus(false);
                } else if (nowIndex == 1) {
                    bottleTileStatus(false);
                    animatorStatus(true);
                } else {
                    bottleTileStatus(false);
                    animatorStatus(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                mBinding.setIndex(nowIndex);
            }
        });
        selectPage(1);
    }

    private void bottleTileStatus(boolean isPlay) {
        Intent data = new Intent("lobster_bottleTitle");
        data.putExtra("isPlay", isPlay);
        activity.sendBroadcast(data);
    }

    private void animatorStatus(boolean isPlay) {
        Intent data = new Intent("lobster_animatorStatus");
        data.putExtra("isPlay", isPlay);
        activity.sendBroadcast(data);
    }

    public void onResume() {
        if (nowIndex == -1)
            return;
        if (nowIndex == 0 || nowIndex == 2) {
            bottleTileStatus(true);
            animatorStatus(false);
        } else if (nowIndex == 1) {
            bottleTileStatus(false);
            animatorStatus(true);
        } else {
            bottleTileStatus(false);
            animatorStatus(false);
        }
    }

    public void onPause() {
        bottleTileStatus(false);
        animatorStatus(false);
    }

    @Override
    public void selectPage(int index) {
        if (nowIndex == index)
            return;
        nowIndex = index;
        mBinding.setIndex(nowIndex);
        mBinding.viewPage.setCurrentItem(index);
    }

    @Override
    public void myInfo() {
        myInfoApi api = new myInfoApi(new HttpOnNextListener<MineInfo>() {
            @Override
            public void onNext(MineInfo o) {
                MineApp.mineInfo = o;
                openedMemberPriceList();
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void openedMemberPriceList() {
        openedMemberPriceListApi api = new openedMemberPriceListApi(new HttpOnNextListener<List<VipInfo>>() {
            @Override
            public void onNext(List<VipInfo> o) {
                VipInfo vipInfo = o.get(0);
                o.get(0).setOriginalPrice(vipInfo.getPrice());
                o.get(1).setOriginalPrice(vipInfo.getPrice() * 3);
                o.get(2).setOriginalPrice(vipInfo.getPrice() * 12);
                MineApp.vipInfoList.clear();
                MineApp.vipInfoList.addAll(o);
                walletAndPop();
                firstOpenMemberPage();
                newDynMsgAllNum(false);
                myImAccountInfo();
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void myImAccountInfo() {
        myImAccountInfoApi api = new myImAccountInfoApi(new HttpOnNextListener<ImAccount>() {
            @Override
            public void onNext(ImAccount o) {
                UserManager.getInstance().setHandleMIMCMsgListener(MainViewModel.this);
                MineApp.imUserId = o.getImUserId();
                MineApp.sMIMCUser = UserManager.getInstance().newMIMCUser(o.getImUserId());
                MineApp.sMIMCUser.login();
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void firstOpenMemberPage() {
        firstOpenMemberPageApi api = new firstOpenMemberPageApi(new HttpOnNextListener<Integer>() {
            @Override
            public void onNext(Integer o) {
                MineApp.isFirstOpen = o == 1;
                activity.sendBroadcast(new Intent("lobster_isFirstOpen"));
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void walletAndPop() {
        walletAndPopApi api = new walletAndPopApi(new HttpOnNextListener<WalletInfo>() {
            @Override
            public void onNext(WalletInfo o) {
                MineApp.walletInfo = o;
                activity.sendBroadcast(new Intent("lobster_updateWallet"));
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void newDynMsgAllNum(boolean isUpdate) {
        newDynMsgAllNumApi api = new newDynMsgAllNumApi(new HttpOnNextListener<MineNewsCount>() {
            @Override
            public void onNext(MineNewsCount o) {
                MineApp.mineNewsCount = o;
                activity.sendBroadcast(new Intent("lobster_newsCount"));
                if (!isUpdate) {
                    pageNo = 1;
                    bottlePageNo = 1;
                    chatList();
                    driftBottleChatList();
                }
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void chatList() {
        chatListApi api = new chatListApi(new HttpOnNextListener<List<ChatList>>() {
            @Override
            public void onNext(List<ChatList> o) {
                for (ChatList chatMsg : o) {
                    if (chatMsg.getUserId() == BaseActivity.dynUserId) {
                        // 评论
                        chatMsg.setMainUserId(BaseActivity.userId);
                        chatMsg.setChatType(5);
                        ChatListDb.getInstance().saveChatList(chatMsg);
                    }
                    if (chatMsg.getUserId() > 10010) {
                        chatMsg.setMainUserId(BaseActivity.userId);
                        chatMsg.setChatType(4);
                        ChatListDb.getInstance().saveChatList(chatMsg);
                    }
                }
                pageNo++;
                chatList();
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    systemChat();
                    flashChatList(1);
                }
            }
        }, activity).setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void flashChatList(int pageNo) {
        flashChatListApi api = new flashChatListApi(new HttpOnNextListener<List<ChatList>>() {
            @Override
            public void onNext(List<ChatList> o) {
                for (ChatList chatMsg : o) {
                    int otherChatCount = Math.min(HistoryMsgDb.getInstance().getOtherChatCount(chatMsg.getUserId(), chatMsg.getFlashTalkId()), 10);
                    if (!ChatListDb.getInstance().hasFlashChat(chatMsg, otherChatCount)) {
                        chatMsg.setChatType(6);
                        chatMsg.setOtherChatCount(chatMsg.getNoReadNum());
                        ChatListDb.getInstance().saveChatList(chatMsg);
                    }
                }
                flashChatList(pageNo + 1);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    activity.sendBroadcast(new Intent("lobster_updateChat"));
                }
            }
        }, activity).setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void systemChat() {
        systemChatApi api = new systemChatApi(new HttpOnNextListener<SystemMsg>() {
            @Override
            public void onNext(SystemMsg o) {
                // 系统消息
                MineApp.mineNewsCount.setContent(o.getStanza());
                MineApp.mineNewsCount.setCreateTime(o.getCreationDate());
                MineApp.mineNewsCount.setMsgType(o.getMsgType());
                MineApp.mineNewsCount.setSystemNewsNum(o.getNoReadNum());
                activity.sendBroadcast(new Intent("lobster_newsCount"));
                contactNum(false);
                visitorBySeeMeCount();
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    activity.sendBroadcast(new Intent("lobster_newsCount"));
                    contactNum(false);
                    visitorBySeeMeCount();
                }
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void driftBottleChatList() {
        driftBottleChatListApi api = new driftBottleChatListApi(new HttpOnNextListener<List<BottleCache>>() {
            @Override
            public void onNext(List<BottleCache> o) {
                for (BottleCache item : o) {
                    item.setMainUserId(BaseActivity.userId);
                    BottleCacheDb.getInstance().saveBottleCache(item);
                }
                bottlePageNo++;
                driftBottleChatList();
            }
        }, activity).setPageNo(bottlePageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void contactNum(boolean isUpdate) {
        contactNumApi api = new contactNumApi(new HttpOnNextListener<ContactNum>() {
            @Override
            public void onNext(ContactNum o) {
                MineApp.contactNum.setUserId(o.getUserId());
                MineApp.contactNum.setFansCount(o.getFansCount());
                MineApp.contactNum.setConcernCount(o.getConcernCount());
                MineApp.contactNum.setBeLikeCount(o.getBeLikeCount());
                MineApp.contactNum.setLikeCount(o.getLikeCount());
                MineApp.contactNum.setBeSuperLikeCount(o.getBeSuperLikeCount());
                MineApp.contactNum.setPraiseCount(o.getPraiseCount());
                int lastBeLikeCount = PreferenceUtil.readIntValue(activity, "nowBeLikeCount" + BaseActivity.userId);
                int count = o.getBeLikeCount() - lastBeLikeCount;
                ChatList chatList = new ChatList();
                chatList.setUserId(BaseActivity.likeUserId);
                chatList.setImage("be_like_logo_icon");
                chatList.setNick("查看谁喜欢我");
                chatList.setMsgType(1);
                chatList.setStanza("TA们喜欢你，正等待你回应");
                chatList.setNoReadNum(MineApp.contactNum.getBeLikeCount());
                chatList.setChatType(1);
                chatList.setMainUserId(BaseActivity.userId);
                chatList.setHasNewBeLike(MineApp.contactNum.getBeLikeCount() > PreferenceUtil.readIntValue(activity, "beLikeCount" + BaseActivity.userId));
                ChatListDb.getInstance().saveChatList(chatList);

                activity.sendBroadcast(new Intent("lobster_newsCount"));
                MineApp.getApp().getFixedThreadPool().execute(() -> {
                    SystemClock.sleep(800);
                    Intent data = new Intent("lobster_updateChatType");
                    data.putExtra("chatType", 1);
                    data.putExtra("isUpdate", isUpdate);
                    activity.sendBroadcast(data);
                });

                if (!isUpdate) {
                    noReadBottleNum(false);
                }
                initRemind(o.getBeLikeCount(), count);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        }, activity).setOtherUserId(BaseActivity.userId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void visitorBySeeMeCount() {
        visitorBySeeMeCountApi api = new visitorBySeeMeCountApi(new HttpOnNextListener<VisitorCount>() {
            @Override
            public void onNext(VisitorCount o) {
                MineApp.contactNum.setVisitorCount(o.getTotalCount());
                activity.sendBroadcast(new Intent("lobster_visitor"));
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void noReadBottleNum(boolean isUpdate) {
        MineApp.noReadBottleNum = BottleCacheDb.getInstance().getUnReadCount();
        ChatList chatList = new ChatList();
        chatList.setUserId(BaseActivity.bottleUserId);
        chatList.setImage("bottle_logo_icon");
        chatList.setNick("漂流瓶");
        chatList.setMsgType(1);
        chatList.setStanza(MineApp.noReadBottleNum == 0 ? "茫茫人海中，需要流浪到何时" : "您有新消息");
        chatList.setNoReadNum(MineApp.noReadBottleNum);
        chatList.setChatType(2);
        chatList.setMainUserId(BaseActivity.userId);
        ChatListDb.getInstance().saveChatList(chatList);
        activity.sendBroadcast(new Intent("lobster_newsCount"));
        MineApp.getApp().getFixedThreadPool().execute(() -> {
            SystemClock.sleep(800);
            Intent data = new Intent("lobster_updateChatType");
            data.putExtra("chatType", 2);
            data.putExtra("isUpdate", isUpdate);
            activity.sendBroadcast(data);
        });
    }

    private void initRemind(int beLikeCount, int count) {
        if (count > 0) {
            PreferenceUtil.saveIntValue(activity, "nowBeLikeCount" + BaseActivity.userId, beLikeCount);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mBinding.remindRelative.getLayoutParams();
            params.setMarginEnd(ObjectUtils.getViewSizeByWidthFromMax(220));
            mBinding.remindRelative.setLayoutParams(params);
            mBinding.tvTitle.setText("快来看看又有");
            mBinding.tvContent.setText(count < 99 ? (count + "") : "99+");
            mBinding.tvSubContent.setText("人喜欢你啦");
            mBinding.setOtherHead("");
            mBinding.remindRelative.setVisibility(View.VISIBLE);
            startAnimator();
        }
    }

    @Override
    public void otherInfo(long otherUserId, CustomMessageBody body, String msgId) {
        otherInfoApi api = new otherInfoApi(new HttpOnNextListener<MemberInfo>() {
            @Override
            public void onNext(MemberInfo o) {
                ChatList chatList = new ChatList();
                chatList.setUserId(otherUserId);
                chatList.setNick(o.getNick());
                chatList.setImage(o.getImage());
                chatList.setCreationDate(DateUtil.getNow(DateUtil.yyyy_MM_dd_HH_mm_ss));
                chatList.setStanza(body.getStanza());
                chatList.setMsgType(body.getMsgType());
                chatList.setNoReadNum(1);
                chatList.setChatType(body.getFlashTalkId() != 0 ? 6 : (otherUserId == BaseActivity.dynUserId ? 5 : 4));
                chatList.setPublicTag("");
                chatList.setEffectType(1);
                chatList.setAuthType(1);
                chatList.setFlashTalkId(body.getFlashTalkId());
                if (body.getFlashTalkId() != 0)
                    chatList.setOtherChatCount(1);
                chatList.setMainUserId(BaseActivity.userId);
                ChatListDb.getInstance().saveChatList(chatList);
                activity.sendBroadcast(new Intent("lobster_newsCount"));
                // 更新会话列表
                Intent data = new Intent("lobster_updateChat");
                data.putExtra("userId", otherUserId);
                data.putExtra("flashTalkId", body.getFlashTalkId());
                activity.sendBroadcast(data);

                // 更新对话页面
                Intent upMessage;
                if (body.getFlashTalkId() == 0) {
                    upMessage = new Intent("lobster_upMessage/friend=" + otherUserId);
                } else {
                    upMessage = new Intent("lobster_upMessage/flashTalkId=" + body.getFlashTalkId());
                }
                upMessage.putExtra("customMessageBody", body);
                upMessage.putExtra("msgId", msgId);
                activity.sendBroadcast(upMessage);

            }
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void pushGoodUser() {
        pushGoodUserApi api = new pushGoodUserApi(new HttpOnNextListener<Integer>() {
            @Override
            public void onNext(Integer o) {
                contactNum(true);
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void toLike(View view) {
        // 喜欢我
        if (MineApp.mineInfo.getMemberType() == 2) {
            ActivityUtils.getMineFCL(2, 0);
            return;
        }
        new VipAdPW(mBinding.getRoot(), 4, "");
    }

    private ObjectAnimator pvh_remind;

    private static PropertyValuesHolder pvhTY, pvhSX, pvhSY, pvhSX_L, pvhSY_L;

    private void createAnimator() {
        pvhTY = PropertyValuesHolder.ofFloat("translationY", 0, -30, 0, -30, 0, -30, 0);
        pvhSX = PropertyValuesHolder.ofFloat("scaleX", 0, 1);
        pvhSY = PropertyValuesHolder.ofFloat("scaleY", 0, 1);
        pvhSX_L = PropertyValuesHolder.ofFloat("scaleX", 1, 0);
        pvhSY_L = PropertyValuesHolder.ofFloat("scaleY", 1, 0);
        pvh_remind = ObjectAnimator.ofPropertyValuesHolder(mBinding.remindRelative, pvhSX, pvhSY).setDuration(500);
    }

    private void startAnimator() {
        vibrator.vibrate(500);
        pvh_remind.setValues(pvhSX, pvhSY);
        pvh_remind.start();
        MineApp.getApp().getFixedThreadPool().execute(() -> {
            SystemClock.sleep(500);
            activity.runOnUiThread(() -> {
                vibrator.cancel();
                pvh_remind.setValues(pvhTY);
                pvh_remind.start();
            });

            SystemClock.sleep(6000);
            activity.runOnUiThread(() -> {
                pvh_remind.setValues(pvhSX_L, pvhSY_L);
                pvh_remind.start();
            });

            SystemClock.sleep(6500);
            activity.runOnUiThread(this::stopAnimator);

        });
    }

    public void stopAnimator() {
        mBinding.remindRelative.setVisibility(View.GONE);
        if (pvh_remind != null && pvh_remind.isRunning())
            pvh_remind.cancel();
    }
}
