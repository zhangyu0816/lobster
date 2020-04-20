package com.zb.module_bottle.windows;

import android.view.View;

import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.model.BottleInfo;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.windows.BasePopupWindow;
import com.zb.module_bottle.BR;
import com.zb.module_bottle.R;
import com.zb.module_bottle.adapter.BottleAdapter;
import com.zb.module_bottle.databinding.PwsBottleContentBinding;

import java.util.Arrays;

import androidx.appcompat.app.AppCompatActivity;

public class BottleContentPW extends BasePopupWindow {
    private BottleInfo bottleInfo;
    private BottleAdapter adapter;
    private boolean isWrite = false;
    private PwsBottleContentBinding binding;

    public BottleContentPW(AppCompatActivity activity, View parentView, BottleInfo bottleInfo, boolean isWrite) {
        super(activity, parentView);
        this.bottleInfo = bottleInfo;
        this.isWrite = isWrite;
        initUI();
    }

    @Override
    public int getRes() {
        return R.layout.pws_bottle_content;
    }

    @Override
    public void initUI() {

        adapter = new BottleAdapter<>(activity, R.layout.item_bottle_content, Arrays.asList(bottleInfo.getText().split("")));

        mBinding.setVariable(BR.pw, this);
        mBinding.setVariable(BR.bottleInfo, bottleInfo);
        mBinding.setVariable(BR.adapter, adapter);
        mBinding.setVariable(BR.isWrite, isWrite);
        binding = (PwsBottleContentBinding) mBinding;
        binding.edContent.setTypeface(MineApp.type);
    }

    @Override
    public void cancel(View view) {
        super.cancel(view);
        dismiss();
    }

    @Override
    public void sure(View view) {
        super.sure(view);
        dismiss();
        if (isWrite) {
            if (binding.edContent.getText().toString().trim().isEmpty()) {
                SCToastUtil.showToast(activity, "    漂流瓶内容不能为空    ");
                return;
            }

        } else {

        }
    }
}
