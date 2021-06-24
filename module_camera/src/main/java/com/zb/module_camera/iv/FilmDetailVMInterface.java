package com.zb.module_camera.iv;

import android.view.View;

public interface FilmDetailVMInterface {

    void toResourceDetail(long id);

    void toUserDetail(View view);

    void findCameraFilmsInfo();

    void findCameraFilmsResourceList();

    void otherInfo();
}
