package com.yimi.rentme.vm;

import android.Manifest;
import android.animation.AnimatorSet;
import android.os.Build;
import android.widget.RelativeLayout;

import com.amap.api.location.AMapLocationListener;
import com.yimi.rentme.BR;
import com.yimi.rentme.databinding.AcMainBinding;
import com.yimi.rentme.iv.MainVMInterface;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.adapter.FragmentAdapter;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.AMapLocation;
import com.zb.lib_base.utils.FragmentUtils;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_card.windows.GuidancePW;

import java.util.ArrayList;

import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class MainViewModel extends BaseViewModel implements MainVMInterface {
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private AcMainBinding mainBinding;
    private int nowIndex = -1;
    private AnimatorSet animatorSet = new AnimatorSet();
    private AMapLocation aMapLocation;

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
        if (MineApp.cityName.isEmpty()) {
            getPermissions();
        }
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
                    PreferenceUtil.saveStringValue(activity, "cityName", MineApp.cityName);
                    PreferenceUtil.saveStringValue(activity, "address", address);
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
}
