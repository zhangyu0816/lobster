package com.zb.module_mine.activity;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.fragment.LocationFragment;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.R;

@Route(path = RouteUtils.Mine_Location)
public class LocationActivity extends MineBaseActivity {

    @Autowired(name = "isDiscover")
    boolean isDiscover;

    private LocationFragment fragment;

    @Override
    public int getRes() {
        return R.layout.mine_location;
    }

    @Override
    public void initUI() {
        fragment = new LocationFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isDiscover", isDiscover);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.location_frag, fragment).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fragment = null;
        mBinding = null;
    }
}
