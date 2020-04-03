package com.zb.module_register.vm;

import android.Manifest;
import android.os.Build;
import android.view.View;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_register.iv.LogoVMInterface;

public class LogoViewModel extends BaseViewModel implements LogoVMInterface {
    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
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
            performCodeWithPermission("虾菇需要访问读外部存储权限", new BaseActivity.PermissionCallback() {
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
        ActivityUtils.getCameraMain();
    }

    @Override
    public void next(View view) {

    }
}
