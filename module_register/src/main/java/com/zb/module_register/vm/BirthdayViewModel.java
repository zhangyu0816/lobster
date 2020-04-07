package com.zb.module_register.vm;

import android.view.View;

import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_register.databinding.RegisterBirthdayBinding;
import com.zb.module_register.iv.BirthdayVMInterface;
import com.zb.module_register.windows.BirthdayPW;

public class BirthdayViewModel extends BaseViewModel implements BirthdayVMInterface {
    @Override
    public void back(View view) {
        super.back(view);
        ActivityUtils.getRegisterNick();
        activity.finish();
    }

    @Override
    public void selectBirthday(View view) {
        new BirthdayPW(activity, ((RegisterBirthdayBinding) mBinding).tvNext,birthday->{
            MineApp.registerInfo.setBirthday(birthday);
            ((RegisterBirthdayBinding) mBinding).setBirthday(birthday);
        });
    }

    @Override
    public void next(View view) {
        if(MineApp.registerInfo.getBirthday().isEmpty()){
            SCToastUtil.showToast(activity,"请选择生日日期");
            return;
        }
        ActivityUtils.getRegisterPhone();
        activity.finish();
    }
}
