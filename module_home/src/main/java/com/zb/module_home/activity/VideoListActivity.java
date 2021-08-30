package com.zb.module_home.activity;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.lib_base.utils.StatusBarUtil;
import com.zb.module_home.BR;
import com.zb.module_home.R;
import com.zb.module_home.vm.VideoListViewModel;


@Route(path = RouteUtils.Home_Video_List)
public class VideoListActivity extends BaseActivity {
    @Autowired(name = "position")
    int position;
    @Autowired(name = "pageNo")
    int pageNo;

    private boolean isFirst = true;
    private VideoListViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.HomeVideoTheme);
        super.onCreate(savedInstanceState);
        StatusBarUtil.setStatusBarColor(activity, R.color.black);
    }

    @Override
    public int getRes() {
        return R.layout.home_video_list;
    }

    @Override
    public void initUI() {
        viewModel = new VideoListViewModel();
        viewModel.position = position;
        viewModel.pageNo = pageNo;
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
        mBinding = null;
        viewModel = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isFirst) {
            viewModel.onResume();
        }
        isFirst = false;
    }
}
