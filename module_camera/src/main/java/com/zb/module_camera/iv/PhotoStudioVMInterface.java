package com.zb.module_camera.iv;

import android.view.View;

public interface PhotoStudioVMInterface {
    void changeLightIndex(View view);

    void changeCameraId(View view);

    void changeZoomUp(View view);

    void changeZoomDown(View view);

    void selectFilm(int index);

    void tackPhoto(View view);

}
