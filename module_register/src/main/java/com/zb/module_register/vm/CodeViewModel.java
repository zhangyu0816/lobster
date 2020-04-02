package com.zb.module_register.vm;

import android.os.CountDownTimer;
import android.view.View;

import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_register.R;
import com.zb.module_register.databinding.RegisterCodeBinding;
import com.zb.module_register.iv.CodeVMInterface;

import androidx.databinding.ViewDataBinding;


public class CodeViewModel extends BaseViewModel implements CodeVMInterface {

    private int second = 10;
    private RegisterCodeBinding bindings;
    private CountDownTimer timer;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        bindings = (RegisterCodeBinding) binding;
        bindings.setRemark(MineApp.getInstance().getResources().getString(R.string.code_second, second));
        timer = new CountDownTimer(second*1000, 1000) {
            public void onTick(long millisUntilFinished) {
                bindings.setRemark(MineApp.getInstance().getResources().getString(R.string.code_second, millisUntilFinished / 1000));
            }

            public void onFinish() {
                bindings.setRemark("验证码没收到？重新试试！");
                timer.cancel();
            }
        };
        timer.start();
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void reset(View view) {
        timer.start();
    }

    @Override
    public void sure(View view) {
        if(bindings.edCode.getText().toString().length()<4){
            SCToastUtil.showToast(activity,"请输入4位有效验证码");
            return;
        }
        SCToastUtil.showToast(activity,"验证成功");
        ActivityUtils.getRegisterLogo();
    }
}
