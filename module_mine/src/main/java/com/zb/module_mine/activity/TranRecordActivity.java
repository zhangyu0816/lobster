package com.zb.module_mine.activity;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.vm.TranRecordViewModel;

@Route(path = RouteUtils.Mine_Tran_Record)
public class TranRecordActivity extends MineBaseActivity {
    @Autowired(name = "tranType")
    int tranType;

    @Override
    public int getRes() {
        return R.layout.mine_tran_record;
    }

    @Override
    public void initUI() {
        TranRecordViewModel viewModel = new TranRecordViewModel();
        viewModel.tranType = tranType;
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.title, tranType == 0 ? "账单明细" : "收益明细");
    }

}
