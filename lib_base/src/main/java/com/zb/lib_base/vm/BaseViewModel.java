package com.zb.lib_base.vm;

import com.zb.lib_base.iv.BaseVMInterface;

import androidx.databinding.ViewDataBinding;

public class BaseViewModel implements BaseVMInterface {
    public ViewDataBinding mBinding;

    @Override
    public void setBinding(ViewDataBinding binding) {
        mBinding = binding;
    }
}
