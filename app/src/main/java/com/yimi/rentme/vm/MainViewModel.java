package com.yimi.rentme.vm;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

import com.alibaba.mobileim.conversation.YWMessage;
import com.yimi.rentme.databinding.AcMainBinding;
import com.yimi.rentme.iv.MainVMInterface;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.adapter.FragmentAdapter;
import com.zb.lib_base.api.bankInfoListApi;
import com.zb.lib_base.api.chatListApi;
import com.zb.lib_base.api.comTypeApi;
import com.zb.lib_base.api.contactNumApi;
import com.zb.lib_base.api.giftListApi;
import com.zb.lib_base.api.joinPairPoolApi;
import com.zb.lib_base.api.likeMeListApi;
import com.zb.lib_base.api.myImAccountInfoApi;
import com.zb.lib_base.api.newDynMsgAllNumApi;
import com.zb.lib_base.api.noReadBottleNumApi;
import com.zb.lib_base.api.openedMemberPriceListApi;
import com.zb.lib_base.api.otherInfoApi;
import com.zb.lib_base.api.rechargeDiscountListApi;
import com.zb.lib_base.api.systemChatApi;
import com.zb.lib_base.api.walletAndPopApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.AreaDb;
import com.zb.lib_base.db.ChatListDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.imcore.CustomMessageBody;
import com.zb.lib_base.imcore.LoginSampleHelper;
import com.zb.lib_base.model.BankInfo;
import com.zb.lib_base.model.ChatList;
import com.zb.lib_base.model.ContactNum;
import com.zb.lib_base.model.GiftInfo;
import com.zb.lib_base.model.ImAccount;
import com.zb.lib_base.model.LikeMe;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.model.MineNewsCount;
import com.zb.lib_base.model.RechargeInfo;
import com.zb.lib_base.model.Report;
import com.zb.lib_base.model.SystemMsg;
import com.zb.lib_base.model.VipInfo;
import com.zb.lib_base.model.WalletInfo;
import com.zb.lib_base.utils.AMapLocation;
import com.zb.lib_base.utils.DateUtil;
import com.zb.lib_base.utils.FragmentUtils;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_card.windows.GuidancePW;
import com.zb.module_mine.BR;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import io.realm.Realm;

public class MainViewModel extends BaseViewModel implements MainVMInterface {
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private AcMainBinding mBinding;
    private ChatListDb chatListDb;
    private int pageNo = 1;
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
    private AreaDb areaDb;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        MineApp.isLogin = true;
        areaDb = new AreaDb(Realm.getDefaultInstance());
        chatListDb = new ChatListDb(Realm.getDefaultInstance());
        mBinding = (AcMainBinding) binding;
        mBinding.tvTitle.setTypeface(MineApp.simplifiedType);
        mBinding.tvContent.setTypeface(MineApp.simplifiedType);
        mBinding.tvSubContent.setTypeface(MineApp.simplifiedType);
        mBinding.setUnReadCount(0);
        mBinding.setNewsCount(0);
        initFragments();

        MineApp.cityName = PreferenceUtil.readStringValue(activity, "cityName");

        loginHelper = LoginSampleHelper.getInstance();
        loginHelper.loginOut_Sample();

        if (!TextUtils.equals(BaseActivity.sessionId, ""))
            myImAccountInfoApi();

        giftList();

        rechargeReceiver = new BaseReceiver(activity, "lobster_recharge") {
            @Override
            public void onReceive(Context context, Intent intent) {
                walletAndPop();
            }
        };

        chatListReceiver = new BaseReceiver(activity, "lobster_ chatList") {
            @Override
            public void onReceive(Context context, Intent intent) {
                pageNo = 1;
                chatList();
            }
        };

        newMsgReceiver = new BaseReceiver(activity, "lobster_newMsg") {
            @Override
            public void onReceive(Context context, Intent intent) {
                int count = intent.getIntExtra("unReadCount", 0);

                YWMessage ywMessage = (YWMessage) intent.getSerializableExtra("ywMessage");
                CustomMessageBody body = (CustomMessageBody) LoginSampleHelper.unpack(ywMessage.getContent());
                long otherUserId = body.getFromId() == BaseActivity.userId ? body.getToId() : body.getFromId();

                chatListDb.updateChatMsg(otherUserId, DateUtil.getNow(DateUtil.yyyy_MM_dd_HH_mm_ss), body.getStanza(), body.getMsgType(), count, new ChatListDb.CallBack() {
                    @Override
                    public void success() {
                        mBinding.setUnReadCount(chatListDb.getAllUnReadNum());
                        Intent data = new Intent("lobster_updateChat");
                        data.putExtra("userId", otherUserId);
                        activity.sendBroadcast(data);
                    }

                    @Override
                    public void fail() {
                        otherInfo(otherUserId, count, body);
                    }
                });

                Intent upMessage = new Intent("lobster_upMessage/friend=" + otherUserId);
                upMessage.putExtra("ywMessage", ywMessage);
                activity.sendBroadcast(upMessage);
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
                newDynMsgAllNum();
            }
        };

        newsCountReceiver = new BaseReceiver(activity, "lobster_newsCount") {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isClean = intent.getBooleanExtra("isClean", false);
                if (isClean) {
                    mBinding.setNewsCount(MineApp.mineNewsCount.getFriendDynamicGiftNum() + MineApp.mineNewsCount.getFriendDynamicReviewNum() + MineApp.mineNewsCount.getFriendDynamicGoodNum() + MineApp.mineNewsCount.getSystemNewsNum());
                }
            }
        };
    }

    public void onDestroy() {
        rechargeReceiver.unregisterReceiver();
        chatListReceiver.unregisterReceiver();
        newMsgReceiver.unregisterReceiver();
        resumeContactNumReceiver.unregisterReceiver();
        bottleNumReceiver.unregisterReceiver();
        mainSelectReceiver.unregisterReceiver();
        newsCountReceiver.unregisterReceiver();
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
                newDynMsgAllNum();
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
    public void newDynMsgAllNum() {
        newDynMsgAllNumApi api = new newDynMsgAllNumApi(new HttpOnNextListener<MineNewsCount>() {
            @Override
            public void onNext(MineNewsCount o) {
                MineApp.mineNewsCount = o;
                mBinding.setNewsCount(MineApp.mineNewsCount.getFriendDynamicGiftNum() + MineApp.mineNewsCount.getFriendDynamicReviewNum() + MineApp.mineNewsCount.getFriendDynamicGoodNum());
                activity.sendBroadcast(new Intent("lobster_newsCount"));
//                systemChat();
                chatList();
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
                mBinding.setNewsCount(MineApp.mineNewsCount.getFriendDynamicGiftNum() + MineApp.mineNewsCount.getFriendDynamicReviewNum() + MineApp.mineNewsCount.getFriendDynamicGoodNum() + MineApp.mineNewsCount.getSystemNewsNum());
                activity.sendBroadcast(new Intent("lobster_newsCount"));
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    MineApp.mineNewsCount.setSystemNewsNum(0);
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
                        mBinding.setNewsCount(MineApp.mineNewsCount.getFriendDynamicGiftNum() + MineApp.mineNewsCount.getFriendDynamicReviewNum() + MineApp.mineNewsCount.getFriendDynamicGoodNum() + MineApp.mineNewsCount.getSystemNewsNum());
                        activity.sendBroadcast(new Intent("lobster_newsCount"));
                    }
                    if (chatMsg.getUserId() > 10010) {
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
                    if (chatListDb.getChatList(4).size() > 0) {
                        activity.sendBroadcast(new Intent("lobster_updateChat"));
                    }
                    mBinding.setUnReadCount(chatListDb.getAllUnReadNum());
                    contactNum(false);
                }
            }
        }, activity).setPageNo(pageNo);
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
        }, activity).setOtherUserId(BaseActivity.userId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void noReadBottleNum(boolean isUpdate) {
        noReadBottleNumApi api = new noReadBottleNumApi(new HttpOnNextListener<Integer>() {
            @Override
            public void onNext(Integer o) {
                MineApp.noReadBottleNum = o;
                ChatList chatList = new ChatList();
                chatList.setUserId(BaseActivity.bottleUserId);
                chatList.setImage("bottle_logo_icon");
                chatList.setNick("漂流瓶");
                chatList.setMsgType(1);
                chatList.setStanza(o == 0 ? "茫茫人海中，需要流浪到何时" : "您有新消息");
                chatList.setNoReadNum(o);
                chatList.setChatType(2);
                chatListDb.saveChatList(chatList);
                mBinding.setUnReadCount(chatListDb.getAllUnReadNum());
                new Handler().postDelayed(() -> {
                    Intent data = new Intent("lobster_updateContactNum");
                    data.putExtra("chatType", 2);
                    data.putExtra("isUpdate", isUpdate);
                    activity.sendBroadcast(data);
                }, 500);

                if (!isUpdate) {
                    beSuperLikeList();
                }
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void beSuperLikeList() {
        likeMeListApi api = new likeMeListApi(new HttpOnNextListener<List<LikeMe>>() {
            @Override
            public void onNext(List<LikeMe> o) {
                for (LikeMe likeMe : o) {
                    ChatList chatList = new ChatList();
                    chatList.setUserId(likeMe.getOtherUserId());
                    chatList.setImage(likeMe.getHeadImage());
                    chatList.setNick(likeMe.getNick());
                    chatList.setMsgType(1);
                    chatList.setStanza("超级喜欢你！");
                    chatList.setNoReadNum(0);
                    chatList.setChatType(3);
                    chatList.setCreationDate(likeMe.getModifyTime());
                    chatListDb.saveChatList(chatList);
                    new Handler().postDelayed(() -> {
                        Intent data = new Intent("lobster_updateContactNum");
                        data.putExtra("chatType", 3);
                        data.putExtra("isUpdate", false);
                        activity.sendBroadcast(data);
                    }, 500);
                }
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    new Handler().postDelayed(() -> {
                        Intent data = new Intent("lobster_updateContactNum");
                        data.putExtra("chatType", 3);
                        data.putExtra("isUpdate", false);
                        activity.sendBroadcast(data);
                    }, 500);
                }
            }
        }, activity).setPageNo(0).setLikeOtherStatus(2);
        HttpManager.getInstance().doHttpDeal(api);
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
    public void otherInfo(long otherUserId, int count, CustomMessageBody body) {
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
                chatList.setNoReadNum(count);
                chatList.setPublicTag("");
                chatList.setEffectType(1);
                chatList.setAuthType(1);
                chatListDb.saveChatList(chatList);
                mBinding.setUnReadCount(chatListDb.getAllUnReadNum());
                Intent data = new Intent("lobster_updateChat");
                data.putExtra("userId", otherUserId);
                activity.sendBroadcast(data);
            }
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    public void startAnimator(String title, String content, String subContent, String logo) {
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

        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
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


}
