package com.yimi.rentme.vm;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import com.alibaba.mobileim.conversation.YWMessage;
import com.yimi.rentme.activity.ForegroundLiveService;
import com.yimi.rentme.databinding.AcMainBinding;
import com.yimi.rentme.iv.MainVMInterface;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.adapter.FragmentAdapter;
import com.zb.lib_base.api.bankInfoListApi;
import com.zb.lib_base.api.chatListApi;
import com.zb.lib_base.api.comTypeApi;
import com.zb.lib_base.api.contactNumApi;
import com.zb.lib_base.api.driftBottleChatListApi;
import com.zb.lib_base.api.giftListApi;
import com.zb.lib_base.api.joinPairPoolApi;
import com.zb.lib_base.api.myImAccountInfoApi;
import com.zb.lib_base.api.newDynMsgAllNumApi;
import com.zb.lib_base.api.openedMemberPriceListApi;
import com.zb.lib_base.api.otherInfoApi;
import com.zb.lib_base.api.rechargeDiscountListApi;
import com.zb.lib_base.api.recommendRankingListApi;
import com.zb.lib_base.api.systemChatApi;
import com.zb.lib_base.api.walletAndPopApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.AreaDb;
import com.zb.lib_base.db.BottleCacheDb;
import com.zb.lib_base.db.ChatListDb;
import com.zb.lib_base.db.HistoryMsgDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.imcore.CustomMessageBody;
import com.zb.lib_base.imcore.LoginSampleHelper;
import com.zb.lib_base.model.BankInfo;
import com.zb.lib_base.model.BottleCache;
import com.zb.lib_base.model.ChatList;
import com.zb.lib_base.model.ContactNum;
import com.zb.lib_base.model.GiftInfo;
import com.zb.lib_base.model.HistoryMsg;
import com.zb.lib_base.model.ImAccount;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.model.MineNewsCount;
import com.zb.lib_base.model.RechargeInfo;
import com.zb.lib_base.model.RecommendInfo;
import com.zb.lib_base.model.Report;
import com.zb.lib_base.model.SystemMsg;
import com.zb.lib_base.model.VipInfo;
import com.zb.lib_base.model.WalletInfo;
import com.zb.lib_base.utils.DateUtil;
import com.zb.lib_base.utils.FragmentUtils;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.TextPW;
import com.zb.module_card.windows.GuidancePW;

import java.util.ArrayList;
import java.util.List;

import androidx.core.app.NotificationManagerCompat;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import io.realm.Realm;

import static android.provider.Settings.EXTRA_APP_PACKAGE;
import static android.provider.Settings.EXTRA_CHANNEL_ID;
import static com.umeng.socialize.utils.ContextUtil.getPackageName;

public class MainViewModel extends BaseViewModel implements MainVMInterface {
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private AcMainBinding mBinding;
    private ChatListDb chatListDb;
    private int pageNo = 1;
    private int bottlePageNo = 1;
    private BottleCacheDb bottleCacheDb;
    private int nowIndex = -1;
    private AnimatorSet animatorSet = new AnimatorSet();

    private LoginSampleHelper loginHelper;
    private BaseReceiver rechargeReceiver;
    private BaseReceiver chatListReceiver;
    private BaseReceiver newMsgReceiver;
    private BaseReceiver resumeContactNumReceiver;
    private BaseReceiver bottleNumReceiver;
    private BaseReceiver mainSelectReceiver;
    private BaseReceiver newsCountReceiver;
    private BaseReceiver unReadCountReceiver;
    private BaseReceiver newDynMsgAllNumReceiver;
    private BaseReceiver recommendReceiver;
    private AreaDb areaDb;
    private HistoryMsgDb historyMsgDb;
    private MineInfo mineInfo;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        MineApp.isLogin = true;
        bottleCacheDb = new BottleCacheDb(Realm.getDefaultInstance());
        areaDb = new AreaDb(Realm.getDefaultInstance());
        chatListDb = new ChatListDb(Realm.getDefaultInstance());
        historyMsgDb = new HistoryMsgDb(Realm.getDefaultInstance());
        mBinding = (AcMainBinding) binding;
        mBinding.tvTitle.setTypeface(MineApp.simplifiedType);
        mBinding.tvContent.setTypeface(MineApp.simplifiedType);
        mBinding.tvSubContent.setTypeface(MineApp.simplifiedType);
        mBinding.setUnReadCount(0);
        mBinding.setNewsCount(0);

        mineInfo = mineInfoDb.getMineInfo();

        MineApp.cityName = PreferenceUtil.readStringValue(activity, "cityName");
        MineApp.sex = mineInfo.getSex() == 0 ? 1 : 0;

        loginHelper = LoginSampleHelper.getInstance();
        loginHelper.loginOut_Sample();

        if (!TextUtils.equals(BaseActivity.sessionId, ""))
            myImAccountInfoApi();

        initFragments();
        giftList();
        recommendRankingList();

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
                YWMessage ywMessage = (YWMessage) intent.getSerializableExtra("ywMessage");
                CustomMessageBody body = (CustomMessageBody) LoginSampleHelper.unpack(ywMessage.getContent());
                long otherUserId = body.getFromId() == BaseActivity.userId ? body.getToId() : body.getFromId();

                if (body.getDriftBottleId() == 0) {
                    if (otherUserId == BaseActivity.systemUserId) {
                        MineApp.mineNewsCount.setSystemNewsNum(MineApp.mineNewsCount.getSystemNewsNum() + 1);
                        activity.sendBroadcast(new Intent("lobster_newsCount"));
                    } else {
                        HistoryMsg historyMsg = new HistoryMsg();
                        historyMsg.setThirdMessageId(ywMessage.getMsgId() + "");
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
                                upMessage.putExtra("ywMessage", ywMessage);
                                activity.sendBroadcast(upMessage);
                            }

                            @Override
                            public void fail() {
                                otherInfo(otherUserId, ywMessage);
                            }
                        });
                        if (body.getMsgType() == 112) {
                            newDynMsgAllNum(true);
                        }
                    }
                } else {

                    HistoryMsg historyMsg = new HistoryMsg();
                    historyMsg.setThirdMessageId(ywMessage.getMsgId() + "");
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
                    upMessage.putExtra("ywMessage", ywMessage);
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

        resumeContactNumReceiver = new BaseReceiver(activity, "lobster_resumeContactNum") {
            @Override
            public void onReceive(Context context, Intent intent) {
                contactNum(true);
            }
        };

        bottleNumReceiver = new BaseReceiver(activity, "lobster_bottleNum") {
            @Override
            public void onReceive(Context context, Intent intent) {
                noReadBottleNum(true);
            }
        };
        mainSelectReceiver = new BaseReceiver(activity, "lobster_mainSelect") {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!MineApp.isLogin) {
                    selectPage(1);
                }
                MineApp.isLogin = true;
                joinPairPool(PreferenceUtil.readStringValue(activity, "longitude"), PreferenceUtil.readStringValue(activity, "latitude"),
                        areaDb.getProvinceId(PreferenceUtil.readStringValue(activity, "provinceName")),
                        areaDb.getCityId(PreferenceUtil.readStringValue(activity, "cityName")),
                        areaDb.getDistrictId(PreferenceUtil.readStringValue(activity, "districtName")));
                myImAccountInfoApi();
                walletAndPop();
                newDynMsgAllNum(false);
            }
        };

        newsCountReceiver = new BaseReceiver(activity, "lobster_newsCount") {
            @Override
            public void onReceive(Context context, Intent intent) {
                mBinding.setNewsCount(MineApp.mineNewsCount.getFriendDynamicGiftNum() + MineApp.mineNewsCount.getFriendDynamicReviewNum() + MineApp.mineNewsCount.getFriendDynamicGoodNum() + MineApp.mineNewsCount.getSystemNewsNum());
            }
        };

        unReadCountReceiver = new BaseReceiver(activity, "lobster_unReadCount") {
            @Override
            public void onReceive(Context context, Intent intent) {
                mBinding.setUnReadCount(chatListDb.getAllUnReadNum());
            }
        };

        recommendReceiver = new BaseReceiver(activity, "lobster_recommend") {
            @Override
            public void onReceive(Context context, Intent intent) {
                recommendRankingList();
            }
        };

        if (PreferenceUtil.readIntValue(activity, "isNotificationEnabled") == 0) {
            if (!isNotificationEnabled()) {
                new Handler().postDelayed(() -> {
                    PreferenceUtil.saveIntValue(activity, "isNotificationEnabled", 1);
                    new TextPW(activity, mBinding.getRoot(), "应用通知", "为了及时收到虾菇通知，请开启通知", "去开启", new TextPW.CallBack() {
                        @Override
                        public void sure() {
                            gotoSet();
                        }
                    });
                }, 1000);
            }
        }
        getPermissions();
    }

    public void onDestroy() {
        rechargeReceiver.unregisterReceiver();
        chatListReceiver.unregisterReceiver();
        newMsgReceiver.unregisterReceiver();
        resumeContactNumReceiver.unregisterReceiver();
        bottleNumReceiver.unregisterReceiver();
        mainSelectReceiver.unregisterReceiver();
        newsCountReceiver.unregisterReceiver();
        unReadCountReceiver.unregisterReceiver();
        newDynMsgAllNumReceiver.unregisterReceiver();
    }

    private boolean isNotificationEnabled() {
        boolean isOpened = false;
        try {
            isOpened = NotificationManagerCompat.from(activity).areNotificationsEnabled();
        } catch (Exception e) {
            e.printStackTrace();
            isOpened = false;
        }
        return isOpened;
    }

    private void gotoSet() {

        if (Build.VERSION.SDK_INT >= 26) {
            // android 8.0引导
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(EXTRA_APP_PACKAGE, getPackageName());
            intent.putExtra(EXTRA_CHANNEL_ID, activity.getApplicationInfo().uid);
            activity.startActivity(intent);
        } else if (Build.VERSION.SDK_INT >= 21) {
            // android 5.0-7.0
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra("app_package", getPackageName());
            intent.putExtra("app_uid", activity.getApplicationInfo().uid);
            activity.startActivity(intent);
        } else {
            // 其他
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            activity.startActivity(intent);
        }
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

    @Override
    public void selectPage(int index) {
        if (nowIndex == index)
            return;
        nowIndex = index;
        mBinding.setIndex(nowIndex);
        mBinding.viewPage.setCurrentItem(index);
    }

    @Override
    public void joinPairPool(String longitude, String latitude, long provinceId, long cityId, long districtId) {
        // 加入匹配池
        joinPairPoolApi api = new joinPairPoolApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {

            }
        }, activity).setLatitude(latitude).setLongitude(longitude).setProvinceId(provinceId).setCityId(cityId).setDistrictId(districtId);
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
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void giftList() {
        giftListApi api = new giftListApi(new HttpOnNextListener<List<GiftInfo>>() {
            @Override
            public void onNext(List<GiftInfo> o) {
                MineApp.giftInfoList.clear();
                MineApp.giftInfoList.addAll(o);
                openedMemberPriceList();
                rechargeDiscountList();
                bankInfoList();
                comType();
                walletAndPop();
                newDynMsgAllNum(false);
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void rechargeDiscountList() {
        rechargeDiscountListApi api = new rechargeDiscountListApi(new HttpOnNextListener<List<RechargeInfo>>() {
            @Override
            public void onNext(List<RechargeInfo> o) {
                MineApp.rechargeInfoList.clear();
                for (RechargeInfo item : o) {
                    if (item.getMoneyType() == 0) {
                        if (item.getExtraGiveMoney() == 0)
                            item.setContent("");
                        else
                            item.setContent(String.format("送%.1f虾菇币", item.getExtraGiveMoney()));
                    } else if (item.getMoneyType() == 1) {
                        item.setContent("最受欢迎");
                    } else {
                        item.setContent("优惠最大");
                    }
                    MineApp.rechargeInfoList.add(item);
                }
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void bankInfoList() {
        bankInfoListApi api = new bankInfoListApi(new HttpOnNextListener<List<BankInfo>>() {
            @Override
            public void onNext(List<BankInfo> o) {
                MineApp.bankInfoList.clear();
                MineApp.bankInfoList.addAll(o);
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void comType() {
        comTypeApi api = new comTypeApi(new HttpOnNextListener<List<Report>>() {
            @Override
            public void onNext(List<Report> o) {
                MineApp.reportList.clear();
                MineApp.reportList.addAll(o);
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
    public void systemChat() {
        systemChatApi api = new systemChatApi(new HttpOnNextListener<SystemMsg>() {
            @Override
            public void onNext(SystemMsg o) {
                MineApp.mineNewsCount.setSystemNewsNum(o.getNoReadNum());
                activity.sendBroadcast(new Intent("lobster_newsCount"));
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    activity.sendBroadcast(new Intent("lobster_newsCount"));
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
                    if (chatMsg.getUserId() == BaseActivity.systemUserId) {
                        // 系统消息
                        MineApp.mineNewsCount.setContent(chatMsg.getStanza());
                        MineApp.mineNewsCount.setCreateTime(chatMsg.getCreationDate());
                        MineApp.mineNewsCount.setMsgType(chatMsg.getMsgType());
                        MineApp.mineNewsCount.setSystemNewsNum(chatMsg.getNoReadNum());
                        activity.sendBroadcast(new Intent("lobster_newsCount"));
                    } else if (chatMsg.getUserId() == BaseActivity.dynUserId) {
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
                    mBinding.setUnReadCount(chatListDb.getAllUnReadNum());
                    contactNum(false);
                }
            }
        }, activity).setPageNo(pageNo);
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
                mBinding.setUnReadCount(chatListDb.getAllUnReadNum());
                new Handler().postDelayed(() -> {
                    Intent data = new Intent("lobster_updateContactNum");
                    data.putExtra("chatType", 1);
                    data.putExtra("isUpdate", isUpdate);
                    activity.sendBroadcast(data);
                }, 500);
                if (!isUpdate) {
                    noReadBottleNum(false);
                    initRemind(o.getBeLikeCount());
                }
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
        mBinding.setUnReadCount(chatListDb.getAllUnReadNum());
        new Handler().postDelayed(() -> {
            Intent data = new Intent("lobster_updateContactNum");
            data.putExtra("chatType", 2);
            data.putExtra("isUpdate", isUpdate);
            activity.sendBroadcast(data);
        }, 500);
    }

    private void initRemind(int beLikeCount) {
        if (PreferenceUtil.readStringValue(activity, "likeRemark_" + BaseActivity.userId).isEmpty()) {
            PreferenceUtil.saveStringValue(activity, "likeRemark_" + BaseActivity.userId, DateUtil.getNow(DateUtil.yyyy_MM_dd_HH_mm));
            PreferenceUtil.saveIntValue(activity, "beLikeCount" + BaseActivity.userId, beLikeCount);
        } else {
            String lastTime = PreferenceUtil.readStringValue(activity, "likeRemark_" + BaseActivity.userId);
            if (DateUtil.getDateCount(DateUtil.getNow(DateUtil.yyyy_MM_dd_HH_mm), lastTime, DateUtil.yyyy_MM_dd_HH_mm, 1000f * 3600f * 24f) > 1) {
                PreferenceUtil.saveStringValue(activity, "likeRemark_" + BaseActivity.userId, DateUtil.getNow(DateUtil.yyyy_MM_dd_HH_mm));
                int count = beLikeCount - PreferenceUtil.readIntValue(activity, "beLikeCount" + BaseActivity.userId);
                if (count > 0) {
                    startAnimator("24小时内有", count < 99 ? (count + "") : "99+", "人喜欢你啦", "");
                }
                PreferenceUtil.saveIntValue(activity, "beLikeCount" + BaseActivity.userId, beLikeCount);
            }
        }
    }

    @Override
    public void otherInfo(long otherUserId, YWMessage ywMessage) {
        otherInfoApi api = new otherInfoApi(new HttpOnNextListener<MemberInfo>() {
            @Override
            public void onNext(MemberInfo o) {
                CustomMessageBody body = (CustomMessageBody) LoginSampleHelper.unpack(ywMessage.getContent());
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
                mBinding.setUnReadCount(chatListDb.getAllUnReadNum());
                // 更新会话列表
                Intent data = new Intent("lobster_updateChat");
                data.putExtra("userId", otherUserId);
                activity.sendBroadcast(data);

                // 更新对话页面
                Intent upMessage = new Intent("lobster_upMessage/friend=" + otherUserId);
                upMessage.putExtra("ywMessage", ywMessage);
                activity.sendBroadcast(upMessage);

            }
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void recommendRankingList() {
        recommendRankingListApi api = new recommendRankingListApi(new HttpOnNextListener<List<RecommendInfo>>() {
            @Override
            public void onNext(List<RecommendInfo> o) {
                MineApp.recommendInfoList.addAll(o);
            }
        }, activity).setCityId(areaDb.getCityId(PreferenceUtil.readStringValue(activity, "cityName"))).setSex(mineInfo.getSex() == 0 ? 1 : 0);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void startAnimator(String title, String content, String subContent, String logo) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mBinding.remindRelative.getLayoutParams();
        params.setMarginEnd(ObjectUtils.getViewSizeByWidthFromMax(220));
        mBinding.remindRelative.setLayoutParams(params);
        mBinding.tvTitle.setText(title);
        mBinding.tvContent.setText(content);
        mBinding.tvSubContent.setText(subContent);
        mBinding.setOtherHead(logo);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mBinding.remindRelative, "scaleX", 0, 1).setDuration(500);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mBinding.remindRelative, "scaleY", 0, 1).setDuration(500);
        ObjectAnimator translateY = ObjectAnimator.ofFloat(mBinding.remindRelative, "translationY", 0, -30, 0, -30, 0, -30, 0).setDuration(500);
        ObjectAnimator scaleXEnd = ObjectAnimator.ofFloat(mBinding.remindRelative, "scaleX", 1, 0).setDuration(500);
        ObjectAnimator scaleYEnd = ObjectAnimator.ofFloat(mBinding.remindRelative, "scaleY", 1, 0).setDuration(500);

        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.play(scaleX).with(scaleY).after(5000);
        animatorSet.play(translateY).after(scaleY);
        animatorSet.play(scaleXEnd).with(scaleYEnd).after(translateY).after(10000);
        animatorSet.start();
    }

    public void stopAnimator() {
        animatorSet.cancel();
    }

    /**
     * 阿里百川登录账号
     */
    private void myImAccountInfoApi() {
        myImAccountInfoApi api = new myImAccountInfoApi(new HttpOnNextListener<ImAccount>() {
            @Override
            public void onNext(ImAccount o) {
                loginHelper.loginOut_Sample();
                loginHelper.login_Sample(activity, o.getImUserId(), o.getImPassWord());
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }
    /**
     * 权限
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            performCodeWithPermission("虾菇需要访问显示通知权限", new BaseActivity.PermissionCallback() {
                        @Override
                        public void hasPermission() {
                            setPermissions();
                        }

                        @Override
                        public void noPermission() {
                        }
                    }, Manifest.permission.FOREGROUND_SERVICE);
        } else {
            setPermissions();
        }
    }

    private void setPermissions() {
        activity.startService(new Intent(activity, ForegroundLiveService.class));
    }

}
