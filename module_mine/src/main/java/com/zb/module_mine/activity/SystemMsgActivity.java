package com.zb.module_mine.activity;

import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.vm.SystemMsgViewModel;

@Route(path = RouteUtils.Mine_System_Msg)
public class SystemMsgActivity extends MineBaseActivity {
    private SystemMsgViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.mine_system_msg;
    }

    @Override
    public void initUI() {
        viewModel = new SystemMsgViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.title, "系统消息");
        mBinding.setVariable(BR.noData, false);
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
