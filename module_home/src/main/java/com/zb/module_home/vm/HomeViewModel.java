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
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.SelectorPW;
import com.zb.lib_base.windows.TextPW;
import com.zb.module_home.databinding.HomeFragBinding;
import com.zb.module_home.iv.HomeVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.ViewDataBinding;

public class HomeViewModel extends BaseViewModel implements HomeVMInterface {
    private HomeFragBinding mBinding;
    private List<String> selectorList = new ArrayList<>();
    private BaseReceiver homeBottleReceiver;
    private BaseReceiver bottleTitleReceiver;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (HomeFragBinding) binding;
        playAnimator(mBinding.circleView);
        selectorList.add("发布照片");
        selectorList.add("发布小视频");

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
        if (PreferenceUtil.readIntValue(activity, "cameraPermission") == 0)
            new TextPW(activity, mBinding.getRoot(), "权限说明",
                    "我们会以申请权限的方式获取设备功能的使用：" +
                            "\n 1、申请相机权限--获取照相功能，" +
                            "\n 2、申请存储权限--获取照册功能，" +
                            "\n 3、申请麦克风权限--获取录制视频功能，" +
                            "\n 4、若你拒绝权限申请，仅无法使用发布动态功能，虾菇app其他功能不受影响，" +
                            "\n 5、可通过app内 我的--设置--权限管理 进行权限操作。",
                    "同意", false, true, new TextPW.CallBack() {
                @Override
                public void sure() {
                    PreferenceUtil.saveIntValue(activity, "cameraPermission", 1);
                    getPermissions1();
                }

                @Override
                public void cancel() {
                    PreferenceUtil.saveIntValue(activity, "cameraPermission", 2);
                }
            });
        else if (checkPermissionGranted(activity, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO))
            getPermissions1();
        else
            SCToastUtil.showToast(activity, "你已拒绝申请相机、存储、麦克风权限，请前往我的--设置--权限管理--权限进行设置", true);
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
    private void getPermissions1() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            performCodeWithPermission("虾菇需要访问存储权限及相机权限", new BaseActivity.PermissionCallback() {
                        @Override
                        public void hasPermission() {
                            setPermissions();
                        }

                        @Override
                        public void noPermission() {
                            PreferenceUtil.saveIntValue(activity, "cameraPermission", 2);
                            SCToastUtil.showToast(activity, "你已拒绝申请相机、存储、麦克风权限，请前往我的--设置--权限管理--权限进行设置", true);
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
        new SelectorPW(mBinding.getRoot(), selectorList, position1 -> {
            if (position1 == 0) {
                ActivityUtils.getCameraMain(activity, true, true, false);
            } else {
                ActivityUtils.getCameraVideo(false);
            }
        });
    }
}
