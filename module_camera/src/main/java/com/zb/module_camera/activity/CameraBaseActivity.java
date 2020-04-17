package com.zb.module_camera.activity;

import android.os.Bundle;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.module_camera.R;

public abstract class CameraBaseActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.CameraTheme);
        super.onCreate(savedInstanceState);
    }
}
