package com.zb.module_register.vm;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.view.View;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.api.myInfoApi;
import com.zb.lib_base.api.registerApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.AreaDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.LoginInfo;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.model.RegisterInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DataCleanManager;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.utils.SimpleItemTouchHelperCallback;
import com.zb.lib_base.utils.uploadImage.PhotoManager;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.SelectorPW;
import com.zb.module_register.R;
import com.zb.module_register.adapter.RegisterAdapter;
import com.zb.module_register.databinding.RegisterImagesBinding;
import com.zb.module_register.iv.ImagesVMInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.recyclerview.widget.ItemTouchHelper;
import io.realm.Realm;

public class ImagesViewModel extends BaseViewModel implements ImagesVMInterface {
    public RegisterAdapter adapter;
    public int _position = 0;
    public List<String> images = new ArrayList<>();
    private SimpleItemTouchHelperCallback callback;
    private PhotoManager photoManager;
    private List<String> selectorList = new ArrayList<>();
    private AreaDb areaDb;

    @Override
    public void back(View view) {
        super.back(view);
        ActivityUtils.getRegisterLogo();
        DataCleanManager.deleteFile(new File(activity.getCacheDir(), "images"));
        activity.finish();
    }

    @Override
    public void setAdapter() {
        areaDb = new AreaDb(Realm.getDefaultInstance());
        images = MineApp.registerInfo.getImageList();
        adapter = new RegisterAdapter<>(activity, R.layout.item_register_image, images, this);
        callback = new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(((RegisterImagesBinding) mBinding).imagesList);
        callback.setSort(true);
        callback.setDragFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT);

        photoManager = new PhotoManager(activity, () -> {
            MineApp.registerInfo.setMoreImages(photoManager.jointWebUrl("#"));
            photoManager.deleteAllFile();
            register(MineApp.registerInfo);
        });

        selectorList.add("替换");
        selectorList.add("删除");
    }

    @Override
    public void complete(View view) {
        String images = "";
        for (String image : MineApp.registerInfo.getImageList()) {
            if (!image.isEmpty()) {
                images += "#" + image;
            }
        }
        if (images.isEmpty()) {
            SCToastUtil.showToast(activity, "请上传至少1张照片", false);
            return;
        }
        images = images.substring(1);
        photoManager.addFiles(Arrays.asList(images.split("#")), () -> photoManager.reUploadByUnSuccess());
    }

    @Override
    public void register(RegisterInfo registerInfo) {
        registerApi api = new registerApi(new HttpOnNextListener<LoginInfo>() {
            @Override
            public void onNext(LoginInfo o) {
                PreferenceUtil.saveLongValue(activity, "userId", o.getId());
                PreferenceUtil.saveStringValue(activity, "sessionId", o.getSessionId());
                PreferenceUtil.saveStringValue(activity, "userName", o.getUserName());
                BaseActivity.update();
                DataCleanManager.deleteFile(new File(activity.getCacheDir(), "images"));
                myInfo();
            }
        }, activity)
                .setUserName(registerInfo.getPhone())
                .setCaptcha(registerInfo.getCaptcha())
                .setNick(registerInfo.getName())
                .setBirthday(registerInfo.getBirthday())
                .setMoreImages(registerInfo.getMoreImages())
                .setSex(registerInfo.getSex())
                .setProvinceId(areaDb.getProvinceId(PreferenceUtil.readStringValue(activity, "provinceName")))
                .setCityId(areaDb.getCityId(MineApp.cityName))
                .setDistrictId(areaDb.getDistrictId(PreferenceUtil.readStringValue(activity, "districtName")));
        api.setPosition(1);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void myInfo() {
        myInfoApi api = new myInfoApi(new HttpOnNextListener<MineInfo>() {
            @Override
            public void onNext(MineInfo o) {
                SCToastUtil.showToast(activity, "注册成功", true);
                mineInfoDb.saveMineInfo(o);
                if (!MineApp.isLogin) {
                    ActivityUtils.getMainActivity();
                    activity.sendBroadcast(new Intent("lobster_mainSelect"));
                }
                activity.finish();
            }
        }, activity);
        api.setPosition(1);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void selectImage(int position) {
        if (images.get(position).isEmpty()) {
            _position = position;
            getPermissions();
        } else {
            new SelectorPW(activity, mBinding.getRoot(), selectorList, position1 -> {
                if (position1 == 0) {
                    _position = position;
                    getPermissions();
                } else {
                    images.set(_position, "");
                    adapter.notifyItemChanged(_position);
                }
            });
        }

    }

    /**
     * 权限
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            performCodeWithPermission( "虾菇需要访问读外部存储权限", new BaseActivity.PermissionCallback() {
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
        ActivityUtils.getCameraMain(activity, false, false);
    }
}
