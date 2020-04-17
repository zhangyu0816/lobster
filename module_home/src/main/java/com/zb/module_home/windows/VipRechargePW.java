package com.zb.module_home.windows;

import android.view.View;

import com.zb.lib_base.model.VipInfo;
import com.zb.lib_base.model.WalletInfo;
import com.zb.lib_base.windows.BasePopupWindow;
import com.zb.lib_base.windows.TextPW;
import com.zb.module_home.BR;
import com.zb.module_home.HomeAdapter;
import com.zb.module_home.R;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class VipRechargePW extends BasePopupWindow {
    private WalletInfo walletInfo;
    private List<VipInfo> vipInfoList;
    private HomeAdapter adapter;
    private int preIndex = -1;

    public VipRechargePW(AppCompatActivity activity, View parentView, WalletInfo walletInfo, List<VipInfo> vipInfoList) {
        super(activity, parentView);
        this.walletInfo = walletInfo;
        this.vipInfoList = vipInfoList;
        initUI();
    }

    @Override
    public int getRes() {
        return R.layout.pws_home_vip_recharge;
    }

    @Override
    public void initUI() {
        adapter = new HomeAdapter<>(activity, R.layout.item_home_vip_info, vipInfoList, this);
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
