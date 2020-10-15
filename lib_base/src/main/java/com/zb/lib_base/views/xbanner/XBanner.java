package com.zb.lib_base.views.xbanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.app.abby.xbanner.XBPagerAdapter;
import com.app.abby.xbanner.XBannerScroller;
import com.zb.lib_base.R;
import com.zb.lib_base.databinding.XbannerBinding;
import com.zb.lib_base.model.Ads;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.IntDef;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

public class XBanner extends RelativeLayout {

    public int delayTime = 3000;
    public int titleTextSize = 16;
    public int sizeNumIndicator = 12;
    public int indicatorMargin = 10;
    public int titleHeight = 100;
    public int titleMarginStart = 20;

    public int pageTransformerDelayIdle = 600;

    //transform delay set to 250ms when dragging
    public static final int PAGE_TRANSFORM_DELAY_DRAGGING = 250;

    //indicator gravity types
    public static final int INDICATOR_START = 0;
    public static final int INDICATOR_CENTER = 1;
    public static final int INDICATOR_END = 2;

    //banner styles
    public static final int CIRCLE_INDICATOR = 0;
    public static final int CIRCLE_INDICATOR_TITLE = 1;
    public static final int CUBE_INDICATOR = 2;
    public static final int NUM_INDICATOR = 3;
    public static final int NUM_INDICATOR_TITLE = 4;

    private Context mContext;
    private int mTitleHeight;
    private int mTitleWidth;
    private int mIndicatorSelected;
    private int mIndicatorUnselected;
    private int mDelayTime;
    private boolean mIsAutoPlay;
    private boolean mIsPlaying;
    private boolean mIsTitlebgAlpha;
    private boolean mIndicatorSet;
    private int mSizeTitleText;
    private int mColorTitle;
    private int mGravity;
    private int mImageCount;
    private int mBannerType;
    private ImageView.ScaleType mScaleType;

    private XBPagerAdapter mAdapter;
    private TextView mBannerTitle;
    private TextView mNumIndicator;
    private List<ImageView> mIndicators;
    private List<View> mBannerImages;
    private List<String> mTitles;
    private List<String> mUrls;
    private List<View> adViewList;
    private XBPagerAdapter.BannerPageListener mBannerPageListner;
    private ImageLoader mImageLoader;

    private static Handler mHandler = new Handler();
    private XBannerScroller xbannerScroller;
    private ViewPagerRunnable mRunnable;
    private List<Ads> adsList;


    public XBanner(Context context) {
        super(context);
        mContext = context;
        initValues();
    }


    public XBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initValues();
        getTypeArrayValue(context, attrs);
    }

    public XBanner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initValues();
        getTypeArrayValue(context, attrs);


    }

    /**
     * Set the gravity of the indicators,START by default
     * Gravity will be END when with a title inside
     * to change the gravity of the indicator
     * Use {@link #setIndicatorGravity(int gravity)}
     */
    @IntDef({INDICATOR_START, INDICATOR_CENTER, INDICATOR_END})
    @Retention(RetentionPolicy.SOURCE)
    public @interface INDICATOR_GRAVITY {
    }

    public XBanner setIndicatorGravity(@INDICATOR_GRAVITY int gravity) {
        mGravity = gravity;
        return this;
    }

    /**
     * Set the indicator type here
     */
    @IntDef({CIRCLE_INDICATOR, CIRCLE_INDICATOR_TITLE, CUBE_INDICATOR, NUM_INDICATOR, NUM_INDICATOR_TITLE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface BANNER_TYPE {
    }

    public XBanner setBannerTypes(@BANNER_TYPE int bannerType) {
        mBannerType = bannerType;
        return this;
    }

    private void getTypeArrayValue(Context context, AttributeSet attr) {
        if (attr == null) {
            return;
        }
        TypedArray typedArray = context.obtainStyledAttributes(attr, com.app.abby.xbanner.R.styleable.XBanner);

        mTitleHeight = typedArray.getDimensionPixelSize(com.app.abby.xbanner.R.styleable.XBanner_title_height, titleHeight);
        mDelayTime = typedArray.getInteger(com.app.abby.xbanner.R.styleable.XBanner_delay_time, delayTime);
        mIsAutoPlay = typedArray.getBoolean(com.app.abby.xbanner.R.styleable.XBanner_is_auto_play, false);
        mSizeTitleText = typedArray.getInteger(com.app.abby.xbanner.R.styleable.XBanner_size_title_text, titleTextSize);
        mGravity = typedArray.getInteger(com.app.abby.xbanner.R.styleable.XBanner_indicator_gravity, INDICATOR_CENTER);
        mColorTitle = typedArray.getColor(com.app.abby.xbanner.R.styleable.XBanner_color_title, Color.WHITE);
        typedArray.recycle();

    }

    private void initValues() {

        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        mTitleWidth = dm.widthPixels * 3 / 4;
        mImageCount = 0;
        mDelayTime = 4000;


        mBannerType = CIRCLE_INDICATOR;
        mColorTitle = Color.WHITE;
        mGravity = INDICATOR_CENTER;
        mScaleType = ImageView.ScaleType.FIT_XY;

        mIndicators = new ArrayList<>();
        mBannerImages = new ArrayList<>();
        mTitles = new ArrayList<>();
        mUrls = new ArrayList<>();
        adViewList = new ArrayList<>();
        adsList = new ArrayList<>();

        mIsPlaying = false;
        mIsTitlebgAlpha = false;
        mIndicatorSet = false;


    }

    private void initView() {

        bindView();
        //Need to set banner type to title types to avoid some logic errors

        String exceptionTitle = "XBanner: " + "Banner type must be set to CIRCLE_INDICATOR_TITLE or NUM_INDICATOR_TITLE to set titles" + ",the default banner type is set to CIRCLE_INDICATOR.";
        initScroller();
        if (mBannerType == CIRCLE_INDICATOR_TITLE || mBannerType == NUM_INDICATOR_TITLE) {
            initViewforTitleType();
        } else if (mTitles.size() > 0) {
            throw new RuntimeException(exceptionTitle);
        }
        initIndicatorContainer();
        initViewPagerAdapter();
    }

    private void initViewforTitleType() {
        setIndicatorGravity(INDICATOR_END);
        initBannerTitle();
        addView(mBannerTitle);
    }

    private void initScroller() {

        try {
            Field xScroller = ViewPager.class.getDeclaredField("mScroller");
            xScroller.setAccessible(true);
            xbannerScroller = new XBannerScroller(mContext, new DecelerateInterpolator());
            xbannerScroller.setDuration(600);
            xScroller.set(binding.viewpager, xbannerScroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void removeScroller() {
        try {
            Field xScroller = ViewPager.class.getDeclaredField("mScroller");
            xScroller.setAccessible(true);
            xScroller.set(binding.viewpager, null);
            xbannerScroller = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Attention,this method must be called before the images set
     * or it does not work
     * the default scale type is FIT_XY
     */
    public XBanner setImageScaleType(ImageView.ScaleType scaleType) {
        mScaleType = scaleType;
        return this;
    }


    /**
     * Create indicators,must be called after banner images set
     *
     * @return ImageView list of indicators
     */
    private List<ImageView> createIndicators() {
        List<ImageView> images = new ArrayList<>();
        for (int i = 0; i < mImageCount; i++) {
            images.add(newIndicator(i));
        }
        return images;
    }


    /**
     * new an indicator with the given resId
     *
     * @param index the index of the Image
     */
    private ImageView newIndicator(int index) {
        ImageView indicator = new ImageView(mContext);
        LinearLayout.LayoutParams params;

        params = new LinearLayout.LayoutParams(14, 14);
        if (mIndicatorSet) {
            indicator.setImageResource(index == 0 ? mIndicatorSelected : mIndicatorUnselected);
        } else if (mBannerType == CIRCLE_INDICATOR_TITLE || mBannerType == CIRCLE_INDICATOR) {
            indicator.setImageResource(index == 0 ? com.app.abby.xbanner.R.drawable.indicator_selected : com.app.abby.xbanner.R.drawable.indicator_unselected);
        } else if (mBannerType == CUBE_INDICATOR) {
            indicator.setImageResource(index == 0 ? com.app.abby.xbanner.R.drawable.indicator_cube_selected : com.app.abby.xbanner.R.drawable.indicator_cube_unselected);
        }
        params.leftMargin = indicatorMargin;
        params.rightMargin = indicatorMargin;
        indicator.setLayoutParams(params);
        return indicator;
    }

    @SuppressLint("SetTextI18n")
    private TextView createNumIndicator() {
        TextView indicator = new TextView(mContext);
        indicator.setTextSize(sizeNumIndicator);
        indicator.setText("1/" + mImageCount);
        indicator.setTextColor(Color.WHITE);
        return indicator;
    }


    public XBanner setImageRes(int[] images) {

        mImageCount = images.length;
        if (mImageCount > 1) {
            mBannerImages.add(newImageFromRes(images[mImageCount - 1]));
            for (int i = 0; i < mImageCount; i++) {
                mBannerImages.add(newImageFromRes(images[i]));
            }
        }
        mBannerImages.add(newImageFromRes(images[0]));

        return this;
    }

    private ImageView newImageFromRes(int res) {
        ImageView image = new ImageView(mContext);
        image.setImageResource(res);
        image.setScaleType(mScaleType);
        return image;
    }

    public XBanner setAds(List<Ads> adsList) {
        this.adsList = adsList;
        for (Ads ads : adsList) {
            mUrls.add(ads.getSmallImage());
            adViewList.add(ads.getView());
            mTitles.add("");
        }
        if (mImageCount == 0) {
            mImageCount = adsList.size();
        }
        return this;
    }

    public XBanner setDelay(int delay) {
        //default delay time is 3000ms
        if (mDelayTime < 0) {
            mDelayTime = titleTextSize;
        } else {
            mDelayTime = delay;
        }
        return this;
    }

    /**
     * Set the res of indicator manually
     * the width and height can be set
     *
     * @param selected   the res of indicator when selected
     * @param unselected the res of indicator when unselected
     */

    public XBanner setUpIndicators(int selected, int unselected) {
        mIndicatorSelected = selected;
        mIndicatorUnselected = unselected;
        mIndicatorSet = true;
        return this;
    }

    public XBanner isAutoPlay(boolean isAutoPlay) {
        mIsAutoPlay = isAutoPlay;
        return this;
    }

    private void showIndicators() {

        if (mBannerType == NUM_INDICATOR || mBannerType == NUM_INDICATOR_TITLE) {
            mNumIndicator = createNumIndicator();
            if (mBannerType == NUM_INDICATOR) {
                applyIndicatorGravity();
            } else {
                binding.indicatorContainer.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
            }
            binding.indicatorContainer.addView(mNumIndicator);

        } else {
            mIndicators.addAll(createIndicators());
            if (mIndicators.size() == 1) {
                binding.indicatorContainer.setVisibility(GONE);
            } else {
                binding.indicatorContainer.setVisibility(VISIBLE);
                for (int i = 0; i < mIndicators.size(); i++) {
                    binding.indicatorContainer.addView(mIndicators.get(i));
                }
            }
        }

    }

    private void applyIndicatorGravity() {
        if (mGravity == INDICATOR_START) {
            binding.indicatorContainer.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        } else if (mGravity == INDICATOR_CENTER) {
            binding.indicatorContainer.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        } else if (mGravity == INDICATOR_END) {
            binding.indicatorContainer.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        }
    }


    private void initBannerTitle() {

        mBannerTitle = new TextView(mContext);
        mBannerTitle.setTextColor(mColorTitle);
        mBannerTitle.setText(mTitles.get(0));
        mBannerTitle.setGravity(Gravity.CENTER_VERTICAL);
        mBannerTitle.setSingleLine();
        mBannerTitle.setTextSize(mSizeTitleText);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mTitleWidth, mTitleHeight);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.leftMargin = titleMarginStart;
        mBannerTitle.setLayoutParams(params);

    }

    private void initIndicatorContainer() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.indicatorContainer.getLayoutParams();
        params.height = 40;
    }

    private XbannerBinding binding;
    private GradientDrawable gradientDrawable;
    private int[] colors = {0x00000000, 0x00000000};

    @SuppressLint("WrongConstant")
    private void bindView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.xbanner, this, true);
        gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
        float[] radii = new float[8];
        radii[0] = radii[1] = 20f;
        radii[2] = radii[3] = 20f;
        radii[4] = radii[5] = 0f;
        radii[6] = radii[7] = 0f;
        gradientDrawable.setCornerRadii(radii);
        gradientDrawable.setGradientType(GradientDrawable.RECTANGLE);

        setBgRes(type - 1);
        if (mIsTitlebgAlpha) {
            binding.indicatorContainer.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private boolean showBg = false;
    private int type = 0;


    public void setBannerBg(int position) {
        if (showBg && type == 0) {
            setBgRes(position - 1);
//            mImageLoader.getPosition(position - 1);
        }
    }

    public XBanner setShowBg(boolean showBg) {
        this.showBg = showBg;
        return this;
    }

    public XBanner setType(int type) {
        this.type = type;
        return this;
    }

    private void setBgRes(int position) {
        if (position == -1) {
            colors = new int[]{Color.argb(0, 0, 0, 0), Color.argb(0, 0, 0, 0)};
        } else if (position == 0) {
            colors = new int[]{Color.argb(255, 255, 221, 176), Color.argb(255, 255, 255, 255)};
        } else if (position == 1) {
            colors = new int[]{Color.argb(255, 176, 193, 255), Color.argb(255, 255, 255, 255)};
        } else if (position == 2) {
            colors = new int[]{Color.argb(255, 255, 176, 190), Color.argb(255, 255, 255, 255)};
        } else if (position == 3) {
            colors = new int[]{Color.argb(255, 194, 176, 255), Color.argb(255, 255, 255, 255)};
        } else if (position == 4) {
            colors = new int[]{Color.argb(255, 198, 255, 176), Color.argb(255, 255, 255, 255)};
        } else if (position == 5) {
            colors = new int[]{Color.argb(255, 255, 203, 176), Color.argb(255, 255, 255, 255)};
        } else {
            colors = new int[]{Color.argb(255, 176, 188, 255), Color.argb(255, 255, 255, 255)};
        }
        gradientDrawable.setColors(colors);
        binding.viewpager.setBackground(gradientDrawable);
    }

    private void initViewPagerAdapter() {

        if (mAdapter == null) {
            mAdapter = new XBPagerAdapter(mBannerPageListner, mImageCount);
        }
        binding.viewpager.setAdapter(mAdapter);
        mAdapter.setData(mBannerImages);
        if (mImageCount > 1) {
            binding.viewpager.setCurrentItem(1);
        } else {
            binding.viewpager.setCurrentItem(0);
        }
        setBannerBg(binding.viewpager.getCurrentItem());
    }

    /**
     * Get the true position of the indicator
     *
     * @param pos the pos of the viewpager now
     */
    private int getTruePos(int pos) {
        //get the position of the indicator
        int truepos = (pos - 1) % mImageCount;
        if (truepos < 0) {
            truepos = mImageCount - 1;
        }
        return truepos;
    }

    /**
     * Config the viewpager listener to the viewpager
     * Only if the image count is greater than 1
     */

    private void setBg(int position, float positionOffset) {
        if (positionOffset == 0) return;
        if (position == 1) {
            int red = (int) ((255 - 176) * positionOffset);
            int green = (int) ((221 - 193) * positionOffset);
            int blue = (int) ((255 - 176) * positionOffset);
            colors = new int[]{Color.argb(255, 255 - red, 221 - green, 176 + blue), Color.argb(255, 255, 255, 255)};
        } else if (position == 2) {
            int red = (int) ((255 - 176) * positionOffset);
            int green = (int) ((193 - 176) * positionOffset);
            int blue = (int) ((255 - 190) * positionOffset);
            colors = new int[]{Color.argb(255, 176 + red, 193 - green, 255 - blue), Color.argb(255, 255, 255, 255)};
        } else if (position == 3) {
            int red = (int) ((255 - 194) * positionOffset);
            int blue = (int) ((255 - 190) * positionOffset);
            colors = new int[]{Color.argb(255, 255 - red, 176, 190 + blue), Color.argb(255, 255, 255, 255)};
        } else if (position == 4) {
            int red = (int) ((198 - 194) * positionOffset);
            int green = (int) ((255 - 176) * positionOffset);
            int blue = (int) ((255 - 176) * positionOffset);
            colors = new int[]{Color.argb(255, 194 + red, 176 + green, 255 - blue), Color.argb(255, 255, 255, 255)};
        } else if (position == 5) {
            int red = (int) ((255 - 198) * positionOffset);
            int green = (int) ((255 - 203) * positionOffset);
            colors = new int[]{Color.argb(255, 198 + red, 255 - green, 176), Color.argb(255, 255, 255, 255)};
        } else if (position == 6) {
            int red = (int) ((255 - 176) * positionOffset);
            int green = (int) ((203 - 188) * positionOffset);
            int blue = (int) ((255 - 176) * positionOffset);
            colors = new int[]{Color.argb(255, 255 - red, 203 - green, 176 + blue), Color.argb(255, 255, 255, 255)};
        } else {
            // 最后一位和第一位左右滑动
            int red = (int) ((255 - 176) * positionOffset);
            int green = (int) ((211 - 188) * positionOffset);
            int blue = (int) ((255 - 176) * positionOffset);
            colors = new int[]{Color.argb(255, 176 + red, 188 + green, 255 - blue), Color.argb(255, 255, 255, 255)};
        }
//        if (position == -1) {
//            colors = new int[]{Color.argb(0, 0, 0, 0), Color.argb(0, 0, 0, 0)};
//        } else if (position == 0) {
//            colors = new int[]{Color.argb(255, 255, 221, 176), Color.argb(255, 255, 255, 255)};
//        } else if (position == 1) {
//            colors = new int[]{Color.argb(255, 176, 193, 255), Color.argb(255, 255, 255, 255)};
//        } else if (position == 2) {
//            colors = new int[]{Color.argb(255, 255, 176, 190), Color.argb(255, 255, 255, 255)};
//        } else if (position == 3) {
//            colors = new int[]{Color.argb(255, 194, 176, 255), Color.argb(255, 255, 255, 255)};
//        } else if (position == 4) {
//            colors = new int[]{Color.argb(255, 198, 255, 176), Color.argb(255, 255, 255, 255)};
//         } else if (position == 5) {
//            colors = new int[]{Color.argb(255, 255, 203, 176), Color.argb(255, 255, 255, 255)};
//        } else {
//            colors = new int[]{Color.argb(255, 176, 188, 255), Color.argb(255, 255, 255, 255)};
//        }
        gradientDrawable.setColors(colors);
        binding.viewpager.setBackground(gradientDrawable);
    }

    private void applyViewPagerAdapterListener() {
        if (mImageCount <= 1) {
            return;
        }
        binding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (showBg)
                    setBg(position, positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
                onIndicatorChange(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                int current = binding.viewpager.getCurrentItem();
                switch (state) {
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        if (mBannerPageListner != null) {
                            mBannerPageListner.onBannerDragging(current);
                        }
                        if (xbannerScroller != null) {
                            xbannerScroller.setDuration(PAGE_TRANSFORM_DELAY_DRAGGING);
                        }
                        if (current == 0) {
                            binding.viewpager.setCurrentItem(mImageCount, false);
                        }
                        if (current == mImageCount + 1) {
                            binding.viewpager.setCurrentItem(1, false);
                        }
                        setBannerBg(binding.viewpager.getCurrentItem());
                        break;
                    case ViewPager.SCROLL_STATE_IDLE:
                        if (mBannerPageListner != null) {
                            mBannerPageListner.onBannerIdle(current);
                        }
                        if (xbannerScroller != null) {
                            xbannerScroller.setDuration(pageTransformerDelayIdle);
                        }
                        if (current == mImageCount + 1) {
                            binding.viewpager.setCurrentItem(1, false);
                        } else if (current == 0) {
                            binding.viewpager.setCurrentItem(mImageCount, false);
                        }
                        setBannerBg(binding.viewpager.getCurrentItem());
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void onIndicatorChange(int position) {

        switch (mBannerType) {
            case CIRCLE_INDICATOR:
                onCircleIndicatorChange(position);
                break;

            case CIRCLE_INDICATOR_TITLE:
                onCircleIndicatorChange(position);
                mBannerTitle.setText(mTitles.get(getTruePos(position)));
                break;

            case CUBE_INDICATOR:
                onCubeIndicatorChange(position);
                break;

            case NUM_INDICATOR:
                onNumIndicatorChange(position);
                break;

            case NUM_INDICATOR_TITLE:
                onNumIndicatorChange(position);
                mBannerTitle.setText(mTitles.get(getTruePos(position)));
                break;
            default:
                break;
        }
    }


    private void onCircleIndicatorChange(int position) {

        mIndicators.get(getTruePos(position)).setImageResource(mIndicatorSet ? mIndicatorSelected : com.app.abby.xbanner.R.drawable.indicator_selected);
        for (int i = 0; i < mIndicators.size(); i++) {
            if (i != getTruePos(position)) {
                mIndicators.get(i).setImageResource(mIndicatorSet ? mIndicatorUnselected : com.app.abby.xbanner.R.drawable.indicator_unselected);
            }
        }
    }

    private void onCubeIndicatorChange(int position) {

        mIndicators.get(getTruePos(position)).setImageResource(mIndicatorSet ? mIndicatorSelected : com.app.abby.xbanner.R.drawable.indicator_cube_selected);
        for (int i = 0; i < mIndicators.size(); i++) {
            if (i != getTruePos(position)) {
                mIndicators.get(i).setImageResource(mIndicatorSet ? mIndicatorUnselected : com.app.abby.xbanner.R.drawable.indicator_cube_unselected);
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private void onNumIndicatorChange(int position) {
        int i = position;
        if (i == 0) {
            i = mImageCount;
        }
        if (position > mImageCount) {
            i = 1;
        }
        mNumIndicator.setText(i + "/" + mImageCount);
    }

    /**
     * start this banner
     */
    public void start() {

        checkImageAndTitleNum();
        loadFromUrlsIfNeeded();
        initView();
        showIndicators();
        applyViewPagerAdapterListener();
        startPlayIfNeeded();

    }


    private void startPlay() {
        if (mIsAutoPlay) {
            mIsPlaying = true;
            mRunnable = new ViewPagerRunnable(binding.viewpager, mImageCount, mDelayTime);

            mHandler.postDelayed(mRunnable, mDelayTime);
        }
    }

    private void startPlayIfNeeded() {
        if (mImageCount > 1 && mIsAutoPlay) {
            startPlay();
        }
    }


    /**
     * check the number of titles and images
     * titles and images must have the same size to avoid some logic error
     */
    private void checkImageAndTitleNum() {

        if (mImageCount != mTitles.size() && (mBannerType == CIRCLE_INDICATOR_TITLE || mBannerType == NUM_INDICATOR_TITLE)) {
            throw new RuntimeException("image size and title size is not the same\n"
                    + "size of images: " + mImageCount + "\n"
                    + "size of titles: " + mTitles.size() + "\n"
                    + "if you do Not need titles,please set the banner type to non-title type");
        }
    }


    /**
     * Load image from urls
     * need to apply an imageloader,see{@link ImageLoader}
     */
    private void loadFromUrlsIfNeeded() {
        if (mImageLoader == null) {
            return;
        }
        if (showBg) {
            if (mBannerImages.isEmpty() && !adViewList.isEmpty()) {
                if (mImageCount > 1) {
                    mBannerImages.add(newView(adsList.get(mImageCount - 1)));
                    for (int i = 0; i < adViewList.size(); i++) {
                        mBannerImages.add(newView(adsList.get(i)));
                    }
                }
                mBannerImages.add(newView(adsList.get(0)));
            }
        } else {
            if (mBannerImages.isEmpty() && !mUrls.isEmpty()) {
                if (mImageCount > 1) {

                    Ads ads = adsList.get(mImageCount - 1);
                    if (ads.getSmallImage().contains(".mp4"))
                        mBannerImages.add(newVideoViewFroUrl(ads));
                    else
                        mBannerImages.add(newImageFroUrl(ads, mImageCount - 1));

                    for (int i = 0; i < mUrls.size(); i++) {
                        Ads ads1 = adsList.get(i);
                        if (ads1.getSmallImage().contains(".mp4"))
                            mBannerImages.add(newVideoViewFroUrl(ads1));
                        else
                            mBannerImages.add(newImageFroUrl(ads1, i));
                    }
                    Ads ads2 = adsList.get(0);
                    if (ads2.getSmallImage().contains(".mp4"))
                        mBannerImages.add(newVideoViewFroUrl(ads2));
                    else
                        mBannerImages.add(newImageFroUrl(ads2, 0));
                } else {
                    Ads ads = adsList.get(0);
                    if (ads.getSmallImage().contains(".mp4"))
                        mBannerImages.add(newVideoViewFroUrl(ads));
                    else
                        mBannerImages.add(newImageFroUrl(ads, 0));
                }
            }
        }
    }


    private ImageView newImageFroUrl(Ads ads, int position) {
        ImageView image = new ImageView(mContext);
        image.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        image.setScaleType(mScaleType);
        mImageLoader.loadImages(mContext, ads, image, position);
        return image;
    }

    private VideoView newVideoViewFroUrl(Ads ads) {
        VideoView videoView = new VideoView(mContext);
        videoView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mImageLoader.loadVideoViews(mContext, ads, videoView);
        return videoView;
    }

    private View newView(Ads ads) {
        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mImageLoader.loadView(linearLayout, ads.getView());
        return ads.getView();
    }

    /**
     * the listener interface for banner event,includeing clicked,dragging and idled
     * the index starts from 0,which item's value starts from 0
     */


    public XBanner setBannerPageListener(XBPagerAdapter.BannerPageListener listener) {
        mBannerPageListner = listener;
        return this;
    }

    public XBanner setImageLoader(ImageLoader imageLoader) {
        mImageLoader = imageLoader;
        return this;
    }


    private class ViewPagerRunnable implements Runnable {
        //avoid memory leak
        private WeakReference<ViewPager> mViewPager;
        int count;
        int delaytime;

        ViewPagerRunnable(ViewPager viewPager, int imagecount, int delay) {
            mViewPager = new WeakReference<>(viewPager);
            count = imagecount;
            delaytime = delay;
        }

        @Override
        public void run() {
            if (count > 1) {
                if (mViewPager.get() != null) {
                    int current = mViewPager.get().getCurrentItem();
                    if (current == count + 1) {
                        mViewPager.get().setCurrentItem(1, false);
                        mHandler.post(this);
                    } else {
                        mViewPager.get().setCurrentItem(current + 1);
                        mHandler.postDelayed(this, delaytime);
                    }
                    XBanner.this.setBannerBg(mViewPager.get().getCurrentItem());
                }

            }

        }


    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        super.dispatchTouchEvent(me);
        if (me.getAction() == MotionEvent.ACTION_DOWN) {
            if (mIsPlaying) {
                mHandler.removeCallbacks(mRunnable);
                mIsPlaying = false;
            }
        }

        if (me.getAction() == MotionEvent.ACTION_UP || me.getAction() == MotionEvent.ACTION_CANCEL
                || me.getAction() == MotionEvent.ACTION_OUTSIDE && !mIsPlaying && mIsAutoPlay) {
            mIsPlaying = true;
            startPlay();
        }
        return true;
    }

    //release banner here
    public void releaseBanner() {

        if (mBannerImages != null) {
            mBannerImages.clear();
        }

        if (mIndicators != null) {
            mIndicators.clear();
        }

        mHandler.removeCallbacks(mRunnable);
        mRunnable = null;
        if (mAdapter != null)
            mAdapter.releaseAdapter();
        mAdapter = null;
        mBannerPageListner = null;
        mImageLoader = null;
        if (binding != null) {
            if (binding.viewpager != null)
                binding.viewpager.setAdapter(null);
            try {
                Field f = ViewPager.class.getDeclaredField("mOnPageChangeListeners");
                f.setAccessible(true);
                f.set(binding.viewpager, null);
            } catch (Exception e) {
                e.printStackTrace();
            }

            binding.viewpager.removeAllViews();
        }
        System.gc();
        System.runFinalization();

    }

}







