package com.zb.module_mine.vm;

import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;

import com.zb.lib_base.api.modifyPassApi;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.databinding.MineModifyPassBinding;
import com.zb.module_mine.iv.ModifyPassVMInterface;

import androidx.databinding.ViewDataBinding;

public class ModifyPassViewModel extends BaseViewModel implements ModifyPassVMInterface {
    private MineModifyPassBinding passBinding;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        passBinding = (MineModifyPassBinding) binding;
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void select(int index) {
        if (index == 1) {
            passBinding.setShowNewPass(!passBinding.getShowNewPass());
            passBinding.edNew.setTransformationMethod(passBinding.getShowNewPass() ? HideReturnsTransformationMethod.getInstance() : PasswordTransformationMethod.getInstance());
        } else {
            passBinding.setShowSurePass(!passBinding.getShowSurePass());
            passBinding.edSure.setTransformationMethod(passBinding.getShowSurePass() ? HideReturnsTransformationMethod.getInstance() : PasswordTransformationMethod.getInstance());
        }
    }

    @Override
    public void modify(View view) {
        if (passBinding.getOldPass().isEmpty()) {
            SCToastUtil.showToastBlack(activity, "请输入旧密码");
            return;
        }
        if (passBinding.getNewPass().isEmpty()) {
            SCToastUtil.showToastBlack(activity, "请输入新密码");
            return;
        }
        if (!TextUtils.equals(passBinding.getNewPass(), passBinding.getSurePass())) {
            SCToastUtil.showToastBlack(activity, "确认密码输入错误");
            return;
        }
        modifyPassApi api = new modifyPassApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                SCToastUtil.showToastBlack(activity, "修改成功");
                activity.finish();
            }
        }, activity).setOldPassWord(passBinding.getOldPass()).setNewPassWord(passBinding.getNewPass());
        HttpManager.getInstance().doHttpDeal(api);
    }
}
