package com.zb.lib_base.vm;

import android.view.View;

import com.zb.lib_base.iv.BaseVMInterface;

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
    public void back(View view) {

    }
}
