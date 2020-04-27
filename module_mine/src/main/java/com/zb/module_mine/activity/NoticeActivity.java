package com.zb.module_mine.activity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.vm.NoticeViewModel;

@Route(path = RouteUtils.Mine_Notice)
public class NoticeActivity extends MineBaseActivity {
    @Override
    public int getRes() {
        return R.layout.mine_notice;
    }

    @Override
    public void initUI() {
        NoticeViewModel viewModel = new NoticeViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
    }
}
