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
        if (checkPermissionGranted(activity, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)) {
            setPermissions();
        } else {
            if (PreferenceUtil.readIntValue(activity, "publishPermission") == 0) {
                PreferenceUtil.saveIntValue(activity, "publishPermission", 1);
                new TextPW(activity, mBinding.getRoot(), "权限说明",
                        "在使用发布动态功能，包括图文、视频时，我们将会申请相机、存储、麦克风权限：" +
                                "\n 1、申请相机权限--发布动态时获取拍摄照片，录制视频功能，" +
                                "\n 2、申请存储权限--发布动态时获取保存和读取图片、视频，" +
                                "\n 3、申请麦克风权限--发布视频动态时获取录制视频音频功能，" +
                                "\n 4、若您点击“同意”按钮，我们方可正式申请上述权限，以便正常发布图文动态、视频动态，" +
                                "\n 5、若您点击“拒绝”按钮，我们将不再主动弹出该提示，您也无法使用发布动态功能，不影响使用其他的虾姑功能/服务，" +
                                "\n 6、您也可以通过“手机设置--应用--虾菇--权限”或app内“我的--设置--权限管理--权限”，手动开启或关闭相机、存储、麦克风权限。",
                        "同意", false, true, this::getPermissions);
            } else {
                if (!checkPermissionGranted(activity, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    SCToastUtil.showToast(activity, "你未开启存储权限，请前往我的--设置--权限管理--权限进行设置", true);
                } else if (!checkPermissionGranted(activity, Manifest.permission.CAMERA)) {
                    SCToastUtil.showToast(activity, "你未开启相机权限，请前往我的--设置--权限管理--权限进行设置", true);
                } else if (!checkPermissionGranted(activity, Manifest.permission.RECORD_AUDIO)) {
                    SCToastUtil.showToast(activity, "你未开启麦克风权限，请前往我的--设置--权限管理--权限进行设置", true);
                }
            }
        }
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
            performCodeWithPermission("虾菇需要访问相机、存储、麦克风权限", new BaseActivity.PermissionCallback() {
                        @Override
                        public void hasPermission() {
                            setPermissions();
                        }

                        @Override
                        public void noPermission() {
                            PreferenceUtil.saveIntValue(activity, "publishPermission", 1);
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
