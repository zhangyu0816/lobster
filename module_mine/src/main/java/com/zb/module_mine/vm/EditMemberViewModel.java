package com.zb.module_mine.vm;

import android.Manifest;
import android.os.Build;
import android.view.View;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.api.modifyMemberInfoApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.AreaDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.utils.SimpleItemTouchHelperCallback;
import com.zb.lib_base.utils.uploadImage.PhotoFile;
import com.zb.lib_base.utils.uploadImage.PhotoManager;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.SelectorPW;
import com.zb.module_mine.R;
import com.zb.module_mine.adapter.MineAdapter;
import com.zb.module_mine.databinding.MineEditMemberBinding;
import com.zb.module_mine.iv.EditMemberVMInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.ItemTouchHelper;
import io.realm.Realm;

public class EditMemberViewModel extends BaseViewModel implements EditMemberVMInterface {
    public MineAdapter adapter;
    public AreaDb areaDb;
    public List<String> imageList = new ArrayList<>();
    public int _position = 0;
    private SimpleItemTouchHelperCallback callback;
    private MineEditMemberBinding mineEditMemberBinding;
    private List<String> selectorList = new ArrayList<>();
    private List<String> selectorImageList = new ArrayList<>();
    public MineInfo mineInfo;
    private PhotoManager photoManager;

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        selectorList.add("女");
        selectorList.add("男");
        selectorImageList.add("替换");
        selectorImageList.add("删除");
        mineEditMemberBinding = (MineEditMemberBinding) binding;
        areaDb = new AreaDb(Realm.getDefaultInstance());
        mineInfo = mineInfoDb.getMineInfo();

        String[] images = mineInfo.getMoreImages().split("#");
        imageList.addAll(Arrays.asList(images));

        for (int i = imageList.size(); i < 6; i++) {
            imageList.add("");
        }

        setAdapter();
        photoManager = new PhotoManager(activity, () -> {
            for (PhotoFile photoFile : photoManager.getPhotoFiles()) {
                int i = imageList.indexOf(photoFile.getFilePath());
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
        for (String image : imageList) {
            if (!image.isEmpty() && !image.contains("Http")) {
                images += "#" + image;
            }
        }
        if (images.isEmpty()) {
            SCToastUtil.showToast(activity, "请上传至少1张照片");
            return;
        }
        images = images.substring(1);
        photoManager.addFiles(Arrays.asList(images.split("#")), () -> photoManager.reUploadByUnSuccess());
    }

    @Override
    public void selectImage(int position) {
        if (imageList.get(position).isEmpty()) {
            _position = position;
            getPermissions();
        } else {
            new SelectorPW(activity, mBinding.getRoot(), selectorImageList, position1 -> {
                if (position1 == 0) {
                    _position = position;
                    getPermissions();
                } else {
                    imageList.set(_position, "");
                    adapter.notifyItemChanged(_position);
                }
            });
        }
    }

    @Override
    public void toEditNick(View view) {
        ActivityUtils.getMineEditContent(2, 1, "编辑昵称", mineInfo.getNick(), "输入你的昵称，昵称不能为空");
    }

    @Override
    public void toSelectSex(View view) {
        new SelectorPW(activity, mBinding.getRoot(), selectorList, position -> mineInfo.setSex(position));
    }

    @Override
    public void toSelectJob(View view) {
        ActivityUtils.getMineSelectJob();
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
        String images = "";
        for (String image : imageList) {
            if (!image.isEmpty()) {
                images += "#" + image;
            }
        }
        images = images.substring(1);
        modifyMemberInfoApi api = new modifyMemberInfoApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                SCToastUtil.showToastBlack(activity, "提交个人信息");
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
                .setProvinceId(areaDb.getProvinceId(PreferenceUtil.readStringValue(activity,"provinceName")))
                .setCityId(areaDb.getCityId(MineApp.cityName))
                .setDistrictId(areaDb.getDistrictId(PreferenceUtil.readStringValue(activity,"districtName")));
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
        ActivityUtils.getCameraMain(activity, false);
    }
}
