package com.zb.module_home.vm;

import android.Manifest;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.api.comTypeApi;
import com.zb.lib_base.api.comsubApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.Report;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DataCleanManager;
import com.zb.lib_base.utils.MNImage;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.utils.uploadImage.PhotoManager;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.TextPW;
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
    private List<Report> reportList = new ArrayList<>();

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (HomeReportBinding) binding;
        setAdapter();
        photoManager = new PhotoManager(activity, () -> comsub(photoManager.jointWebUrl(",")));
    }

    @Override
    public void back(View view) {
        super.back(view);
        MineApp.selectMap.clear();
        DataCleanManager.deleteFile(new File(activity.getCacheDir(), "images"));
        activity.finish();
    }

    @Override
    public void right(View view) {
        super.right(view);
        if (prePosition == -1) {
            SCToastUtil.showToast(activity, "?????????????????????", true);
            return;
        }
        if (mBinding.getContent().isEmpty()) {
            SCToastUtil.showToast(activity, "?????????????????????", true);
            return;
        }
        if (images.size() == 1) {
            SCToastUtil.showToast(activity, "?????????????????????", true);
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
        adapter = new HomeAdapter<>(activity, R.layout.item_report, reportList, this);
        images.add("add_image_icon");
        imageAdapter = new HomeAdapter<>(activity, R.layout.item_report_image, images, this);
        comType();
    }

    @Override
    public void previewImage(int position) {
        if (position == images.size() - 1) {
            if (checkPermissionGranted(activity, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                setPermissions();
            } else {
                if (PreferenceUtil.readIntValue(activity, "photoPermission") == 0) {
                    PreferenceUtil.saveIntValue(activity, "photoPermission", 1);
                    new TextPW(activity, mBinding.getRoot(), "????????????",
                            "????????????????????????????????????????????????????????????????????????????????????????????????" +
                                    "\n 1?????????????????????--??????????????????????????????????????????" +
                                    "\n 2?????????????????????--???????????????????????????????????????????????????" +
                                    "\n 3????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????" +
                                    "\n 4??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????/?????????" +
                                    "\n 5????????????????????????????????????--??????--??????--????????????app????????????--??????--????????????--?????????????????????????????????????????????????????????",
                            "??????", false, true, this::getPermissions);
                } else {
                    if (!checkPermissionGranted(activity, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        SCToastUtil.showToast(activity, "??????????????????????????????????????????--??????--????????????--??????????????????", true);
                    } else if (!checkPermissionGranted(activity, Manifest.permission.CAMERA)) {
                        SCToastUtil.showToast(activity, "??????????????????????????????????????????--??????--????????????--??????????????????", true);
                    }
                }
            }
        } else {
            ArrayList<String> imageList = new ArrayList<>();
            for (int i = 0; i < images.size() - 1; i++) {
                imageList.add(images.get(i));
            }
            MNImage.imageBrowser(activity, mBinding.getRoot(), 0, imageList, position, true, position12 -> {
                try {
                    int count = MineApp.selectMap.remove(images.get(position12));
                    for (Map.Entry<String, Integer> entry : MineApp.selectMap.entrySet()) {
                        if (entry.getValue() > count) {
                            MineApp.selectMap.put(entry.getKey(), entry.getValue() - 1);
                        }
                    }
                } catch (Exception ignored) {

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
                SCToastUtil.showToast(activity, "??????????????????????????????????????????????????????", true);
                back(null);
            }
        }, activity)
                .setComplainTypeId(reportList.get(prePosition).getId())
                .setComText(mBinding.getContent())
                .setComUserId(otherUserId)
                .setImages(images);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void comType() {
        comTypeApi api = new comTypeApi(new HttpOnNextListener<List<Report>>() {
            @Override
            public void onNext(List<Report> o) {
                reportList.addAll(o);
                adapter.notifyDataSetChanged();
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    /**
     * ??????
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            performCodeWithPermission("?????????????????????????????????????????????", new BaseActivity.PermissionCallback() {
                        @Override
                        public void hasPermission() {
                            setPermissions();
                        }

                        @Override
                        public void noPermission() {
                            PreferenceUtil.saveIntValue(activity, "photoPermission", 1);
                        }
                    }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else {
            setPermissions();
        }
    }

    private void setPermissions() {
        MineApp.toPublish = false;
        ActivityUtils.getCameraMain(activity, true, false, false);
    }
}
