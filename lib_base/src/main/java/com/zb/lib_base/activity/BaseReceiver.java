package com.zb.lib_base.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.appcompat.app.AppCompatActivity;


/**
 * @author DIY
 * @date 2019-03-13
 */

public abstract class BaseReceiver extends BroadcastReceiver {

    public AppCompatActivity activity;

    public BaseReceiver(AppCompatActivity activity, String name) {
        this.activity = activity;
        activity.registerReceiver(this, new IntentFilter(name));
    }

    @Override
    public abstract void onReceive(Context context, Intent intent);

    public void unregisterReceiver() {
        try {
            activity.unregisterReceiver(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface CallBack {
        void success(Intent intent);
    }
}