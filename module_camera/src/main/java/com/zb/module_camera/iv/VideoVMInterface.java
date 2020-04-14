package com.zb.module_camera.iv;

import android.view.View;

public interface VideoVMInterface {

    void cancel(View view);

    void reset(View view);

    void upload(View view);

    void changeSizeIndex(int index);

    void changeCameraId(View view);

    void createRecorder(View view);

    void stopRecorder(View view);

    void selectIndex(int index);
}
