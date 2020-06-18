package com.zb.module_mine.activity;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.databinding.MineLocationBinding;
import com.zb.module_mine.vm.LocationViewModel;

@Route(path = RouteUtils.Mine_Location)
public class LocationActivity extends MineBaseActivity {

    @Autowired(name = "isDiscover")
    boolean isDiscover;

    private Bundle savedInstanceState;
    private MineLocationBinding locationBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
    }

    @Override
    public int getRes() {
        return R.layout.mine_location;
    }

    @Override
    public void initUI() {
        LocationViewModel viewModel = new LocationViewModel();
        viewModel.isDiscover = isDiscover;
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.title, "修改定位");

        locationBinding = (MineLocationBinding) mBinding;
        locationBinding.mapView.onCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        locationBinding.mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationBinding.mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationBinding.mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationBinding.mapView.onDestroy();
    }
}
