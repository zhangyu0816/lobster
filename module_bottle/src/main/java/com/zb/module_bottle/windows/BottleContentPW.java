package com.zb.module_bottle.windows;

import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.api.castBottleApi;
import com.zb.lib_base.api.pickBottleApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.BottleInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.windows.BottleQuestionPW;
import com.zb.module_bottle.BR;
import com.zb.module_bottle.R;
import com.zb.module_bottle.adapter.BottleAdapter;
import com.zb.module_bottle.databinding.PwsBottleContentBinding;

import java.util.Arrays;

import androidx.databinding.DataBindingUtil;

public class BottleContentPW extends PopupWindow {
    private BottleInfo bottleInfo;
    private BottleAdapter adapter;
    private boolean isWrite;
    private PwsBottleContentBinding mBinding;
    private RxAppCompatActivity activity;
    private PopupWindow pw;

    public BottleContentPW(RxAppCompatActivity activity, View parentView, BottleInfo bottleInfo, boolean isWrite, CallBack callBack) {
        this.activity = activity;
        this.bottleInfo = bottleInfo;
        this.isWrite = isWrite;
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.pws_bottle_content, null, false);
        View view = mBinding.getRoot();

        pw = new PopupWindow(view, LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT, true);
        pw.setBackgroundDrawable(new BitmapDrawable());
        pw.setContentView(view);
        pw.showAtLocation(parentView, Gravity.CENTER, 0, 0);
        pw.setOnDismissListener(() ->
                {
                    if (callBack != null)
                        callBack.cancelBack();
                }
        );

        initUI();
    }

    public interface CallBack {
        void cancelBack();
    }

    private void initUI() {
        adapter = new BottleAdapter<>(activity, R.layout.item_bottle_content, Arrays.asList(bottleInfo.getText().split("")));
        mBinding.setVariable(BR.pw, this);
        mBinding.setVariable(BR.bottleInfo, bottleInfo);
        mBinding.setVariable(BR.adapter, adapter);
        mBinding.setVariable(BR.isWrite, isWrite);
        mBinding.setVariable(BR.btnName, isWrite ? activity.getResources().getString(R.string.bottle_throw) : activity.getResources().getString(R.string.bottle_reply));
        mBinding.edContent.setTypeface(MineApp.type);

        mBinding.tvSure.setOnClickListener(view -> {
            if (isWrite) {
                if (mBinding.edContent.getText().toString().trim().isEmpty()) {
                    SCToastUtil.showToast(activity, "漂流瓶内容不能为空", true);
                    return;
                }
                castBottle();
            } else {
                pickBottle(2);
            }
        });

        mBinding.tvCancel.setOnClickListener(view -> pickBottle(1));

        mBinding.ivBack.setOnClickListener(view -> pw.dismiss());

        mBinding.ivQuestion.setOnClickListener(view -> {
            pw.dismiss();
            new BottleQuestionPW(activity, mBinding.getRoot());
        });
    }

    // 创建漂流瓶
    private void castBottle() {
        castBottleApi api = new castBottleApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                SCToastUtil.showToast(activity, "扔到海里了", true);
                pw.dismiss();
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
                pw.dismiss();
            }
        }, activity).setDriftBottleId(bottleInfo.getDriftBottleId()).setDriftBottleType(driftBottleType);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
