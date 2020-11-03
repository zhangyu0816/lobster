package com.zb.module_mine.vm;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.markProductListApi;
import com.zb.lib_base.api.myBankCardsApi;
import com.zb.lib_base.api.myInfoApi;
import com.zb.lib_base.api.openMakePartnerApi;
import com.zb.lib_base.api.payOrderForTranShareApi;
import com.zb.lib_base.api.realNameVerifyApi;
import com.zb.lib_base.api.shareChangeCashApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.CustomProgressDialog;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.Authentication;
import com.zb.lib_base.model.MineBank;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.model.OrderNumber;
import com.zb.lib_base.model.OrderTran;
import com.zb.lib_base.model.ShareProduct;
import com.zb.lib_base.model.WebShare;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.utils.ShareUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.CashPW;
import com.zb.lib_base.windows.FunctionPW;
import com.zb.lib_base.windows.PaymentPW;
import com.zb.lib_base.windows.TextPW;
import com.zb.module_mine.databinding.MineWebBinding;
import com.zb.module_mine.iv.MineWebVMInterface;
import com.zb.module_mine.views.CodeLayout;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.List;

import androidx.databinding.ViewDataBinding;

public class MineWebViewModel extends BaseViewModel implements MineWebVMInterface {
    private MineWebBinding mBinding;
    private long makeProductId;
    private double openMoney;
    private BaseReceiver addBankReceiver;
    private BaseReceiver openPartnerReceiver;
    private long bankAccountId;
    private double mMoney;
    public String url = "";
    private DecimalFormat df;

    private WebShare mWebShare;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (MineWebBinding) binding;
        df = new DecimalFormat("#####0.0");
        if (url.contains("shareSign"))
            addBankReceiver = new BaseReceiver(activity, "lobster_addBank") {
                @Override
                public void onReceive(Context context, Intent intent) {
                    myBankCards();
                }
            };
        if (url.contains("share.html")) {
            markProductList();
            openPartnerReceiver = new BaseReceiver(activity, "lobster_openPartner") {
                @Override
                public void onReceive(Context context, Intent intent) {
                    mBinding.webView.loadUrl(url);
                    myInfo();
                }
            };
        }

        init();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        mWebShare = new WebShare();

        WebSettings webSettings = mBinding.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        setZoomControlGoneX(mBinding.webView.getSettings());
        mBinding.webView.loadUrl(url);
        mBinding.webView.removeJavascriptInterface("searchBoxJavaBridge_");
        mBinding.webView.removeJavascriptInterface("accessibilityTraversal");
        mBinding.webView.removeJavascriptInterface("accessibility");
        mBinding.webView.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                view.stopLoading();
                view.clearView();
                SCToastUtil.showToast(activity, "页面加载失败,请检查您的网络连接 !", true);
                activity.finish();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String mUrl) {
                if (mUrl.contains("shareRegister")) {
                    new FunctionPW(mBinding.getRoot(), MineApp.mineInfo.getImage().replace("YM0000", "430X430"), "邂逅不过一场梦",
                            "来虾菇，送你VIP，心动女生任你挑选", mUrl + "?superUserId=" + BaseActivity.userId, true, false, false, false, null);
                } else if (mUrl.contains("shareSign")) {
                    if (url.contains("shareSign")) {
                        activity.finish();
                    }
                    ActivityUtils.getMineWeb("邀请好友赚钱", mUrl + "?userId=" + BaseActivity.userId + "&sessionId=" + BaseActivity.sessionId +
                            "&pfDevice=Android&pfAppType=203&pfAppVersion=" + MineApp.versionName);
                } else if (mUrl.contains("Share_faceToFacePage")) {
                    ActivityUtils.getMineWeb("邀请好友赚钱", mUrl);
                } else if (mUrl.contains("xg_openPartner")) {
                    new TextPW(mBinding.getRoot(), "开通合伙人", "成为虾菇合伙人享受以下特权：\n  1，送一年VIP会员特权。\n  2，邀请新用户使用虾菇可以享受佣金。\n  3，本活动最终解释权归虾菇所有。",
                            "￥" + df.format(openMoney) + " 确认开通", () -> openMakePartner());
                } else if (mUrl.contains("xg_changeCash_")) {
                    String money = mUrl.substring(mUrl.indexOf("xg_changeCash_")).replace("xg_changeCash_", "");
                    if (!money.isEmpty()) {
                        mMoney = Double.parseDouble(df.format(Double.parseDouble(money)));
                    }
                    CustomProgressDialog.showLoading(activity, "提现处理中");
                    realNameVerify();
                } else if (mUrl.contains("qqshare:")) {
                    share(mUrl.replace("qqshare:", ""), "qqshare");
                } else if (mUrl.contains("wxshare:")) {
                    share(mUrl.replace("wxshare:", ""), "wxshare");
                } else if (mUrl.contains("contentcopy:")) {
                    share(mUrl.replace("contentcopy:", ""), "contentcopy");
                } else if (mUrl.contains("wxfriend:")) {
                    share(mUrl.replace("wxfriend:", ""), "wxfriend");
                } else if (mUrl.contains("shareimage:")) {
                    share(mUrl.replace("shareimage:", ""), "shareimage");
                } else
                    view.loadUrl(mUrl);
                return true;
            }

            @Override
            public void onLoadResource(WebView view, String url) {
            }
        });
        mBinding.webView.setWebChromeClient(new WebChromeClient() {
            //网页加载进度
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    mBinding.progress.setVisibility(View.GONE);
                } else {
                    if (mBinding.progress.getVisibility() == View.GONE) {
                        mBinding.progress.setVisibility(View.VISIBLE);
                    }
                    mBinding.progress.setProgress(newProgress);
                }
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                result.confirm();
                SCToastUtil.showToast(activity, message, true);
                return true;
            }
        });
        mBinding.webView.setDownloadListener((url, userAgent, contentDisposition, mimeType, contentLength) -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse(url));
            activity.startActivity(intent);
        });
    }

    public void onDestroy() {
        try {
            if (addBankReceiver != null)
                addBankReceiver.unregisterReceiver();
            if (openPartnerReceiver != null)
                openPartnerReceiver.unregisterReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mBinding.webView.destroy();
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    private void share(String jsonStr, String type) {
        try {
            String share = URLDecoder.decode(jsonStr, "utf-8");
            if (TextUtils.equals("contentcopy", type))
                copy(share);
            else {
                JSONObject object = new JSONObject(share);
                if (object.has("imgUrl"))
                    mWebShare.setImgUrl(object.optString("imgUrl"));
                if (object.has("url"))
                    mWebShare.setUrl(object.optString("url"));
                if (object.has("shareTitle"))
                    mWebShare.setShareTitle(object.optString("shareTitle"));
                if (object.has("title"))
                    mWebShare.setTitle(object.optString("title"));
                if (object.has("desc"))
                    mWebShare.setDesc(object.optString("desc"));
                if (object.has("contentType"))
                    mWebShare.setContentType(object.optInt("contentType"));
                if (object.has("coorX"))
                    mWebShare.setCoorX(object.optInt("coorX"));
                if (object.has("coorY"))
                    mWebShare.setCoorY(object.optInt("coorY"));
                if (object.has("qrCodeW"))
                    mWebShare.setQrCodeW(object.optInt("qrCodeW"));
                if (object.has("qrCodeH"))
                    mWebShare.setQrCodeH(object.optInt("qrCodeH"));
                if (TextUtils.equals("shareimage", type)) {
                    getPermissions();
                } else
                    ShareUtil.share(activity, mWebShare.getImgUrl(), mWebShare.getShareTitle(), mWebShare.getDesc(), mWebShare.getUrl(), type);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 权限
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            performCodeWithPermission("虾菇需要访问读写外部存储权限", new BaseActivity.PermissionCallback() {
                @Override
                public void hasPermission() {
                    setPermissions();
                }

                @Override
                public void noPermission() {
                }
            }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else {
            setPermissions();
        }
    }

    private void setPermissions() {
        CodeLayout codeLayout = new CodeLayout(activity);
        codeLayout.setData(mWebShare);
    }

    @Override
    public void myInfo() {
        myInfoApi api = new myInfoApi(new HttpOnNextListener<MineInfo>() {
            @Override
            public void onNext(MineInfo o) {
                MineApp.mineInfo = o;
            }
        }, activity);
        new Handler().postDelayed(() -> HttpManager.getInstance().doHttpDeal(api), 500);
    }

    @Override
    public void markProductList() {
        markProductListApi api = new markProductListApi(new HttpOnNextListener<List<ShareProduct>>() {
            @Override
            public void onNext(List<ShareProduct> o) {
                for (ShareProduct item : o) {
                    if (item.getMarkeType() == 1) {
                        makeProductId = item.getId();
                        openMoney = item.getPrice();
                    }
                }
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void openMakePartner() {
        if (makeProductId == 0) {
            return;
        }
        openMakePartnerApi api = new openMakePartnerApi(new HttpOnNextListener<OrderNumber>() {
            @Override
            public void onNext(OrderNumber o) {
                payOrderForTranShare(o.getOrderNumber());
            }
        }, activity).setMakeProductId(makeProductId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void payOrderForTranShare(String orderNumber) {
        payOrderForTranShareApi api = new payOrderForTranShareApi(new HttpOnNextListener<OrderTran>() {
            @Override
            public void onNext(OrderTran o) {
                new PaymentPW(mBinding.getRoot(), o, 3);
            }
        }, activity).setOrderNumber(orderNumber);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void realNameVerify() {
        realNameVerifyApi api = new realNameVerifyApi(new HttpOnNextListener<Authentication>() {
            @Override
            public void onNext(Authentication o) {
                if (o.getIsChecked() == 1) {
                    myBankCards();
                } else {
                    CustomProgressDialog.stopLoading();
                    if (o.getIsChecked() == 0 || o.getIsChecked() == 100) {
                        new TextPW(mBinding.getRoot(), "实名认证", "实名认证还在审核中，请稍后再试！");
                    } else {
                        new TextPW(mBinding.getRoot(), "实名认证", "你的实名认证审核失败，请前往查看失败原因并重新提交！", "重新认证", () -> ActivityUtils.getMineAuthentication(o));
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                CustomProgressDialog.stopLoading();
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    new TextPW(mBinding.getRoot(), "实名认证", "你还未实名认证无法提现,请前往提交实名认证信息！", "去认证", () -> ActivityUtils.getMineAuthentication(new Authentication()));
                }
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void myBankCards() {
        myBankCardsApi api = new myBankCardsApi(new HttpOnNextListener<List<MineBank>>() {
            @Override
            public void onNext(List<MineBank> o) {
                new CashPW(mBinding.getRoot(), o.get(0), mMoney, o, mineBank -> {
                    bankAccountId = mineBank.getId();
                    shareChangeCash();
                });
            }

            @Override
            public void onError(Throwable e) {
                CustomProgressDialog.stopLoading();
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    ActivityUtils.getMineBindingBank();
                }
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void shareChangeCash() {
        shareChangeCashApi api = new shareChangeCashApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                CustomProgressDialog.stopLoading();
                SCToastUtil.showToast(activity, "已提交提现信息", true);
                mBinding.webView.reload();
            }

            @Override
            public void onError(Throwable e) {
                CustomProgressDialog.stopLoading();
            }
        }, activity).setMoney(mMoney).setBankAccountId(bankAccountId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    //通过反射隐藏webview的缩放按钮 适用于3.0和以后
    private void setZoomControlGoneX(WebSettings view) {
        Class classType = view.getClass();
        try {
            Method[] ms = classType.getMethods();
            for (Method m : ms) {
                if (m.getName().equals("setDisplayZoomControls")) {
                    try {
                        m.invoke(view, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 实现文本复制功能
     *
     * @param content 要复制的内容
     */

    private void copy(String content) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) activity
                .getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
    }
}
