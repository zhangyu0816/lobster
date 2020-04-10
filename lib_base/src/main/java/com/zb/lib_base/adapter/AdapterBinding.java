package com.zb.lib_base.adapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.jakewharton.rxbinding2.view.RxView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.R;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.DisplayUtils;
import com.zb.lib_base.utils.GlideCircleTransform;
import com.zb.lib_base.utils.GlideRoundTransform;
import com.zb.lib_base.utils.GridSpacingItemDecoration;
import com.zb.lib_base.utils.MyDecoration;
import com.zb.lib_base.utils.StaggeredDividerItemDecoration;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class AdapterBinding {

    // 按钮防抖
    @SuppressLint("CheckResult")
    @BindingAdapter("onClickDelayed")
    public static void onClick(final View view, final View.OnClickListener listener) {
        RxView.clicks(view)
                //两秒钟之内只取一个点击事件，防抖操作
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> {
                    listener.onClick(view);
                });
    }

    // RecyclerView
    @BindingAdapter(value = {"adapter", "recyclerType", "size", "color", "gridNum", "includeEdge"}, requireAll = false)
    public static void setAdapter(RecyclerView view, BindingItemAdapter adapter, int recyclerType, float size, int color, int gridNum, boolean includeEdge) {
        view.setAdapter(adapter);
        if (recyclerType == 0) {
            // 竖向列表
            view.setLayoutManager(new LinearLayoutManager(view.getContext()));
            if (view.getItemDecorationCount() == 0) {
                view.addItemDecoration(new MyDecoration(view.getContext(), LinearLayoutManager.HORIZONTAL, (int) size, view.getContext().getResources().getColor(color)));
            }
        } else if (recyclerType == 1) {
            // 横向列表
            LinearLayoutManager manager = new LinearLayoutManager(view.getContext());
            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
            view.setLayoutManager(manager);
        } else if (recyclerType == 2) {
            // 九宫格
            view.setLayoutManager(new GridLayoutManager(view.getContext(), gridNum));
            if (view.getItemDecorationCount() == 0 && size > 0) {
                view.addItemDecoration(new GridSpacingItemDecoration(gridNum, (int) size, includeEdge));
            }
        } else if (recyclerType == 3) {
            // 九宫格
            view.setLayoutManager(new GridLayoutManager(view.getContext(), gridNum));
        } else {
            // 瀑布流
            StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
            view.setItemAnimator(null);
            view.setLayoutManager(manager);
            if (view.getItemDecorationCount() == 0) {
                view.addItemDecoration(new StaggeredDividerItemDecoration((AppCompatActivity) view.getContext(), (int) size));
            }
            // 防止顶部item出现空白
            view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    int[] first = new int[2];
                    manager.findFirstCompletelyVisibleItemPositions(first);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && (first[0] == 1 || first[1] == 1)) {
                        manager.invalidateSpanAssignments();
                    }
                }
            });

        }
    }

    // 下拉刷新
    @BindingAdapter("onRefreshListener")
    public static void onRefreshListener(SmartRefreshLayout view, OnRefreshListener onRefreshListener) {
        view.setOnRefreshListener(onRefreshListener);
    }

    // 上拉加载更多
    @BindingAdapter("onLoadMoreListener")
    public static void onLoadMoreListener(SmartRefreshLayout view, OnLoadMoreListener onLoadMoreListener) {
        view.setOnLoadMoreListener(onLoadMoreListener);
    }

    // 计算view大小
    @BindingAdapter(value = {"viewWidthSize", "viewHeightSize"}, requireAll = false)
    public static void viewSize(View view, int widthSize, int heightSize) {
        ViewGroup.LayoutParams para = view.getLayoutParams();
        para.width = widthSize;
        para.height = heightSize;
        view.setLayoutParams(para);
    }

    // 加载图片
    @SuppressLint("CheckResult")
    @BindingAdapter(value = {"imageUrl", "imageRes", "defaultRes", "viewWidthSize", "viewHeightSize", "isCircle", "isRound", "roundSize", "countSize", "cornerType", "scaleType"}, requireAll = false)
    public static void loadImage(ImageView view, String imageUrl, int imageRes, int defaultRes, int widthSize, int heightSize, boolean isCircle, boolean isRound, int roundSize, boolean countSize, int cornerType, int scaleType) {
        RequestOptions cropOptions = new RequestOptions().centerCrop();
        if (isCircle) {
            // 圆图
            cropOptions.transform(new GlideCircleTransform());
//            cropOptions.circleCrop();
        }
        if (isRound) {
            // 圆角图
            if (cornerType == 0) {
                cropOptions.transform(new GlideRoundTransform(roundSize, 0));
            } else {
                if (scaleType == 0)
                    scaleType = GlideRoundTransform.FIT_CENTER;
                cropOptions.transform(new GlideRoundTransform(roundSize, 0, cornerType, scaleType));
            }
        }

        if (countSize) {
            Glide.with(view.getContext()).asBitmap().load(imageUrl).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    if (widthSize == 0) {
                        viewSize(view, (int) (resource.getWidth() * (float) heightSize / MineApp.H), heightSize);
                    }
                    if (heightSize == 0) {
                        viewSize(view, heightSize, (int) (resource.getHeight() * (float) widthSize / MineApp.W));
                    }
                    view.setImageBitmap(resource);
                }
            });
        } else {
            viewSize(view, widthSize, heightSize);
            RequestBuilder builder;
            if (defaultRes == 0) {
                builder = Glide.with(view.getContext()).asDrawable().apply(cropOptions);
            } else {
                builder = Glide.with(view.getContext()).asDrawable().thumbnail(Glide.with(view.getContext()).load(defaultRes)).apply(cropOptions);
            }

            if (imageRes != 0) {
                builder.load(imageRes).into(view);
            } else {
                if (imageUrl != null && imageUrl.equals("add_image_icon")) {
                    builder.load(R.mipmap.add_image_icon).into(view);
                } else {
                    builder.load(imageUrl).into(view);
                }
            }
        }
    }


}
