package com.zb.module_mine.vm;

import android.content.Intent;
import android.text.InputType;
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
        mBinding.edBankAccount.setInputType(InputType.TYPE_CLASS_NUMBER);
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
            if (bankInfo.getBankType() == 1) {
                mBinding.edBankAccount.setInputType(InputType.TYPE_CLASS_NUMBER);
            } else {
                mBinding.edBankAccount.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
            }
            mBinding.setShowAddress(bankInfo.getBankType() == 1);
        });
    }

    @Override
    public void submit(View view) {
        if (bankInfo == null) {
            SCToastUtil.showToast(activity, "请选择开户行", true);
            return;
        }
//        if (mBinding.getName().isEmpty()) {
//            SCToastUtil.showToast(activity, "请输入户名", true);
//            return;
//        }
        if (mBinding.getBankAccount().isEmpty()) {
            SCToastUtil.showToast(activity, "请输入" + bankInfo.getBankName() + "账号", true);
            return;
        }
        if (bankInfo.getBankType() == 1) {
            if (!checkBankCard(mBinding.getBankAccount())) {
                SCToastUtil.showToast(activity, "银行卡账号错误", true);
                return;
            }
            // 银行卡
            if (mBinding.getBankAddress().isEmpty()) {
                SCToastUtil.showToast(activity, "请输入开户网点", true);
                return;
            }
        } else {
            // 支付宝
            if (!mBinding.getBankAccount().matches(MineApp.PHONE_NUMBER_REG) && !mBinding.getBankAccount().matches(MineApp.EMAIL_REG)) {
                SCToastUtil.showToast(activity, bankInfo.getBankName() + "账号错误", true);
                return;
            }
        }
        bindBankCard();
    }


    @Override
    public void bankList(View view) {
        ActivityUtils.getMineBankList(false);
    }

    @Override
    public void bindBankCard() {
        bindBankCardApi api = new bindBankCardApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                SCToastUtil.showToast(activity, "添加成功", true);
                activity.sendBroadcast(new Intent("lobster_addBank"));
                activity.finish();
            }
        }, activity).setBankId(bankInfo.getId()).setAccountNo(mBinding.getBankAccount()).setOpenAccountLocation(mBinding.getBankAddress());
        HttpManager.getInstance().doHttpDeal(api);
    }

    //验证银行卡号
    private boolean checkBankCard(String cardId) {
        char bit = getBankCardCheckCode(cardId.substring(0, cardId.length() - 1));
        if (bit == 'N') {
            return false;
        }
        return cardId.charAt(cardId.length() - 1) == bit;
    }

    //从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
    private char getBankCardCheckCode(String nonCheckCodeCardId) {
        if (nonCheckCodeCardId == null || nonCheckCodeCardId.trim().length() == 0
                || !nonCheckCodeCardId.matches("\\d+")) {
            //如果传的不是数据返回N
            return 'N';
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
    }

}
