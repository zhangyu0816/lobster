package com.zb.module_mine.windows;

import android.view.View;

import com.zb.lib_base.model.BankInfo;
import com.zb.lib_base.windows.BasePopupWindow;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.adapter.MineAdapter;

import java.util.List;

public class BankPW extends BasePopupWindow {
    public MineAdapter adapter;
    private List<BankInfo> bankInfoList;
    private CallBack callBack;

    public BankPW(View parentView, List<BankInfo> bankInfoList, CallBack callBack) {
        super(parentView, true);
        this.bankInfoList = bankInfoList;
        this.callBack = callBack;
        initUI();
    }

    @Override
    public int getRes() {
        return R.layout.pws_bank;
    }

    @Override
    public void initUI() {
        adapter = new MineAdapter<>(activity, R.layout.item_bank, bankInfoList, this);
        mBinding.setVariable(BR.pw, this);
    }

    public void select(int position) {
        callBack.success(bankInfoList.get(position));
        dismiss();
    }

    public interface CallBack {
        void success(BankInfo bankInfo);
    }
}
