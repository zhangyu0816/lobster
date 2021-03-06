package com.zb.module_mine.vm;

import android.content.Intent;
import android.text.InputType;
import android.view.View;

import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.databinding.MineEditContentBinding;
import com.zb.module_mine.iv.EditContentVMInterface;

import androidx.databinding.ViewDataBinding;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class EditContentViewModel extends BaseViewModel implements EditContentVMInterface {
    private MineEditContentBinding contentBinding;
    public int type;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        contentBinding = (MineEditContentBinding) binding;
        if (type == 6) {
            contentBinding.edContent.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
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
                SCToastUtil.showToast(activity, "名称不能为空", true);
                return;
            }
        }
        if(type==6){
            if(!contentBinding.getContent().matches(MineApp.HEIGHT_REG)){
                SCToastUtil.showToast(activity, "请输入正确身高", true);
                return;
            }
        }
        Intent data = new Intent("lobster_member");
        data.putExtra("type", type);
        data.putExtra("content", contentBinding.getContent());
        LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(data);
        activity.finish();
    }

    @Override
    public void cleanContent(View view) {
        contentBinding.setContent("");
    }
}
