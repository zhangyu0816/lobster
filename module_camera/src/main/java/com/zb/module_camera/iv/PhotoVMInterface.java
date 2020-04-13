package com.zb.module_camera.iv;

import android.view.View;

public interface PhotoVMInterface {
    void cancel(View view);

    void reset(View view);

    void changeSizeIndex(int index);

    void changeLightIndex(int index);

    void changeCameraId(View view);

    void selectIndex(int index);

    void createPhoto(View view);

    void upload(View view);
}
