package com.zb.module_home.windows;

import android.view.View;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.model.GiftInfo;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.windows.BasePopupWindow;
import com.zb.lib_base.windows.RechargePW;
import com.zb.module_home.BR;
import com.zb.module_home.R;
import com.zb.module_home.adapter.HomeAdapter;

public class GiftPW extends BasePopupWindow {

    private HomeAdapter adapter;
    private int preIndex = -1;
    private CallBack mCallBack;

    public GiftPW(RxAppCompatActivity activity, View parentView,CallBack callBack) {
        super(activity, parentView, true);
        mCallBack = callBack;
        initUI();
    }

    @Override
    public int getRes() {
        return R.layout.pws_home_gift;
    }

    @Override
    public void initUI() {
        adapter = new HomeAdapter<>(activity, R.layout.item_home_pws_gift, MineApp.giftInfoList, this);
        mBinding.setVariable(BR.pw, this);
        mBinding.setVariable(BR.walletInfo, MineApp.walletInfo);
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
        new RechargePW(activity, mBinding.getRoot());
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
        if (MineApp.walletInfo.getWallet() < MineApp.giftInfoList.get(preIndex).getPayMoney()) {
            SCToastUtil.showToast(activity, "钱包余额不足，请先充值", true);
            return;
        }
        mCallBack.selectGiftInfo(MineApp.giftInfoList.get(preIndex));
        dismiss();
    }

    public interface CallBack {
        void selectGiftInfo(GiftInfo giftInfo);
    }
}
