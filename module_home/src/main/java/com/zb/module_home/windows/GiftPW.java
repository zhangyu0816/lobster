package com.zb.module_home.windows;

import android.view.View;

import com.zb.lib_base.model.GiftInfo;
import com.zb.lib_base.model.RechargeInfo;
import com.zb.lib_base.model.WalletInfo;
import com.zb.lib_base.windows.BasePopupWindow;
import com.zb.module_home.BR;
import com.zb.module_home.R;
import com.zb.module_home.adapter.HomeAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class GiftPW extends BasePopupWindow {

    private WalletInfo walletInfo;
    private HomeAdapter adapter;
    private List<GiftInfo> giftInfoList;
    private int preIndex = -1;

    public GiftPW(AppCompatActivity activity, View parentView, WalletInfo walletInfo, List<GiftInfo> giftInfoList) {
        super(activity, parentView, true);
        this.walletInfo = walletInfo;
        this.giftInfoList = giftInfoList;
        initUI();
    }

    @Override
    public int getRes() {
        return R.layout.pws_home_gift;
    }

    @Override
    public void initUI() {
        adapter = new HomeAdapter<>(activity, R.layout.item_home_pws_gift, giftInfoList, this);
        mBinding.setVariable(BR.pw, this);
        mBinding.setVariable(BR.walletInfo, walletInfo);
        mBinding.setVariable(BR.adapter, adapter);
    }

    @Override
    public void cancel(View view) {
        super.cancel(view);
        dismiss();
    }

    @Override
    public void recharge(View view) {
        super.recharge(view);
        dismiss();
        List<RechargeInfo> rechargeInfoList = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            RechargeInfo rechargeInfo = new RechargeInfo();
            if (i < 2) {
                rechargeInfo.setPriceDesc("最受欢迎");
            }
            rechargeInfoList.add(rechargeInfo);
        }
        new RechargePW(activity, mBinding.getRoot(), walletInfo, rechargeInfoList);
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
    public void payGift(View view) {
        super.payGift(view);
        dismiss();
        new GiveSuccessPW( activity, mBinding.getRoot(),  giftInfoList.get(preIndex));
    }
}
