package com.zb.module_card.iv;

import android.view.View;

import com.zb.lib_base.model.PairInfo;
import com.zb.module_card.adapter.CardAdapter;

public interface CardVMInterface {

    void selectCard(View currentView, int position);

    void returnView(View view);

    void superLike(View currentView, PairInfo pairInfo);

    void exposure(View view);

    void leftBtn(View currentView, CardAdapter adapter, int position);

    void rightBtn(View currentView, CardAdapter adapter, int position);

    void selectCity(View view);

    void prePairList(boolean needProgress);

    void makeEvaluate(PairInfo pairInfo, int likeOtherStatus);

}
