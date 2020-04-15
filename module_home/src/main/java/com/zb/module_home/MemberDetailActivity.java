package com.zb.module_home;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;

@Route(path = RouteUtils.Home_Member_Detail)
public class MemberDetailActivity extends HomeBaseActivity {
    @Override
    public int getRes() {
        return R.layout.home_member_detail;
    }

    @Override
    public void initUI() {

    }
}
