package com.zb.lobster.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.lobster.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView hello = findViewById(R.id.hello);
        hello.setOnClickListener(v -> {
            ARouter.getInstance().build(RouteUtils.Mine_Activity_Main).navigation();
        });
    }
}
