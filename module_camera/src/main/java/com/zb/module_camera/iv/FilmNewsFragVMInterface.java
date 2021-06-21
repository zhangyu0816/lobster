package com.zb.module_camera.iv;

import com.zb.lib_base.model.FilmMsg;

public interface FilmNewsFragVMInterface {
    void toFilmResourceDetail(FilmMsg filmMsg,int position);

    void cameraFilmMsgList();

    void readCameraFilmMsg(long cameraFilmResourceReviewMsgId);
}
