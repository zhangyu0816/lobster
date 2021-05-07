package com.zb.lib_base.iv;

import android.view.View;

public interface MemberDiscoverVMInterface {

    void dynPiazzaList();

    void personOtherDyn();

    void otherInfo();

    void onRefreshForNet(View view);

    void clickItem(int position);

    void toMemberDetail(int position);

    void doLike(View view, int position);

    void dynDoLike();

    void dynCancelLike();
}
