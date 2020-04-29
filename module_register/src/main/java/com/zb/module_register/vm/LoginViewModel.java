package com.zb.module_register.vm;

import android.view.View;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.api.loginByPassApi;
import com.zb.lib_base.api.myInfoApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.LoginInfo;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_register.databinding.RegisterLoginBinding;
import com.zb.module_register.iv.LoginVMInterface;

import androidx.databinding.ViewDataBinding;

public class LoginViewModel extends BaseViewModel implements LoginVMInterface {
    private RegisterLoginBinding loginBinding;

    @Override
    public void back(View view) {
        super.back(view);
        ActivityUtils.getRegisterPhone(true);
        activity.finish();
    }

    @Override
    public void right(View view) {
        super.right(view);
        ActivityUtils.getRegisterCode(true);
        activity.finish();
    }

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        loginBinding = (RegisterLoginBinding) binding;
    }

    @Override
    public void complete(View view) {
        if (loginBinding.getPass().length() < 6) {
            SCToastUtil.showToast(activity, "请输入不少于6位的密码");
            return;
        }
        loginByPass();
    }

    @Override
    public void loginByPass() {
        loginByPassApi api = new loginByPassApi(new HttpOnNextListener<LoginInfo>() {
            @Override
            public void onNext(LoginInfo o) {
                PreferenceUtil.saveLongValue(activity, "userId", o.getId());
                PreferenceUtil.saveStringValue(activity, "sessionId", o.getSessionId());
                PreferenceUtil.saveStringValue(activity, "userName", o.getUserName());
                BaseActivity.update();
                myInfo();
            }
        }, activity)
                .setUserName(MineApp.registerInfo.getPhone())
                .setPassWord(loginBinding.getPass());
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void myInfo() {
        myInfoApi api = new myInfoApi(new HttpOnNextListener<MineInfo>() {
            @Override
            public void onNext(MineInfo o) {
                SCToastUtil.showToast(activity, "登录成功");
                mineInfoDb.saveMineInfo(o);
                ActivityUtils.getMainActivity();
                activity.finish();
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
