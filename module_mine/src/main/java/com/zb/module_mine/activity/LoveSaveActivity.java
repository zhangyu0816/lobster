package com.zb.module_mine.activity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.vm.LoveSaveViewModel;

@Route(path = RouteUtils.Mine_LoveSave)
public class LoveSaveActivity extends BaseScreenActivity {

    private LoveSaveViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.ac_love_save;
    }

    @Override
    public void initUI() {
        viewModel = new LoveSaveViewModel();
        mBinding.setVariable(BR.viewModel, viewModel);
        viewModel.setBinding(mBinding);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
    }
}
