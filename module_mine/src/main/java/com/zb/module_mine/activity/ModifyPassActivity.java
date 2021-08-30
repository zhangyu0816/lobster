package com.zb.module_mine.activity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.vm.ModifyPassViewModel;

@Route(path = RouteUtils.Mine_Modify_Pass)
public class ModifyPassActivity extends MineBaseActivity {

    private ModifyPassViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.mine_modify_pass;
    }

    @Override
    public void initUI() {
        viewModel = new ModifyPassViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.title, "修改密码");
        mBinding.setVariable(BR.oldPass, "");
        mBinding.setVariable(BR.newPass, "");
        mBinding.setVariable(BR.surePass, "");
        mBinding.setVariable(BR.showNewPass, false);
        mBinding.setVariable(BR.showSurePass, false);
        mBinding.setVariable(BR.type, 1);
        mBinding.setVariable(BR.phone, PreferenceUtil.readStringValue(activity, "userName"));
        mBinding.setVariable(BR.code, "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinding = null;
        viewModel = null;
    }
}
