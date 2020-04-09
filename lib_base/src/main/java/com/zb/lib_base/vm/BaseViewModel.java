package com.zb.lib_base.vm;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.iv.BaseVMInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;

public class BaseViewModel implements BaseVMInterface {
    public ViewDataBinding mBinding;
    public AppCompatActivity activity;

    @Override
    public void setBinding(ViewDataBinding binding) {
        mBinding = binding;
        activity = (AppCompatActivity) mBinding.getRoot().getContext();
    }

    @Override
    public void clickItem(int position) {

    }

    /**
     * Android M运行时权限请求封装
     *
     * @param permissionDes 权限描述
     * @param runnable      请求权限回调
     * @param permissions   请求的权限（数组类型），直接从Manifest中读取相应的值，比如Manifest.permission.WRITE_CONTACTS
     */
    public void performCodeWithPermission(@NonNull String permissionDes, BaseActivity.PermissionCallback runnable, @NonNull String... permissions) {
        if (activity != null && activity instanceof BaseActivity) {
            ((BaseActivity) activity).performCodeWithPermission(permissionDes, runnable, permissions);
        }
    }
}
