package com.maning.imagebrowserlibrary;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.maning.imagebrowserlibrary.listeners.OnClickListener;
import com.maning.imagebrowserlibrary.listeners.OnDeleteImageListener;
import com.maning.imagebrowserlibrary.listeners.OnLongClickListener;
import com.maning.imagebrowserlibrary.model.ImageBrowserConfig;
import com.maning.imagebrowserlibrary.transforms.DefaultTransformer;
import com.maning.imagebrowserlibrary.transforms.DepthPageTransformer;
import com.maning.imagebrowserlibrary.transforms.RotateDownTransformer;
import com.maning.imagebrowserlibrary.transforms.RotateUpTransformer;
import com.maning.imagebrowserlibrary.transforms.ZoomInTransformer;
import com.maning.imagebrowserlibrary.transforms.ZoomOutSlideTransformer;
import com.maning.imagebrowserlibrary.transforms.ZoomOutTransformer;
import com.maning.imagebrowserlibrary.view.CircleIndicator;
import com.maning.imagebrowserlibrary.view.MNViewPager;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.ArrayList;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;


/**
 * 图片浏览的页面
 */
public class MNImageBrowserActivity extends RxAppCompatActivity {

    private Context context;

    //    private MNGestureView mnGestureView;
    private MNViewPager viewPagerBrowser;
    private RelativeLayout rl_black_bg;
    private RelativeLayout rl_indicator;
    private TextView numberIndicator;
    private CircleIndicator circleIndicator;
    private ImageView ivBack;
    private ImageView ivDelete;

    //图片地址
    private ArrayList<String> imageUrlList;
    //当前位置
    private int currentPosition;
    //当前切换的动画
    private ImageBrowserConfig.TransformType transformType;
    //切换器
    private ImageBrowserConfig.IndicatorType indicatorType;
    //图片加载引擎
    public ImageEngine imageEngine;
    //监听
    public OnLongClickListener onLongClickListener;
    public OnClickListener onClickListener;
    public OnDeleteImageListener onDeleteImageListener;
    //相关配置信息
    public static ImageBrowserConfig imageBrowserConfig;
    private MyAdapter imageBrowserAdapter;
    private View mCurrentView;
    private boolean showDelete = false;
    private boolean isDelete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindowFullScreen();
        setContentView(R.layout.activity_mnimage_browser);
        context = this;

        initViews();

        initData();

        initViewPager();

    }

    private void setWindowFullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(View.SYSTEM_UI_FLAG_FULLSCREEN);// 导致华为手机模糊
            getWindow().addFlags(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);// 导致华为手机黑屏
//            getWindow().addFlags(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            getWindow().addFlags(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void initViews() {
        viewPagerBrowser = (MNViewPager) findViewById(R.id.viewPagerBrowser);
//        mnGestureView = (MNGestureView) findViewById(R.id.mnGestureView);
        rl_black_bg = (RelativeLayout) findViewById(R.id.rl_black_bg);
        rl_indicator = (RelativeLayout) findViewById(R.id.rl_indicator);
        circleIndicator = (CircleIndicator) findViewById(R.id.circleIndicator);
        numberIndicator = (TextView) findViewById(R.id.numberIndicator);
        ivBack = findViewById(R.id.iv_back);
        ivDelete = findViewById(R.id.iv_delete);
//        circleIndicator.setVisibility(View.GONE);
//        numberIndicator.setVisibility(View.GONE);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishBrowser();
            }
        });
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    numberIndicator.setText(String.valueOf((currentPosition + 1) + "/" + imageUrlList.size()));
                    initViewPager();
                    isDelete = false;
                }

            }
        });
    }

    private void initData() {
        imageUrlList = imageBrowserConfig.getImageList();
        currentPosition = imageBrowserConfig.getPosition();
        transformType = imageBrowserConfig.getTransformType();
        imageEngine = imageBrowserConfig.getImageEngine();
        onClickListener = imageBrowserConfig.getOnClickListener();
        onDeleteImageListener = imageBrowserConfig.getOnDeleteImageListener();
        onLongClickListener = imageBrowserConfig.getOnLongClickListener();
        indicatorType = imageBrowserConfig.getIndicatorType();
        showDelete = imageBrowserConfig.isShowDelete();

        ivDelete.setVisibility(showDelete ? View.VISIBLE : View.GONE);
        if (imageUrlList.size() <= 1) {
            numberIndicator.setVisibility(View.GONE);
        }
        numberIndicator.setText(String.valueOf((currentPosition + 1) + "/" + imageUrlList.size()));

//        if (imageUrlList.size() <= 1) {
//            rl_indicator.setVisibility(View.GONE);
//        } else {
//            rl_indicator.setVisibility(View.VISIBLE);
//            if (indicatorType == ImageBrowserConfig.IndicatorType.Indicator_Number) {
//                numberIndicator.setVisibility(View.VISIBLE);
//                numberIndicator.setText(String.valueOf((currentPosition + 1) + "/" + imageUrlList.size()));
//            } else {
//                circleIndicator.setVisibility(View.VISIBLE);
//            }
//        }
    }

    private void initViewPager() {
        imageBrowserAdapter = new MyAdapter();
        viewPagerBrowser.setAdapter(imageBrowserAdapter);
        viewPagerBrowser.setCurrentItem(currentPosition);
        setViewPagerTransforms();
        circleIndicator.setViewPager(viewPagerBrowser);
        viewPagerBrowser.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.e("", position + "");
                Log.e("", positionOffset + "");
                Log.e("", positionOffsetPixels + "");
            }

            @Override
            public void onPageSelected(int position) {
                if (!isDelete) {
                    currentPosition = position;
                    numberIndicator.setText(String.valueOf((position + 1) + "/" + imageUrlList.size()));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.e("", state + "");
            }
        });

//        mnGestureView.setOnGestureListener(new MNGestureView.OnCanSwipeListener() {
//            @Override
//            public boolean canSwipe() {
//                View view = imageBrowserAdapter.getPrimaryItem();
//                PhotoView imageView = (PhotoView) view.findViewById(R.id.imageView);
//                if (imageView.getScale() != 1.0) {
//                    return false;
//                }
//                return true;
//            }
//        });

//        mnGestureView.setOnSwipeListener(new MNGestureView.OnSwipeListener() {
//            @Override
//            public void downSwipe() {
//                finishBrowser();
//            }
//
//            @Override
//            public void onSwiping(float deltaY) {
////                rl_indicator.setVisibility(View.GONE);
//
//                float mAlpha = 1 - deltaY / 500;
//                if (mAlpha < 0.3) {
//                    mAlpha = 0f;
//                }
//                if (mAlpha > 1) {
//                    mAlpha = 1;
//                }
//                rl_black_bg.setAlpha(mAlpha);
//            }
//
//            @Override
//            public void overSwipe() {
////                if (imageUrlList.size() > 1) {
////                    rl_indicator.setVisibility(View.VISIBLE);
////                }
////                rl_black_bg.setAlpha(1);
//            }
//        });
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
//        rl_indicator.setVisibility(View.GONE);
//        rl_black_bg.setAlpha(0);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        finish();
//        this.overridePendingTransition(0, R.anim.browser_exit_anim);
    }

    @Override
    public void onBackPressed() {
        finishBrowser();
    }


    private class MyAdapter extends PagerAdapter {


        private LayoutInflater layoutInflater;

        public MyAdapter() {
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            mCurrentView = (View) object;
        }

        public View getPrimaryItem() {
            return mCurrentView;
        }

        @Override
        public int getCount() {
            return imageUrlList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View inflate = layoutInflater.inflate(R.layout.mn_image_browser_item_show_image, container, false);
            final PhotoView imageView = (PhotoView) inflate.findViewById(R.id.imageView);
            final RelativeLayout rl_browser_root = (RelativeLayout) inflate.findViewById(R.id.rl_browser_root);
            final String url = imageUrlList.get(position);
            //图片加载
            imageEngine.loadImage(context, url, imageView);

//            rl_browser_root.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    finishBrowser();
//                }
//            });

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //单击事件
                    if (onClickListener != null) {
                        onClickListener.onClick(MNImageBrowserActivity.this, imageView, position, url);
                    }
//                    finishBrowser();
                }
            });

            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (onLongClickListener != null) {
                        onLongClickListener.onLongClick(MNImageBrowserActivity.this, imageView, position, url);
                    }
                    return false;
                }
            });

            container.addView(inflate);
            return inflate;
        }
    }

}
