package com.zb.module_mine.vm;

import android.view.View;

import com.zb.lib_base.model.Reward;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.R;
import com.zb.module_mine.adapter.MineAdapter;
import com.zb.module_mine.iv.GiftNewsVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.ViewDataBinding;

public class GiftNewsViewModel extends BaseViewModel implements GiftNewsVMInterface {
    public MineAdapter adapter;
    private List<Reward> rewardList = new ArrayList<>();

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        setAdapter();
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void setAdapter() {
        adapter = new MineAdapter<>(activity, R.layout.item_mine_gift_news, rewardList, this);
    }
}
