package com.zb.lib_base.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.app.MineApp;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public abstract class BaseReceiver extends BroadcastReceiver {

    public RxAppCompatActivity activity;

    public BaseReceiver(RxAppCompatActivity activity, String name) {
        this.activity = activity;
        LocalBroadcastManager.getInstance(MineApp.sContext).registerReceiver(this, new IntentFilter(name));
    }

    @Override
    public abstract void onReceive(Context context, Intent intent);

    public void unregisterReceiver() {
        LocalBroadcastManager.getInstance(MineApp.sContext).unregisterReceiver(this);
    }
}
