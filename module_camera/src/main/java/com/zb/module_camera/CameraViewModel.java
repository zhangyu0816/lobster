package com.zb.module_camera;

import android.view.View;

import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_camera.adapter.CameraAdapter;

import java.util.ArrayList;
import java.util.List;

public class CameraViewModel extends BaseViewModel implements CameraVMInterface {

    private CameraAdapter adapter;
    private List<String> images = new ArrayList<>();

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void setAdapter() {
        adapter = new CameraAdapter<>(activity, R.layout.item_camera, images, this);
    }

    @Override
    public void selectImage(View view) {

    }
}
