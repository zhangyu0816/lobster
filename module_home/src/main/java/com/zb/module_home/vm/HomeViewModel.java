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
import com.zb.module_home.utils.Compressor;
import com.zb.module_home.utils.InitListener;

import androidx.databinding.ViewDataBinding;

public class HomeViewModel extends BaseViewModel implements HomeVMInterface {
    private HomeFragBinding mBinding;
    private BaseReceiver homeBottleReceiver;
    private Compressor mCompressor;

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
        getPermissions(0);
    }

    public void onDestroy() {
        homeBottleReceiver.unregisterReceiver();
    }

    @Override
    public void publishDiscover(View view) {
        getPermissions(1);
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
    private void getPermissions(int type) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            performCodeWithPermission("虾菇需要访问读写外部存储权限及相机权限", new BaseActivity.PermissionCallback() {
                        @Override
                        public void hasPermission() {
                            setPermissions(type);
                        }

                        @Override
                        public void noPermission() {
                        }
                    }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO);
        } else {
            setPermissions(type);
        }
    }

    private void setPermissions(int type) {
        if(type==0){
            BaseActivity.createFfmpegFile();
            mCompressor = new Compressor(activity);
            mCompressor.loadBinary(new InitListener() {
                @Override
                public void onLoadSuccess() {
                }

                @Override
                public void onLoadFail(String reason) {
                }
            });
        }else{
            MineApp.toPublish = true;
            MineApp.toContinue = false;
            if (mBinding.viewPage.getCurrentItem() == 2) {
                ActivityUtils.getCameraVideo(false);
            } else {
                ActivityUtils.getCameraMain(activity, true, true, false);
            }
        }
    }
}
