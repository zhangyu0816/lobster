package com.zb.module_mine.activity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.vm.NewsManagerViewModel;

@Route(path = RouteUtils.Mine_News_Manager)
public class NewsManagerActivity extends MineBaseActivity {
    private NewsManagerViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.mine_news_manager;
    }

    @Override
    public void initUI() {

        viewModel = new NewsManagerViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.title, "我的消息");
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.newDynMsgAllNum();
    }
}
