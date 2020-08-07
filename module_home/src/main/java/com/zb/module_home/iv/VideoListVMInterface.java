package com.zb.module_home.iv;

import com.zb.lib_base.model.DiscoverInfo;

import androidx.databinding.ViewDataBinding;

public interface VideoListVMInterface {

    void dynPiazzaList();

    void toMemberDetail(ViewDataBinding binding, DiscoverInfo discoverInfo);

    void toReviews(int position);

    void doGood(int position);

    void doShare(DiscoverInfo discoverInfo);

    void doReward(DiscoverInfo discoverInfo);
}
