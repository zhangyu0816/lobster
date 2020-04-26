package com.zb.module_home.vm;

import android.view.View;

import com.zb.lib_base.model.Reward;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_home.R;
import com.zb.module_home.adapter.HomeAdapter;
import com.zb.module_home.iv.RewardListVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.ViewDataBinding;


public class RewardListViewModel extends BaseViewModel implements RewardListVMInterface {

    public HomeAdapter adapter;
    private List<Reward>  rewardList = new ArrayList<>();

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        rewardList.add(new Reward());
        rewardList.add(new Reward());
        rewardList.add(new Reward());
        rewardList.add(new Reward());
        rewardList.add(new Reward());
        rewardList.add(new Reward());
        setAdapter();
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void setAdapter() {
        adapter = new HomeAdapter<>(activity, R.layout.item_home_reward_ranking,rewardList,this);
    }
}
