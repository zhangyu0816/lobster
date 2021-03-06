package com.zb.lib_base.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.maning.imagebrowserlibrary.Discover;
import com.maning.imagebrowserlibrary.ImageEngine;
import com.maning.imagebrowserlibrary.listeners.OnClickListener;
import com.maning.imagebrowserlibrary.listeners.OnDeleteImageListener;
import com.maning.imagebrowserlibrary.listeners.OnDiscoverListener;
import com.maning.imagebrowserlibrary.listeners.OnLongClickListener;
import com.maning.imagebrowserlibrary.model.ImageBrowserConfig;
import com.maning.imagebrowserlibrary.transforms.DefaultTransformer;
import com.maning.imagebrowserlibrary.transforms.DepthPageTransformer;
import com.maning.imagebrowserlibrary.transforms.RotateDownTransformer;
import com.maning.imagebrowserlibrary.transforms.RotateUpTransformer;
import com.maning.imagebrowserlibrary.transforms.ZoomInTransformer;
import com.maning.imagebrowserlibrary.transforms.ZoomOutSlideTransformer;
import com.maning.imagebrowserlibrary.transforms.ZoomOutTransformer;
import com.maning.imagebrowserlibrary.view.MNViewPager;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.R;
import com.zb.lib_base.http.CustomProgressDialog;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.utils.water.WaterMark;
import com.zb.lib_base.windows.TextPW;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;


/**
 * ?????????????????????
 */
public class MNImageBrowserActivity extends BaseActivity {


    private MNViewPager viewPagerBrowser;
    private TextView numberIndicator;
    private ImageView ivDelete;

    //????????????
    private ArrayList<String> imageUrlList;
    //????????????
    private int currentPosition;
    //?????????????????????
    private ImageBrowserConfig.TransformType transformType;
    //??????????????????
    public ImageEngine imageEngine;
    //??????
    private OnLongClickListener onLongClickListener;
    private OnClickListener onClickListener;
    private OnDeleteImageListener onDeleteImageListener;
    //??????????????????
    public static ImageBrowserConfig imageBrowserConfig;
    private MyAdapter imageBrowserAdapter;
    private View mCurrentView;
    private boolean isDelete = false;

    private Discover mDiscover;
    private ImageView ivLogoBg;
    private ImageView ivLogo;
    private TextView tvNick;
    private TextView tvTime;
    private TextView tvAttention;
    private TextView tvText;
    private ImageView ivGood;
    private TextView tvGood;
    private TextView tvReview;
    private OnDiscoverListener mOnDiscoverListener;
    private RequestOptions cropOptions;

    private RelativeLayout saveRelative;
    private TextView tvSave;
    private View tvClose;
    private String saveUrl = "";
    private long otherUserId = 0;

    @Override
    public int getRes() {
        return R.layout.activity_mnimage_browser;
    }

    @Override
    public void initUI() {
        cropOptions = new RequestOptions().centerCrop();
        cropOptions.circleCrop();

        initViews();

        initData();

        initViewPager();
    }

    private void initViews() {
        viewPagerBrowser = (MNViewPager) findViewById(R.id.viewPagerBrowser);
        numberIndicator = (TextView) findViewById(R.id.numberIndicator);
        ivDelete = findViewById(R.id.iv_delete);
        ivLogoBg = findViewById(R.id.iv_logo_bg);
        ivLogo = findViewById(R.id.iv_logo);
        tvNick = findViewById(R.id.tv_nick);
        tvTime = findViewById(R.id.tv_time);
        tvAttention = findViewById(R.id.tv_attention);
        tvText = findViewById(R.id.tv_text);

        ivGood = findViewById(R.id.iv_good);
        tvGood = findViewById(R.id.tv_good);
        tvReview = findViewById(R.id.tv_review);
        saveRelative = findViewById(R.id.save_relative);
        tvSave = findViewById(R.id.tv_save);
        tvClose = findViewById(R.id.tv_close);


        findViewById(R.id.iv_back).setOnClickListener(v -> finishBrowser());
        ivDelete.setOnClickListener(v -> {
            if (onDeleteImageListener != null)
                onDeleteImageListener.delete(currentPosition);
            isDelete = true;
            imageBrowserAdapter.destroyItem(viewPagerBrowser, currentPosition, mCurrentView);
            imageUrlList.remove(currentPosition);
            imageBrowserAdapter.notifyDataSetChanged();
            if (imageUrlList.size() == 0) {
                finishBrowser();
            } else {
                if (currentPosition > 0) {
                    currentPosition--;
                }
                numberIndicator.setText((currentPosition + 1) + "/" + imageUrlList.size());
                initViewPager();
                isDelete = false;
            }
        });

        findViewById(R.id.review_linear).setOnClickListener(view -> {
            if (mOnDiscoverListener != null) {
                mOnDiscoverListener.review();
            }
            finishBrowser();
        });

        findViewById(R.id.share_linear).setOnClickListener(view -> {
            if (mOnDiscoverListener != null) {
                mOnDiscoverListener.share();
            }
            finishBrowser();
        });
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        imageUrlList = imageBrowserConfig.getImageList();
        currentPosition = imageBrowserConfig.getPosition();
        transformType = imageBrowserConfig.getTransformType();
        imageEngine = imageBrowserConfig.getImageEngine();
        onClickListener = imageBrowserConfig.getOnClickListener();
        onDeleteImageListener = imageBrowserConfig.getOnDeleteImageListener();
        onLongClickListener = imageBrowserConfig.getOnLongClickListener();
        mOnDiscoverListener = imageBrowserConfig.getOnDiscoverListener();
        mDiscover = imageBrowserConfig.getDiscover();
        otherUserId = imageBrowserConfig.getOtherUserId();
        // ????????????
        if (mDiscover != null) {
            Glide.with(this).load(R.mipmap.empty_icon).apply(cropOptions).into(ivLogoBg);
            Glide.with(this).load(mDiscover.getImage()).apply(cropOptions).into(ivLogo);
            tvNick.setText(mDiscover.getNick());
            tvTime.setText(getTimeToToday(mDiscover.getCreateTime()));
            tvText.setText(mDiscover.getText());
            tvGood.setText(mDiscover.getGoodNum() + "");
            tvReview.setText(mDiscover.getReviews() + "");
        }
        //?????????
        boolean showDelete = imageBrowserConfig.isShowDelete();

        ivDelete.setVisibility(showDelete ? View.VISIBLE : View.GONE);
        if (imageUrlList.size() <= 1) {
            numberIndicator.setVisibility(View.GONE);
        }
        numberIndicator.setText((currentPosition + 1) + "/" + imageUrlList.size());

        findViewById(R.id.iv_top).setVisibility(mDiscover != null ? View.VISIBLE : View.GONE);
        findViewById(R.id.iv_bottom).setVisibility(mDiscover != null ? View.VISIBLE : View.GONE);
        findViewById(R.id.discover_linear).setVisibility(mDiscover != null ? View.VISIBLE : View.GONE);
        if (imageBrowserConfig.isAttention()) {
            tvAttention.setText("?????????");
            tvAttention.setTextColor(Color.WHITE);
            tvAttention.setBackgroundResource(R.drawable.btn_bg_white_trans_radius60);
        }

        tvAttention.setOnClickListener(view -> {
            if (imageBrowserConfig.isAttention()) {
                tvAttention.setText("??????");
                tvAttention.setTextColor(Color.parseColor("#333333"));
                tvAttention.setBackgroundResource(R.drawable.btn_bg_white_radius60);
            } else {
                tvAttention.setText("?????????");
                tvAttention.setTextColor(Color.WHITE);
                tvAttention.setBackgroundResource(R.drawable.btn_bg_white_trans_radius60);
            }
            imageBrowserConfig.setAttention(!imageBrowserConfig.isAttention());
            mOnDiscoverListener.attention();
        });

        if (imageBrowserConfig.isGood()) {
            ivGood.setBackgroundResource(R.drawable.image_good_select);
        }

        findViewById(R.id.good_linear).setOnClickListener(view -> {
            if (imageBrowserConfig.isGood()) {
                ivGood.setBackgroundResource(R.drawable.image_good_unselect);
                tvGood.setText(String.format("%d", Integer.parseInt(tvGood.getText().toString()) - 1));
            } else {
                ivGood.setBackgroundResource(R.drawable.image_good_select);
                tvGood.setText(String.format("%d", Integer.parseInt(tvGood.getText().toString()) + 1));
            }
            imageBrowserConfig.setGood(!imageBrowserConfig.isGood());
            mOnDiscoverListener.good();
        });

        tvSave.setOnClickListener(view -> {
            if (checkPermissionGranted(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                setPermissions();
            } else {
                if (PreferenceUtil.readIntValue(activity, "writePermission") == 0) {
                    PreferenceUtil.saveIntValue(activity, "writePermission", 1);
                    new TextPW(activity, viewPagerBrowser, "????????????",
                            "???????????????????????????????????????????????????" +
                                    "\n 1?????????????????????--???????????????????????????" +
                                    "\n 2????????????????????????????????????????????????????????????????????????????????????????????????" +
                                    "\n 3??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????/?????????" +
                                    "\n 4????????????????????????????????????--??????--??????--????????????app????????????--??????--????????????--????????????????????????????????????????????????",
                            "??????", false, true, this::getPermissions);
                } else {
                    SCToastUtil.showToast(activity, "??????????????????????????????????????????--??????--????????????--??????????????????", true);
                }
            }
            saveRelative.setVisibility(View.GONE);
        });
        tvClose.setOnClickListener(view -> saveRelative.setVisibility(View.GONE));
    }

    private boolean checkPermissionGranted(RxAppCompatActivity activity, String... permissions) {
        boolean flag = true;
        for (String p : permissions) {
            if (ActivityCompat.checkSelfPermission(activity, p) != PackageManager.PERMISSION_GRANTED) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    /**
     * ??????
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            performCodeWithPermission("??????????????????????????????", new BaseActivity.PermissionCallback() {
                @Override
                public void hasPermission() {
                    setPermissions();
                }

                @Override
                public void noPermission() {
                    PreferenceUtil.saveIntValue(activity, "writePermission", 1);
                }
            }, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else {
            setPermissions();
        }
    }


    private void setPermissions() {
        CustomProgressDialog.showLoading(activity, "????????????");
        WaterMark.getInstance().saveImage(activity, otherUserId, saveUrl);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initViewPager() {
        imageBrowserAdapter = new MyAdapter();
        viewPagerBrowser.setAdapter(imageBrowserAdapter);
        viewPagerBrowser.setCurrentItem(currentPosition);
        setViewPagerTransforms();
        viewPagerBrowser.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.e("", position + "");
                Log.e("", positionOffset + "");
                Log.e("", positionOffsetPixels + "");
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onPageSelected(int position) {
                if (!isDelete) {
                    currentPosition = position;
                    numberIndicator.setText((position + 1) + "/" + imageUrlList.size());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.e("", state + "");
            }
        });
    }

    private void setViewPagerTransforms() {
        if (transformType == ImageBrowserConfig.TransformType.Transform_Default) {
            viewPagerBrowser.setPageTransformer(true, new DefaultTransformer());
        } else if (transformType == ImageBrowserConfig.TransformType.Transform_DepthPage) {
            viewPagerBrowser.setPageTransformer(true, new DepthPageTransformer());
        } else if (transformType == ImageBrowserConfig.TransformType.Transform_RotateDown) {
            viewPagerBrowser.setPageTransformer(true, new RotateDownTransformer());
        } else if (transformType == ImageBrowserConfig.TransformType.Transform_RotateUp) {
            viewPagerBrowser.setPageTransformer(true, new RotateUpTransformer());
        } else if (transformType == ImageBrowserConfig.TransformType.Transform_ZoomIn) {
            viewPagerBrowser.setPageTransformer(true, new ZoomInTransformer());
        } else if (transformType == ImageBrowserConfig.TransformType.Transform_ZoomOutSlide) {
            viewPagerBrowser.setPageTransformer(true, new ZoomOutSlideTransformer());
        } else if (transformType == ImageBrowserConfig.TransformType.Transform_ZoomOut) {
            viewPagerBrowser.setPageTransformer(true, new ZoomOutTransformer());
        } else {
            viewPagerBrowser.setPageTransformer(true, new DefaultTransformer());
        }
    }

    private void finishBrowser() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        finish();
    }

    private String getTimeToToday(String strDate) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long between;
        try {
            Date end = new Date();
            Date begin = dfs.parse(strDate);
            between = (end.getTime() - begin.getTime());// ????????????????????????
        } catch (ParseException e) {
            return "";
        }
        long day = between / (24 * 60 * 60 * 1000);
        long hour = (between / (60 * 60 * 1000) - day * 24);
        long min = ((between / (60 * 1000)) - day * 24 * 60 - hour * 60);
        if (day > 0) {
            if (day < 7)
                return day + "??????";
            else if (day < 14)
                return "1?????????";
            else if (day < 21)
                return "2?????????";
            else if (day < 28)
                return "3?????????";
            else
                return "1?????????";
        }
        if (hour > 0) {
            return hour + "?????????";
        }
        if (min > 0) {
            return min + "?????????";
        } else {
            return "??????";
        }
    }

    @Override
    public void onBackPressed() {
        finishBrowser();
    }


    private class MyAdapter extends PagerAdapter {


        private LayoutInflater layoutInflater;

        public MyAdapter() {
            layoutInflater = LayoutInflater.from(activity);
        }

        @Override
        public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            mCurrentView = (View) object;
        }

        @Override
        public int getCount() {
            return imageUrlList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {
            View inflate = layoutInflater.inflate(R.layout.mn_image_browser_item_show_image, container, false);
            final PhotoView imageView = (PhotoView) inflate.findViewById(R.id.imageView);
            final String url = imageUrlList.get(position);
            //????????????
            imageEngine.loadImage(activity, url, imageView);

            imageView.setOnClickListener(view -> {
                //????????????
                if (onClickListener != null) {
                    onClickListener.onClick(activity, imageView, position, url);
                }
            });

            imageView.setOnLongClickListener(view -> {
                if (otherUserId != 0) {
                    saveUrl = url;
                    saveRelative.setVisibility(View.VISIBLE);
                }
                return false;
            });

            container.addView(inflate);
            return inflate;
        }
    }

}
