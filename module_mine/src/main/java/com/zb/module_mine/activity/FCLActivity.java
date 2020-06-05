package com.zb.module_mine.activity;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.vm.FCLViewModel;

@Route(path = RouteUtils.Mine_FCL)
public class FCLActivity extends MineBaseActivity {
    @Autowired(name = "position")
    int position;

    private FCLViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.mine_fcl;
    }

    @Override
    public void initUI() {
        viewModel = new FCLViewModel();
        viewModel.position = position;
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.position, position);
        mBinding.setVariable(BR.title, position == 0 ? "我的关注" : (position == 1 ? "我的粉丝" : "被喜欢"));
        mBinding.setVariable(BR.noData, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
    }
}
