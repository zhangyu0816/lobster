package com.zb.module_mine.vm;

import android.view.View;

import com.zb.lib_base.model.WalletInfo;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.TextPW;
import com.zb.module_mine.iv.GiftRecordVMInterface;

import androidx.databinding.ViewDataBinding;

public class GiftRecordViewModel extends BaseViewModel implements GiftRecordVMInterface {
    public WalletInfo walletInfo;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        walletInfo = new WalletInfo();
        walletInfo.setCanWithdrawCreditWallet(100f);
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void right(View view) {
        super.right(view);
    }

    @Override
    public void withdraw(View view) {

    }

    @Override
    public void toProfitRecord(View view) {

    }

    @Override
    public void toBindingZFB(View view) {

    }

    @Override
    public void taxIncome(View view) {
        new TextPW(activity, mBinding.getRoot(), "今日税前收益说明", "说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明");
    }

    @Override
    public void incomeDeposit(View view) {
        new TextPW(activity, mBinding.getRoot(), "收益押金说明", "说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明");
    }
}
