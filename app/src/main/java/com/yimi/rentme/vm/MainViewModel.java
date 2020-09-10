package com.yimi.rentme.vm;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.widget.RelativeLayout;

import com.yimi.rentme.R;
import com.yimi.rentme.databinding.AcMainBinding;
import com.yimi.rentme.iv.MainVMInterface;
import com.yimi.rentme.utils.OpenNotice;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.adapter.FragmentAdapter;
import com.zb.lib_base.api.chatListApi;
import com.zb.lib_base.api.contactNumApi;
import com.zb.lib_base.api.driftBottleChatListApi;
import com.zb.lib_base.api.newDynMsgAllNumApi;
import com.zb.lib_base.api.openedMemberPriceListApi;
import com.zb.lib_base.api.otherInfoApi;
import com.zb.lib_base.api.pushGoodUserApi;
import com.zb.lib_base.api.systemChatApi;
import com.zb.lib_base.api.walletAndPopApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.BottleCacheDb;
import com.zb.lib_base.db.ChatListDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.imcore.CustomMessageBody;
import com.zb.lib_base.model.BottleCache;
import com.zb.lib_base.model.ChatList;
import com.zb.lib_base.model.ContactNum;
import com.zb.lib_base.model.HistoryMsg;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.model.MineNewsCount;
import com.zb.lib_base.model.SystemMsg;
import com.zb.lib_base.model.VipInfo;
import com.zb.lib_base.model.WalletInfo;
import com.zb.lib_base.utils.DateUtil;
import com.zb.lib_base.utils.FragmentUtils;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_card.windows.GuidancePW;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import io.realm.Realm;

public class MainViewModel extends BaseViewModel implements MainVMInterface {
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private AcMainBinding mBinding;

    private int pageNo = 1;
    private int bottlePageNo = 1;
    private BottleCacheDb bottleCacheDb;
    private int nowIndex = -1;

    private BaseReceiver rechargeReceiver;
    private BaseReceiver chatListReceiver;
    private BaseReceiver newMsgReceiver;
    private BaseReceiver bottleNumReceiver;
    private BaseReceiver newsCountReceiver;
    private BaseReceiver unReadCountReceiver;
    private BaseReceiver newDynMsgAllNumReceiver;
    private BaseReceiver contactNumReceiver;

    private int time = 2 * 60 * 1000;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            handler.sendEmptyMessageDelayed(0, time);
            pushGoodUser();
            return false;
        }
    });
    private Vibrator vibrator;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        bottleCacheDb = new BottleCacheDb(Realm.getDefaultInstance());
        mBinding = (AcMainBinding) binding;
        mBinding.tvTitle.setTypeface(MineApp.simplifiedType);
        mBinding.tvContent.setTypeface(MineApp.simplifiedType);
        mBinding.tvSubContent.setTypeface(MineApp.simplifiedType);
        mBinding.setUnReadCount(0);
        mBinding.setNewsCount(0);

        vibrator = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);

        MineApp.cityName = PreferenceUtil.readStringValue(activity, "cityName");
        MineApp.sex = PreferenceUtil.readIntValue(activity, "mySex", -2) == -2 ? (MineApp.mineInfo.getSex() == 0 ? 1 : 0) : PreferenceUtil.readIntValue(activity, "mySex", -2);
        MineApp.minAge = PreferenceUtil.readIntValue(activity, "myMinAge", 18);
        MineApp.maxAge = PreferenceUtil.readIntValue(activity, "myMaxAge", 70);

        initFragments();
        openedMemberPriceList();

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

        newMsgReceiver = new BaseReceiver(activity, "lobster_newMsg") {
            @Override
            public void onReceive(Context context, Intent intent) {
                CustomMessageBody body = (CustomMessageBody) intent.getSerializableExtra("customMessageBody");
                String msgId = intent.getStringExtra("msgId");
                long otherUserId = body.getFromId() == BaseActivity.userId ? body.getToId() : body.getFromId();

                if (body.getDriftBottleId() == 0) {
                    if (otherUserId == BaseActivity.systemUserId) {
                        MineApp.mineNewsCount.setSystemNewsNum(MineApp.mineNewsCount.getSystemNewsNum() + 1);
                        activity.sendBroadcast(new Intent("lobster_newsCount"));
                    } else {
                        HistoryMsg historyMsg = new HistoryMsg();
                        historyMsg.setThirdMessageId(msgId);
                        historyMsg.setFromId(body.getFromId());
                        historyMsg.setToId(body.getToId());
                        historyMsg.setTitle(body.getSummary());
                        historyMsg.setStanza(body.getStanza());
                        historyMsg.setMsgType(body.getMsgType());
                        historyMsg.setResLink(body.getResLink());
                        historyMsg.setResTime(body.getResTime());
                        historyMsg.setCreationDate(DateUtil.getNow(DateUtil.yyyy_MM_dd_HH_mm_ss));
                        historyMsg.setOtherUserId(otherUserId);
                        historyMsg.setMsgChannelType(1);
                        historyMsg.setDriftBottleId(0);
                        historyMsg.setMainUserId(BaseActivity.userId);
                        historyMsgDb.saveHistoryMsg(historyMsg);

                        chatListDb.updateChatMsg(otherUserId, DateUtil.getNow(DateUtil.yyyy_MM_dd_HH_mm_ss), body.getStanza(), body.getMsgType(), new ChatListDb.CallBack() {
                            @Override
                            public void success() {
                                // 更新会话列表
                                mBinding.setUnReadCount(chatListDb.getAllUnReadNum());
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
                } else {

                    HistoryMsg historyMsg = new HistoryMsg();
                    historyMsg.setThirdMessageId(msgId);
                    historyMsg.setFromId(body.getFromId());
                    historyMsg.setToId(body.getToId());
                    historyMsg.setTitle(body.getSummary());
                    historyMsg.setStanza(body.getStanza());
                    historyMsg.setMsgType(body.getMsgType());
                    historyMsg.setResLink(body.getResLink());
                    historyMsg.setResTime(body.getResTime());
                    historyMsg.setCreationDate(DateUtil.getNow(DateUtil.yyyy_MM_dd_HH_mm_ss));
                    historyMsg.setOtherUserId(otherUserId);
                    historyMsg.setMsgChannelType(2);
                    historyMsg.setDriftBottleId(body.getDriftBottleId());
                    historyMsg.setMainUserId(BaseActivity.userId);
                    historyMsgDb.saveHistoryMsg(historyMsg);

                    BottleCache dbData = bottleCacheDb.getBottleCache(body.getDriftBottleId());
                    BottleCache bottleCache = new BottleCache();
                    bottleCache.setDriftBottleId(body.getDriftBottleId());
                    bottleCache.setUserId(otherUserId);
                    bottleCache.setCreationDate(DateUtil.getNow(DateUtil.yyyy_MM_dd_HH_mm_ss));
                    bottleCache.setStanza(body.getStanza());
                    bottleCache.setMsgType(body.getMsgType());
                    bottleCache.setNoReadNum(dbData == null ? 1 : dbData.getNoReadNum() + 1);
                    bottleCache.setMainUserId(BaseActivity.userId);
                    bottleCacheDb.saveBottleCache(bottleCache);

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
                }
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
                mBinding.setUnReadCount(chatListDb.getAllUnReadNum() + mBinding.getNewsCount());
            }
        };

        unReadCountReceiver = new BaseReceiver(activity, "lobster_unReadCount") {
            @Override
            public void onReceive(Context context, Intent intent) {
                mBinding.setNewsCount(MineApp.mineNewsCount.getFriendDynamicGiftNum() + MineApp.mineNewsCount.getFriendDynamicReviewNum() + MineApp.mineNewsCount.getFriendDynamicGoodNum() + MineApp.mineNewsCount.getSystemNewsNum());
                mBinding.setUnReadCount(chatListDb.getAllUnReadNum() + mBinding.getNewsCount());
            }
        };

        contactNumReceiver = new BaseReceiver(activity, "lobster_contactNum") {
            @Override
            public void onReceive(Context context, Intent intent) {
                contactNum(true);
            }
        };

        new OpenNotice(activity, mBinding.getRoot());

        handler.sendEmptyMessageDelayed(0, time);
        createAnimator();
    }

    public void onDestroy() {
        rechargeReceiver.unregisterReceiver();
        chatListReceiver.unregisterReceiver();
        newMsgReceiver.unregisterReceiver();
        bottleNumReceiver.unregisterReceiver();
        newsCountReceiver.unregisterReceiver();
        unReadCountReceiver.unregisterReceiver();
        newDynMsgAllNumReceiver.unregisterReceiver();
        contactNumReceiver.unregisterReceiver();
    }


    private void initFragments() {
        fragments.clear();
        fragments.add(FragmentUtils.getHomeFragment());
        fragments.add(FragmentUtils.getCardFragment());
        fragments.add(FragmentUtils.getChatFragment());
        fragments.add(FragmentUtils.getMineFragment());
        mBinding.viewPage.setAdapter(new FragmentAdapter(activity.getSupportFragmentManager(), fragments));
        mBinding.viewPage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
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
        new Handler().postDelayed(() -> {
            if (PreferenceUtil.readIntValue(activity, "showGuidance") == 0) {
                new GuidancePW(activity, mBinding.getRoot());
            }
        }, 500);

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
                newDynMsgAllNum(false);
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
                        chatListDb.saveChatList(chatMsg);
                    }
                    if (chatMsg.getUserId() > 10010) {
                        chatMsg.setMainUserId(BaseActivity.userId);
                        chatMsg.setChatType(4);
                        chatListDb.saveChatList(chatMsg);
                    }
                }
                pageNo++;
                chatList();
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    activity.sendBroadcast(new Intent("lobster_updateChat"));
                    systemChat();
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
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    activity.sendBroadcast(new Intent("lobster_newsCount"));
                    contactNum(false);
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
                    bottleCacheDb.saveBottleCache(item);
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
                MineApp.contactNum = o;
                ChatList chatList = new ChatList();
                chatList.setUserId(BaseActivity.likeUserId);
                chatList.setImage("be_like_logo_icon");
                chatList.setNick("查看谁喜欢我");
                chatList.setMsgType(1);
                chatList.setStanza("TA们喜欢你，正等待你回应");
                chatList.setNoReadNum(MineApp.contactNum.getBeLikeCount());
                chatList.setChatType(1);
                chatList.setMainUserId(BaseActivity.userId);
                chatListDb.saveChatList(chatList);
                activity.sendBroadcast(new Intent("lobster_newsCount"));
                new Handler().postDelayed(() -> {
                    Intent data = new Intent("lobster_updateChatType");
                    data.putExtra("chatType", 1);
                    data.putExtra("isUpdate", isUpdate);
                    activity.sendBroadcast(data);
                }, 500);
                if (!isUpdate) {
                    noReadBottleNum(false);
                }
                initRemind(o.getBeLikeCount());
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        }, activity).setOtherUserId(BaseActivity.userId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void noReadBottleNum(boolean isUpdate) {
        MineApp.noReadBottleNum = bottleCacheDb.getUnReadCount();
        ChatList chatList = new ChatList();
        chatList.setUserId(BaseActivity.bottleUserId);
        chatList.setImage("bottle_logo_icon");
        chatList.setNick("漂流瓶");
        chatList.setMsgType(1);
        chatList.setStanza(MineApp.noReadBottleNum == 0 ? "茫茫人海中，需要流浪到何时" : "您有新消息");
        chatList.setNoReadNum(MineApp.noReadBottleNum);
        chatList.setChatType(2);
        chatList.setMainUserId(BaseActivity.userId);
        chatListDb.saveChatList(chatList);
        activity.sendBroadcast(new Intent("lobster_newsCount"));
        new Handler().postDelayed(() -> {
            Intent data = new Intent("lobster_updateChatType");
            data.putExtra("chatType", 2);
            data.putExtra("isUpdate", isUpdate);
            activity.sendBroadcast(data);
        }, 500);
    }

    private void initRemind(int beLikeCount) {
        int lastBeLikeCount = PreferenceUtil.readIntValue(activity, "nowBeLikeCount" + BaseActivity.userId);
        int count = beLikeCount - lastBeLikeCount;
        if (count > 0) {
            PreferenceUtil.saveIntValue(activity, "nowBeLikeCount" + BaseActivity.userId, beLikeCount);
            appSound();
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mBinding.remindRelative.getLayoutParams();
            params.setMarginEnd(ObjectUtils.getViewSizeByWidthFromMax(220));
            mBinding.remindRelative.setLayoutParams(params);
            mBinding.tvTitle.setText("快来看看有");
            mBinding.tvContent.setText(count < 99 ? (count + "") : "99+");
            mBinding.tvSubContent.setText("人喜欢你啦");
            mBinding.setOtherHead("");
            startAnimator();
        }
    }

    private void appSound() {
        // 播放声音
        MediaPlayer mPlayer = MediaPlayer.create(activity, R.raw.msn);
        try {
            vibrator.vibrate(300);
            if (mPlayer != null) {
                mPlayer.stop();
            }
            mPlayer.prepare();
            mPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        new Handler().postDelayed(() -> {
            vibrator.cancel();
            if (mPlayer != null) {
                mPlayer.stop();
                mPlayer.release();//释放资源
            }
        }, 500);
    }

    @Override
    public void otherInfo(long otherUserId, CustomMessageBody body, String msgId) {
        otherInfoApi api = new otherInfoApi(new HttpOnNextListener<MemberInfo>() {
            @Override
            public void onNext(MemberInfo o) {
                ChatList dbData = chatListDb.getChatMsg(otherUserId, otherUserId == BaseActivity.dynUserId ? 5 : 4);
                ChatList chatList = new ChatList();
                chatList.setUserId(otherUserId);
                chatList.setNick(o.getNick());
                chatList.setImage(o.getImage());
                chatList.setCreationDate(DateUtil.getNow(DateUtil.yyyy_MM_dd_HH_mm_ss));
                chatList.setStanza(body.getStanza());
                chatList.setMsgType(body.getMsgType());
                chatList.setNoReadNum(dbData == null ? 1 : dbData.getNoReadNum() + 1);
                chatList.setChatType(otherUserId == BaseActivity.dynUserId ? 5 : 4);
                chatList.setPublicTag("");
                chatList.setEffectType(1);
                chatList.setAuthType(1);
                chatList.setMainUserId(BaseActivity.userId);
                chatListDb.saveChatList(chatList);
                activity.sendBroadcast(new Intent("lobster_newsCount"));
                // 更新会话列表
                Intent data = new Intent("lobster_updateChat");
                data.putExtra("userId", otherUserId);
                activity.sendBroadcast(data);

                // 更新对话页面
                Intent upMessage = new Intent("lobster_upMessage/friend=" + otherUserId);
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

    private ObjectAnimator pvh_remind;

    private static PropertyValuesHolder pvhTY, pvhSX, pvhSY, pvhSX_L, pvhSY_L;

    private void createAnimator() {
        pvhTY = PropertyValuesHolder.ofFloat("translationY",0, -30, 0, -30, 0, -30, 0);
        pvhSX = PropertyValuesHolder.ofFloat("scaleX", 0, 1);
        pvhSY = PropertyValuesHolder.ofFloat("scaleY", 0, 1);
        pvhSX_L = PropertyValuesHolder.ofFloat("scaleX", 1, 0);
        pvhSY_L = PropertyValuesHolder.ofFloat("scaleY", 1, 0);
        pvh_remind = ObjectAnimator.ofPropertyValuesHolder(mBinding.remindRelative, pvhSX, pvhSY).setDuration(500);
    }

    private void startAnimator() {
        pvh_remind.setValues(pvhSX, pvhSY);
        pvh_remind.start();
        new Handler().postDelayed(() -> {
            pvh_remind.setValues(pvhTY);
            pvh_remind.start();
        },500);
        new Handler().postDelayed(() -> {
            pvh_remind.setValues(pvhSX_L,pvhSY_L);
            pvh_remind.start();
        },11000);
        new Handler().postDelayed(this::stopAnimator, 11500);
    }

    public void stopAnimator() {
        if (pvh_remind != null && pvh_remind.isRunning())
            pvh_remind.cancel();
    }
}
