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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
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
            SCToastUtil.showToast(activity, "???????????????1?????????", true);
            return;
        }

        if (MineApp.mineInfo.getPersonalitySign().isEmpty()) {
            SCToastUtil.showToast(activity, "??????????????????????????????", true);
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
            if (checkPermissionGranted(activity, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                setPermissions();
            } else {
                if (PreferenceUtil.readIntValue(activity, "photoPermission") == 0) {
                    PreferenceUtil.saveIntValue(activity, "photoPermission", 1);
                    new TextPW(activity, mBinding.getRoot(), "????????????",
                            "?????????????????????????????????????????????????????????????????????????????????????????????????????????" +
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
        ActivityUtils.getMineEditContent(2, 1, "????????????", MineApp.mineInfo.getNick(), "???????????????????????????????????????");
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
        ActivityUtils.getMineEditContent(3, 10, "??????????????????", MineApp.mineInfo.getPersonalitySign(), "??????????????????...");
    }

    @Override
    public void toSelectTag(View view) {
        ActivityUtils.getMineSelectTag(MineApp.mineInfo.getServiceTags());
    }

    @Override
    public void toEditHeight(View view) {
        ActivityUtils.getMineEditContent(6, 1, "????????????", MineApp.mineInfo.getHeight() + "", "???????????????????????????cm");
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
                    SCToastUtil.showToast(activity, "????????????????????????", true);
                    updateImages(images);
                    LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_updateMineInfo"));
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
        // type   1?????????  2?????????
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
     * ??????
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            performCodeWithPermission("???????????????????????????????????????", new BaseActivity.PermissionCallback() {
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
        ActivityUtils.getCameraMain(activity, false, true, false);
    }
}
