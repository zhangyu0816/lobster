package com.zb.module_mine.vm;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.view.View;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.api.checkFaceApi;
import com.zb.lib_base.api.modifyMemberInfoApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.AreaDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DataCleanManager;
import com.zb.lib_base.utils.MNImage;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.utils.SimpleItemTouchHelperCallback;
import com.zb.lib_base.utils.uploadImage.PhotoFile;
import com.zb.lib_base.utils.uploadImage.PhotoManager;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.BirthdayPW;
import com.zb.lib_base.windows.TextPW;
import com.zb.module_mine.R;
import com.zb.module_mine.adapter.MineAdapter;
import com.zb.module_mine.databinding.MineEditMemberBinding;
import com.zb.module_mine.iv.EditMemberVMInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.ItemTouchHelper;

public class EditMemberViewModel extends BaseViewModel implements EditMemberVMInterface {
    public MineAdapter adapter;
    public List<String> imageList = new ArrayList<>();
    public int _position = 0;
    private MineEditMemberBinding mineEditMemberBinding;
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
        mineEditMemberBinding.setMineInfo(MineApp.mineInfo);
        if (MineApp.mineInfo.getMoreImages().isEmpty()) {
            imageList.add(MineApp.mineInfo.getImage());
        } else {
            String[] images = MineApp.mineInfo.getMoreImages().split("#");
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
            photoManager.deleteAllFile();
            modifyMemberInfo();
        });
    }

    @Override
    public void setAdapter() {
        adapter = new MineAdapter<>(activity, R.layout.item_mine_image, imageList, this);
        SimpleItemTouchHelperCallback callback = new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mineEditMemberBinding.imagesList);
        callback.setSort(true);
        callback.setDragFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT);
    }


    @Override
    public void save(View view) {
        StringBuilder images = new StringBuilder();
        StringBuilder uploadImages = new StringBuilder();
        for (String image : imageList) {
            if (!image.isEmpty()) {
                images.append("#").append(image);
            }
            if (!image.isEmpty() && !image.contains("http://") && !image.contains("https://")) {
                uploadImages.append("#").append(image);
            }
        }
        if (images.length() == 0) {
            SCToastUtil.showToast(activity, "请上传至少1张照片", true);
            return;
        }

        if (MineApp.mineInfo.getPersonalitySign().isEmpty()) {
            SCToastUtil.showToast(activity, "请添加自己的个性签名", true);
            return;
        }
        if (uploadImages.length() == 0) {
            modifyMemberInfo();
        } else {
            uploadImages = new StringBuilder(uploadImages.substring(1));
            photoManager.addFiles(Arrays.asList(uploadImages.toString().split("#")), () -> photoManager.reUploadByUnSuccess());
        }
    }

    @Override
    public void selectImage(int position) {
        _position = position;
        if (imageList.get(position).isEmpty()) {
            if (PreferenceUtil.readIntValue(activity, "cameraPermission") == 0)
                new TextPW(activity, mBinding.getRoot(), "权限说明",
                        "我们会以申请权限的方式获取设备功能的使用：" +
                                "\n 1、申请相机权限--获取照相功能，" +
                                "\n 2、申请存储权限--获取照册功能，" +
                                "\n 3、若你拒绝权限申请，仅无法使用上传头像功能，虾菇app其他功能不受影响，" +
                                "\n 4、可通过app内 我的--设置--权限管理 进行权限操作。",
                        "同意", false, true, new TextPW.CallBack() {
                    @Override
                    public void sure() {
                        PreferenceUtil.saveIntValue(activity, "cameraPermission", 1);
                        getPermissions1();
                    }

                    @Override
                    public void cancel() {
                        SCToastUtil.showToast(activity, "你未申请相机、存储权限，请前往我的--设置--权限管理--权限进行设置", true);
                        PreferenceUtil.saveIntValue(activity, "cameraPermission", 2);
                    }
                });
            else {
                if (!checkPermissionGranted(activity, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    SCToastUtil.showToast(activity, "你未申请存储权限，请前往我的--设置--权限管理--权限进行设置", true);
                    return;
                } else if (!checkPermissionGranted(activity, Manifest.permission.CAMERA)) {
                    SCToastUtil.showToast(activity, "你未申请相机权限，请前往我的--设置--权限管理--权限进行设置", true);
                    return;
                }
                getPermissions1();
            }
        } else {
            ArrayList<String> images = new ArrayList<>();
            for (String s : imageList) {
                if (!s.isEmpty()) {
                    images.add(s);
                }
            }
            MNImage.imageBrowser(activity, mBinding.getRoot(), 0, images, position, true, position12 -> {
                adapter.notifyItemRemoved(position12);
                imageList.remove(position12);
                imageList.add("");
                adapter.notifyDataSetChanged();
            });
        }
    }

    @Override
    public void toEditNick(View view) {
        ActivityUtils.getMineEditContent(2, 1, "编辑名称", MineApp.mineInfo.getNick(), "输入你的名称，名称不能为空");
    }

    @Override
    public void toSelectBirthday(View view) {
        new BirthdayPW(mBinding.getRoot(), MineApp.mineInfo.getBirthday(), birthday -> {
            updateContent(birthday, 5);
            mineEditMemberBinding.setMineInfo(MineApp.mineInfo);
        });
    }

    @Override
    public void toSelectJob(View view) {
        ActivityUtils.getMineSelectJob(MineApp.mineInfo.getJob());
    }

    @Override
    public void toEditSign(View view) {
        ActivityUtils.getMineEditContent(3, 10, "编辑个性签名", MineApp.mineInfo.getPersonalitySign(), "编辑个性签名...");
    }

    @Override
    public void toSelectTag(View view) {
        ActivityUtils.getMineSelectTag(MineApp.mineInfo.getServiceTags());
    }

    @Override
    public void toEditHeight(View view) {
        ActivityUtils.getMineEditContent(6, 1, "编辑身高", MineApp.mineInfo.getHeight() + "", "输入你的身高，单位cm");
    }

    private int index = 0;

    private void checkFace() {
        if (index < imageList.size()) {
            if (imageList.get(index).isEmpty()) {
                index++;
                checkFace();
                return;
            }
            checkFaceApi api = new checkFaceApi(new HttpOnNextListener() {
                @Override
                public void onNext(Object o) {
                    images += "#" + imageList.get(index);
                    index++;
                    checkFace();
                }
            }, activity).setFaceImage(imageList.get(index));
            HttpManager.getInstance().doHttpDeal(api);
        } else {
            images = images.substring(1);
            modifyMemberInfoApi api = new modifyMemberInfoApi(new HttpOnNextListener() {
                @Override
                public void onNext(Object o) {
                    SCToastUtil.showToast(activity, "个人信息提交成功", true);
                    updateImages(images);
                    activity.sendBroadcast(new Intent("lobster_updateMineInfo"));
                    activity.finish();
                }
            }, activity)
                    .setBirthday(MineApp.mineInfo.getBirthday())
                    .setImage(imageList.get(0))
                    .setMoreImages(images)
                    .setNick(MineApp.mineInfo.getNick())
                    .setJob(MineApp.mineInfo.getJob())
                    .setPersonalitySign(MineApp.mineInfo.getPersonalitySign())
                    .setSex(MineApp.mineInfo.getSex())
                    .setServiceTags(MineApp.mineInfo.getServiceTags())
                    .setHeight(MineApp.mineInfo.getHeight())
                    .setProvinceId(AreaDb.getInstance().getProvinceId(PreferenceUtil.readStringValue(activity, "provinceName")))
                    .setCityId(AreaDb.getInstance().getCityId(MineApp.cityName))
                    .setDistrictId(AreaDb.getInstance().getDistrictId(PreferenceUtil.readStringValue(activity, "districtName")));
            HttpManager.getInstance().doHttpDeal(api);
        }
    }

    @Override
    public void modifyMemberInfo() {
        images = "";
        index = 0;
        checkFace();
    }

    private void updateImages(String images) {
        MineApp.mineInfo.setImage(images.split("#")[0]);
        MineApp.mineInfo.setMoreImages(images);
    }

    public void updateContent(String content, int type) {
        // type   1：工作  2：昵称
        if (type == 1)
            MineApp.mineInfo.setJob(content);
        else if (type == 2)
            MineApp.mineInfo.setNick(content);
        else if (type == 3)
            MineApp.mineInfo.setPersonalitySign(content);
        else if (type == 4)
            MineApp.mineInfo.setServiceTags(content);
        else if (type == 5)
            MineApp.mineInfo.setBirthday(content);
        else if (type == 6)
            MineApp.mineInfo.setHeight(Integer.parseInt(content));
    }

    /**
     * 权限
     */
    private void getPermissions1() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            performCodeWithPermission("虾菇需要访问读外部存储权限", new BaseActivity.PermissionCallback() {
                        @Override
                        public void hasPermission() {
                            setPermissions();
                        }

                        @Override
                        public void noPermission() {
                            PreferenceUtil.saveIntValue(activity, "cameraPermission", 2);
                            SCToastUtil.showToast(activity, "你未申请相机、存储权限，请前往我的--设置--权限管理--权限进行设置", true);
                        }
                    }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else {
            setPermissions();
        }
    }

    private void setPermissions() {
        MineApp.toPublish = false;
        ActivityUtils.getCameraMain(activity, false, true, false);
    }
}
