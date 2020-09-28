package com.zb.module_bottle.activity;

import android.os.Bundle;
import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.lib_base.utils.SoftHideKeyBoardUtil;
import com.zb.lib_base.utils.StatusBarUtil;
import com.zb.module_bottle.BR;
import com.zb.module_bottle.R;
import com.zb.module_bottle.vm.BottleChatViewModel;


@Route(path = RouteUtils.Bottle_Chat)
public class BottleChatActivity extends BaseActivity {
    @Autowired(name = "driftBottleId")
    long driftBottleId;
    @Autowired(name = "isNotice")
    boolean isNotice;

    private BottleChatViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.BottleBlackTheme);
        super.onCreate(savedInstanceState);
        StatusBarUtil.statusBarLightMode(this);
    }

    @Override
    public int getRes() {
        return R.layout.bottle_chat;
    }

    @Override
    public void initUI() {
        fitComprehensiveScreen();
        viewModel = new BottleChatViewModel();
        viewModel.driftBottleId = driftBottleId;
        viewModel.isNotice = isNotice;
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.isEmoji, false);
        SoftHideKeyBoardUtil.assistActivity(activity, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            viewModel.back(null);
            return true;
        }
        return false;
    }
}
