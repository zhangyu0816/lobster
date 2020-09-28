package com.zb.lib_base.adapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.jakewharton.rxbinding2.view.RxView;
import com.library.flowlayout.FlowLayoutManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.R;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.model.PairInfo;
import com.zb.lib_base.utils.glide.BlurTransformation;
import com.zb.lib_base.utils.glide.GlideRoundTransform;
import com.zb.lib_base.utils.glide.GridSpacingItemDecoration;
import com.zb.lib_base.utils.glide.MyDecoration;
import com.zb.lib_base.views.card.CardItemTouchHelperCallback;
import com.zb.lib_base.views.card.CardLayoutManager;
import com.zb.lib_base.views.card.OnRecyclerItemClickListener;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
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
                    if (listener != null)
                        listener.onClick(view);
                });
    }

    // RecyclerView
    @BindingAdapter(value = {"adapter", "recyclerType", "size", "color", "gridNum", "includeEdge", "cardCallback"}, requireAll = false)
    public static void setAdapter(RecyclerView view, BindingItemAdapter adapter, int recyclerType, float size, int color, int gridNum, boolean includeEdge, CardItemTouchHelperCallback<PairInfo> cardCallback) {
        view.setAdapter(adapter);
        if (recyclerType == 0) {
            // 竖向列表
            view.setLayoutManager(new LinearLayoutManager(view.getContext()));
            if (size != 0) {
                if (view.getItemDecorationCount() == 0) {
                    view.addItemDecoration(new MyDecoration(view.getContext(), LinearLayoutManager.HORIZONTAL, (int) size, view.getContext().getResources().getColor(color)));
                }
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
        } else if (recyclerType == 4) {
            // 瀑布流
            StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
            view.setItemAnimator(null);
            view.setLayoutManager(manager);
            // 防止顶部item出现空白
            view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    int[] first = new int[2];
                    manager.findFirstCompletelyVisibleItemPositions(first);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && (first[0] == 1 || first[1] == 1)) {
                        manager.invalidateSpanAssignments();
                    }
                }
            });
        } else if (recyclerType == 5) {
            // 横向排列自动换行
            view.setLayoutManager(new FlowLayoutManager());
        } else if (recyclerType == 6) {
            // 卡片滑动
            view.setItemAnimator(new DefaultItemAnimator());

            ItemTouchHelper touchHelper = new ItemTouchHelper(cardCallback);
            view.setLayoutManager(new CardLayoutManager());
            touchHelper.attachToRecyclerView(view);
            view.addOnItemTouchListener(new OnRecyclerItemClickListener(view, touchHelper) {
            });
        }
        try {
            if (view.getItemAnimator() != null)
                view.getItemAnimator().setChangeDuration(0);
        } catch (Exception ignored) {
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
    @BindingAdapter(value = {"imageUrl", "imageRes", "defaultRes", "viewWidthSize", "viewHeightSize", "isCircle", "isRound", "roundSize", "countSize", "cornerType", "isBlur"}, requireAll = false)
    public static void loadImage(ImageView view, String imageUrl, int imageRes, int defaultRes, int widthSize, int heightSize, boolean isCircle, boolean isRound, int roundSize, boolean countSize, int cornerType, boolean isBlur) {
        try {
            RequestOptions cropOptions = new RequestOptions().centerCrop();
            RequestOptions defaultOptions = new RequestOptions().centerCrop();
            if (isCircle) {
                // 圆图
                cropOptions.circleCrop();
                defaultOptions.circleCrop();
            }
            if (isRound) {
                // 圆角图
                MultiTransformation<Bitmap> multiTransformation;
                if (cornerType == 0) {
                    multiTransformation = new MultiTransformation<>(new CenterCrop(), new GlideRoundTransform(roundSize, 0));
                    cropOptions.transform(multiTransformation);
                    defaultOptions.transform(multiTransformation);
                } else {
                    if (cornerType == 6) {
                        cornerType = GlideRoundTransform.CORNER_TOP;
                    }
                    cropOptions.transform(new GlideRoundTransform(roundSize, 0, cornerType, GlideRoundTransform.FIT_CENTER));
                    defaultOptions.transform(new GlideRoundTransform(roundSize, 0, cornerType, GlideRoundTransform.FIT_CENTER));
                }
            }

            if (isBlur) {
                MultiTransformation<Bitmap> multiTransformation;
                if (isCircle) {
                    multiTransformation = new MultiTransformation<>(new BlurTransformation(), new CircleCrop());
                } else {
                    multiTransformation = new MultiTransformation<>(new BlurTransformation());
                }
                cropOptions.transform(multiTransformation);
                defaultOptions.transform(multiTransformation);
            }
            if (countSize) {
                Glide.with(view.getContext()).asBitmap().load(imageUrl).apply(cropOptions).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        float width = (float) resource.getWidth();
                        float height = (float) resource.getHeight();
                        if (widthSize == 0) {
                            viewSize(view, (int) (width * MineApp.H / height), MineApp.H);
                        } else {
                            if (width > height) {
                                viewSize(view, (int) (width * heightSize * 0.7 / height), (int) (heightSize * 0.7f));
                            } else {
                                viewSize(view, widthSize, (int) (height * widthSize / width));
                            }
                        }
                        view.setImageBitmap(resource);
                    }
                });
            } else {
                viewSize(view, widthSize, heightSize);
                RequestBuilder<android.graphics.drawable.Drawable> builder;
                if (defaultRes == 0) {
                    builder = Glide.with(view.getContext()).asDrawable().apply(cropOptions);
                } else {
                    RequestBuilder<android.graphics.drawable.Drawable> thumb = Glide.with(view.getContext()).asDrawable().apply(defaultOptions);
                    builder = Glide.with(view.getContext()).asDrawable().thumbnail(thumb.load(defaultRes)).apply(cropOptions);
                }

                if (imageRes != 0) {
                    builder.load(imageRes).into(view);
                } else {
                    if (imageUrl != null && imageUrl.equals("add_image_icon")) {
                        builder.load(R.mipmap.add_image_icon).into(view);
                    } else if (imageUrl != null && imageUrl.equals("empty_icon")) {
                        builder.load(R.mipmap.empty_icon).into(view);
                    } else if (imageUrl != null && imageUrl.equals("be_like_logo_icon")) {
                        builder.load(R.mipmap.be_like_logo_icon).into(view);
                    } else if (imageUrl != null && imageUrl.equals("bottle_logo_icon")) {
                        builder.load(R.mipmap.bottle_logo_icon).into(view);
                    } else if (imageUrl != null && imageUrl.equals("review_tag_icon")) {
                        builder.load(R.mipmap.review_tag_icon).into(view);
                    } else if (imageUrl != null && imageUrl.equals("card_no_data_icon")) {
                        builder.load(R.mipmap.card_no_data_icon).into(view);
                    } else if (imageUrl != null && imageUrl.equals("card_out_line_bg")) {
                        builder.load(R.mipmap.card_out_line_bg).into(view);
                    } else if (imageUrl != null && imageUrl.equals("card_progress_icon")) {
                        builder.load(R.mipmap.card_progress_icon).into(view);
                    } else if (imageUrl != null && imageUrl.equals("vip_ad_1_male_right")) {
                        builder.load(R.drawable.vip_ad_1_male_right).into(view);
                    } else if (imageUrl != null && imageUrl.equals("vip_ad_3_male")) {
                        builder.load(R.mipmap.vip_ad_3_male).into(view);
                    } else if (imageUrl != null && imageUrl.equals("vip_ad_3_logo")) {
                        builder.load(R.drawable.vip_ad_3_logo).into(view);
                    } else if (imageUrl != null && imageUrl.equals("vip_ad_3_logo_male")) {
                        builder.load(R.drawable.vip_ad_3_logo_male).into(view);
                    } else {
                        if (imageUrl != null && imageUrl.contains(".mp3"))
                            imageUrl = "";
                        builder.load(imageUrl).into(view);
                    }
                }
            }
        } catch (Exception ignored) {

        }
    }

}
