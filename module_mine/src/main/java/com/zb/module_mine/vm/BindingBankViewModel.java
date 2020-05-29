package com.zb.module_mine.vm;

import android.view.View;

import com.zb.lib_base.api.bindBankCardApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.BankInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.R;
import com.zb.module_mine.databinding.MineBindingBankBinding;
import com.zb.module_mine.iv.BindingBankVMInterface;
import com.zb.module_mine.windows.BankPW;

import androidx.databinding.ViewDataBinding;

public class BindingBankViewModel extends BaseViewModel implements BindingBankVMInterface {
    private MineBindingBankBinding mBinding;
    private BankInfo bankInfo;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (MineBindingBankBinding) binding;
        mBinding.setShowAddress(true);
        mBinding.tvBankName.setText("请选择开户行");
        mBinding.tvBank.setText("开户行");
        mBinding.tvBank.setTestSize(16);
        mBinding.tvName.setText("户名");
        mBinding.tvName.setTestSize(16);
        mBinding.tvBankTitle.setText("银行卡");
        mBinding.tvBankTitle.setTestSize(16);
        mBinding.tvBankAddress.setText("开户网点");
        mBinding.tvBankAddress.setTestSize(16);
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void selectBank(View view) {
        new BankPW(activity, mBinding.getRoot(), MineApp.bankInfoList, bankInfo1 -> {
            bankInfo = bankInfo1;
            mBinding.tvBankName.setText(bankInfo.getBankName());
            mBinding.tvBankName.setTextColor(activity.getResources().getColor(R.color.black_252));
            mBinding.tvBankTitle.setText(bankInfo.getBankType() == 1 ? "银行卡" : bankInfo.getBankName());
            mBinding.edBankAccount.setHint(bankInfo.getBankType() == 1 ? "请输入银行卡卡号" : "请输入" + bankInfo.getBankName() + "账号");
            mBinding.setShowAddress(bankInfo.getBankType() == 1);
        });
    }

    @Override
    public void submit(View view) {
        if (bankInfo == null) {
            SCToastUtil.showToastBlack(activity, "请选择开户行");
            return;
        }
//        if (mBinding.getName().isEmpty()) {
//            SCToastUtil.showToastBlack(activity, "请输入户名");
//            return;
//        }
        if (mBinding.getBankAccount().isEmpty()) {
            SCToastUtil.showToastBlack(activity, "请输入" + bankInfo.getBankName() + "账号");
            return;
        }
        if (bankInfo.getBankType() == 1) {
            if (mBinding.getBankAddress().isEmpty()) {
                SCToastUtil.showToastBlack(activity, "请输入开户网点");
                return;
            }
        }
        bindBankCard();
    }

    @Override
    public void bankList(View view) {
        ActivityUtils.getMineBankList();
    }

    @Override
    public void bindBankCard() {
        bindBankCardApi api = new bindBankCardApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                SCToastUtil.showToastBlack(activity, "添加成功");
                activity.finish();
            }
        }, activity).setBankId(bankInfo.getId()).setAccountNo(mBinding.getBankAccount()).setOpenAccountLocation(mBinding.getBankAddress());
        HttpManager.getInstance().doHttpDeal(api);
    }
}
