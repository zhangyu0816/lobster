package com.zb.module_camera;

import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_camera.adapter.CameraAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.databinding.ViewDataBinding;

public class CameraViewModel extends BaseViewModel implements CameraVMInterface {

    public CameraAdapter adapter;
    private List<String> images = new ArrayList<>();

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        setAdapter();
        buildImagesBucketList();
    }

    @Override
    public void setAdapter() {
        adapter = new CameraAdapter<>(activity, R.layout.item_camera, images, this);
    }

    @Override
    public void selectImage(int position) {
        adapter.setSelectIndex(position);
    }

    /**
     * 获取本地图片
     */
    private void buildImagesBucketList() {
        String columns[] = new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.PICASA_ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.SIZE, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        Cursor cur =activity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null,
                null);

        File file = null;
        if (cur.moveToFirst()) {
            int photoPathIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            do {
                String path = cur.getString(photoPathIndex);
                file = new File(path);
                if (file.length() != 0)
                    images.add(path);
                else
                    file = null;
            } while (cur.moveToNext());
        }
        cur.close();
        Collections.reverse(images);

        Log.i("images",images.toString());
    }
}
