package com.zb.module_bottle.windows;

import android.view.View;

import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.windows.BasePopupWindow;
import com.zb.module_bottle.BR;
import com.zb.module_bottle.R;
import com.zb.module_bottle.adapter.BottleAdapter;

import androidx.appcompat.app.AppCompatActivity;

public class BottleVipPW extends BasePopupWindow {

    private BottleAdapter adapter;
    private int preIndex = -1;

    public BottleVipPW(AppCompatActivity activity, View parentView) {
        super(activity, parentView, true);
        initUI();
    }

    @Override
    public int getRes() {
        return R.layout.pws_bottle_vip;
    }

    @Override
    public void initUI() {
        adapter = new BottleAdapter<>(activity, R.layout.item_bottle_vip, MineApp.vipInfoList, this);
        mBinding.setVariable(BR.pw, this);
        mBinding.setVariable(BR.adapter, adapter);
    }

    @Override
    public void sure(View view) {
        super.sure(view);
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
}
