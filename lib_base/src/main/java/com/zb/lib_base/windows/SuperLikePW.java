package com.zb.lib_base.windows;

import android.view.View;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.BR;
import com.zb.lib_base.R;


public class SuperLikePW extends BasePopupWindow {

    private String myHead;
    private String otherHead;
    private boolean isPair;
    private int mySex;
    private int otherSex;
    private CallBack callBack;

    public SuperLikePW(RxAppCompatActivity activity, View parentView, String myHead, String otherHead, boolean isPair, int mySex, int otherSex, CallBack callBack) {
        super(activity, parentView, !isPair);
        this.myHead = myHead;
        this.otherHead = otherHead;
        this.isPair = isPair;
        this.mySex = mySex;
        this.otherSex = otherSex;
        this.callBack = callBack;
        initUI();
    }

    @Override
    public int getRes() {
        return R.layout.pws_super_like;
    }

    @Override
    public void initUI() {
        mBinding.setVariable(BR.pw,this);
        mBinding.setVariable(BR.myHead, myHead);
        mBinding.setVariable(BR.otherHead, otherHead);
        mBinding.setVariable(BR.isPair, isPair);
        mBinding.setVariable(BR.mySex, mySex);
        mBinding.setVariable(BR.otherSex, otherSex);
    }

    public interface CallBack {
        void success();
    }

    public void toChat(View view) {
        dismiss();
        if (callBack != null)
            callBack.success();
    }
}
