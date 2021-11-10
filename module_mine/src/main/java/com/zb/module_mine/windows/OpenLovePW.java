package com.zb.module_mine.windows;

import android.view.View;

import com.zb.lib_base.BR;
import com.zb.lib_base.app.MineApp;
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
        String open = MineApp.loveMineInfo.getMemberType() == 2 ? "续费" : "开通";
        String title = preIndex == 0 ? open + "地摊盲盒包月权限" : (preIndex == 1 ? open + "地摊盲盒包季权限" : open + "地摊盲盒包年权限");
        String name = preIndex == 0 ? open + "包月权限" : (preIndex == 1 ? open + "包季权限" : open + "包年权限");
        mBinding.setVariable(BR.btnName, "¥" + vipInfoList.get(preIndex).getPrice() + " " + name);
        mBinding.setVariable(BR.title, title);
    }

    public void openLove(View view) {
        MineApp.pfAppType = "205";
        submitOpenedMemberOrder(vipInfoList.get(preIndex).getMemberOfOpenedProductId(), null);
    }
}
