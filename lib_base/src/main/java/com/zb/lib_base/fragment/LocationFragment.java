package com.zb.lib_base.fragment;

import android.os.Bundle;

import com.zb.lib_base.BR;
import com.zb.lib_base.R;
import com.zb.lib_base.activity.BaseFragment;
import com.zb.lib_base.databinding.FragLocationBinding;
import com.zb.lib_base.vm.LocationViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LocationFragment extends BaseFragment {

    private Bundle savedInstanceState;
    private FragLocationBinding locationBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
    }

    @Override
    public int getRes() {
        return R.layout.frag_location;
    }

    @Override
    public void initUI() {
        Bundle bundle = getArguments();
        LocationViewModel viewModel = new LocationViewModel();
        assert bundle != null;
        viewModel.isDiscover = bundle.getBoolean("isDiscover");
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.title, "修改定位");

        locationBinding = (FragLocationBinding) mBinding;
        locationBinding.mapView.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        locationBinding.mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        locationBinding.mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        locationBinding.mapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationBinding.mapView.onDestroy();
    }
}
