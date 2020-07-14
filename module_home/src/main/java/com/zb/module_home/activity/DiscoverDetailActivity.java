package com.zb.module_home.activity;

import android.net.Uri;
import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_home.BR;
import com.zb.module_home.R;
import com.zb.module_home.vm.DiscoverDetailViewModel;

import java.util.Objects;

@Route(path = RouteUtils.Home_Discover_Detail)
public class DiscoverDetailActivity extends HomeBaseActivity {

    @Autowired(name = "friendDynId")
    long friendDynId;

    private DiscoverDetailViewModel viewModel;
    private Uri uri;

    @Override
    public int getRes() {
        return R.layout.home_discover_detail;
    }

    @Override
    public void initUI() {
        fitComprehensiveScreen();
        viewModel = new DiscoverDetailViewModel();

        uri = getIntent().getData();
        if (friendDynId == 0 && uri != null) {
            friendDynId = Long.parseLong(Objects.requireNonNull(uri.getQueryParameter("friendDynId")));
        }

        viewModel.friendDynId = friendDynId;
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel,viewModel);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            viewModel.back(null);
            return true;
        }
        return false;
    }
}
