package com.zb.module_camera.vm;

import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;

import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.model.FileModel;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_camera.R;
import com.zb.module_camera.adapter.CameraAdapter;
import com.zb.module_camera.databinding.CameraMainBinding;
import com.zb.module_camera.iv.CameraVMInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.databinding.ViewDataBinding;

public class CameraViewModel extends BaseViewModel implements CameraVMInterface {

    public CameraAdapter adapter;
    private Map<String, List<String>> imageMap = new HashMap<>();
    private List<String> images = new ArrayList<>();
    private CameraMainBinding mainBinding;
    private List<FileModel> fileList = new ArrayList<>();
    private String columns[] = new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
    private Cursor cur;
    public CameraAdapter fileAdapter;

    private int selectCount = 0; // 选中的张数
    private int maxCount = 9; // 最大数量
    private int selectIndex = -1;
    public boolean isMore = false;
    public boolean showBottom = false;
    public boolean showVideo;

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
        fileList.add(new FileModel("所有图片", "", 0));
        imageMap.put("所有图片", new ArrayList<>());

        mainBinding.setTitle(fileList.get(0).getFileName());
        mainBinding.setShowList(false);
        cur = activity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null,
                null);
        buildImagesBucketList();
        if (isMore) {
            selectCount = MineApp.selectMap.size();
        }
    }

    @Override
    public void setAdapter() {
        adapter = new CameraAdapter<>(activity, R.layout.item_camera_image, images, this);

        fileAdapter = new CameraAdapter<>(activity, R.layout.item_camera_file, fileList, this);
    }

    @Override
    public void selectIndex(int index) {
        if (index == 1) {
            ActivityUtils.getCameraVideo(true);
        } else if (index == 2) {
            ActivityUtils.getCameraPhoto(isMore, showBottom, showVideo);
        }
        activity.finish();
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
    public void selectFileIndex(int position) {
        mainBinding.setShowList(false);
        mainBinding.setTitle(fileList.get(position).getFileName());
        images.clear();
        images.addAll(imageMap.get(fileList.get(position).getFileName()));
        adapter.notifyDataSetChanged();
        selectImage(0);
    }


    @Override
    public void selectImage(int position) {
        adapter.setSelectIndex(position);
        if (selectIndex != -1) {
            adapter.notifyItemChanged(selectIndex);
        }
        adapter.notifyItemChanged(position);
        selectIndex = position;
        if (isMore) {
            selectMoreImage(position);
        }
    }

    private void selectMoreImage(int position) {
        String selectImage = images.get(position);
        if (MineApp.selectMap.containsKey(selectImage)) {
            int count = MineApp.selectMap.get(selectImage);
            selectCount--;
            MineApp.selectMap.remove(selectImage);
            adapter.notifyItemChanged(position);
            for (Map.Entry<String, Integer> entry : MineApp.selectMap.entrySet()) {
                if (entry.getValue() > count) {
                    MineApp.selectMap.put(entry.getKey(), entry.getValue() - 1);
                    adapter.notifyItemChanged(images.indexOf(entry.getKey()));
                }
            }
        } else {
            if (selectCount == maxCount) {
                SCToastUtil.showToast(activity, "一次最多可选取" + maxCount + "张图片", true);
                return;
            }
            selectCount++;
            MineApp.selectMap.put(selectImage, selectCount);
            adapter.notifyItemChanged(position);
        }
    }


    @Override
    public void upload(View view) {
        if (isMore) {
            new Thread(() -> {
                List<Map.Entry<String, Integer>> list = new ArrayList<>(MineApp.selectMap.entrySet());
                //升序排序
                Collections.sort(list, (o1, o2) -> o1.getValue() - o2.getValue());

                List<String> imageList = new ArrayList<>();
                for (Map.Entry<String, Integer> mapping : list) {
                    imageList.add(mapping.getKey());
                }

                if (MineApp.toPublish && !MineApp.toContinue) {
                    MineApp.cameraType = 0;
                    MineApp.isMore = true;
                    MineApp.filePath = TextUtils.join(",", imageList);
                    ActivityUtils.getHomePublishImage();
                    activity.finish();
                } else {
                    Intent data = new Intent("lobster_camera");
                    data.putExtra("cameraType", 0);
                    data.putExtra("isMore", true);
                    data.putExtra("filePath", TextUtils.join(",", imageList));
                    activity.sendBroadcast(data);
                    activity.finish();
                }
            }).start();
        } else {
            if (selectIndex == -1) {
                SCToastUtil.showToast(activity, "请选择照片", true);
                return;
            }
            Intent data = new Intent();
            data.putExtra("fileName", images.get(selectIndex));
            activity.setResult(1, data);
            activity.finish();
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
                boolean hasName = false;
                for (FileModel fileModel : fileList) {
                    if (fileName == null)
                        break;
                    if (fileModel == null)
                        break;
                    if (fileModel.getFileName() == null)
                        break;
                    if (fileModel.getFileName().equals(fileName)) {
                        hasName = true;
                        break;
                    }
                }
                if (!hasName) {
                    fileList.add(new FileModel(fileName, "", 0));
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

        for (FileModel item : fileList) {
            List<String> temp = imageMap.get(item.getFileName());
            if (temp.size() == 0) return;
            Collections.reverse(temp);
            item.setImage(temp.get(0));
            item.setSize(temp.size());
        }
    }
}
