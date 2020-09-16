package com.zb.module_mine.vm;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;

import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.changeCashApi;
import com.zb.lib_base.api.myBankCardsApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.MineBank;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.databinding.MineWithdrawBinding;
import com.zb.module_mine.iv.WithdrawVMInterface;

import java.util.List;

import androidx.databinding.ViewDataBinding;

public class WithdrawViewModel extends BaseViewModel implements WithdrawVMInterface {
    private MineWithdrawBinding mBinding;
    public MineBank mineBank;
    private BaseReceiver selectBankReceiver;
    private BaseReceiver addBankReceiver;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (MineWithdrawBinding) binding;
        mBinding.edMoney.setInputType(InputType.TYPE_CLASS_NUMBER
                | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mBinding.edMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!mBinding.getMoney().isEmpty()) {
                    String s = editable.toString();
                    if (s.contains(".")) {
                        if (s.length() - 1 - s.indexOf(".") > 2) {
                            s = s.substring(0, s.indexOf(".") + (2 + 1));
                            mBinding.setMoney(s);
                        }
                    }

                    //限制只能输入一次小数点
                    if (s.contains(".")) {
                        if (s.indexOf(".", s.indexOf(".") + 1) > 0) {
                            mBinding.setMoney(s.substring(0, s.length() - 1));
                        }
                    }

                    //第一次输入为点的时候
                    if (s.trim().equals(".")) {
                        mBinding.setMoney("0" + s);
                    }

                    //个位数为0的时候
                    if (s.startsWith("0") && s.trim().length() > 1) {
                        if (!s.substring(1, 2).equals(".")) {
                            mBinding.setMoney(s.subSequence(0, 1).toString());
                        }
                    }
                    mBinding.edMoney.setSelection(s.length());

                    if (MineApp.walletInfo.getCanWithdrawCreditWallet() < Float.parseFloat(mBinding.getMoney())) {
                        mBinding.setMoney(MineApp.walletInfo.getCanWithdrawCreditWallet() + "");
                    }
                }
            }
        });

        selectBankReceiver = new BaseReceiver(activity, "lobster_selectBank") {
            @Override
            public void onReceive(Context context, Intent intent) {
                mineBank = (MineBank) intent.getSerializableExtra("mineBank");
                mBinding.setMineBank(mineBank);
            }
        };
        addBankReceiver = new BaseReceiver(activity, "lobster_addBank") {
            @Override
            public void onReceive(Context context, Intent intent) {
                myBankCards();
            }
        };
        myBankCards();
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    public void onDestroy() {
        try {
            selectBankReceiver.unregisterReceiver();
            addBankReceiver.unregisterReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void myBankCards() {
        myBankCardsApi api = new myBankCardsApi(new HttpOnNextListener<List<MineBank>>() {
            @Override
            public void onNext(List<MineBank> o) {
                mineBank = o.get(0);
                mBinding.setMineBank(mineBank);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    mineBank = new MineBank();
                    mBinding.setMineBank(mineBank);
                }
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void addBankCard(View view) {
        ActivityUtils.getMineBindingBank();
    }

    @Override
    public void selectBank(View view) {
        ActivityUtils.getMineBankList(true);
    }

    @Override
    public void withdraw(View view) {
        if (MineApp.walletInfo.getCanWithdrawCreditWallet() == 0) {
            SCToastUtil.showToast(activity, "暂无可提现收益", true);
            return;
        }

        if (mBinding.getMoney().isEmpty()) {
            SCToastUtil.showToast(activity, "请输入提现金额", true);
            return;
        }
        if (mineBank.getId() == 0) {
            SCToastUtil.showToast(activity, "请选择提现账号", true);
            return;
        }

        changeCashApi api = new changeCashApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                SCToastUtil.showToast(activity, "已提交提现信息", true);
                activity.sendBroadcast(new Intent("lobster_recharge"));
                activity.finish();
            }
        }, activity).setMoney(mBinding.getMoney()).setBankAccountId(mineBank.getId());
        HttpManager.getInstance().doHttpDeal(api);
    }
}
