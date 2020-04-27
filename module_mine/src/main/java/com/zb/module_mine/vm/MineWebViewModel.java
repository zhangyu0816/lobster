package com.zb.module_mine.vm;

import android.view.View;

import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.iv.MineWebVMInterface;

public class MineWebViewModel extends BaseViewModel implements MineWebVMInterface {
    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }
}
