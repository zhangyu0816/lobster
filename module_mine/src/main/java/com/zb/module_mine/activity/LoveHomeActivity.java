package com.zb.module_mine.activity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.vm.LoveHomeViewModel;

@Route(path = RouteUtils.Mine_LoveHome)
public class LoveHomeActivity extends BaseScreenActivity {

    private LoveHomeViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.ac_love_home;
    }

    @Override
    public void initUI() {
        viewModel = new LoveHomeViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
    }
}
