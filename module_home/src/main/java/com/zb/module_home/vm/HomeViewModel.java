package com.zb.module_home.vm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_home.databinding.HomeFragBinding;
import com.zb.module_home.iv.HomeVMInterface;

import androidx.databinding.ViewDataBinding;

public class HomeViewModel extends BaseViewModel implements HomeVMInterface {
    private HomeFragBinding mBinding;
    private BaseReceiver homeBottleReceiver;
    private BaseReceiver bottleTitleReceiver;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (HomeFragBinding) binding;
        playAnimator(mBinding.circleView);
        mBinding.setShowBottle(true);
        homeBottleReceiver = new BaseReceiver(activity, "lobster_homeBottle") {
            @Override
            public void onReceive(Context context, Intent intent) {
                mBinding.setShowBottle(intent.getIntExtra("index", 1) == 1);
            }
        };

        bottleTitleReceiver = new BaseReceiver(activity, "lobster_bottleTitle") {
            @Override
            public void onReceive(Context context, Intent intent) {
                mBinding.setIsPlay(intent.getBooleanExtra("isPlay", false));
            }
        };
        mBinding.setIsPlay(false);
    }

    public void onDestroy() {
        try {
            homeBottleReceiver.unregisterReceiver();
            bottleTitleReceiver.unregisterReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void publishDiscover(View view) {
        getPermissions();
    }

    @Override
    public void toSearch(View view) {
        ActivityUtils.getHomeSearch();
    }

    @Override
    public void entryBottle(View view) {
        ActivityUtils.getBottleThrow();
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
        if (mBinding.viewPage.getCurrentItem() == 2) {
            ActivityUtils.getCameraVideo(false);
        } else {
            ActivityUtils.getCameraMain(activity, true, true, false);
        }
    }
}
