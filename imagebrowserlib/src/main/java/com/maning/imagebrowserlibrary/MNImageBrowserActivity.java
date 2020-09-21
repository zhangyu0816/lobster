package com.maning.imagebrowserlibrary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
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

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;


/**
 * 图片浏览的页面
 */
public class MNImageBrowserActivity extends RxAppCompatActivity {

    private Context context;

    private MNViewPager viewPagerBrowser;
    private TextView numberIndicator;
    private CircleIndicator circleIndicator;
    private ImageView ivDelete;

    //图片地址
    private ArrayList<String> imageUrlList;
    //当前位置
    private int currentPosition;
    //当前切换的动画
    private ImageBrowserConfig.TransformType transformType;
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
    private boolean isDelete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mnimage_browser);
        context = this;

        initViews();

        initData();

        initViewPager();

    }

    private void initViews() {
        viewPagerBrowser = (MNViewPager) findViewById(R.id.viewPagerBrowser);
        circleIndicator = (CircleIndicator) findViewById(R.id.circleIndicator);
        numberIndicator = (TextView) findViewById(R.id.numberIndicator);
        ivDelete = findViewById(R.id.iv_delete);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishBrowser();
            }
        });
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
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
                    numberIndicator.setText((currentPosition + 1) + "/" + imageUrlList.size());
                    initViewPager();
                    isDelete = false;
                }

            }
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
        //切换器
        boolean showDelete = imageBrowserConfig.isShowDelete();

        ivDelete.setVisibility(showDelete ? View.VISIBLE : View.GONE);
        if (imageUrlList.size() <= 1) {
            numberIndicator.setVisibility(View.GONE);
        }
        numberIndicator.setText((currentPosition + 1) + "/" + imageUrlList.size());
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
            //图片加载
            imageEngine.loadImage(context, url, imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //单击事件
                    if (onClickListener != null) {
                        onClickListener.onClick(MNImageBrowserActivity.this, imageView, position, url);
                    }
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
