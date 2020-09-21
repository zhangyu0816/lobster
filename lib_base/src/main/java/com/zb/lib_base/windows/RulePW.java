package com.zb.lib_base.windows;

import android.graphics.drawable.BitmapDrawable;
import android.text.Html;
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

import androidx.databinding.DataBindingUtil;

/**
 * Created by DIY on 2018-02-02.
 */

public class RulePW extends PopupWindow {

    private int type = 0;

    public RulePW(RxAppCompatActivity activity, View v, int type, CallBack callBack) {

        PwsRuleBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.pws_rule, null, false);
        View view = mBinding.getRoot();

        PopupWindow pw = new PopupWindow(view, LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT, true);
        pw.setBackgroundDrawable(new BitmapDrawable());
        pw.setContentView(view);
        pw.showAtLocation(v, Gravity.CENTER, 0, 0);
        pw.setOnDismissListener(() ->
                {
                    if (this.type == 0) {
                        callBack.cancelBack();
                    }
                }
        );
        mBinding.setPw(RulePW.this);
        if (type == 1) {
            // 注册
            mBinding.tvTitle.setText("注册协议及隐私政策");
            mBinding.tvContent.setText(Html.fromHtml("<span>" +
                    "<font color='#000000' size='26px'>在您注册成为虾菇用户的过程中，您需要完成我们的注册流程并通过点击同意的形式在线签署以下协议，</font>"
                    +
                    "<font color='#000000' size='26px'><b><u>请您务必仔细阅读、充分理解协议中的条款内容后再点击同意。</b></u></font>" +
                    "</span>"));

        } else if (type == 2) {
            mBinding.tvTitle.setText("虾菇服务隐私政策条款");
            mBinding.tvContent.setText(Html.fromHtml("<span>" +
                    "<font color='#000000' size='26px'>欢迎您使用虾菇！我们将通过《虾菇隐私政策》帮助您了解我们收集、使用、存储和共享个人信息的情况，了解您的相关权利。为了帮您保存下载的应用及识别设备、安全风险，我们需要申请设备权限和设备信息。如您同意，请点击下方按钮以接受我们的服务。</font>"
                    +
                    "<font color='#000000' size='26px'><b><u>请您务必仔细阅读、充分理解协议中的条款内容后再点击同意。</b></u></font>" +
                    "</span>"));
            mBinding.tvRule1.setVisibility(View.GONE);
        } else if (type == 3) {
            mBinding.tvTitle.setText("隐私政策及收集个人信息");
            mBinding.tvContent.setText(Html.fromHtml("<span>" +
                    "<font color='#000000' size='26px'>您在使用提现服务时，为了保障您的账户和资金安全，我们必须获取和使用您的姓名、身份证号、银行卡或支付宝账号。如您选择不提供上述信息，您可能无法使用提现服务。</font>"
                    +
                    "<font color='#000000' size='26px'><b><u>请您务必仔细阅读、充分理解协议中的条款内容后再点击同意。</b></u></font>" +
                    "</span>"));
            mBinding.tvRule1.setVisibility(View.GONE);
        }

        mBinding.tvRule1.setOnClickListener(v12 ->
                ActivityUtils.getMineWeb("注册协议", HttpManager.BASE_URL + "mobile/xiagu_reg_protocol.html"));

        mBinding.tvRule2.setOnClickListener(v12 ->
                ActivityUtils.getMineWeb("隐私政策", HttpManager.BASE_URL + "mobile/xiagu_privacy_protocol.html"));

        mBinding.tvSure.setOnClickListener(v1 -> {
            this.type = 1;
            callBack.sureBack();
            pw.dismiss();
        });
        mBinding.tvCancel.setOnClickListener(v1 -> {
            this.type = 0;
            pw.dismiss();
        });
    }

    public interface CallBack {
        void sureBack();

        void cancelBack();
    }
}
