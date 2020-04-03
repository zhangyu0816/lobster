package com.zb.lobster.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lobster.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView hello = findViewById(R.id.hello);
        hello.setOnClickListener(v -> {
            ActivityUtils.getRegisterMain();
        });
    }
}
