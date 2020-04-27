package com.zb.lib_base.windows;

import android.view.View;

import com.zb.lib_base.BR;
import com.zb.lib_base.R;
import com.zb.lib_base.adapter.BaseAdapter;
import com.zb.lib_base.model.RechargeInfo;
import com.zb.lib_base.model.WalletInfo;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class RechargePW extends BasePopupWindow {
    private WalletInfo walletInfo;
    private List<RechargeInfo> rechargeInfoList;
    private BaseAdapter adapter;
    private int preIndex = -1;

    public RechargePW(AppCompatActivity activity, View parentView, WalletInfo walletInfo, List<RechargeInfo> rechargeInfoList) {
        super(activity, parentView, true);
        this.walletInfo = walletInfo;
        this.rechargeInfoList = rechargeInfoList;
        initUI();
    }

    @Override
    public int getRes() {
        return R.layout.pws_home_vip_recharge;
    }

    @Override
    public void initUI() {
        adapter = new BaseAdapter<>(activity, R.layout.item_home_vip_info, rechargeInfoList, this);
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
        dismiss();
        new TextPW(activity, mBinding.getRoot(), "您已充值成功！", "充值金额50元，获得88个虾菇币，账户余额为128个虾菇币");
    }
}
