package com.zb.module_mine.activity;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.vm.EditMemberViewModel;

@Route(path = RouteUtils.Mine_Edit_Member)
public class EditMemberActivity extends MineBaseActivity {
    private EditMemberViewModel viewModel;
    private BaseReceiver memberReceiver;
    private BaseReceiver cameraReceiver;

    @Override
    public int getRes() {
        return R.layout.mine_edit_member;
    }

    @Override
    public void initUI() {
        viewModel = new EditMemberViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.title, "个人资料设置");

        memberReceiver = new BaseReceiver(activity, "lobster_member") {
            @Override
            public void onReceive(Context context, Intent intent) {
                int type = intent.getIntExtra("type", 0);
                String content = intent.getStringExtra("content");
                viewModel.mineInfoDb.updateNick(content, type);
                viewModel.mineInfo = viewModel.mineInfoDb.getMineInfo();
                mBinding.setVariable(BR.viewModel, viewModel);
            }
        };
        cameraReceiver = new BaseReceiver(activity, "lobster_camera") {
            @Override
            public void onReceive(Context context, Intent intent) {
                String fileName = intent.getStringExtra("filePath");
                // 拍照
                if (viewModel.imageList.get(viewModel._position).isEmpty()) {
                    for (int i = 0; i < viewModel.imageList.size(); i++) {
                        if (viewModel.imageList.get(i).isEmpty()) {
                            viewModel._position = i;
                            viewModel.imageList.set(viewModel._position, fileName);
                            viewModel.adapter.notifyItemChanged(viewModel._position);
                            return;
                        }
                    }
                } else {
                    viewModel.imageList.set(viewModel._position, fileName);
                    viewModel.adapter.notifyItemChanged(viewModel._position);
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == 1) {
            String fileName = data.getStringExtra("fileName");
            if (viewModel.imageList.get(viewModel._position).isEmpty()) {
                for (int i = 0; i < viewModel.imageList.size(); i++) {
                    if (viewModel.imageList.get(i).isEmpty()) {
                        viewModel._position = i;
                        viewModel.imageList.set(viewModel._position, fileName);
                        viewModel.adapter.notifyItemChanged(viewModel._position);
                        return;
                    }
                }
            } else {
                viewModel.imageList.set(viewModel._position, fileName);
                viewModel.adapter.notifyItemChanged(viewModel._position);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        memberReceiver.unregisterReceiver();
        cameraReceiver.unregisterReceiver();
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
