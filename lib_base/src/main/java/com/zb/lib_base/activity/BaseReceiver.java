package com.zb.lib_base.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

public abstract class BaseReceiver extends BroadcastReceiver {

    public RxAppCompatActivity activity;

    public BaseReceiver(RxAppCompatActivity activity, String name) {
        this.activity = activity;
        activity.registerReceiver(this, new IntentFilter(name));
    }

    @Override
    public abstract void onReceive(Context context, Intent intent);

    public void unregisterReceiver() {
        activity.unregisterReceiver(this);
    }
}
