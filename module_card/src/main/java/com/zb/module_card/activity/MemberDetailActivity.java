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
    @Autowired(name = "showLike")
    boolean showLike;
    private MemberDetailViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.card_member_detail;
    }

    @Override
    public void initUI() {
        viewModel = new MemberDetailViewModel();
        viewModel.otherUserId = userId;
        viewModel.showLike = showLike;
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
    }
}
