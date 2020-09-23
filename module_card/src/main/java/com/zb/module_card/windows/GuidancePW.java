package com.zb.module_card.windows;

import android.view.View;

import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.windows.BasePopupWindow;
import com.zb.module_card.BR;
import com.zb.module_card.R;


public class GuidancePW extends BasePopupWindow {

    public GuidancePW(View parentView) {
        super(parentView, false);
        initUI();
    }

    @Override
    public int getRes() {
        return R.layout.pws_guidance;
    }

    @Override
    public void initUI() {
        mBinding.setVariable(BR.pw, this);
        mBinding.setVariable(BR.position, 1);
    }

    @Override
    public void selectIndex(int position) {
        super.selectIndex(position);
        mBinding.setVariable(BR.position, position + 1);
        if (position == 8) {
            PreferenceUtil.saveIntValue(activity, "showGuidance",1);
            dismiss();
        }
    }
}
