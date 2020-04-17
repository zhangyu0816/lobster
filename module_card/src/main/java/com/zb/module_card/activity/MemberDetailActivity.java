package com.zb.module_card.activity;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_card.BR;
import com.zb.module_card.R;
import com.zb.module_card.vm.MemberDetailViewModel;

@Route(path = RouteUtils.Card_Member_Detail)
public class MemberDetailActivity extends CardBaseActivity {

    @Autowired(name = "userId")
    long userId;

    @Override
    public int getRes() {
        return R.layout.card_member_detail;
    }

    @Override
    public void initUI() {
        fitComprehensiveScreen();
        MemberDetailViewModel viewModel = new MemberDetailViewModel();
        viewModel.userId = userId;
        mBinding.setVariable(BR.viewModel, viewModel);
        viewModel.setBinding(mBinding);
    }
}
