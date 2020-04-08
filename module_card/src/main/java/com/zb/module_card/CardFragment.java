package com.zb.module_card;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseFragment;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_card.vm.CardViewModel;

@Route(path = RouteUtils.Card_Fragment)
public class CardFragment extends BaseFragment {

    @Override
    public int getRes() {
        return R.layout.card_frag;
    }

    @Override
    public void initUI() {
        CardViewModel viewModel = new CardViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
    }
}
