package com.yimi.rentme.iv;

import com.zb.lib_base.imcore.CustomMessageBody;

public interface MainVMInterface {

    void selectPage(int index);

    void joinPairPool(String longitude, String latitude);

    void openedMemberPriceList();

    void giftList();

    void rechargeDiscountList();

    void bankInfoList();

    void comType();

    void walletAndPop();

    void newDynMsgAllNum();

    void systemChat();

    void chatList();

    void otherInfo(long otherUserId, int count, CustomMessageBody body);
}
