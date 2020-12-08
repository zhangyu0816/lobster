package com.zb.lib_base.views.xbanner;

import android.widget.ImageView;

import com.zb.lib_base.R;
import com.zb.lib_base.model.Ads;

import java.util.ArrayList;
import java.util.List;

public class XUtils {
    private static ArrayList<String> imageList = new ArrayList<>();

    public static void showBanner(XBanner view, List<Ads> adList, int bannerType, ImageLoader imageLoader, CallBack callBack, XBanner.CallBack positionCallBack) {
        imageList.clear();
        if (callBack != null) {
            for (Ads item : adList) {
                imageList.add(item.getSmallImage());
            }
        }
        try {
            view.setImageScaleType(ImageView.ScaleType.FIT_CENTER)
                    .setAds(adList)
                    .setBannerTypes(bannerType)
                    .setImageLoader(imageLoader)
                    .setBannerPageListener(item -> {
                        if (callBack != null)
                            callBack.click(item, imageList);
                    })
                    .setIndicatorGravity(XBanner.INDICATOR_START)
                    .setDelay(3000)
                    .setUpIndicators(R.drawable.banner_circle_pressed, R.drawable.banner_circle_unpressed)
                    .isAutoPlay(false)
                    .setCallBack(positionCallBack)
                    .start();
        } catch (Exception ignored) {

        }
    }

    public interface CallBack {
        void click(int position, ArrayList<String> imageList);
    }
}
