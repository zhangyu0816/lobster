package com.zb.lib_base.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public abstract class BaseContextReceiver extends BroadcastReceiver {

    public Context activity;

    public BaseContextReceiver(Context activity, String name) {
        this.activity = activity;
        activity.registerReceiver(this, new IntentFilter(name));
    }

    @Override
    public abstract void onReceive(Context context, Intent intent);

    public void unregisterReceiver() {
        activity.unregisterReceiver(this);
    }
}
