package com.zb.lib_base.windows;

import android.view.View;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.BR;
import com.zb.lib_base.R;
import com.zb.lib_base.api.dynDoReviewApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.databinding.PwsEditBinding;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.utils.SCToastUtil;

public class EditPW extends BasePopupWindow {
    private PwsEditBinding binding;
    private long friendDynId;

    public EditPW(RxAppCompatActivity activity, View parentView, long friendDynId) {
        super(activity, parentView, true);
        this.friendDynId = friendDynId;
        initUI();
    }

    @Override
    public int getRes() {
        return R.layout.pws_edit;
    }

    @Override
    public void initUI() {
        mBinding.setVariable(BR.pw, this);
        binding = (PwsEditBinding) mBinding;
        binding.edContent.setTypeface(MineApp.QingSongShouXieTiType);
    }

    @Override
    public void sure(View view) {
        super.sure(view);
        if (binding.edContent.getText().toString().isEmpty()) {
            SCToastUtil.showToast(activity, "请输入要回复的信息", true);
            return;
        }

        dynDoReviewApi api = new dynDoReviewApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                SCToastUtil.showToast(activity, "发布成功", true);
                dismiss();
            }
        }, activity).setFriendDynId(friendDynId).setText(binding.edContent.getText().toString());
        api.setDialogTitle("正在回复");
        HttpManager.getInstance().doHttpDeal(api);
    }

    public interface CallBack {
        void sure();
    }
}
