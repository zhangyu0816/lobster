package com.zb.module_register.vm;

import android.view.View;

import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_register.databinding.RegisterNickBinding;
import com.zb.module_register.iv.NickVMInterface;

public class NickViewModel extends BaseViewModel implements NickVMInterface {

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void next(View view) {
        String nick = ((RegisterNickBinding) mBinding).getNick();
        if(nick.isEmpty()){
            SCToastUtil.showToast(activity,"请填写昵称");
            return;
        }
        MineApp.registerInfo.setName(nick);
        ActivityUtils.getRegisterPhone();
    }
}
