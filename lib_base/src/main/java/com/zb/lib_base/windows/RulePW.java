package com.zb.lib_base.windows;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
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
import com.zb.lib_base.utils.SCToastUtil;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

/**
 * Created by DIY on 2018-02-02.
 */

public class RulePW extends PopupWindow {
    private String registerRule = "《虾菇用户注册协议》";
    private String privacyRule = "《虾菇隐私政策》";
    private String content = "";

    public RulePW(RxAppCompatActivity activity, View v, CallBack callBack) {

        PwsRuleBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.pws_rule, null, false);
        View view = mBinding.getRoot();

        PopupWindow pw = new PopupWindow(view, LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT, true);
        pw.setBackgroundDrawable(new BitmapDrawable());
        pw.setContentView(view);
        pw.showAtLocation(v, Gravity.CENTER, 0, 0);
        mBinding.setPw(RulePW.this);

        mBinding.setClickSelect(false);

        mBinding.tvTitle.setText("注册协议及隐私政策");
        mBinding.tvContent.setMovementMethod(ScrollingMovementMethod.getInstance());

        content = "欢迎您使用虾菇！虾菇是由温州专帮信息科技有限公司（以下简称“我们”）研发和运营的在线交友平台。" +
                "我们将通过" + registerRule + "和" + privacyRule + "帮助您了解我们收集、使用、存储和共享个人信息的情况，以及您所享有的相关权利。" +
                "\n 1.为了向您提供微信/QQ快捷登录功能、分享功能、微信支付功能，我们集成了友盟SDK。使用时，我们会获取设备信息（IMEI/Mac/android ID/IDFA/OPENUDID/GUID/SIM卡IMSI/地理位置信息）、存储、用户名称、头像及性别等内容。" +
                "\n 2.为了向您提供支付宝支付功能，我们集成了AlipaySDK。我们会获取设备信息（IMEI/Mac/android ID/IDFA/OPENUDID/GUID/SIM卡IMSI/地理位置信息）、存储等内容。" +
                "\n 3.为了向您提供推送功能，我们集成了个推SDK。使用时，我们会获取设备平台、设备厂商、设备品牌、设备识别码等设备信息，应用列表信息、网络信息以及位置相关信息。" +
                "\n 4.为了向您提供聊天功能，我们集成了小米MIMC即时通讯SDK。使用时，我们会获取网络访问、访问网络状态、访问WLAN状态、获取手机设备识别码 （如imei、imsi、idfa、android ID）、读取/修改储存权限，录音权限。" +
                "\n 5.为了向您提供地图功能，我们集成了高德SDK。使用时，我们会获取手机状态和身份、地理位置、存储卡内容。" +
                "\n 6.您可以在我的--设置页面管理您的个人信息及您的授权。" +
                "\n 7.我们会采用业界领先的安全技术保护好您的个人信息。" +
                "\n\n\n" +
                "您可以通过阅读完整版" + registerRule + "和" + privacyRule + "了解详细信息。" +
                "\n 如您同意，请点击“同意”开始接受我们的服务。";

        SpannableString style = new SpannableString(content);

        int rule1Start = content.indexOf(registerRule);
        int rule1End = rule1Start + registerRule.length();
        int rule2Start = content.indexOf(privacyRule);
        int rule2End = rule2Start + privacyRule.length();

        int rule1StartLast = content.lastIndexOf(registerRule);
        int rule1EndLast = rule1StartLast + registerRule.length();
        int rule2StartLast = content.lastIndexOf(privacyRule);
        int rule2EndLast = rule2StartLast + privacyRule.length();

        style.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                callBack.registerRule();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setUnderlineText(false);
            }
        }, rule1Start, rule1End, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        style.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                callBack.privacyRule();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setUnderlineText(false);
            }
        }, rule2Start, rule2End, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        style.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                callBack.registerRule();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setUnderlineText(false);
            }
        }, rule1StartLast, rule1EndLast, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        style.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                callBack.privacyRule();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setUnderlineText(false);
            }
        }, rule2StartLast, rule2EndLast, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(new ForegroundColorSpan(Color.parseColor("#0d88c1")), rule1Start, rule1End, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(new ForegroundColorSpan(Color.parseColor("#0d88c1")), rule2Start, rule2End, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(new ForegroundColorSpan(Color.parseColor("#0d88c1")), rule1StartLast, rule1EndLast, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(new ForegroundColorSpan(Color.parseColor("#0d88c1")), rule2StartLast, rule2EndLast, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mBinding.tvContent.setText(style);
        mBinding.tvContent.setMovementMethod(LinkMovementMethod.getInstance());

        mBinding.tvSure.setOnClickListener(v1 -> {
            if (!mBinding.getClickSelect()) {
                SCToastUtil.showToast(activity, "请仔细阅读底部协议，并勾选", true);
                return;
            }
            callBack.sureBack();
            pw.dismiss();
        });
        mBinding.tvCancel.setOnClickListener(v1 -> {
            callBack.cancelBack();
            pw.dismiss();
        });

        SpannableString style1 = new SpannableString("请阅读《用户注册协议》和《隐私政策》并勾选");

        style1.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                callBack.registerRule();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setUnderlineText(false);
            }
        }, 3, 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style1.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                callBack.privacyRule();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setUnderlineText(false);
            }
        }, 12, 18, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style1.setSpan(new ForegroundColorSpan(Color.parseColor("#0d88c1")), 3, 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style1.setSpan(new ForegroundColorSpan(Color.parseColor("#0d88c1")), 12, 18, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mBinding.tvClick.setText(style1);
        mBinding.tvClick.setMovementMethod(LinkMovementMethod.getInstance());

        mBinding.clickLinear.setOnClickListener(v1 -> mBinding.setClickSelect(!mBinding.getClickSelect()));
    }

    public interface CallBack {
        void sureBack();

        void cancelBack();

        void registerRule();

        void privacyRule();
    }
}
