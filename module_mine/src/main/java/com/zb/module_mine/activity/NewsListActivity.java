package com.zb.module_mine.activity;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.vm.NewsListViewModel;

@Route(path = RouteUtils.Mine_News_list)
public class NewsListActivity extends MineBaseActivity {
    @Autowired(name = "reviewType")
    int reviewType;

    private NewsListViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.mine_news_list;
    }

    @Override
    public void initUI() {
        viewModel = new NewsListViewModel();
        viewModel.reviewType = reviewType;
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.noData, true);
        mBinding.setVariable(BR.reviewType, reviewType);
        mBinding.setVariable(BR.title, reviewType == 1 ? "我的评论" : (reviewType == 2 ? "我的点赞" : "我的礼物"));
    }
}
