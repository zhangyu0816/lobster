package com.zb.module_home.vm;

import android.Manifest;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;

import com.maning.imagebrowserlibrary.MNImage;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.api.comsubApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DataCleanManager;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.utils.uploadImage.PhotoManager;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_home.R;
import com.zb.module_home.adapter.HomeAdapter;
import com.zb.module_home.databinding.HomeReportBinding;
import com.zb.module_home.iv.ReportVMInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.databinding.ViewDataBinding;

public class ReportViewModel extends BaseViewModel implements ReportVMInterface {
    public long otherUserId;
    public HomeAdapter adapter;
    public HomeAdapter imageAdapter;
    public List<String> images = new ArrayList<>();
    private PhotoManager photoManager;
    private int prePosition = -1;
    private HomeReportBinding mBinding;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (HomeReportBinding) binding;
        setAdapter();
        photoManager = new PhotoManager(activity, () -> {
            comsub(photoManager.jointWebUrl(","));
        });
    }

    @Override
    public void back(View view) {
        super.back(view);
        MineApp.selectMap.clear();
        MineApp.selectPathMap.clear();
        MineApp.cutImageViewMap.clear();
        DataCleanManager.deleteFile(new File(activity.getCacheDir(), "images"));
        activity.finish();
    }

    @Override
    public void right(View view) {
        super.right(view);
        if (prePosition == -1) {
            SCToastUtil.showToast(activity, "请选择举报类型", true);
            return;
        }
        if (mBinding.getContent().isEmpty()) {
            SCToastUtil.showToast(activity, "请填写举报理由", true);
            return;
        }
        if (images.size() == 1) {
            SCToastUtil.showToast(activity, "请上传图片证据", true);
            return;
        }

        List<String> imageList = new ArrayList<>();
        for (String url : images) {
            if (!TextUtils.equals(url, "add_image_icon")) {
                imageList.add(url);
            }
        }
        photoManager.addFiles(imageList, () -> photoManager.reUploadByUnSuccess());
    }

    @Override
    public void selectPosition(int position) {
        super.selectPosition(position);
        if (prePosition != position) {
            adapter.setSelectIndex(position);
            if (prePosition != -1) {
                adapter.notifyItemChanged(prePosition);
            }
            adapter.notifyItemChanged(position);
            prePosition = position;
        }
    }

    @Override
    public void setAdapter() {
        adapter = new HomeAdapter<>(activity, R.layout.item_report, MineApp.reportList, this);
        images.add("add_image_icon");
        imageAdapter = new HomeAdapter<>(activity, R.layout.item_report_image, images, this);
    }

    @Override
    public void previewImage(int position) {
        if (position == images.size() - 1) {
            getPermissions();
        } else {
            ArrayList<String> imageList = new ArrayList<>();
            for (int i = 0; i < images.size() - 1; i++) {
                imageList.add(images.get(i));
            }
            MNImage.imageBrowser(activity, mBinding.getRoot(), imageList, position, true, position12 -> {
                try {
                    int count = MineApp.selectMap.remove(MineApp.selectPathMap.get(images.get(position12)));
                    MineApp.cutImageViewMap.remove(MineApp.selectPathMap.get(images.get(position12)));
                    MineApp.selectPathMap.remove(images.get(position12));
                    for (Map.Entry<String, Integer> entry : MineApp.selectMap.entrySet()) {
                        if (entry.getValue() > count) {
                            MineApp.selectMap.put(entry.getKey(), entry.getValue() - 1);
                        }
                    }
                } catch (Exception e) {

                }
                imageAdapter.notifyItemRemoved(position12);
                images.remove(position12);
                imageAdapter.notifyDataSetChanged();
            });
        }
    }

    @Override
    public void comsub(String images) {
        comsubApi api = new comsubApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                photoManager.deleteAllFile();
                SCToastUtil.showToast(activity, "举报信息已提交，我们会审核后进行处理", true);
                back(null);
            }
        }, activity)
                .setComplainTypeId(MineApp.reportList.get(prePosition).getId())
                .setComText(mBinding.getContent())
                .setComUserId(otherUserId)
                .setImages(images);
        HttpManager.getInstance().doHttpDeal(api);
    }

    /**
     * 权限
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            performCodeWithPermission("虾菇需要访问读写外部存储权限及相机权限", new BaseActivity.PermissionCallback() {
                        @Override
                        public void hasPermission() {
                            setPermissions();
                        }

                        @Override
                        public void noPermission() {
                        }
                    }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO);
        } else {
            setPermissions();
        }
    }

    private void setPermissions() {
        ActivityUtils.getCameraMain(activity, true, false, false);
    }
}
