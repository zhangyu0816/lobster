package com.zb.module_camera.iv;

import android.view.View;

public interface PhotoWallVMInterface {

    void selectImage(int position, String image);

    void enlarge(int position, String image);

    void deleteImage(int position, String image);

    void wash(View view);
}
