package com.yimi.rentme.vm;

import android.Manifest;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.widget.RelativeLayout;

import com.yimi.rentme.BR;
import com.yimi.rentme.databinding.AcMainBinding;
import com.yimi.rentme.iv.MainVMInterface;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.adapter.FragmentAdapter;
import com.zb.lib_base.api.bankInfoListApi;
import com.zb.lib_base.api.comTypeApi;
import com.zb.lib_base.api.giftListApi;
import com.zb.lib_base.api.joinPairPoolApi;
import com.zb.lib_base.api.myImAccountInfoApi;
import com.zb.lib_base.api.openedMemberPriceListApi;
import com.zb.lib_base.api.rechargeDiscountListApi;
import com.zb.lib_base.api.walletAndPopApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.imcore.LoginSampleHelper;
import com.zb.lib_base.model.BankInfo;
import com.zb.lib_base.model.GiftInfo;
import com.zb.lib_base.model.ImAccount;
import com.zb.lib_base.model.RechargeInfo;
import com.zb.lib_base.model.Report;
import com.zb.lib_base.model.VipInfo;
import com.zb.lib_base.model.WalletInfo;
import com.zb.lib_base.utils.AMapLocation;
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

public class MainViewModel extends BaseViewModel implements MainVMInterface {
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private AcMainBinding mainBinding;
    private int nowIndex = -1;
    private AnimatorSet animatorSet = new AnimatorSet();
    private AMapLocation aMapLocation;

    private LoginSampleHelper loginHelper;
    private BaseReceiver rechargeReceiver;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
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
        rechargeReceiver = new BaseReceiver(activity, "lobster_recharge") {
            @Override
            public void onReceive(Context context, Intent intent) {
                walletAndPop();
            }
        };
    }

    public void onDestroy(){
        rechargeReceiver.unregisterReceiver();
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
            }
        }, activity);
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
