package com.zb.module_chat.vm;

import android.view.View;

import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_chat.iv.ChatFragVMInterface;

public class ChatFragViewModel extends BaseViewModel implements ChatFragVMInterface {
    @Override
    public void entryBottle(View view) {
        ActivityUtils.getBottleThrow();
    }
}
