package com.zb.module_card.activity;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.lib_base.utils.StatusBarUtil;
import com.zb.module_card.BR;
import com.zb.module_card.R;
import com.zb.module_card.vm.MemberDetailViewModel;

@Route(path = RouteUtils.Card_Member_Detail)
public class MemberDetailActivity extends BaseActivity {

    @Autowired(name = "userId")
    long userId;
    @Autowired(name = "showLike")
    boolean showLike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.CardTheme);
        super.onCreate(savedInstanceState);
        StatusBarUtil.statusBarLightModeNotFull(this);
    }

    @Override
    public int getRes() {
        return R.layout.card_member_detail;
    }

    @Override
    public void initUI() {
        MemberDetailViewModel viewModel = new MemberDetailViewModel();
        viewModel.otherUserId = userId;
        viewModel.showLike = showLike;
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.showLike, showLike);
        mBinding.setVariable(BR.viewModel, viewModel);
    }
}
