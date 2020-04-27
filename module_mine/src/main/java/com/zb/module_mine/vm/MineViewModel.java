package com.zb.module_mine.vm;

import android.view.View;

import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.BR;
import com.zb.module_mine.iv.MineVMInterface;

import androidx.databinding.ViewDataBinding;

public class MineViewModel extends BaseViewModel implements MineVMInterface {

    public MemberInfo memberInfo;

    public MineViewModel() {
        memberInfo = new MemberInfo();
        memberInfo.setNick("租我把");
        memberInfo.setMemberType(1);
        memberInfo.setImage(MineApp.logo);
    }

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding.setVariable(BR.hasNewBeLike, false);
    }

    @Override
    public void publishDiscover(View view) {
        ActivityUtils.getHomePublishImage();
    }

    @Override
    public void openVip(View view) {
        ActivityUtils.getMineOpenVip(memberInfo);
    }

    @Override
    public void toMemberDetail(View view) {
    }

    @Override
    public void toNews(View view) {
        ActivityUtils.getMineNewsManager();
    }

    @Override
    public void toSetting(View view) {
        ActivityUtils.getMineSetting();
    }
}
