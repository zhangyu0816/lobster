package com.zb.module_card.fragment;


import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseFragment;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_card.BR;
import com.zb.module_card.R;
import com.zb.module_card.vm.CardViewModel;

@Route(path = RouteUtils.Card_Fragment)
public class CardFragment extends BaseFragment {

    private CardViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.card_frag;
    }

    @Override
    public void initUI() {
        viewModel = new CardViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.cityName, MineApp.cityName);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (viewModel != null)
            viewModel.onDestroy();
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.i("Fragment", "CardFragment_onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("Fragment", "CardFragment_onPause");
    }
}
