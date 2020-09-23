package com.zb.lib_base.http;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.R;

public class CustomProgressDialog extends Dialog implements DialogInterface.OnCancelListener {

    private volatile static CustomProgressDialog sDialog;
    private static ObjectAnimator animator;

    private CustomProgressDialog(RxAppCompatActivity context, CharSequence message) {
        super(context, R.style.CustomProgressDialog);

        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_custom_progress, null);
        TextView tvMessage = view.findViewById(R.id.tv_message);
        ImageView progress = view.findViewById(R.id.progress);
        animator = ObjectAnimator.ofFloat(progress, "rotation", 0, 360).setDuration(700);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(Animation.INFINITE);
        if (!TextUtils.isEmpty(message)) {
            tvMessage.setText(message);
        }
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addContentView(view, lp);

        setOnCancelListener(this);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
    }

    public static synchronized void showLoading(RxAppCompatActivity context, CharSequence message) {
        showLoading(context, message, true);
    }

    public static synchronized void showLoading(RxAppCompatActivity context, CharSequence message, boolean cancelable) {
        if (sDialog != null && sDialog.isShowing()) {
            sDialog.dismiss();
        }

        if (context == null || !cancelable) {
            return;
        }
        sDialog = new CustomProgressDialog(context, message);
        sDialog.setCancelable(true);

        if (sDialog != null && !sDialog.isShowing() && !context.isFinishing()) {
            animator.start();
            sDialog.show();
        }
    }

    public static synchronized void stopLoading() {
        if (sDialog != null && sDialog.isShowing()) {
            if (animator != null)
                animator.cancel();
            sDialog.dismiss();
        }
        sDialog = null;
    }
}
