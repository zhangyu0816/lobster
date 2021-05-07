package com.zb.module_mine.activity;

import android.view.KeyEvent;

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
    @Autowired(name = "otherUserId")
    long otherUserId;

    private FCLViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.mine_fcl;
    }

    @Override
    public void initUI() {
        viewModel = new FCLViewModel();
        viewModel.position = position;
        viewModel.otherUserId = otherUserId;
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.otherUserId, otherUserId);
        mBinding.setVariable(BR.position, position);
        mBinding.setVariable(BR.title, position == 0 ? (otherUserId == 0 ? "我的关注" : "Ta的关注") : (position == 1 ? (otherUserId == 0 ? "我的粉丝" : "TA的粉丝") : (position == 2 ? "谁喜欢我" : "谁看过我")));
        mBinding.setVariable(BR.noData, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            viewModel.back(null);
            return true;
        }
        return false;
    }
}
