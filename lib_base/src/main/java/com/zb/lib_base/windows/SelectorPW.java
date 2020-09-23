package com.zb.lib_base.windows;

import android.view.View;

import com.zb.lib_base.BR;
import com.zb.lib_base.R;
import com.zb.lib_base.adapter.BaseAdapter;

import java.util.List;

public class SelectorPW extends BasePopupWindow {

    private BaseAdapter adapter;
    private List<String> selectorList;
    private CallBack mCallBack;

    public SelectorPW(View parentView, List<String> selectorList, CallBack callBack) {
        super(parentView, true);
        this.selectorList = selectorList;
        mCallBack = callBack;
        initUI();
    }

    @Override
    public int getRes() {
        return R.layout.pws_selector;
    }

    @Override
    public void initUI() {
        adapter = new BaseAdapter<>(activity, R.layout.item_selector, selectorList, this);
        mBinding.setVariable(BR.adapter, adapter);
        mBinding.setVariable(BR.pw, this);
    }

    @Override
    public void cancel(View view) {
        super.cancel(view);
        dismiss();
    }

    @Override
    public void selectIndex(int position) {
        super.selectIndex(position);
        mCallBack.select(position);
        dismiss();
    }

    public interface CallBack {
        void select(int position);
    }
}
