package com.app.abby.xbanner;

import android.content.Context;
import android.widget.ImageView;
import android.widget.VideoView;

/**
 * Created by Abby on 9/22/2017.
 */

public interface ImageLoader {
    void loadImages(Context context, Ads ads, ImageView image);

    void loadVideoViews(Context context, Ads ads, VideoView videoView);
}
