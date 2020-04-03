package com.zb.module_camera;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.utils.RouteUtils;

@Route(path = RouteUtils.Camera_Main)
public class MainActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.CameraTheme);
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getRes() {
        return R.layout.camera_main;
    }

    @Override
    public void initUI() {

    }
}
