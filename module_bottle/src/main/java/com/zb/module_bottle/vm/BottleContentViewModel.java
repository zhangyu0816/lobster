package com.zb.module_bottle.vm;

import android.view.View;

import com.zb.lib_base.api.castBottleApi;
import com.zb.lib_base.api.pickBottleApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.BottleInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_bottle.R;
import com.zb.module_bottle.adapter.BottleAdapter;
import com.zb.module_bottle.databinding.BottleContentBinding;
import com.zb.module_bottle.iv.BottleContentVMInterface;

import java.util.Arrays;

import androidx.databinding.ViewDataBinding;

public class BottleContentViewModel extends BaseViewModel implements BottleContentVMInterface {

    public BottleAdapter adapter;
    public BottleInfo bottleInfo;
    private BottleContentBinding mBinding;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (BottleContentBinding) binding;
        mBinding.edContent.setTypeface(MineApp.type);
        setAdapter();
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void setAdapter() {
        adapter = new BottleAdapter<>(activity, R.layout.item_bottle_content, Arrays.asList(bottleInfo.getText().split("")));
    }

    @Override
    public void sure(View view) {
        if (bottleInfo.getDriftBottleId() == 0) {
            if (mBinding.edContent.getText().toString().trim().isEmpty()) {
                SCToastUtil.showToast(activity, "漂流瓶内容不能为空", true);
                return;
            }
            castBottle();
        } else {
            pickBottle(2);
        }
    }

    @Override
    public void cancel(View view) {
        pickBottle(1);
    }

    // 创建漂流瓶
    private void castBottle() {
        castBottleApi api = new castBottleApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                SCToastUtil.showToast(activity, "扔到海里了", true);
                activity.finish();
            }
        }, activity).setText(mBinding.edContent.getText().toString());
        HttpManager.getInstance().doHttpDeal(api);
    }

    // 扔回海里
    private void pickBottle(int driftBottleType) {
        pickBottleApi api = new pickBottleApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                if (driftBottleType == 2) {
                    ActivityUtils.getBottleChat(bottleInfo.getDriftBottleId());
                }
                activity.finish();
            }
        }, activity).setDriftBottleId(bottleInfo.getDriftBottleId()).setDriftBottleType(driftBottleType);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
