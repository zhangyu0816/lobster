package com.zb.module_mine.vm;

import android.view.View;

import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.R;
import com.zb.module_mine.adapter.MineAdapter;
import com.zb.module_mine.databinding.MineOpenVipBinding;
import com.zb.module_mine.iv.OpenVipVMInterface;

import androidx.databinding.ViewDataBinding;

public class OpenVipViewModel extends BaseViewModel implements OpenVipVMInterface {
    public MineAdapter adapter;
    public MemberInfo memberInfo;
    private int preIndex = -1;
    private MineOpenVipBinding vipBinding;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        vipBinding = (MineOpenVipBinding) binding;
        setAdapter();
    }

    @Override
    public void setAdapter() {
        adapter = new MineAdapter<>(activity, R.layout.item_mine_vip, MineApp.vipInfoList, this);
    }

    @Override
    public void getVip(int index) {
        if (index == 0 && adapter.getSelectIndex() == -1) {
            vipBinding.scrollView.scrollTo(0, vipBinding.scrollView.getHeight());
        } else {
            memberInfo.setMemberExpireTime("2020-08-09");
            memberInfo.setMemberType(memberInfo.getMemberType() == 1 ? 2 : 1);
        }
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void selectIndex(int position) {
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
