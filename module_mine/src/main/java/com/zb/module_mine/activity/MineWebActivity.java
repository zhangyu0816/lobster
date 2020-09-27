package com.zb.module_mine.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.windows.FunctionPW;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.databinding.MineWebBinding;
import com.zb.module_mine.vm.MineWebViewModel;

import java.lang.reflect.Method;

@Route(path = RouteUtils.Mine_Web)
public class MineWebActivity extends MineBaseActivity {
    @Autowired(name = "title")
    String title = "";
    @Autowired(name = "url")
    String url = "";

    private MineWebBinding binding;
    private MineWebViewModel viewModel;
    private BaseReceiver openPartnerReceiver;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.webView.destroy();
        try {
            openPartnerReceiver.unregisterReceiver();
        } catch (Exception ignored) {
        }
    }

    @Override
    public int getRes() {
        return R.layout.mine_web;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void initUI() {
        viewModel = new MineWebViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.title, title);

        binding = (MineWebBinding) mBinding;
        WebSettings webSettings = binding.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        setZoomControlGoneX(binding.webView.getSettings(), new Object[]{false});
        binding.webView.loadUrl(url);
        binding.webView.removeJavascriptInterface("searchBoxJavaBridge_");
        binding.webView.removeJavascriptInterface("accessibilityTraversal");
        binding.webView.removeJavascriptInterface("accessibility");
        binding.webView.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                view.stopLoading();
                view.clearView();
                SCToastUtil.showToast(activity, "页面加载失败,请检查您的网络连接 !", true);
                finish();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("xg_register")) {
                    new FunctionPW(mBinding.getRoot(), MineApp.mineInfo.getImage().replace("YM0000", "430X430"), "邂逅不过一场梦",
                            "来虾菇，送你VIP，心动女生任你挑选", url, true, false, false, false, null);
                } else if (url.contains("xg_withdraw")) {
                    ActivityUtils.getMineWeb("返佣提现", url);
                } else if (url.contains("xg_openPartner")) {
                    viewModel.openMakePartner();
                } else
                    view.loadUrl(url);
                return true;
            }

            @Override
            public void onLoadResource(WebView view, String url) {
            }
        });
        binding.webView.setWebChromeClient(new WebChromeClient() {
            //网页加载进度
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    binding.progress.setVisibility(View.GONE);
                } else {
                    if (binding.progress.getVisibility() == View.GONE) {
                        binding.progress.setVisibility(View.VISIBLE);
                    }
                    binding.progress.setProgress(newProgress);
                }
            }
        });
        binding.webView.setDownloadListener((url, userAgent, contentDisposition, mimeType, contentLength) -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });

        openPartnerReceiver = new BaseReceiver(activity, "lobster_openPartner") {
            @Override
            public void onReceive(Context context, Intent intent) {
                binding.webView.loadUrl(url);
            }
        };
    }

    //通过反射隐藏webview的缩放按钮 适用于3.0和以后
    private void setZoomControlGoneX(WebSettings view, Object[] args) {
        Class classType = view.getClass();
        try {
            Class[] argsClass = new Class[args.length];

            for (int i = 0, j = args.length; i < j; i++) {
                argsClass[i] = args[i].getClass();
            }
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

    @Override
    // 设置回退
    // 覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && binding.webView.canGoBack()) {
            binding.webView.goBack(); // goBack()表示返回WebView的上一页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
