package com.zb.module_mine.activity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.R;

@Route(path = RouteUtils.Mine_Wallet)
public class WalletActivity extends MineBaseActivity {
    @Override
    public int getRes() {
        return R.layout.mine_wallet;
    }

    @Override
    public void initUI() {

    }
}
