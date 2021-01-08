package com.zb.module_mine.activity;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.model.Authentication;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.vm.AuthenticationViewModel;

@Route(path = RouteUtils.Mine_Authentication)
public class AuthenticationActivity extends MineBaseActivity {
    @Autowired(name = "authentication")
    Authentication authentication;

    private AuthenticationViewModel viewModel;
    private BaseReceiver cameraReceiver;

    @Override
    public int getRes() {
        return R.layout.mine_authentication;
    }

    @Override
    public void initUI() {
        viewModel = new AuthenticationViewModel();
        viewModel.authentication = authentication;
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.title, "实名认证");
        viewModel.setBinding(mBinding);

        cameraReceiver = new BaseReceiver(activity, "lobster_camera") {
            @Override
            public void onReceive(Context context, Intent intent) {
                String path = intent.getStringExtra("filePath");
                viewModel.imageList.set(viewModel._position, path);
                viewModel.adapter.notifyItemChanged(viewModel._position);
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == 1) {
            String fileName = data.getStringExtra("fileName");
            viewModel.imageList.set(viewModel._position, fileName);
            viewModel.adapter.notifyItemChanged(viewModel._position);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            cameraReceiver.unregisterReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
