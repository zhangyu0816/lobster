package com.zb.lib_base.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.zb.lib_base.app.MineApp;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public abstract class BaseContextReceiver extends BroadcastReceiver {

    public Context activity;

    public BaseContextReceiver(Context activity, String name) {
        this.activity = activity;
        LocalBroadcastManager.getInstance(MineApp.sContext).registerReceiver(this, new IntentFilter(name));
    }

    @Override
    public abstract void onReceive(Context context, Intent intent);

    public void unregisterReceiver() {
        LocalBroadcastManager.getInstance(MineApp.sContext).unregisterReceiver(this);
    }
}
