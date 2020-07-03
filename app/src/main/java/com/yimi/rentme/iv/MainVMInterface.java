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

    void newDynMsgAllNum();

    void systemChat();

    void chatList();

    void contactNum(boolean isUpdate);

    void noReadBottleNum(boolean isUpdate);

    void beSuperLikeList();

    void otherInfo(long otherUserId, int count, CustomMessageBody body);
}
