package com.zb.module_camera;

import android.view.View;

public interface CameraVMInterface {

    void selectImage(int position);

    void selectFileIndex(int position);

    void upload(View view);

    void selectTitle(View view);
}
