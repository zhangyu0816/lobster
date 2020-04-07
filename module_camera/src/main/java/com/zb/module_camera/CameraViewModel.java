package com.zb.module_camera;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_camera.adapter.CameraAdapter;
import com.zb.module_camera.databinding.CameraMainBinding;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;

public class CameraViewModel extends BaseViewModel implements CameraVMInterface {

    public CameraAdapter adapter;
    private List<String> images = new ArrayList<>();
    private CameraMainBinding mainBinding;

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mainBinding = (CameraMainBinding) binding;
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
        Glide.with(activity).asBitmap().load(images.get(position)).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                mainBinding.ivCut.setImageBitmap(resource);
            }
        });
    }

    @Override
    public void upload(View view) {
        Bitmap bitmap = mainBinding.ivCut.getCutBitmap();
        File file = BaseActivity.getImageFile();
        try {

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();

            Intent data = new Intent();
            data.putExtra("fileName", file.getAbsolutePath());
            activity.setResult(1, data);
            activity.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取本地图片
     */
    private void buildImagesBucketList() {
        String columns[] = new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.PICASA_ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.SIZE, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        Cursor cur = activity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null,
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

        Log.i("images", images.toString());
    }
}
