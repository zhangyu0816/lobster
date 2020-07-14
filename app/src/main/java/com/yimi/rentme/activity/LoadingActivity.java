package com.yimi.rentme.activity;

import com.igexin.sdk.PushManager;
import com.yimi.rentme.BR;
import com.yimi.rentme.R;
import com.yimi.rentme.getui.DemoIntentService;
import com.yimi.rentme.getui.DemoPushService;
import com.yimi.rentme.vm.LoadingViewModel;

public class LoadingActivity extends AppBaseActivity {
    @Override
    public int getRes() {
        return R.layout.ac_loading;
    }

    @Override
    public void initUI() {
        fitComprehensiveScreen();
        // 个推注册
        PushManager.getInstance().initialize(this.getApplicationContext(), DemoPushService.class);
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), DemoIntentService.class);
        LoadingViewModel viewModel = new LoadingViewModel();
        mBinding.setVariable(BR.viewModel, viewModel);
        viewModel.setBinding(mBinding);
    }
}
