package com.zb.module_mine.vm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.adapter.FragmentAdapter;
import com.zb.lib_base.api.myInfoApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.FragmentUtils;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.VipAdPW;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.databinding.MineFragBinding;
import com.zb.module_mine.iv.MineVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MineViewModel extends BaseViewModel implements MineVMInterface {

    public FragmentManager fm;
    private MineInfo mineInfo;
    private MineFragBinding mBinding;
    private BaseReceiver updateMineInfoReceiver;
    private BaseReceiver newsCountReceiver;
    private BaseReceiver openVipReceiver;
    private BaseReceiver updateContactNumReceiver;
    private BaseReceiver mainSelectReceiver;
    private List<Fragment> fragments = new ArrayList<>();

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mineInfo = mineInfoDb.getMineInfo();
        mBinding = (MineFragBinding) binding;
        playAnimator(mBinding.circleView);
        mBinding.setMineNewsCount(MineApp.mineNewsCount);
        if (MineApp.contactNum != null) {
            mBinding.setContactNum(MineApp.contactNum);
            mBinding.setHasNewBeLike(MineApp.contactNum.getBeLikeCount() > PreferenceUtil.readIntValue(activity, "beLikeCount" + BaseActivity.userId));
        }
        mBinding.setMineInfo(mineInfo);

        updateMineInfoReceiver = new BaseReceiver(activity, "lobster_updateMineInfo") {
            @Override
            public void onReceive(Context context, Intent intent) {
                mineInfo = mineInfoDb.getMineInfo();
                mBinding.setMineInfo(mineInfo);
            }
        };
        newsCountReceiver = new BaseReceiver(activity, "lobster_newsCount") {
            @Override
            public void onReceive(Context context, Intent intent) {
                mBinding.setMineNewsCount(MineApp.mineNewsCount);
            }
        };

        // 开通会员
        openVipReceiver = new BaseReceiver(activity, "lobster_openVip") {
            @Override
            public void onReceive(Context context, Intent intent) {
                myInfo();
            }
        };

        updateContactNumReceiver = new BaseReceiver(activity, "lobster_updateContactNum") {
            @Override
            public void onReceive(Context context, Intent intent) {
                int chatType = intent.getIntExtra("chatType", 0);
                if (chatType == 1) {
                    mBinding.setContactNum(MineApp.contactNum);
                    mBinding.setHasNewBeLike(MineApp.contactNum.getBeLikeCount() > PreferenceUtil.readIntValue(activity, "beLikeCount" + BaseActivity.userId));
                }
            }
        };

        mainSelectReceiver = new BaseReceiver(activity, "lobster_mainSelect") {
            @Override
            public void onReceive(Context context, Intent intent) {
                mineInfo = mineInfoDb.getMineInfo();
                mBinding.setMineInfo(mineInfo);
            }
        };
        initFragments();
    }

    public void onDestroy() {
        updateMineInfoReceiver.unregisterReceiver();
        newsCountReceiver.unregisterReceiver();
        openVipReceiver.unregisterReceiver();
        updateContactNumReceiver.unregisterReceiver();
        mainSelectReceiver.unregisterReceiver();
    }

    private void initFragments() {
        fragments.clear();
        fragments.add(FragmentUtils.getCardMemberDiscoverFragment(1));
        fragments.add(FragmentUtils.getCardMemberVideoFragment(1));
        mBinding.viewPage.setAdapter(new FragmentAdapter(fm, fragments));
        initTabLayout(new String[]{"动态", "小视频"}, mBinding.tabLayout, mBinding.viewPage, R.color.black_252, R.color.black_827, 0);
    }

    @Override
    public void publishDiscover(View view) {
//        MineApp.toPublish = false;
//        MineApp.cameraType = 0;
//        MineApp.filePath = "";
//        MineApp.isMore = false;
//        MineApp.time = 0;
//        ActivityUtils.getHomePublishImage();
        getPermissions();
    }

    @Override
    public void openVip(View view) {
        if (MineApp.vipInfoList.size() > 0)
            ActivityUtils.getMineOpenVip();
    }

    @Override
    public void toEditMember(View view) {
        ActivityUtils.getMineEditMember();
    }

    @Override
    public void toNews(View view) {
        ActivityUtils.getMineNewsManager();
    }

    @Override
    public void toSetting(View view) {
        ActivityUtils.getMineSetting();
    }

    @Override
    public void contactNumDetail(int position) {
        if (position == 2) {
            if (mineInfo.getMemberType() == 2) {
                PreferenceUtil.saveIntValue(activity, "beLikeCount" + BaseActivity.userId, MineApp.contactNum.getBeLikeCount());
                mBinding.setVariable(BR.hasNewBeLike, false);
                ActivityUtils.getMineFCL(2);
                return;
            }
            new VipAdPW(activity, mBinding.getRoot(), false, 4, "");
        } else {
            ActivityUtils.getMineFCL(position);
        }
    }

    @Override
    public void myInfo() {
        myInfoApi api = new myInfoApi(new HttpOnNextListener<MineInfo>() {
            @Override
            public void onNext(MineInfo o) {
                mineInfo = o;
                mineInfoDb.saveMineInfo(o);
                mBinding.setMineInfo(mineInfo);
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    /**
     * 权限
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            performCodeWithPermission("虾菇需要访问读写外部存储权限及相机权限", new BaseActivity.PermissionCallback() {
                        @Override
                        public void hasPermission() {
                            setPermissions();
                        }

                        @Override
                        public void noPermission() {
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
        if (mBinding.viewPage.getCurrentItem() == 1) {
            ActivityUtils.getCameraVideo(false);
        } else {
            ActivityUtils.getCameraMain(activity, true, true, false);
        }
    }
}
