package com.maning.imagebrowserlibrary.model;

import com.maning.imagebrowserlibrary.ImageEngine;
import com.maning.imagebrowserlibrary.listeners.OnClickListener;
import com.maning.imagebrowserlibrary.listeners.OnDeleteImageListener;
import com.maning.imagebrowserlibrary.listeners.OnLongClickListener;

import java.util.ArrayList;

/**
 * <pre>
 *     author : maning
 *     e-mail : xxx@xx
 *     time   : 2018/04/10
 *     desc   : 相关配置信息
 *     version: 1.0
 * </pre>
 */
public class ImageBrowserConfig {

    //枚举类型
    public enum TransformType {
        Transform_Default,
        Transform_DepthPage,
        Transform_RotateDown,
        Transform_RotateUp,
        Transform_ZoomIn,
        Transform_ZoomOutSlide,
        Transform_ZoomOut,
    }

    //枚举类型
    public enum IndicatorType {
        Indicator_Circle,
        Indicator_Number
    }

    //当前位置
    private int position;
    private TransformType transformType = TransformType.Transform_Default;
    private IndicatorType indicatorType = IndicatorType.Indicator_Number;
    private ArrayList<String> imageList = new ArrayList<>();
    private ImageEngine imageEngine;
    private OnClickListener onClickListener;
    private OnLongClickListener onLongClickListener;
    private OnDeleteImageListener onDeleteImageListener;
    private boolean showDelete = false;

    public IndicatorType getIndicatorType() {
        return indicatorType;
    }

    public void setIndicatorType(IndicatorType indicatorType) {
        this.indicatorType = indicatorType;
    }

    public OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public OnLongClickListener getOnLongClickListener() {
        return onLongClickListener;
    }

    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    public OnDeleteImageListener getOnDeleteImageListener() {
        return onDeleteImageListener;
    }

    public void setOnDeleteImageListener(OnDeleteImageListener onDeleteImageListener) {
        this.onDeleteImageListener = onDeleteImageListener;
    }

    public ArrayList<String> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<String> imageList) {
        this.imageList = imageList;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public ImageEngine getImageEngine() {
        return imageEngine;
    }

    public void setImageEngine(ImageEngine imageEngine) {
        this.imageEngine = imageEngine;
    }

    public TransformType getTransformType() {
        return transformType;
    }

    public void setTransformType(TransformType transformType) {
        this.transformType = transformType;
    }

    public boolean isShowDelete() {
        return showDelete;
    }

    public void setShowDelete(boolean showDelete) {
        this.showDelete = showDelete;
    }
}
