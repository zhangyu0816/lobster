package com.zb.module_mine.vm;

import android.view.View;

import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.iv.MineVMInterface;

public class MineViewModel extends BaseViewModel implements MineVMInterface {

    public MemberInfo memberInfo;

    public MineViewModel() {
        memberInfo = new MemberInfo();
        memberInfo.setNick("租我把");
        memberInfo.setMemberType(2);
    }

    @Override
    public void publishDiscover(View view) {
        ActivityUtils.getHomePublishImage();
    }
}
