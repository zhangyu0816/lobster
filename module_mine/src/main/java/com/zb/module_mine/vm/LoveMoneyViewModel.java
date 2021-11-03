package com.zb.module_mine.vm;

import android.view.View;

import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.adapter.MineAdapter;
import com.zb.module_mine.databinding.AcLoveMoneyBinding;

import androidx.databinding.ViewDataBinding;

public class LoveMoneyViewModel extends BaseViewModel {
    private AcLoveMoneyBinding mBinding;
    public MineAdapter adapter;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (AcLoveMoneyBinding) binding;
        mBinding.setTitle("我的收益");
        setAdapter();
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void setAdapter() {

    }

    public void withdraw(View view) {
    }

    public void openLove(View view) {

    }
}
