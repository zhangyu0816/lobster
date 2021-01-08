package com.zb.module_mine.activity;

import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.vm.RealNameViewModel;

@Route(path = RouteUtils.Mine_Real_Name)
public class RealNameActivity extends MineBaseActivity {
    private RealNameViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.mine_real_name;
    }

    @Override
    public void initUI() {
        viewModel = new RealNameViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.title, "真人认证");
        mBinding.setVariable(BR.step, 1);
        mBinding.setVariable(BR.remind, "此认证照片仅用于认证，不会在您的个人主页上分享！");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (viewModel != null)
                viewModel.back(null);
            return true;
        }
        return false;
    }
}
