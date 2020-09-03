package com.zb.module_register.vm;

import android.content.Intent;
import android.view.View;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.api.loginByPassApi;
import com.zb.lib_base.api.myImAccountInfoApi;
import com.zb.lib_base.api.myInfoApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.imcore.LoginSampleHelper;
import com.zb.lib_base.model.ImAccount;
import com.zb.lib_base.model.LoginInfo;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_register.R;
import com.zb.module_register.databinding.RegisterLoginBinding;
import com.zb.module_register.iv.LoginVMInterface;

import androidx.databinding.ViewDataBinding;

public class LoginViewModel extends BaseViewModel implements LoginVMInterface {
    private RegisterLoginBinding loginBinding;
    private LoginSampleHelper loginHelper;

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
        loginBinding.setPass(PreferenceUtil.readStringValue(activity, "loginPass"));
        loginBinding.edPass.setText(PreferenceUtil.readStringValue(activity, "loginPass"));
        if (!loginBinding.getPass().isEmpty()) {
            loginBinding.edPass.setSelection(loginBinding.getPass().length());
            loginBinding.tvNext.setBackgroundResource(R.drawable.btn_bg_white_radius60);
            loginBinding.tvNext.setTextColor(MineApp.getInstance().getResources().getColor(R.color.purple_7a4));
        }
    }

    @Override
    public void complete(View view) {
        if (loginBinding.getPass().length() < 6) {
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
                PreferenceUtil.saveStringValue(activity, "loginPass", loginBinding.getPass());
                BaseActivity.update();
                MineApp.isThreeLogin = false;
                PreferenceUtil.saveIntValue(activity, "myIsThreeLogin", 0);
                myInfo();
            }
        }, activity)
                .setUserName(MineApp.registerInfo.getPhone())
                .setPassWord(loginBinding.getPass());
        api.setPosition(1);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void myInfo() {
        myInfoApi api = new myInfoApi(new HttpOnNextListener<MineInfo>() {
            @Override
            public void onNext(MineInfo o) {
                SCToastUtil.showToast(activity, "登录成功", false);
                mineInfoDb.saveMineInfo(o);
                loginHelper = LoginSampleHelper.getInstance();
                loginHelper.loginOut_Sample();
                myImAccountInfoApi();
            }
        }, activity);
        api.setPosition(1);
        HttpManager.getInstance().doHttpDeal(api);
    }

    /**
     * 阿里百川登录账号
     */
    private void myImAccountInfoApi() {
        myImAccountInfoApi api = new myImAccountInfoApi(new HttpOnNextListener<ImAccount>() {
            @Override
            public void onNext(ImAccount o) {
                loginHelper.loginOut_Sample();
                loginHelper.login_Sample(activity, o.getImUserId(), o.getImPassWord());
                if (MineApp.isLogin) {
                    activity.sendBroadcast(new Intent("lobster_mainSelect"));
                }else {
                    ActivityUtils.getMainActivity();
                }
                activity.finish();
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
