package com.zb.module_mine.activity;

import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.vm.SelectJobViewModel;

@Route(path = RouteUtils.Mine_Select_Job)
public class SelectJobActivity extends MineBaseActivity {
    @Autowired(name = "job")
    String job = "";

    private SelectJobViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.mine_select_job;
    }

    @Override
    public void initUI() {
        viewModel = new SelectJobViewModel();
        viewModel.job = job;
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.title, "选择职业");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            viewModel.back(null);
            mBinding = null;
            viewModel = null;
            return true;
        }
        return false;
    }
}
