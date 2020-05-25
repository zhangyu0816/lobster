package com.zb.module_chat.iv;

import android.view.View;
import android.widget.ImageView;

import com.zb.lib_base.model.HistoryMsg;

import java.io.File;

public interface ChatVMInterface {

    void otherInfo();

    void historyMsgList(int pageNo);

    void thirdHistoryMsgList(int pageNo);

    void toDetail(View view);

    void toImageVideo(HistoryMsg historyMsg);

    void toVoice(ImageView view, HistoryMsg historyMsg, int direction);

    void toVoiceKeyboard(View view);

    void toCamera(View view);

    void toEmoji(View view);

    void stopPlayer();

    void uploadSound(File file,int resTime);
}
