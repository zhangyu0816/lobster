package com.zb.module_camera;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.views.CutImageView;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_camera.adapter.CameraAdapter;
import com.zb.module_camera.databinding.CameraMainBinding;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.ViewDataBinding;

public class CameraViewModel extends BaseViewModel implements CameraVMInterface {

    public CameraAdapter adapter;
    private Map<String, List<String>> imageMap = new HashMap<>();
    private List<String> images = new ArrayList<>();
    private CameraMainBinding mainBinding;
    private List<String> fileList = new ArrayList<>();
    private String columns[] = new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
    private Cursor cur;
    public CameraAdapter fileAdapter;

    private int selectCount = 0; // 选中的张数
    private int maxCount = 9; // 最大数量
    private List<String> selectPaths = new ArrayList<>();
    private int selectIndex = -1;
    public boolean isMore = false;

    private Map<Integer, CutImageView> tempMap = new HashMap<>();
    private boolean selectMore = false;

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
        fileList.add("所有图片");
        imageMap.put("所有图片", new ArrayList<>());

        mainBinding.setTitle(fileList.get(0));
        mainBinding.setShowList(false);
        cur = activity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null,
                null);
        buildImagesBucketList();
        if (isMore) {
            selectCount = MineApp.selectMap.size();
            for (Map.Entry<String, CutImageView> entry : MineApp.cutImageViewMap.entrySet()) {
                CutImageView cutImageView = entry.getValue();
                cutImageView.countSize(ObjectUtils.getViewSizeByHeight(0.5f), ObjectUtils.getViewSizeByWidth(1f));
                MineApp.cutImageViewMap.put(entry.getKey(), entry.getValue());
            }
        } else {
            selectImage(0);
        }
    }

    @Override
    public void setAdapter() {
        adapter = new CameraAdapter<>(activity, R.layout.item_camera, images, this);

        fileAdapter = new CameraAdapter<>(activity, R.layout.item_file, fileList, this);
    }

    @Override
    public void selectTitle(View view) {
        if (mainBinding.getShowList()) {
            mainBinding.setShowList(false);
        } else {
            mainBinding.setShowList(true);
        }
    }

    @Override
    public void selectImage(int position) {
        selectIndex = position;
        adapter.setSelectIndex(position);
        Glide.with(activity).asBitmap().load(images.get(position)).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                mainBinding.ivCut.setImageBitmap(resource);
            }
        });
        if (isMore) {
            mainBinding.cutLayout.removeAllViews();
            CutImageView cutImageView;
            if (tempMap.containsKey(position)) {
                cutImageView = tempMap.get(position);
            } else {
                cutImageView = new CutImageView(activity);
            }
            cutImageView.countSize(ObjectUtils.getViewSizeByHeight(0.5f), ObjectUtils.getViewSizeByWidth(1f));
            mainBinding.cutLayout.addView(cutImageView);
            Glide.with(activity).asBitmap().load(images.get(position)).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    cutImageView.setImageBitmap(resource);
                    tempMap.put(position, cutImageView);
                    if (selectMore) {
                        selectMore = false;
                        selectMoreImage(position);
                    }
                }
            });
        }
    }

    @Override
    public void selectImageByMore(int position) {

        if (selectIndex != position) {
            selectMore = true;
            selectImage(position);
        } else {
            selectMoreImage(position);
        }
    }

    private void selectMoreImage(int position) {
        if (MineApp.selectMap.containsKey(images.get(position))) {
            int count = MineApp.selectMap.get(images.get(position));
            selectCount--;
            MineApp.selectMap.remove(images.get(position));
            MineApp.cutImageViewMap.remove(images.get(position));
            adapter.notifyItemChanged(position);
            for (Map.Entry<String, Integer> entry : MineApp.selectMap.entrySet()) {
                if (entry.getValue() > count) {
                    MineApp.selectMap.put(entry.getKey(), entry.getValue() - 1);
                    adapter.notifyItemChanged(images.indexOf(entry.getKey()));
                }
            }
        } else {
            if (selectCount == maxCount) {
                SCToastUtil.showToast(activity, "一次最多可选取" + maxCount + "张图片");
                return;
            }
            selectCount++;
            MineApp.selectMap.put(images.get(position), selectCount);
            if (tempMap.containsKey(position))
                MineApp.cutImageViewMap.put(images.get(position), tempMap.get(position));
            adapter.notifyItemChanged(position);
        }
    }

    @Override
    public void selectFileIndex(int position) {
        mainBinding.setShowList(false);
        mainBinding.setTitle(fileList.get(position));
        images.clear();
        for (int i = 0; i < imageMap.get(fileList.get(position)).size(); i++) {
            images.add(imageMap.get(fileList.get(position)).get(i));
        }
        Collections.reverse(images);
        adapter.notifyDataSetChanged();
        selectImage(0);
    }

    @Override
    public void upload(View view) {
        if (isMore) {
            new Thread(() -> {
                for (Map.Entry<String, CutImageView> entry : MineApp.cutImageViewMap.entrySet()) {
                    Bitmap bitmap = entry.getValue().getCutBitmap();
                    if (bitmap != null) {
                        File file = BaseActivity.getImageFile();
                        try {
                            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                            bos.flush();
                            bos.close();
                            selectPaths.add(file.getAbsolutePath());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                Intent data = new Intent();
                data.putExtra("filePaths", TextUtils.join(",", selectPaths));
                activity.setResult(1, data);
                activity.finish();
            }).start();
        } else {
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
    }

    /**
     * 获取本地图片
     */
    private void buildImagesBucketList() {
        File file = null;
        if (cur.moveToFirst()) {
            int photoPathIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            do {
                String path = cur.getString(photoPathIndex);
                String fileName = cur.getString(1);
                if (!fileList.contains(fileName)) {
                    fileList.add(fileName);
                    imageMap.put(fileName, new ArrayList<>());
                }

                file = new File(path);
                if (file.length() != 0) {
                    imageMap.get(fileName).add(path);
                    imageMap.get("所有图片").add(path);
                } else
                    file = null;
            } while (cur.moveToNext());
        }
        cur.close();
        for (int i = 0; i < imageMap.get("所有图片").size(); i++) {
            images.add(imageMap.get("所有图片").get(i));
        }
        Collections.reverse(images);
        adapter.notifyDataSetChanged();
    }
}
