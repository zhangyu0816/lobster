package com.zb.module_bottle.activity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_bottle.BR;
import com.zb.module_bottle.R;
import com.zb.module_bottle.vm.BottleThrowViewModel;

@Route(path = RouteUtils.Bottle_Throw)
public class BottleThrowActivity extends BottleBaseActivity {

    private BottleThrowViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.bottle_throw;
    }

    @Override
    public void initUI() {
        fitScreen();
        viewModel = new BottleThrowViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.title, "我的漂流瓶");
        mBinding.setVariable(BR.noReadNum, MineApp.noReadBottleNum);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewModel.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        viewModel.onResume();
    }
}
