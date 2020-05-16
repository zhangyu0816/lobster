package com.zb.lib_base.windows;

import android.view.View;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.BR;
import com.zb.lib_base.R;


public class CountUsedPW extends BasePopupWindow {
    private int type;

    public CountUsedPW(RxAppCompatActivity activity, View parentView, int type) {
        super(activity, parentView, false);
        this.type = type;
        initUI();
    }

    @Override
    public int getRes() {
        return R.layout.pws_count_used;
    }

    @Override
    public void initUI() {
        mBinding.setVariable(BR.pw, this);
        if (type == 1) {
            mBinding.setVariable(BR.title, "今日划错反悔\n次数用完啦");
            mBinding.setVariable(BR.content, "");
            mBinding.setVariable(BR.imageRes,activity.getResources().getDrawable(R.mipmap.vip_ad_2));
        } else {
            mBinding.setVariable(BR.title, "今日超级喜欢用完啦");
            mBinding.setVariable(BR.content, "明天再来，可先关注你喜欢的人哦~");
            mBinding.setVariable(BR.imageRes, activity.getResources().getDrawable(R.mipmap.vip_ad_3));
        }
    }

    @Override
    public void sure(View view) {
        super.sure(view);
        dismiss();
    }
}
