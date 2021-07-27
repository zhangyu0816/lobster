package com.zb.module_mine.vm;

import android.content.Intent;
import android.view.View;

import com.zb.lib_base.api.myBankCardsApi;
import com.zb.lib_base.api.removeBankCardApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.MineBank;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.SelectorPW;
import com.zb.module_mine.R;
import com.zb.module_mine.adapter.MineAdapter;
import com.zb.module_mine.iv.BankListVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.ViewDataBinding;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class BankListViewModel extends BaseViewModel implements BankListVMInterface {
    public MineAdapter adapter;
    public boolean isSelect;
    private List<MineBank> mineBankList = new ArrayList<>();
    private List<String> selectorList = new ArrayList<>();

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        selectorList.add("删除");
        setAdapter();
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void setAdapter() {
        adapter = new MineAdapter<>(activity, R.layout.item_my_bank, mineBankList, this);
        myBankCards();
    }

    @Override
    public void myBankCards() {
        myBankCardsApi api = new myBankCardsApi(new HttpOnNextListener<List<MineBank>>() {
            @Override
            public void onNext(List<MineBank> o) {
                mineBankList.addAll(o);
                adapter.notifyDataSetChanged();
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void delete(int position) {
        if (isSelect) {
            Intent data = new Intent("lobster_selectBank");
            data.putExtra("mineBank", mineBankList.get(position));
            LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(data);
            activity.finish();
        } else
            new SelectorPW(mBinding.getRoot(), selectorList, position1 -> removeBankCard(position));
    }

    @Override
    public void removeBankCard(int position) {
        MineBank mineBank = mineBankList.get(position);
        removeBankCardApi api = new removeBankCardApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                mineBankList.remove(position);
                adapter.notifyDataSetChanged();
            }
        }, activity).setBankAccountId(mineBank.getId());
        api.setDialogTitle(mineBank.getBankType() == 1 ? "删除银行卡" : "删除" + mineBank.getBankName() + "账号");
        HttpManager.getInstance().doHttpDeal(api);
    }
}
