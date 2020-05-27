package com.zb.module_mine.activity;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.vm.GiftRecordViewModel;

@Route(path = RouteUtils.Mine_Gift_Record)
public class GiftRecordActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.MineTheme);
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getRes() {
        return R.layout.mine_gift_record;
    }

    @Override
    public void initUI() {
        fitComprehensiveScreen();
        GiftRecordViewModel viewModel = new GiftRecordViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.title, "礼物收益");
        mBinding.setVariable(BR.right, "帮助");
    }
}
