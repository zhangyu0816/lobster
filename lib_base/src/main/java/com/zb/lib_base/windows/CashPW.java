package com.zb.lib_base.windows;

import android.view.View;

import com.zb.lib_base.R;
import com.zb.lib_base.adapter.BaseAdapter;
import com.zb.lib_base.databinding.PwsCashBinding;
import com.zb.lib_base.model.MineBank;

import java.util.List;

public class CashPW extends BasePopupWindow {
    private MineBank mMineBank;
    private double mMoney;
    private List<MineBank> mMineBankList;
    private PwsCashBinding binding;
    private CallBack mCallBack;
    public BaseAdapter adapter;

    public CashPW(View parentView, MineBank mineBank, double money, List<MineBank> mineBankList, CallBack callBack) {
        super(parentView, true);
        mMineBank = mineBank;
        mMoney = money;
        mMineBankList = mineBankList;
        mCallBack = callBack;
        initUI();
    }

    @Override
    public int getRes() {
        return R.layout.pws_cash;
    }

    @Override
    public void initUI() {
        binding = (PwsCashBinding) mBinding;
        mMineBank.setBankInfo(mMineBank.getBankName() + "(" + mMineBank.getAccountNo().substring(mMineBank.getAccountNo().length() - 4) + ")");
        adapter = new BaseAdapter<>(activity, R.layout.item_cash_bank, mMineBankList, this);
        binding.setPw(this);
        binding.setMineBank(mMineBank);
        binding.setMoney("¥ " + mMoney);
        binding.setShowList(false);

    }

    public void selectBank(View view) {
        binding.setShowList(!binding.getShowList());
    }

    @Override
    public void selectIndex(int position) {
        super.selectIndex(position);
        mMineBank = mMineBankList.get(position);
        mMineBank.setBankInfo(mMineBank.getBankName() + "(" + mMineBank.getAccountNo().substring(mMineBank.getAccountNo().length() - 4) + ")");
        binding.setMineBank(mMineBank);
        binding.setShowList(false);
    }

    public void changeCash(View view) {
        mCallBack.success(mMineBank);
        dismiss();
    }

    public interface CallBack {
        void success(MineBank mineBank);
    }
}
