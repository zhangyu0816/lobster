package com.zb.module_camera;

import android.os.Bundle;

import com.zb.lib_base.activity.BaseActivity;

public abstract class CameraBaseActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.CameraTheme);
        super.onCreate(savedInstanceState);
    }
}
