package com.zb.lib_base.windows;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.R;
import com.zb.lib_base.databinding.PwsRuleBinding;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.utils.ActivityUtils;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

/**
 * Created by DIY on 2018-02-02.
 */

public class RulePW extends PopupWindow {

    public RulePW(RxAppCompatActivity activity, View v, CallBack callBack) {

        PwsRuleBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.pws_rule, null, false);
        View view = mBinding.getRoot();

        PopupWindow pw = new PopupWindow(view, LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT, true);
        pw.setBackgroundDrawable(new BitmapDrawable());
        pw.setContentView(view);
        pw.showAtLocation(v, Gravity.CENTER, 0, 0);
        mBinding.setPw(RulePW.this);

        mBinding.tvTitle.setText("注册协议及隐私政策");

        SpannableString style = new SpannableString("欢迎您使用虾姑！虾姑是由温州专帮信息科技有限公司（以下简称“我们”）研发和运营的在线交友平台。" +
                "我们将通过《虾姑用户注册协议》和《虾姑隐私政策》帮助您了解我们收集、使用、存储和共享个人信息的情况，以及您所享有的相关权利。" +
                "\n 1.为了向您提供好友推荐、好友聊天、动态互动等服务，我们需要收集您的设备信息、好友偏好、通知设置等个人信息。" +
                "\n 2.您可以在我的--设置页面管理您的个人信息及您的授权。" +
                "\n 3.我们会采用业界领先的安全技术保护好您的个人信息。" +
                "\n\n\n" +
                "您可以通过阅读完整版《虾姑用户注册协议》和《虾姑隐私政策》了解详细信息。" +
                "\n 如您同意，请点击“同意”开始接受我们的服务。");

        style.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                ActivityUtils.getMineWeb("注册协议", HttpManager.BASE_URL + "mobile/xiagu_reg_protocol.html");
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setUnderlineText(false);
            }
        }, 52, 62, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                ActivityUtils.getMineWeb("隐私政策", HttpManager.BASE_URL + "mobile/xiagu_privacy_protocol.html");
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setUnderlineText(false);
            }
        }, 63, 71, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                ActivityUtils.getMineWeb("注册协议", HttpManager.BASE_URL + "mobile/xiagu_reg_protocol.html");
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setUnderlineText(false);
            }
        }, 236, 246, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                ActivityUtils.getMineWeb("隐私政策", HttpManager.BASE_URL + "mobile/xiagu_privacy_protocol.html");
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setUnderlineText(false);
            }
        }, 247, 255, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(new ForegroundColorSpan(Color.parseColor("#0d88c1")), 52, 62, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(new ForegroundColorSpan(Color.parseColor("#0d88c1")), 63, 71, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(new ForegroundColorSpan(Color.parseColor("#0d88c1")), 236, 246, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(new ForegroundColorSpan(Color.parseColor("#0d88c1")), 247, 255, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mBinding.tvContent.setText(style);
        mBinding.tvContent.setMovementMethod(LinkMovementMethod.getInstance());

        mBinding.tvSure.setOnClickListener(v1 -> {
            callBack.sureBack();
            pw.dismiss();
        });
        mBinding.tvCancel.setOnClickListener(v1 -> {
            callBack.cancelBack();
            pw.dismiss();
        });
    }

    public interface CallBack {
        void sureBack();

        void cancelBack();
    }
}
