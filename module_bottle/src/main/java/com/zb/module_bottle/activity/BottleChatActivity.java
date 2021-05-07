package com.zb.module_bottle.activity;

import android.os.Bundle;
import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.lib_base.utils.StatusBarUtil;
import com.zb.lib_base.vm.BaseChatViewModel;
import com.zb.module_bottle.R;


@Route(path = RouteUtils.Bottle_Chat)
public class BottleChatActivity extends BaseActivity {
    @Autowired(name = "driftBottleId")
    long driftBottleId;
    @Autowired(name = "isNotice")
    boolean isNotice;

    private BaseChatViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.BottleBlackTheme);
        super.onCreate(savedInstanceState);
        StatusBarUtil.transparencyBar(this);
    }

    @Override
    public int getRes() {
        return R.layout.base_chat;
    }

    @Override
    public void initUI() {
        fitComprehensiveScreen();
        viewModel = new BaseChatViewModel();
        viewModel.driftBottleId = driftBottleId;
        viewModel.isNotice = isNotice;
        viewModel.msgChannelType = 2;
        viewModel.setBinding(mBinding);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (viewModel != null)
            viewModel.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (viewModel != null)
                viewModel.back(null);
            return true;
        }
        return false;
    }
}
