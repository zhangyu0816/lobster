package com.zb.module_home;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.utils.KeyBroadUtils;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_home.databinding.HomeDiscoverDetailBinding;
import com.zb.module_home.vm.DiscoverDetailViewModel;

@Route(path = RouteUtils.Home_Discover_Detail)
public class DiscoverDetailActivity extends BaseActivity {

    @Autowired(name = "friendDynId")
    long friendDynId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.HomeDetailTheme);
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getRes() {
        return R.layout.home_discover_detail;
    }

    @Override
    public void initUI() {
        DiscoverDetailViewModel viewModel = new DiscoverDetailViewModel();
        viewModel.friendDynId = friendDynId;
        mBinding.setVariable(BR.viewModel, viewModel);
        viewModel.setBinding(mBinding);

        HomeDiscoverDetailBinding binding = (HomeDiscoverDetailBinding) mBinding;
        KeyBroadUtils.controlKeyboardLayout(binding.mainLayout,binding.bottomLayout);
    }

}
