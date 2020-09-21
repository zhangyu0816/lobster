package com.zb.module_mine.activity;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.vm.GRGiftViewModel;

@Route(path = RouteUtils.Mine_GRGift)
public class GRGiftActivity extends MineBaseActivity {
    @Autowired(name = "friendDynGiftType")
    int friendDynGiftType;

    @Override
    public int getRes() {
        return R.layout.mine_give_receive_gift;
    }

    @Override
    public void initUI() {
        GRGiftViewModel viewModel = new GRGiftViewModel();
        viewModel.friendDynGiftType = friendDynGiftType;
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.title, friendDynGiftType == 1 ? "送礼记录" : "收礼记录");
        mBinding.setVariable(BR.noData, true);
        mBinding.setVariable(BR.remark, friendDynGiftType == 1 ? "暂无送礼记录" : "暂无收礼记录");
    }
}
