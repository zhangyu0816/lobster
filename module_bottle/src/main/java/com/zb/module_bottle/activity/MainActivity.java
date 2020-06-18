package com.zb.module_bottle.activity;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.lib_base.utils.StatusBarUtil;
import com.zb.module_bottle.BR;
import com.zb.module_bottle.R;
import com.zb.module_bottle.vm.BottleViewModel;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Route(path = RouteUtils.Bottle_Main)
public class MainActivity extends BottleBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.statusBarLightMode(this);
    }

    @Override
    public int getRes() {
        return R.layout.bottle_main;
    }

    @Override
    public void initUI() {
        fitComprehensiveScreen();
        BottleViewModel viewModel = new BottleViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        checkNetWork();
    }

    private void checkNetWork() {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url("http://www.baidu.com").build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    mBinding.setVariable(BR.isOutLine, true);
                }

                @Override
                public void onResponse(Call call, Response response) {
                    mBinding.setVariable(BR.isOutLine, false);
                }
            });
        } catch (Exception e) {
            mBinding.setVariable(BR.isOutLine, false);
        }
    }
}
