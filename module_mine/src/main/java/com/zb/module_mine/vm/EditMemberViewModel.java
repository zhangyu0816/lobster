package com.zb.module_mine.vm;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.view.View;

import com.maning.imagebrowserlibrary.MNImage;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.api.modifyMemberInfoApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DataCleanManager;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.utils.SimpleItemTouchHelperCallback;
import com.zb.lib_base.utils.uploadImage.PhotoFile;
import com.zb.lib_base.utils.uploadImage.PhotoManager;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.BirthdayPW;
import com.zb.module_mine.R;
import com.zb.module_mine.adapter.MineAdapter;
import com.zb.module_mine.databinding.MineEditMemberBinding;
import com.zb.module_mine.iv.EditMemberVMInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.ItemTouchHelper;

public class EditMemberViewModel extends BaseViewModel implements EditMemberVMInterface {
    public MineAdapter adapter;
    public List<String> imageList = new ArrayList<>();
    public int _position = 0;
    private SimpleItemTouchHelperCallback callback;
    private MineEditMemberBinding mineEditMemberBinding;
    public MineInfo mineInfo;
    private PhotoManager photoManager;
    private String images = "";

    @Override
    public void back(View view) {
        super.back(view);
        DataCleanManager.deleteFile(new File(activity.getCacheDir(), "images"));
        activity.finish();
    }

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mineEditMemberBinding = (MineEditMemberBinding) binding;
        mineInfo = mineInfoDb.getMineInfo();
        if (mineInfo.getMoreImages().isEmpty()) {
            imageList.add(mineInfo.getImage());
        } else {
            String[] images = mineInfo.getMoreImages().split("#");
            imageList.addAll(Arrays.asList(images));
        }
        for (int i = imageList.size(); i < 6; i++) {
            imageList.add("");
        }

        setAdapter();
        photoManager = new PhotoManager(activity, () -> {
            for (PhotoFile photoFile : photoManager.getPhotoFiles()) {
                int i = imageList.indexOf(photoFile.getSrcFilePath());
                imageList.set(i, photoFile.getWebUrl());
            }

            modifyMemberInfo();
        });
    }

    @Override
    public void setAdapter() {
        adapter = new MineAdapter<>(activity, R.layout.item_mine_image, imageList, this);
        callback = new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mineEditMemberBinding.imagesList);
        callback.setSort(true);
        callback.setDragFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT);
    }


    @Override
    public void save(View view) {
        String images = "";
        String uploadImages = "";
        for (String image : imageList) {
            if (!image.isEmpty()) {
                images += "#" + image;
            }
            if (!image.isEmpty() && !image.contains("http://") && !image.contains("https://")) {
                uploadImages += "#" + image;
            }
        }
        if (images.isEmpty()) {
            SCToastUtil.showToast(activity, "请上传至少1张照片", true);
            return;
        }

        if (mineInfo.getPersonalitySign().isEmpty()) {
            SCToastUtil.showToast(activity, "请添加自己的个性签名", true);
            return;
        }
        if (uploadImages.isEmpty()) {
            modifyMemberInfo();
        } else {
            uploadImages = uploadImages.substring(1);
            photoManager.addFiles(Arrays.asList(uploadImages.split("#")), () -> photoManager.reUploadByUnSuccess());
        }
    }

    @Override
    public void selectImage(int position) {
        _position = position;
        if (imageList.get(position).isEmpty()) {
            getPermissions();
        } else {
            ArrayList<String> images = new ArrayList<>();
            for (String s : imageList) {
                if (!s.isEmpty()) {
                    images.add(s);
                }
            }
            MNImage.imageBrowser(activity, mBinding.getRoot(), images, position, true, position12 -> {
                adapter.notifyItemRemoved(position12);
                imageList.remove(position12);
                imageList.add("");
                adapter.notifyDataSetChanged();
            });
        }
    }

    @Override
    public void toEditNick(View view) {
        ActivityUtils.getMineEditContent(2, 1, "编辑名称", mineInfo.getNick(), "输入你的名称，名称不能为空");
    }

    @Override
    public void toSelectBirthday(View view) {
        new BirthdayPW(activity, mBinding.getRoot(), mineInfo.getBirthday(), birthday -> {
            mineInfoDb.updateContent(birthday, 5);
            mineInfo = mineInfoDb.getMineInfo();
            mBinding.setVariable(BR.viewModel, EditMemberViewModel.this);
        });
    }

    @Override
    public void toSelectJob(View view) {
        ActivityUtils.getMineSelectJob(mineInfo.getJob());
    }

    @Override
    public void toEditSign(View view) {
        ActivityUtils.getMineEditContent(3, 10, "编辑个性签名", mineInfo.getPersonalitySign(), "编辑个性签名...");
    }

    @Override
    public void toSelectTag(View view) {
        ActivityUtils.getMineSelectTag(mineInfo.getServiceTags());
    }

    @Override
    public void modifyMemberInfo() {
        images = "";
        for (String image : imageList) {
            if (!image.isEmpty()) {
                images += "#" + image;
            }
        }
        images = images.substring(1);
        modifyMemberInfoApi api = new modifyMemberInfoApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                SCToastUtil.showToast(activity, "个人信息提交成功", true);
                mineInfoDb.updateImages(images);
                activity.sendBroadcast(new Intent("lobster_updateMineInfo"));
                activity.finish();
            }
        }, activity)
                .setBirthday(mineInfo.getBirthday())
                .setImage(imageList.get(0))
                .setMoreImages(images)
                .setNick(mineInfo.getNick())
                .setJob(mineInfo.getJob())
                .setPersonalitySign(mineInfo.getPersonalitySign())
                .setSex(mineInfo.getSex())
                .setServiceTags(mineInfo.getServiceTags())
                .setProvinceId(areaDb.getProvinceId(PreferenceUtil.readStringValue(activity, "provinceName")))
                .setCityId(areaDb.getCityId(MineApp.cityName))
                .setDistrictId(areaDb.getDistrictId(PreferenceUtil.readStringValue(activity, "districtName")));
        HttpManager.getInstance().doHttpDeal(api);
    }

    /**
     * 权限
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            performCodeWithPermission("虾菇需要访问读外部存储权限", new BaseActivity.PermissionCallback() {
                @Override
                public void hasPermission() {
                    setPermissions();
                }

                @Override
                public void noPermission() {
                }
            }, Manifest.permission.READ_EXTERNAL_STORAGE);
        } else {
            setPermissions();
        }
    }

    private void setPermissions() {
        MineApp.toPublish = false;
        ActivityUtils.getCameraMain(activity, false, true, false);
    }
}
