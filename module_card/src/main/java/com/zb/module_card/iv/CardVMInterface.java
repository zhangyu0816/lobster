package com.zb.module_card.iv;

import android.view.View;

import com.zb.lib_base.model.PairInfo;
import com.zb.module_card.adapter.CardAdapter;

public interface CardVMInterface {

    void selectCard(View currentView);

    void returnView(View view);

    void superLike(View currentView, PairInfo pairInfo);

    void exposure(View view);

    void leftBtn(View currentView, CardAdapter adapter);

    void rightBtn(View currentView, CardAdapter adapter);

    void selectImage(CardAdapter adapter, int position);

    void selectCity(View view);

    void prePairList(boolean needProgress);

    void makeEvaluate(PairInfo pairInfo, int likeOtherStatus);

    void onRefresh(View view);

}
