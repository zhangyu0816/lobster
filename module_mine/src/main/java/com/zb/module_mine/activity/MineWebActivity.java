package com.zb.module_mine.activity;

import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.vm.MineWebViewModel;

@Route(path = RouteUtils.Mine_Web)
public class MineWebActivity extends MineBaseActivity {
    @Autowired(name = "title")
    String title = "";
    @Autowired(name = "url")
    String url = "";

    private MineWebViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.mine_web;
    }

    @Override
    public void initUI() {
        viewModel = new MineWebViewModel();
        viewModel.url = url;
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.title, title);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
        mBinding = null;
        viewModel = null;
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
