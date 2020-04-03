package com.zb.module_mine;

import android.os.Bundle;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;

import androidx.appcompat.app.AppCompatActivity;

@Route(path = RouteUtils.Mine_Main)
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_main);
        TextView mine = findViewById(R.id.mine);
        mine.setText("我的");
    }
}
