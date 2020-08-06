package com.zb.module_home.windows;

import android.view.View;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.utils.DownLoad;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.windows.BasePopupWindow;
import com.zb.module_home.R;
import com.zb.module_home.databinding.PwsDownloadBinding;

public class DownloadPW extends BasePopupWindow {
    private PwsDownloadBinding binding;
    private String fileUrl;

    public DownloadPW(RxAppCompatActivity activity, View parentView, String fileUrl) {
        super(activity, parentView, false);
        this.fileUrl = fileUrl;
        initUI();
    }

    @Override
    public int getRes() {
        return R.layout.pws_download;
    }

    @Override
    public void initUI() {
        binding = (PwsDownloadBinding) mBinding;
        binding.setPw(this);
        binding.setProgress("下载中（0.00%）");
        DownLoad.downloadLocation(fileUrl, new DownLoad.CallBack() {
            @Override
            public void success(String filePath) {
                SCToastUtil.showToast(activity,"下载成功",true);
                dismiss();
            }

            @Override
            public void onLoading(long total, long current) {
                binding.setProgress(String.format("下载中（%.2f", (float) current / (float) total) + "%）");
                binding.progress.setMax((int) total);
                binding.progress.setProgress((int) current);
            }
        });
    }

    @Override
    public void cancel(View view) {
        super.cancel(view);
        DownLoad.stop();
        dismiss();
    }
}
