package com.zb.module_home.activity;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.lib_base.utils.StatusBarUtil;
import com.zb.module_home.BR;
import com.zb.module_home.R;
import com.zb.module_home.vm.SearchViewModel;

@Route(path = RouteUtils.Home_Search)
public class SearchActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.HomeGreyTheme);
        super.onCreate(savedInstanceState);
        StatusBarUtil.statusBarLightModeNotFull(this);
    }

    @Override
    public int getRes() {
        return R.layout.home_search;
    }

    @Override
    public void initUI() {
        SearchViewModel viewModel = new SearchViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel,viewModel);

    }
}
