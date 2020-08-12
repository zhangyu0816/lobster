package com.zb.module_home.iv;

import com.zb.lib_base.model.DiscoverInfo;

public interface VideoListVMInterface {

    void videoPlay(DiscoverInfo discoverInfo);

    void dynPiazzaList();

    void toMemberDetail(DiscoverInfo discoverInfo);

    void toReviews(int position);

    void doGood(int position);

    void doReward(DiscoverInfo discoverInfo);

    void follow(DiscoverInfo discoverInfo);

    void more(DiscoverInfo discoverInfo);
}
