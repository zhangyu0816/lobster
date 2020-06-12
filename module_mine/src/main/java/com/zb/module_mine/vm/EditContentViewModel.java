package com.zb.module_mine.vm;

import android.content.Intent;
import android.view.View;

import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.databinding.MineEditContentBinding;
import com.zb.module_mine.iv.EditContentVMInterface;

import androidx.databinding.ViewDataBinding;

public class EditContentViewModel extends BaseViewModel implements EditContentVMInterface {
    private MineEditContentBinding contentBinding;
    public int type;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        contentBinding = (MineEditContentBinding) binding;
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void submit(View view) {
        if (type == 2) {
            if (contentBinding.getContent().isEmpty()) {
                SCToastUtil.showToast(activity, "昵称不能为空", true);
                return;
            }
        }
        Intent data = new Intent("lobster_member");
        data.putExtra("type", type);
        data.putExtra("content", contentBinding.getContent());
        activity.sendBroadcast(data);
        activity.finish();
    }
}
