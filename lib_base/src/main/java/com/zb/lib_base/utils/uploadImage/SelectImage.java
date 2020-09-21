package com.zb.lib_base.utils.uploadImage;

import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by DIY on 2016-12-13.
 */
public class SelectImage {
    private List<String> onlineImages = new ArrayList<>();//网络图片
    private List<String> systemImages = new ArrayList<>();//相册
    private List<String> photoImages = new ArrayList<>();//拍照
    private List<Integer> positions = new ArrayList<>();//相册已选的图片位置

    private String addImage = "";

    public void setAddImage(String addImage) {
        this.addImage = addImage;
    }

    public SelectImage() {

    }

    public List<String> getOnlineImages() {
        return onlineImages;
    }

    public void setOnlineImages(List<String> onlineImages) {
        this.onlineImages = onlineImages;
    }

    /*拍照*/
    public List<String> getPhotoImages() {
        return photoImages;
    }

    public void addPhotoImage(int position, String path) {
        if (position == -1)
            photoImages.add(path);
        else
            photoImages.add(position, path);
    }

    /*系统相册选择位置*/
    public List<Integer> getPositions() {
        return positions;
    }

    public void setPositions(List<Integer> positions) {
        this.positions = positions;
    }

    /*系统相册*/
    public List<String> getSystemImages() {
        return systemImages;
    }

    public void addSysImage(int position, String path) {
        if (position == -1)
            systemImages.add(path);
        else
            systemImages.add(position, path);
    }

    public void addAllSysImage(int position, List<String> paths) {
        if (position == -1)
            systemImages.addAll(paths);
        else
            systemImages.addAll(position, paths);
    }

    public void clearSysImages() {
        systemImages.clear();
    }

    /**
     * 删除照片
     *
     * @param photoManager
     * @param path
     */
    public void deleteImage(PhotoManager photoManager, String path) {
        Iterator<String> iterator = getSystemImages().iterator();
        int i = 0;
        while (iterator.hasNext()) {
            String s = iterator.next();
            if (TextUtils.equals(path, s)) {
                if (getOnlineImages().contains(path)) {
                    getOnlineImages().remove(path);
                } else if (getPhotoImages().contains(path)) {
                    getPhotoImages().remove(path);
                    photoManager.deleteSrcFile(path);
                } else {
                    try {
                        if (positions.size() > 0) {
                            int index = i - getOnlineImages().size() - getPhotoImages().size();
                            if (addImage.isEmpty())
                                positions.remove(index);
                            else
                                positions.remove(index - 1);
                        }
                    } catch (Exception ignored) {
                    }

                    photoManager.deleteSrcFile(path);
                }
                iterator.remove();
                break;
            }
            i++;
        }
    }

    /**
     * 添加拍照照片
     *
     * @param photoManager
     * @param path
     */
    public void addByPhoto(PhotoManager photoManager, String path) {
        addPhotoImage(-1, path);
        addSysImage(-1, path);
        photoManager.addFile(new File(path));
    }

    /**
     * 添加相册图片
     *
     * @param photoManager
     * @param selectIds
     * @param addImage
     * @param imagePaths
     */
    public void addByCamera(PhotoManager photoManager, List<Integer> selectIds, String addImage, List<String> imagePaths) {
        setPositions(selectIds);
        clearSysImages();

        addAllSysImage(-1, getOnlineImages());
        addAllSysImage(-1, getPhotoImages());
        addAllSysImage(-1, imagePaths);
        if (!addImage.isEmpty())
            addSysImage(-1, addImage);
        photoManager.deleteAllFile();
        photoManager.addFiles(getPhotoImages());
        photoManager.addFiles(imagePaths);
    }

    /**
     * 添加相册图片
     *
     * @param photoManager
     * @param selectIds
     * @param addImage
     * @param imagePaths
     */
    public void addByCamera(PhotoManager photoManager, List<Integer> selectIds, String addImage, List<String> imagePaths, File logoFile) {
        setPositions(selectIds);
        clearSysImages();
        if (!addImage.isEmpty())
            addSysImage(-1, addImage);
        addAllSysImage(-1, getOnlineImages());
        addAllSysImage(-1, getPhotoImages());
        addAllSysImage(-1, imagePaths);
        photoManager.deleteAllFile();
        photoManager.addFile(logoFile);
        photoManager.addFiles(getPhotoImages());
        photoManager.addFiles(imagePaths);
    }

    /**
     * 添加网络图片
     *
     * @param images
     */
    public void addByOnline(String images) {
        List<String> imageList = new ArrayList<>();
        String[] imageStr = images.split(",");
        for (String s : imageStr) {
            if (!s.equals("0"))
                imageList.add(s);
        }
        setOnlineImages(imageList);
        addAllSysImage(-1, getOnlineImages());
    }
}
