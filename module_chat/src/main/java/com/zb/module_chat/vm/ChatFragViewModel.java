package com.zb.module_chat.vm;

import android.view.View;

import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_chat.iv.ChatFragVMInterface;

import androidx.databinding.ViewDataBinding;

public class ChatFragViewModel extends BaseViewModel implements ChatFragVMInterface {
    public MineInfo mineInfo;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mineInfo = mineInfoDb.getMineInfo();
    }

    @Override
    public void entryBottle(View view) {
        ActivityUtils.getBottleThrow();
    }
}
