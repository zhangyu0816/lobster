package com.zb.module_camera.iv;

import android.view.View;

public interface PhotoStudioVMInterface {
    void changeLightIndex(View view);

    void changeCameraId(View view);

    void changeZoomUp(View view);

    void changeZoomDown(View view);

    void selectFilm(int index);

    void setFilm(View view);

    void wash(View view);

    void tackPhoto(View view);

    void toPhotoWall(View view);

    void toPhotoGroup(View view);

    void toMyFilm(View view);

    void setBottom(View view);

    void findCameraFilms();

    void washResource();

    void cameraFilmMsCount();
}
