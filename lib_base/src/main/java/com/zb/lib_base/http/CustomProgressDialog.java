package com.zb.lib_base.http;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zb.lib_base.R;

import java.lang.ref.WeakReference;

import androidx.appcompat.app.AppCompatActivity;

public class CustomProgressDialog extends Dialog implements DialogInterface.OnCancelListener {

    private WeakReference<AppCompatActivity> mContext = new WeakReference<>(null);
    private volatile static CustomProgressDialog sDialog;

    private CustomProgressDialog(AppCompatActivity context, CharSequence message) {
        super(context, R.style.CustomProgressDialog);

        mContext = new WeakReference<>(context);

        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_custom_progress, null);
        TextView tvMessage = (TextView) view.findViewById(R.id.tv_message);
        if (!TextUtils.isEmpty(message)) {
            tvMessage.setText(message);
        }
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addContentView(view, lp);

        setOnCancelListener(this);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        // 点手机返回键等触发Dialog消失，应该取消正在进行的网络请求等
        AppCompatActivity context = mContext.get();
        if (context != null) {
//            MyHttpClient.cancelRequests(context);
        }
    }

    public static synchronized void showLoading(AppCompatActivity context) {
        showLoading(context, "loading...");
    }

    public static synchronized void showLoading(AppCompatActivity context, CharSequence message) {
        showLoading(context, message, true);
    }

    public static synchronized void showLoading(AppCompatActivity context, CharSequence message, boolean cancelable) {
        if (sDialog != null && sDialog.isShowing()) {
            sDialog.dismiss();
        }

        if (context == null || !(context instanceof AppCompatActivity)) {
            return;
        }
        sDialog = new CustomProgressDialog(context, message);
        sDialog.setCancelable(cancelable);

        if (sDialog != null && !sDialog.isShowing() && !((AppCompatActivity) context).isFinishing()) {
            sDialog.show();
        }
    }

    public static synchronized void stopLoading() {
        if (sDialog != null && sDialog.isShowing()) {
            sDialog.dismiss();
        }
        sDialog = null;
    }
}
