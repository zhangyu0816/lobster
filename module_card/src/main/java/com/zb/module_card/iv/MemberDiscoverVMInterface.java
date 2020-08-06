package com.zb.module_card.iv;

import android.view.View;

public interface MemberDiscoverVMInterface {

    void entryBottle(View view);

    void dynPiazzaList();

    void personOtherDyn();

    void otherInfo();

    void onRefreshForNet(View view);

    void clickItem(int position);

    void doLike(int position);

    void dynDoLike();

    void dynCancelLike();
}
