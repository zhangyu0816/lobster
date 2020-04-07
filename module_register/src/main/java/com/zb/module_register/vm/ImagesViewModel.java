package com.zb.module_register.vm;

import android.Manifest;
import android.os.Build;
import android.view.View;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.SimpleItemTouchHelperCallback;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_register.R;
import com.zb.module_register.adapter.RegisterAdapter;
import com.zb.module_register.databinding.RegisterImagesBinding;
import com.zb.module_register.iv.ImagesVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.ItemTouchHelper;

public class ImagesViewModel extends BaseViewModel implements ImagesVMInterface {
    public RegisterAdapter adapter;
    public int _position = 0;
    public List<String> images = new ArrayList<>();
    private SimpleItemTouchHelperCallback callback;

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void setAdapter() {
        images = MineApp.registerInfo.getImageList();
        adapter = new RegisterAdapter<>(activity, R.layout.item_images, images, this);
        callback = new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(((RegisterImagesBinding)mBinding).imagesList);
//        callback.setImages(images);
//        callback.setSort(true);
//        callback.setDragFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT);
    }

    @Override
    public void complete(View view) {

    }

    @Override
    public void selectImage(int position) {
        _position = position;
        getPermissions();
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
        ActivityUtils.getCameraMain(activity);
    }
}
