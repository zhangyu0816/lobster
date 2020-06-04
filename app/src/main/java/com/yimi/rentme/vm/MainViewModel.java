package com.yimi.rentme.vm;

import android.Manifest;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.widget.RelativeLayout;

import com.alibaba.mobileim.conversation.YWMessage;
import com.yimi.rentme.BR;
import com.yimi.rentme.databinding.AcMainBinding;
import com.yimi.rentme.iv.MainVMInterface;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.adapter.FragmentAdapter;
import com.zb.lib_base.api.bankInfoListApi;
import com.zb.lib_base.api.chatListApi;
import com.zb.lib_base.api.comTypeApi;
import com.zb.lib_base.api.giftListApi;
import com.zb.lib_base.api.joinPairPoolApi;
import com.zb.lib_base.api.myImAccountInfoApi;
import com.zb.lib_base.api.newDynMsgAllNumApi;
import com.zb.lib_base.api.openedMemberPriceListApi;
import com.zb.lib_base.api.otherInfoApi;
import com.zb.lib_base.api.rechargeDiscountListApi;
import com.zb.lib_base.api.systemChatApi;
import com.zb.lib_base.api.walletAndPopApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.ChatListDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.imcore.CustomMessageBody;
import com.zb.lib_base.imcore.LoginSampleHelper;
import com.zb.lib_base.model.BankInfo;
import com.zb.lib_base.model.ChatList;
import com.zb.lib_base.model.GiftInfo;
import com.zb.lib_base.model.ImAccount;
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

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import io.realm.Realm;

public class MainViewModel extends BaseViewModel implements MainVMInterface {
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private AcMainBinding mainBinding;
    private ChatListDb chatListDb;
    private int pageNo = 1;
    private int nowIndex = -1;
    private AnimatorSet animatorSet = new AnimatorSet();
    private AMapLocation aMapLocation;
    private LoginSampleHelper loginHelper;
    private BaseReceiver rechargeReceiver;
    private BaseReceiver chatListReceiver;
    private BaseReceiver newMsgReceiver;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        chatListDb = new ChatListDb(Realm.getDefaultInstance());
        mainBinding = (AcMainBinding) binding;
        mainBinding.tvTitle.setTypeface(MineApp.simplifiedType);
        mainBinding.tvContent.setTypeface(MineApp.simplifiedType);
        mainBinding.tvSubContent.setTypeface(MineApp.simplifiedType);
        initFragments();
        aMapLocation = new AMapLocation(activity);
        MineApp.cityName = PreferenceUtil.readStringValue(activity, "cityName");
        getPermissions();
        openedMemberPriceList();
        loginHelper = LoginSampleHelper.getInstance();
        loginHelper.loginOut_Sample();

        if (!TextUtils.equals(BaseActivity.sessionId, ""))
            myImAccountInfoApi();

        giftList();
        rechargeDiscountList();
        bankInfoList();
        comType();
        walletAndPop();
        newDynMsgAllNum();
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
                        mainBinding.setUnReadCount(chatListDb.getAllUnReadNum());
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
    }

    public void onDestroy() {
        rechargeReceiver.unregisterReceiver();
        chatListReceiver.unregisterReceiver();
        newMsgReceiver.unregisterReceiver();
    }

    private void initFragments() {
        fragments.clear();
        fragments.add(FragmentUtils.getHomeFragment());
        fragments.add(FragmentUtils.getCardFragment());
        fragments.add(FragmentUtils.getChatFragment());
        fragments.add(FragmentUtils.getMineFragment());
        mainBinding.viewPage.setAdapter(new FragmentAdapter(activity.getSupportFragmentManager(), fragments));
        mainBinding.viewPage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                nowIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                mainBinding.setIndex(nowIndex);
                if (state == 0 && nowIndex == 1 && PreferenceUtil.readIntValue(activity, "showGuidance") == 0) {
                    new GuidancePW(activity, mBinding.getRoot());
                }
            }
        });

        selectPage(0);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mainBinding.remindRelative.getLayoutParams();
        params.setMarginEnd(ObjectUtils.getViewSizeByWidthFromMax(220));
        mainBinding.remindRelative.setLayoutParams(params);

        mainBinding.tvTitle.setText("24小时内有");
        mainBinding.tvContent.setText("99+");
        mainBinding.tvSubContent.setText("人喜欢你啦");

        mBinding.setVariable(BR.otherHead, MineApp.logo);

//        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mainBinding.remindRelative, "scaleX", 0, 1).setDuration(500);
//        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mainBinding.remindRelative, "scaleY", 0, 1).setDuration(500);
//        ObjectAnimator translateY = ObjectAnimator.ofFloat(mainBinding.remindRelative, "translationY", 0, -30, 0, -30, 0, -30, 0).setDuration(500);
//        ObjectAnimator scaleXEnd = ObjectAnimator.ofFloat(mainBinding.remindRelative, "scaleX", 1, 0).setDuration(500);
//        ObjectAnimator scaleYEnd = ObjectAnimator.ofFloat(mainBinding.remindRelative, "scaleY", 1, 0).setDuration(500);
//
//        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
//        animatorSet.play(scaleX).with(scaleY).after(5000);
//        animatorSet.play(translateY).after(scaleY);
//        animatorSet.play(scaleXEnd).with(scaleYEnd).after(translateY).after(10000);
//        animatorSet.start();
    }

    @Override
    public void selectPage(int index) {
        if (nowIndex == index)
            return;
        nowIndex = index;
        mainBinding.setIndex(nowIndex);
        mainBinding.viewPage.setCurrentItem(index);
    }

    @Override
    public void joinPairPool(String longitude, String latitude) {
        // 加入匹配池
        joinPairPoolApi api = new joinPairPoolApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {

            }
        }, activity).setLatitude(latitude).setLongitude(longitude);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void openedMemberPriceList() {
        openedMemberPriceListApi api = new openedMemberPriceListApi(new HttpOnNextListener<List<VipInfo>>() {
            @Override
            public void onNext(List<VipInfo> o) {
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
                MineApp.giftInfoList.addAll(o);
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void rechargeDiscountList() {
        rechargeDiscountListApi api = new rechargeDiscountListApi(new HttpOnNextListener<List<RechargeInfo>>() {
            @Override
            public void onNext(List<RechargeInfo> o) {
                MineApp.rechargeInfoList.addAll(o);
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void bankInfoList() {
        bankInfoListApi api = new bankInfoListApi(new HttpOnNextListener<List<BankInfo>>() {
            @Override
            public void onNext(List<BankInfo> o) {
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
                        MineApp.mineNewsCount.setContent(chatMsg.getStanza());
                        MineApp.mineNewsCount.setCreateTime(chatMsg.getCreationDate());
                        MineApp.mineNewsCount.setMsgType(chatMsg.getMsgType());
                        MineApp.mineNewsCount.setSystemNewsNum(chatMsg.getNoReadNum());
                        activity.sendBroadcast(new Intent("lobster_newsCount"));
                    }
                    if (chatMsg.getUserId() > 10010) {
                        chatListDb.saveChatList(chatMsg);
                    }
                }
                pageNo++;
                chatList();
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    if (chatListDb.getChatList().size() > 0) {
                        activity.sendBroadcast(new Intent("lobster_updateChat"));
                    }
                    mainBinding.setUnReadCount(chatListDb.getAllUnReadNum());
                }
            }
        }, activity).setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
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
                mainBinding.setUnReadCount(chatListDb.getAllUnReadNum());
                Intent data = new Intent("lobster_updateChat");
                data.putExtra("userId", otherUserId);
                activity.sendBroadcast(data);
            }
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    public void stopAnimator() {
        animatorSet.cancel();
    }

    /**
     * 权限
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            performCodeWithPermission("虾菇需要访问定位权限", new BaseActivity.PermissionCallback() {
                        @Override
                        public void hasPermission() {
                            setLocation();
                        }

                        @Override
                        public void noPermission() {
                            baseLocation();
                        }
                    }, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE);
        } else {
            setLocation();
        }
    }

    private void setLocation() {
        aMapLocation.start(location -> {
            if (location != null) {
                if (location.getErrorCode() == 0) {
                    MineApp.cityName = location.getCity();
                    String address = location.getAddress();
                    String longitude = location.getLongitude() + "";
                    String latitude = location.getLatitude() + "";

                    PreferenceUtil.saveStringValue(activity, "longitude", longitude);
                    PreferenceUtil.saveStringValue(activity, "latitude", latitude);
                    PreferenceUtil.saveStringValue(activity, "provinceName", location.getProvince());
                    PreferenceUtil.saveStringValue(activity, "cityName", MineApp.cityName);
                    PreferenceUtil.saveStringValue(activity, "districtName", location.getDistrict());
                    PreferenceUtil.saveStringValue(activity, "address", address);
                    joinPairPool(longitude, latitude);
                }
                aMapLocation.stop();
                aMapLocation.destroy();
            }
        });
    }

    private void baseLocation() {
        PreferenceUtil.saveStringValue(activity, "longitude", "120.641956");
        PreferenceUtil.saveStringValue(activity, "latitude", "28.021994");
        PreferenceUtil.saveStringValue(activity, "cityName", "温州市");
        PreferenceUtil.saveStringValue(activity, "address", "浙江省温州市鹿城区望江东路175号靠近温州银行(文化支行)");
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
