package com.zb.lib_base.activity;

import android.os.Bundle;

import com.alibaba.android.arouter.launcher.ARouter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

public abstract class BaseActivity extends AppCompatActivity {

    public ViewDataBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, getRes());

        ARouter.getInstance().inject(this);
        initUI();
    }

    public abstract int getRes();

    public abstract void initUI();

    @Override
    protected void onDestroy() {
        if (mBinding != null)
            mBinding.unbind();

        super.onDestroy();
    }
}
