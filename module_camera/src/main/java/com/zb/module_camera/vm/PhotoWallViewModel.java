package com.zb.module_camera.vm;

import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;

import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.model.Film;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.BigPhotoDF;
import com.zb.lib_base.windows.FilmRinseDF;
import com.zb.module_camera.R;
import com.zb.module_camera.adapter.CameraAdapter;
import com.zb.module_camera.databinding.AcPhotoWallBinding;
import com.zb.module_camera.iv.PhotoWallVMInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import androidx.databinding.ViewDataBinding;

public class PhotoWallViewModel extends BaseViewModel implements PhotoWallVMInterface {

    private AcPhotoWallBinding mBinding;
    public int surplusCount;
    public Film mFilm;
    private List<String> images = new ArrayList<>();
    public List<String> selectImages = new ArrayList<>();
    public CameraAdapter adapter;
    private String[] columns = new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
    private Cursor cur;

    public CameraAdapter selectAdapter;


    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (AcPhotoWallBinding) binding;
        mBinding.setTitle("手机相册");
        cur = activity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null,
                null);
        setAdapter();
        buildImagesBucketList();
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void setAdapter() {
        adapter = new CameraAdapter<>(activity, R.layout.item_wall_image, images, this);

        selectAdapter = new CameraAdapter<>(activity, R.layout.item_select_image, selectImages, this);
    }

    @Override
    public void selectImage(int position, String image) {
        if (selectImages.contains(image)) {
            Iterator<String> iterator = selectImages.iterator();
            while (iterator.hasNext()) {
                String s = iterator.next();
                if (TextUtils.equals(image, s)) {
                    iterator.remove();
                    adapter.notifyItemChanged(position);
                    selectAdapter.notifyDataSetChanged();
                    mBinding.setSelectCount(selectImages.size());
                    mBinding.setSurplusCount((MineApp.filmMaxSize - surplusCount + selectImages.size()) + "/" + MineApp.filmMaxSize);
                    break;
                }
            }
        } else {
            if (selectImages.size() >= surplusCount) {
                SCToastUtil.showToast(activity, "已超过剩余底片数量，选取失败", true);
                return;
            }
            selectImages.add(image);
            adapter.notifyItemChanged(position);
            selectAdapter.notifyDataSetChanged();
            mBinding.setSelectCount(selectImages.size());
            mBinding.setSurplusCount((MineApp.filmMaxSize - surplusCount + selectImages.size()) + "/" + MineApp.filmMaxSize);
        }

    }

    @Override
    public void enlarge(int position, String image) {
        new BigPhotoDF(activity).setImageUrl(image).setBigPhotoCallBack(() -> {
            if (selectImages.contains(image)) return;
            if (selectImages.size() >= surplusCount) {
                SCToastUtil.showToast(activity, "已超过剩余底片数量，选取失败", true);
                return;
            }
            selectImages.add(image);
            adapter.notifyItemChanged(position);
            selectAdapter.notifyDataSetChanged();
            mBinding.setSelectCount(selectImages.size());
            mBinding.setSurplusCount((MineApp.filmMaxSize - surplusCount + selectImages.size()) + "/" + MineApp.filmMaxSize);
        }).show(activity.getSupportFragmentManager());
    }

    @Override
    public void deleteImage(int position, String image) {
        Iterator<String> iterator = selectImages.iterator();
        while (iterator.hasNext()) {
            String s = iterator.next();
            if (TextUtils.equals(image, s)) {
                iterator.remove();
                adapter.notifyItemChanged(images.indexOf(image));
                selectAdapter.notifyDataSetChanged();
                mBinding.setSelectCount(selectImages.size());
                mBinding.setSurplusCount((MineApp.filmMaxSize - surplusCount + selectImages.size()) + "/" + MineApp.filmMaxSize);
                break;
            }
        }
    }

    @Override
    public void wash(View view) {
        new FilmRinseDF(activity).setFilm(mFilm).setFilmRinseCallBack(() -> {
           MineApp.sFilmResourceDb.updateImages(mFilm.getId(), TextUtils.join("#", selectImages), true);
            activity.sendBroadcast(new Intent("lobster_washSuccess"));
            activity.finish();
        }).show(activity.getSupportFragmentManager());
    }

    /**
     * 获取本地图片
     */
    private void buildImagesBucketList() {
        if (cur.moveToFirst()) {
            int photoPathIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            do {
                String path = cur.getString(photoPathIndex);
                images.add(path);
            } while (cur.moveToNext());
        }
        cur.close();
        Collections.reverse(images);
        adapter.notifyDataSetChanged();
    }
}
