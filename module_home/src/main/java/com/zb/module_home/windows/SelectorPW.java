package com.zb.module_home.windows;

import android.view.View;

import com.zb.lib_base.windows.BasePopupWindow;
import com.zb.module_home.BR;
import com.zb.module_home.HomeAdapter;
import com.zb.module_home.R;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class SelectorPW extends BasePopupWindow {

    private HomeAdapter adapter;
    private List<String> selectorList;
    private CallBack mCallBack;

    public SelectorPW(AppCompatActivity activity, View parentView, List<String> selectorList, CallBack callBack) {
        super(activity, parentView);
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
        adapter = new HomeAdapter<>(activity, R.layout.item_selector, selectorList, this);
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
