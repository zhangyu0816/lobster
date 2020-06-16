package com.zb.module_register.vm;

import android.Manifest;
import android.os.Build;
import android.view.View;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_register.databinding.RegisterLogoBinding;
import com.zb.module_register.iv.LogoVMInterface;

import androidx.databinding.ViewDataBinding;

public class LogoViewModel extends BaseViewModel implements LogoVMInterface {
    private RegisterLogoBinding logoBinding;

    @Override
    public void back(View view) {
        super.back(view);
        ActivityUtils.getRegisterCode(false);
        activity.finish();
    }

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        logoBinding = (RegisterLogoBinding) binding;
    }

    @Override
    public void upload(View view) {
        getPermissions();
    }

    /**
     * 权限
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            performCodeWithPermission( "虾菇需要访问读外部存储权限", new BaseActivity.PermissionCallback() {
                @Override
                public void hasPermission() {
                    setPermissions();
                }

                @Override
                public void noPermission() {
                }
            }, Manifest.permission.READ_EXTERNAL_STORAGE);
        } else {
            setPermissions();
        }
    }

    private void setPermissions() {
        ActivityUtils.getCameraMain(activity, false, false);
    }

    @Override
    public void next(View view) {
        if (logoBinding.getImageUrl().isEmpty()) {
            SCToastUtil.showToast(activity, "请上传头像", false);
            return;
        }
        MineApp.registerInfo.getImageList().clear();
        MineApp.registerInfo.getImageList().add(logoBinding.getImageUrl());
        MineApp.registerInfo.getImageList().add("");
        MineApp.registerInfo.getImageList().add("");
        MineApp.registerInfo.getImageList().add("");
        MineApp.registerInfo.getImageList().add("");
        MineApp.registerInfo.getImageList().add("");
        ActivityUtils.getRegisterImages();
        activity.finish();
    }
}
