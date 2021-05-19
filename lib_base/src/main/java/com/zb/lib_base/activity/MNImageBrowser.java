package com.zb.lib_base.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.maning.imagebrowserlibrary.Discover;
import com.maning.imagebrowserlibrary.ImageEngine;
import com.maning.imagebrowserlibrary.listeners.OnClickListener;
import com.maning.imagebrowserlibrary.listeners.OnDeleteImageListener;
import com.maning.imagebrowserlibrary.listeners.OnDiscoverListener;
import com.maning.imagebrowserlibrary.listeners.OnLongClickListener;
import com.maning.imagebrowserlibrary.model.ImageBrowserConfig;

import java.util.ArrayList;

import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

/**
 * Created by maning on 2017/5/25.
 */
public class MNImageBrowser {

    private Context context;
    private ImageBrowserConfig imageBrowserConfig;

    private MNImageBrowser(Context context) {
        this.context = context;
        imageBrowserConfig = new ImageBrowserConfig();
    }

    public static MNImageBrowser with(Context context) {
        return new MNImageBrowser(context);
    }

    public MNImageBrowser setImageList(ArrayList<String> imageList) {
        imageBrowserConfig.setImageList(imageList);
        return this;
    }

    public MNImageBrowser setCurrentPosition(int position) {
        imageBrowserConfig.setPosition(position);
        return this;
    }

    public MNImageBrowser setTransformType(ImageBrowserConfig.TransformType transformType) {
        imageBrowserConfig.setTransformType(transformType);
        return this;
    }

    public MNImageBrowser setImageEngine(ImageEngine imageEngine) {
        imageBrowserConfig.setImageEngine(imageEngine);
        return this;
    }

    public MNImageBrowser setOnClickListener(OnClickListener onClickListener) {
        imageBrowserConfig.setOnClickListener(onClickListener);
        return this;
    }

    public MNImageBrowser setOnDeleteImageListener(OnDeleteImageListener onDeleteImageListener) {
        imageBrowserConfig.setOnDeleteImageListener(onDeleteImageListener);
        return this;
    }

    public MNImageBrowser setOnLongClickListener(OnLongClickListener onLongClickListener) {
        imageBrowserConfig.setOnLongClickListener(onLongClickListener);
        return this;
    }

    public MNImageBrowser setIndicatorType(ImageBrowserConfig.IndicatorType indicatorType) {
        imageBrowserConfig.setIndicatorType(indicatorType);
        return this;
    }

    public MNImageBrowser setShowDelete(boolean showDelete) {
        imageBrowserConfig.setShowDelete(showDelete);
        return this;
    }

    public MNImageBrowser setDiscover(Discover discover) {
        imageBrowserConfig.setDiscover(discover);
        return this;
    }

    public MNImageBrowser isAttention(boolean isAttention) {
        imageBrowserConfig.setAttention(isAttention);
        return this;
    }

    public MNImageBrowser isGood(boolean isGood) {
        imageBrowserConfig.setGood(isGood);
        return this;
    }

    public MNImageBrowser setOnDiscoverListener(OnDiscoverListener onDiscoverListener) {
        imageBrowserConfig.setOnDiscoverListener(onDiscoverListener);
        return this;
    }

    public MNImageBrowser setOtherUserId(long otherUserId) {
        imageBrowserConfig.setOtherUserId(otherUserId);
        return this;
    }

    public void show(View view) {
        //判断是不是空
        if (imageBrowserConfig.getImageList() == null || imageBrowserConfig.getImageList().size() <= 0) {
            return;
        }
        if (imageBrowserConfig.getImageEngine() == null) {
            return;
        }
        if (imageBrowserConfig.getIndicatorType() == null) {
            imageBrowserConfig.setIndicatorType(ImageBrowserConfig.IndicatorType.Indicator_Number);
        }
        MNImageBrowserActivity.imageBrowserConfig = imageBrowserConfig;
        context.startActivity(new Intent(context, MNImageBrowserActivity.class));
    }

    private static void startBrowserAvtivity(Context context, View view, Intent intent) {
        //android V4包的类,用于两个activity转场时的缩放效果实现
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeScaleUpAnimation(view, view.getWidth() / 2, view.getHeight() / 2, 0, 0);
        try {
            ActivityCompat.startActivity(context, intent, optionsCompat.toBundle());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            context.startActivity(intent);
        }
    }

}
