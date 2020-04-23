package com.zb.module_mine.fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseFragment;
import com.zb.lib_base.adapter.FragmentAdapter;
import com.zb.lib_base.utils.FragmentUtils;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.databinding.MineFragBinding;
import com.zb.module_mine.vm.MineViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;

@Route(path = RouteUtils.Mine_Fragment)
public class MineFragment extends BaseFragment {
    private MineViewModel viewModel;
    private MineFragBinding binding;
    private List<Fragment> fragments = new ArrayList<>();

    @Override
    public int getRes() {
        return R.layout.mine_frag;
    }

    @Override
    public void initUI() {
        viewModel = new MineViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        binding = (MineFragBinding) mBinding;
        initFragments();
    }

    private void initFragments() {
        fragments.clear();
        fragments.add(FragmentUtils.getCardMemberDiscoverFragment(10001));
        fragments.add(FragmentUtils.getCardMemberVideoFragment(10002));
        binding.viewPage.setAdapter(new FragmentAdapter(getChildFragmentManager(), fragments));
        viewModel.initTabLayout(new String[]{"动态", "小视频"}, binding.tabLayout, binding.viewPage, R.color.black_252, R.color.black_827);
    }
}
