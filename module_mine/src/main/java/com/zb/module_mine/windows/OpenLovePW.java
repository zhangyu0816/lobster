package com.zb.module_mine.windows;

import android.view.View;

import com.zb.lib_base.BR;
import com.zb.lib_base.api.submitOpenedMemberOrderApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.OrderNumber;
import com.zb.lib_base.model.VipInfo;
import com.zb.lib_base.windows.BasePopupWindow;
import com.zb.module_mine.R;
import com.zb.module_mine.adapter.MineAdapter;

import java.util.ArrayList;
import java.util.List;

public class OpenLovePW extends BasePopupWindow {
    private List<VipInfo> vipInfoList = new ArrayList<>();
    public MineAdapter<VipInfo> adapter;
    private int preIndex = 1;

    public OpenLovePW(View parentView) {
        super(parentView, false);
    }

    public OpenLovePW setVipInfoList(List<VipInfo> vipInfoList) {
        this.vipInfoList = vipInfoList;
        return this;
    }

    @Override
    public int getRes() {
        return R.layout.pws_open_love;
    }

    @Override
    public void initUI() {
        adapter = new MineAdapter<>(activity, R.layout.item_love_money, vipInfoList, this);
        adapter.setSelectIndex(1);
        mBinding.setVariable(BR.pw, this);
        setBtn();
    }

    public void back(View view) {
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
            setBtn();
        }
    }

    private void setBtn() {
        String name = preIndex == 0 ? " 开通包月权限" : (preIndex == 1 ? " 开通包季权限" : " 开通包年权限");
        mBinding.setVariable(BR.btnName, "¥" + vipInfoList.get(preIndex).getPrice() + name);
    }

    public void openLove(View view) {
        MineApp.pfAppType = "205";
        submitOpenedMemberOrder(MineApp.vipInfoList.get(preIndex).getMemberOfOpenedProductId(), null);
    }
}
