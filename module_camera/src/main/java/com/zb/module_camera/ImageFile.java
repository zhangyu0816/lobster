package com.zb.module_camera;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class ImageFile extends BaseObservable {
    String fileName = "";
    String image = "";
    int size = 0;

    public ImageFile() {
    }

    public ImageFile(String fileName, String image, int size) {
        setFileName(fileName);
        setImage(image);
        setSize(size);
    }

    @Bindable
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        notifyPropertyChanged(BR.fileName);
    }

    @Bindable
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
        notifyPropertyChanged(BR.image);
    }

    @Bindable
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
        notifyPropertyChanged(BR.size);
    }
}