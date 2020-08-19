package com.yimi.rentme.iv;

import com.alibaba.mobileim.conversation.YWMessage;

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

    void systemChat();

    void chatList();

    void driftBottleChatList();

    void contactNum(boolean isUpdate);

    void noReadBottleNum(boolean isUpdate);

    void otherInfo(long otherUserId,  YWMessage ywMessage);

    void recommendRankingList();
}
