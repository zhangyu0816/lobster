package com.yimi.rentme.activity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yimi.rentme.BR;
import com.yimi.rentme.R;
import com.yimi.rentme.vm.BindingPhoneViewModel;
import com.zb.lib_base.utils.RouteUtils;

@Route(path = RouteUtils.Main_Binding_Phone)
public class BindingPhoneActivity extends AppBaseActivity {

    @Override
    public int getRes() {
        return R.layout.ac_binding_phone;
    }

    @Override
    public void initUI() {
        BindingPhoneViewModel viewModel = new BindingPhoneViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.title, "绑定手机号");
    }
}
