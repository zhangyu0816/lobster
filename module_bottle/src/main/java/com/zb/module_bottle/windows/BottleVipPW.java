package com.zb.module_bottle.windows;

import android.view.View;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.windows.BasePopupWindow;
import com.zb.module_bottle.BR;
import com.zb.module_bottle.R;
import com.zb.module_bottle.adapter.BottleAdapter;

public class BottleVipPW extends BasePopupWindow {

    private BottleAdapter adapter;
    private int preIndex = -1;

    public BottleVipPW(RxAppCompatActivity activity, View parentView) {
        super(activity, parentView, false);
        initUI();
    }

    @Override
    public int getRes() {
        return R.layout.pws_bottle_vip;
    }

    @Override
    public void initUI() {
        adapter = new BottleAdapter<>(activity, R.layout.item_bottle_vip, MineApp.vipInfoList, this);
        preIndex = 1;
        adapter.setSelectIndex(1);
        mBinding.setVariable(BR.pw, this);
        mBinding.setVariable(BR.adapter, adapter);
    }

    @Override
    public void cancel(View view) {
        super.cancel(view);
        dismiss();
    }

    @Override
    public void sure(View view) {
        super.sure(view);
        if (preIndex == -1) {
            SCToastUtil.showToast(activity, "请选择VIP套餐", true);
            return;
        }
        submitOpenedMemberOrder(MineApp.vipInfoList.get(preIndex).getMemberOfOpenedProductId());
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
}
