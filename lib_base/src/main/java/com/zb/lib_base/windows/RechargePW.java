package com.zb.lib_base.windows;

import android.view.View;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.BR;
import com.zb.lib_base.R;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.adapter.BaseAdapter;
import com.zb.lib_base.api.rechargeWalletApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.databinding.PwsHomeVipRechargeBinding;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.OrderTran;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.utils.SCToastUtil;

public class RechargePW extends BasePopupWindow {
    private BaseAdapter adapter;
    private int preIndex = -1;
    private PwsHomeVipRechargeBinding binding;

    public RechargePW(RxAppCompatActivity activity, View parentView) {
        super(activity, parentView, true);
        initUI();
    }

    @Override
    public int getRes() {
        return R.layout.pws_home_vip_recharge;
    }

    @Override
    public void initUI() {
        binding = (PwsHomeVipRechargeBinding) mBinding;
        adapter = new BaseAdapter<>(activity, R.layout.item_home_vip_info, MineApp.rechargeInfoList, this);

        mBinding.setVariable(BR.pw, this);
        mBinding.setVariable(BR.walletInfo, MineApp.walletInfo);
        mBinding.setVariable(BR.adapter, adapter);
        binding.walletList.postDelayed(() -> {
            if (MineApp.rechargeInfoList.size() < 4) {
                AdapterBinding.viewSize(binding.walletList, ObjectUtils.getViewSizeByWidth(1.0f), ObjectUtils.getViewSizeByWidth(0.25f));
            }
        },200);
    }

    @Override
    public void cancel(View view) {
        super.cancel(view);
        dismiss();
    }

    @Override
    public void selectIndex(int position) {
        super.selectIndex(position);
        if (preIndex != position) {
            adapter.setSelectIndex(position);
            if (preIndex != -1) {
                adapter.notifyItemChanged(preIndex);
            }
            adapter.notifyItemChanged(position);
            preIndex = position;
        }
    }

    @Override
    public void showRule(View view) {
        super.showRule(view);
        dismiss();
    }

    @Override
    public void recharge(View view) {
        super.recharge(view);
        if (preIndex == -1) {
            SCToastUtil.showToastBlack(activity, "请选择充值套餐");
            return;
        }
        rechargeWallet();
    }

    private void rechargeWallet() {
        rechargeWalletApi api = new rechargeWalletApi(new HttpOnNextListener<OrderTran>() {
            @Override
            public void onNext(OrderTran o) {
                dismiss();
                new PaymentPW(activity, mBinding.getRoot(), o, 2);
            }
        }, activity).setMoney(MineApp.rechargeInfoList.get(preIndex).getOriginalMoney()).setMoneyDiscountId(MineApp.rechargeInfoList.get(preIndex).getId());
        HttpManager.getInstance().doHttpDeal(api);
    }
}
