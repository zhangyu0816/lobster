package com.zb.module_mine.activity;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.vm.BindingPhoneViewModel;

@Route(path = RouteUtils.Mine_Binding_Phone)
public class BindingPhoneActivity extends MineBaseActivity {
    @Autowired(name = "isRegister")
    boolean isRegister;
    @Autowired(name = "isFinish")
    boolean isFinish;

    private BindingPhoneViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.ac_binding_phone;
    }

    @Override
    public void initUI() {
        viewModel = new BindingPhoneViewModel();
        viewModel.isRegister = isRegister;
        viewModel.isFinish = isFinish;
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.title, "绑定手机号");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinding = null;
        viewModel = null;
    }
}
