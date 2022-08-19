package com.zb.lib_base.windows;

import android.annotation.SuppressLint;
import android.view.View;

import com.zb.lib_base.BR;
import com.zb.lib_base.R;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.adapter.BaseAdapter;
import com.zb.lib_base.api.rechargeDiscountListApi;
import com.zb.lib_base.api.rechargeWalletApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.databinding.PwsHomeVipRechargeBinding;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.OrderTran;
import com.zb.lib_base.model.RechargeInfo;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.utils.SCToastUtil;

import java.util.ArrayList;
import java.util.List;

public class RechargePW extends BasePopupWindow {
    private BaseAdapter adapter;
    private int preIndex = -1;
    private PwsHomeVipRechargeBinding binding;
    private List<RechargeInfo> rechargeInfoList = new ArrayList<>();

    public RechargePW(View parentView) {
        super(parentView, true);
        initUI();
    }

    @Override
    public int getRes() {
        return R.layout.pws_home_vip_recharge;
    }

    @Override
    public void initUI() {
        binding = (PwsHomeVipRechargeBinding) mBinding;
        adapter = new BaseAdapter<>(activity, R.layout.item_home_vip_info, rechargeInfoList, this);

        mBinding.setVariable(BR.pw, this);
        mBinding.setVariable(BR.walletInfo, MineApp.walletInfo);
        mBinding.setVariable(BR.adapter, adapter);
        rechargeDiscountList();
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
            SCToastUtil.showToast(activity, "请选择充值套餐", true);
            return;
        }
        rechargeWallet();
    }

    private void rechargeWallet() {
        rechargeWalletApi api = new rechargeWalletApi(new HttpOnNextListener<OrderTran>() {
            @Override
            public void onNext(OrderTran o) {
                dismiss();
                new PaymentPW(mBinding.getRoot(), o, 2);
            }
        }, activity).setMoney(rechargeInfoList.get(preIndex).getOriginalMoney()).setMoneyDiscountId(rechargeInfoList.get(preIndex).getId());
        HttpManager.getInstance().doHttpDeal(api);
    }


    private void rechargeDiscountList() {
        rechargeDiscountListApi api = new rechargeDiscountListApi(new HttpOnNextListener<List<RechargeInfo>>() {
            @SuppressLint({"NotifyDataSetChanged", "DefaultLocale"})
            @Override
            public void onNext(List<RechargeInfo> o) {
                for (RechargeInfo item : o) {
                    if (item.getMoneyType() == 0) {
                        if (item.getExtraGiveMoney() == 0)
                            item.setContent("");
                        else
                            item.setContent(String.format("送%.1f虾菇币", item.getExtraGiveMoney()));
                    } else if (item.getMoneyType() == 1) {
                        item.setContent("最受欢迎");
                    } else {
                        item.setContent("优惠最大");
                    }
                    rechargeInfoList.add(item);
                }
                adapter.notifyDataSetChanged();
                binding.walletList.postDelayed(() -> {
                    if (rechargeInfoList.size() < 4) {
                        AdapterBinding.viewSize(binding.walletList, ObjectUtils.getViewSizeByWidth(1.0f), ObjectUtils.getViewSizeByWidth(0.25f));
                    }
                }, 200);
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
