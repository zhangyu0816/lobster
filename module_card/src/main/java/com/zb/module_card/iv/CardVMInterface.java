package com.zb.module_card.iv;

import android.view.View;

import com.zb.lib_base.model.PairInfo;
import com.zb.module_card.adapter.CardAdapter;

public interface CardVMInterface {

    void selectCard(View currentView);

    void exposure(View view);

    void leftBtn(View currentView, CardAdapter adapter);

    void rightBtn(View currentView, CardAdapter adapter);

    void selectImage(int position);

    void selectCity(View view);

    void prePairList(boolean needProgress);

    void makeEvaluate(PairInfo pairInfo, int likeOtherStatus);

    void onRefresh(View view);

    void joinPairPool(String longitude, String latitude, long provinceId, long cityId, long districtId);
}
