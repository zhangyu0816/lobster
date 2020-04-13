package com.zb.module_camera.iv;

import android.view.View;

public interface CameraVMInterface {

    void selectIndex(int index);

    void selectTitle(View view);

    void selectImage(int position);

    void selectImageByMore(int position);

    void selectFileIndex(int position);

    void upload(View view);

}
