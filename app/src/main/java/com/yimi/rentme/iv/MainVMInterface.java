package com.yimi.rentme.iv;

import com.zb.lib_base.imcore.CustomMessageBody;

public interface MainVMInterface {

    void selectPage(int index);

    void joinPairPool(String longitude, String latitude, long provinceId, long cityId, long districtId);

    void openedMemberPriceList();

    void giftList();

    void rechargeDiscountList();

    void bankInfoList();

    void comType();

    void walletAndPop();

    void newDynMsgAllNum(boolean isUpdate);


    void chatList();

    void driftBottleChatList();

    void contactNum(boolean isUpdate);

    void noReadBottleNum(boolean isUpdate);

    void otherInfo(long otherUserId, CustomMessageBody body, String msgId);

    void recommendRankingList();

    void pushGoodUser();
}
