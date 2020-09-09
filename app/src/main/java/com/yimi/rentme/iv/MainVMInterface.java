package com.yimi.rentme.iv;

import com.zb.lib_base.imcore.CustomMessageBody;

public interface MainVMInterface {

    void selectPage(int index);

    void openedMemberPriceList();

    void walletAndPop();

    void newDynMsgAllNum(boolean isUpdate);

    void chatList();

    void driftBottleChatList();

    void contactNum(boolean isUpdate);

    void noReadBottleNum(boolean isUpdate);

    void otherInfo(long otherUserId, CustomMessageBody body, String msgId);

    void pushGoodUser();
}
