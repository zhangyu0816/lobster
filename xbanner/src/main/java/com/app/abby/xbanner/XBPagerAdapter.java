package com.app.abby.xbanner;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.viewpager.widget.PagerAdapter;

/**
 * Created by DIY on 2017-12-18.
 */

public class XBPagerAdapter extends PagerAdapter {
    List<View> mData;
    XBanner.BannerPageListener mBannerPageListner;
    int mImageCount;
    View view;


    XBPagerAdapter(XBanner.BannerPageListener listener, int imagecount) {
        mData = new ArrayList<>();
        mBannerPageListner = listener;
        mImageCount = imagecount;
    }

    /**
     * Add data,will not clear the data already exists
     *
     * @param data the ImageViews to be added
     */
    public void addData(List<View> data) {
        if (mData != null) {
            mData.addAll(data);
            notifyDataSetChanged();
        }
    }

    /**
     * Reset the data
     */
    public void setData(List<View> data) {

        if (mData != null) {
            mData.clear();
            mData.addAll(data);
            notifyDataSetChanged();
        }

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public int getCount() {
        return mData.size();
    }


    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        container.addView(mData.get(position));
        view = mData.get(position);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBannerPageListner != null) {
                    mBannerPageListner.onBannerClick(getTruePos(position));
                }
            }
        });
        return mData.get(position);
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (mData != null && mData.size() > 0) {
            container.removeView(mData.get(position));
        }

    }

    private int getTruePos(int pos) {
        //get the position of the indicator
        int truepos = (pos - 1) % mImageCount;
        if (truepos < 0) {
            truepos = mImageCount - 1;
        }
        return truepos;
    }

    public void releaseAdapter() {

        mBannerPageListner = null;
        if (mData != null && mData.size() > 0) {
            for (int i = 0; i < mData.size(); i++) {
                mData.get(i).setOnClickListener(null);
            }
            mData.clear();
        }

        view.setOnClickListener(null);
        view = null;
    }
}
