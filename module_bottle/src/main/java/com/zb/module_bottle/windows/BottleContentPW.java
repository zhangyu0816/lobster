package com.zb.module_bottle.windows;

import android.view.View;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.api.castBottleApi;
import com.zb.lib_base.api.pickBottleApi;
import com.zb.lib_base.api.replyBottleApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.BottleInfo;
import com.zb.lib_base.model.BottleMsg;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.windows.BasePopupWindow;
import com.zb.module_bottle.BR;
import com.zb.module_bottle.R;
import com.zb.module_bottle.adapter.BottleAdapter;
import com.zb.module_bottle.databinding.PwsBottleContentBinding;

import java.util.Arrays;

public class BottleContentPW extends BasePopupWindow {
    private BottleInfo bottleInfo;
    private BottleAdapter adapter;
    private boolean isWrite;
    private boolean isReply = false;
    private PwsBottleContentBinding binding;

    public BottleContentPW(RxAppCompatActivity activity, View parentView, BottleInfo bottleInfo, boolean isWrite) {
        super(activity, parentView, true);
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
        mBinding.setVariable(BR.btnName, isWrite ? activity.getResources().getString(R.string.bottle_throw) : activity.getResources().getString(R.string.bottle_reply));
        binding = (PwsBottleContentBinding) mBinding;
        binding.edContent.setTypeface(MineApp.type);
    }

    @Override
    public void cancel(View view) {
        super.cancel(view);
        pickBottle(1);
    }

    @Override
    public void sure(View view) {
        super.sure(view);
        if (isWrite) {
            if (binding.edContent.getText().toString().trim().isEmpty()) {
                SCToastUtil.showToastBlack(activity, isReply ? "回复内容不能为空" : "漂流瓶内容不能为空");
                return;
            }
            if (isReply)
                pickBottle(2);
            else
                castBottle();
        } else {
            isReply = true;
            isWrite = true;
            mBinding.setVariable(BR.isWrite, true);
            mBinding.setVariable(BR.btnName, "立即回复");
        }
    }

    // 创建漂流瓶
    private void castBottle() {
        castBottleApi api = new castBottleApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                SCToastUtil.showToastBlack(activity, "扔到海里了");
                dismiss();
            }
        }, activity).setText(binding.edContent.getText().toString());
        HttpManager.getInstance().doHttpDeal(api);
    }

    // 扔回海里
    private void pickBottle(int driftBottleType) {
        pickBottleApi api = new pickBottleApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                if (driftBottleType == 2) {
                    replyBottle();
                } else
                    dismiss();
            }
        }, activity).setDriftBottleId(bottleInfo.getDriftBottleId()).setDriftBottleType(driftBottleType);
        HttpManager.getInstance().doHttpDeal(api);
    }

    // 回信
    private void replyBottle() {
        replyBottleApi api = new replyBottleApi(new HttpOnNextListener<BottleMsg>() {
            @Override
            public void onNext(BottleMsg o) {
                SCToastUtil.showToastBlack(activity, "回信成功");
                dismiss();
            }
        }, activity).setDriftBottleId(bottleInfo.getDriftBottleId()).setText(binding.edContent.getText().toString());
        HttpManager.getInstance().doHttpDeal(api);
    }
}
