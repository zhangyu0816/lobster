package com.zb.module_camera.vm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.api.cameraCancelLikeApi;
import com.zb.lib_base.api.cameraDoLikeApi;
import com.zb.lib_base.api.cameraReviewApi;
import com.zb.lib_base.api.cameraSeeLikersApi;
import com.zb.lib_base.api.cameraSeeReviewsApi;
import com.zb.lib_base.api.findCameraFilmsResourceApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.FilmResourceLikeDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.FilmComment;
import com.zb.lib_base.model.FilmInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_camera.R;
import com.zb.module_camera.adapter.CameraAdapter;
import com.zb.module_camera.databinding.AcFilmResourceDetailBinding;
import com.zb.module_camera.iv.FilmResourceDetailVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.ViewDataBinding;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class FilmResourceDetailViewModel extends BaseViewModel implements FilmResourceDetailVMInterface {
    public long cameraFilmResourceId;
    public String filmName;
    private AcFilmResourceDetailBinding mBinding;
    private FilmInfo mFilmInfo;
    private boolean hasMore = true;
    private int pageNo = 1;

    public CameraAdapter likeAdapter;
    public CameraAdapter commentAdapter;
    private List<FilmComment> likeList = new ArrayList<>();
    private List<FilmComment> commentList = new ArrayList<>();
    private long reviewId = 0;
    private boolean isUpdate = false;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (AcFilmResourceDetailBinding) binding;
        mBinding.setTitle(filmName);
        mBinding.setContent("");
        mBinding.setContentHint("说点什么...");
        mBinding.commentList.setNestedScrollingEnabled(false);
        mBinding.likeList.setNestedScrollingEnabled(false);
        mBinding.scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            //判断是否滑到的底部
            if (hasMore && scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                //调用刷新控件对应的加载更多方法
                pageNo++;
                cameraSeeReviews();
            }
        });
        // 发送
        mBinding.edContent.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                cameraReview();
            }
            return true;
        });
        mBinding.edContent.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (mBinding.getContent().isEmpty()) {
                    reviewId = 0;
                    mBinding.setContentHint("说点什么...");
                }
            }
            return false;
        });
        mBinding.setHasLike(false);
        mBinding.setHasComment(false);
        mBinding.setHasLikeTag(FilmResourceLikeDb.getInstance().hasDate(cameraFilmResourceId));
        setAdapter();
        findCameraFilmsResource();
        cameraSeeLikers(1);
        cameraSeeReviews();

    }

    @Override
    public void back(View view) {
        super.back(view);
        if (isUpdate)
            LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_updateFilmResource"));
        activity.finish();
    }

    @Override
    public void setAdapter() {
        likeAdapter = new CameraAdapter<>(activity, R.layout.item_film_like, likeList, this);

        commentAdapter = new CameraAdapter<>(activity, R.layout.item_film_comment, commentList, this);
    }

    @Override
    public void doLike(View view) {
        if (FilmResourceLikeDb.getInstance().hasDate(cameraFilmResourceId)) {
            mBinding.goodView.playUnlike();
            cameraCancelLike();
        } else {
            mBinding.goodView.playLike();
            cameraDoLike();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void cameraDoLike() {
        cameraDoLikeApi api = new cameraDoLikeApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                mBinding.setHasLikeTag(true);
                FilmResourceLikeDb.getInstance().saveDate(cameraFilmResourceId);
                likeList.clear();
                likeAdapter.notifyDataSetChanged();
                cameraSeeLikers(1);
                isUpdate = true;
                findCameraFilmsResource();
            }
        }, activity).setCameraFilmResourceId(cameraFilmResourceId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void cameraCancelLike() {
        cameraCancelLikeApi api = new cameraCancelLikeApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                mBinding.setHasLikeTag(false);
                FilmResourceLikeDb.getInstance().deleteDate(cameraFilmResourceId);
                likeList.clear();
                likeAdapter.notifyDataSetChanged();
                cameraSeeLikers(1);
                isUpdate = true;
                findCameraFilmsResource();
            }
        }, activity).setCameraFilmResourceId(cameraFilmResourceId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void doComment(View view) {
        mBinding.edContent.setFocusable(true);
        showImplicit(mBinding.edContent);
    }

    @Override
    public void toUserDetail(long otherUserId) {
        ActivityUtils.getCardMemberDetail(otherUserId, false);
    }

    @Override
    public void toReview(FilmComment filmComment) {
        if (reviewId == 0) {
            reviewId = filmComment.getReviewId();
            mBinding.setContentHint("@" + filmComment.getNick());
        } else {
            reviewId = 0;
            mBinding.setContentHint("说点什么...");
        }
    }

    @Override
    public void findCameraFilmsResource() {
        findCameraFilmsResourceApi api = new findCameraFilmsResourceApi(new HttpOnNextListener<FilmInfo>() {
            @Override
            public void onNext(FilmInfo o) {
                mFilmInfo = o;
                mBinding.setFilmInfo(mFilmInfo);
                if (!isUpdate)
                    Glide.with(activity).asBitmap().load(mFilmInfo.getImage()).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            float width = resource.getWidth();
                            float height = resource.getHeight();
                            int viewHeight = (int) (MineApp.W * height / width);

                            if (viewHeight > MineApp.H * 0.7) {
                                AdapterBinding.viewSize(mBinding.ivImage, MineApp.W, (int) (MineApp.H * 0.7f));
                                AdapterBinding.viewSize(mBinding.viewHeight, MineApp.W, (int) (MineApp.H * 0.7f));
                            } else {
                                AdapterBinding.viewSize(mBinding.ivImage, MineApp.W, viewHeight);
                                AdapterBinding.viewSize(mBinding.viewHeight, MineApp.W, viewHeight);
                            }

                            Glide.with(activity).load(mFilmInfo.getImage()).apply(new RequestOptions().centerCrop()).into(mBinding.ivImage);
                        }
                    });
            }
        }, activity).setCameraFilmResourceId(cameraFilmResourceId);
        api.setShowProgress(!isUpdate);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void cameraSeeLikers(int pageNo) {
        cameraSeeLikersApi api = new cameraSeeLikersApi(new HttpOnNextListener<List<FilmComment>>() {
            @Override
            public void onNext(List<FilmComment> o) {
                likeList.addAll(o);
                cameraSeeLikers(pageNo + 1);
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    mBinding.setHasLike(likeList.size() > 0);
                    if (likeList.size() > 0)
                        likeAdapter.notifyDataSetChanged();
                }
            }
        }, activity).setCameraFilmResourceId(cameraFilmResourceId).setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void cameraSeeReviews() {
        cameraSeeReviewsApi api = new cameraSeeReviewsApi(new HttpOnNextListener<List<FilmComment>>() {
            @Override
            public void onNext(List<FilmComment> o) {
                int start = commentList.size();
                commentList.addAll(o);
                commentAdapter.notifyItemChanged(start, commentList.size());
                mBinding.setHasComment(true);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    hasMore = false;
                    mBinding.setHasComment(commentList.size() > 0);
                }
            }
        }, activity).setCameraFilmResourceId(cameraFilmResourceId).setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void cameraReview() {
        cameraReviewApi api = new cameraReviewApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                commentList.clear();
                commentAdapter.notifyDataSetChanged();
                hasMore = true;
                pageNo = 1;
                cameraSeeReviews();
                mBinding.setContentHint("说点什么...");
                mBinding.setContent("");
                isUpdate = true;
                findCameraFilmsResource();
            }
        }, activity).setCameraFilmResourceId(cameraFilmResourceId).setReviewId(reviewId).setText(mBinding.getContent());
        HttpManager.getInstance().doHttpDeal(api);
    }
}
