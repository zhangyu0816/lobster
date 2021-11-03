package com.zb.lib_base.windows;

import android.content.Context;
import android.text.ClipboardManager;
import android.view.View;

import com.zb.lib_base.R;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.databinding.PwsWxBinding;
import com.zb.lib_base.utils.SCToastUtil;

public class WXPW extends BasePopupWindow {
    private String wx = "";
    private PwsWxBinding binding;
    private CallBack callBack;

    public WXPW(View parentView) {
        super(parentView, false);
    }

    public WXPW setWx(String wx) {
        this.wx = wx;
        return this;
    }

    public WXPW setCallBack(CallBack callBack) {
        this.callBack = callBack;
        return this;
    }

    @Override
    public int getRes() {
        return R.layout.pws_wx;
    }

    @Override
    public void initUI() {
        binding = (PwsWxBinding) mBinding;
        binding.setPw(this);
        binding.tvWx.setText(wx);
        binding.tvWx.setTypeface(MineApp.blackbold);
    }

    @Override
    public void cancel(View view) {
        super.cancel(view);
        callBack.onFinish();
        dismiss();
    }

    @Override
    public void sure(View view) {
        super.sure(view);
        copy(wx);
        callBack.onFinish();
        dismiss();
    }

    private void copy(String content) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) activity
                .getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
        SCToastUtil.showToast(activity, "复制成功", true);
    }

    public interface CallBack {
        void onFinish();
    }

}
