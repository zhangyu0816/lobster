package com.zb.module_mine.iv;

import android.view.View;

public interface MineVMInterface {

    void publishDiscover(View view);

    void openVip(View view);

    void openShare(int index);

    void toEditMember(View view);

    void toNews(View view);

    void toSetting(View view);

    void toReward(View view);

    void contactNumDetail(int position);
}
