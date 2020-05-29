package com.zb.module_mine.vm;

import android.view.View;

import com.zb.lib_base.api.walletAndPopApi;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.WalletInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.BR;
import com.zb.module_mine.iv.GiftRecordVMInterface;

import androidx.databinding.ViewDataBinding;

public class GiftRecordViewModel extends BaseViewModel implements GiftRecordVMInterface {
    public WalletInfo walletInfo;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        walletAndPop();
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
        ActivityUtils.getMineTranRecord(31);
    }

    @Override
    public void toBindingZFB(View view) {
        ActivityUtils.getMineBindingBank();
    }

    @Override
    public void incomeDeposit(View view) {
//        new TextPW(activity, mBinding.getRoot(), "收益押金说明", "说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明");
    }

    @Override
    public void walletAndPop() {
        walletAndPopApi api = new walletAndPopApi(new HttpOnNextListener<WalletInfo>() {
            @Override
            public void onNext(WalletInfo o) {
                walletInfo = o;
                mBinding.setVariable(BR.viewModel, GiftRecordViewModel.this);
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
