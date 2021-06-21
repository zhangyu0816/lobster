package com.zb.module_camera.vm;

import android.content.Intent;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.api.cameraFilmMsgListApi;
import com.zb.lib_base.api.readCameraFilmMsgApi;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.FilmMsg;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_camera.R;
import com.zb.module_camera.adapter.CameraAdapter;
import com.zb.module_camera.databinding.FragFilmNewsBinding;
import com.zb.module_camera.iv.FilmNewsFragVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

public class FilmNewsFragViewModel extends BaseViewModel implements FilmNewsFragVMInterface, OnRefreshListener, OnLoadMoreListener {
    private FragFilmNewsBinding mBinding;
    private List<FilmMsg> mFilmMsgList = new ArrayList<>();
    private int pageNo = 1;
    public CameraAdapter adapter;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (FragFilmNewsBinding) binding;
        setAdapter();
    }

    @Override
    public void setAdapter() {
        adapter = new CameraAdapter<>(activity, R.layout.item_my_film_msg, mFilmMsgList, this);
        cameraFilmMsgList();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        pageNo++;
        cameraFilmMsgList();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        mBinding.refresh.setEnableLoadMore(true);
        mFilmMsgList.clear();
        adapter.notifyDataSetChanged();
        pageNo = 1;
        cameraFilmMsgList();
    }

    @Override
    public void toFilmResourceDetail(FilmMsg filmMsg, int position) {
        ActivityUtils.getCameraFilmResourceDetail(filmMsg.getCameraFilmResourceId(), "");
        if (filmMsg.getIsRead() == 0) {
            readCameraFilmMsg(filmMsg.getId());
            filmMsg.setIsRead(1);
            mFilmMsgList.set(position, filmMsg);
            adapter.notifyItemChanged(position);
            activity.sendBroadcast(new Intent("lobster_readFilmMsg"));
        }
    }

    @Override
    public void cameraFilmMsgList() {
        cameraFilmMsgListApi api = new cameraFilmMsgListApi(new HttpOnNextListener<List<FilmMsg>>() {
            @Override
            public void onNext(List<FilmMsg> o) {
                int start = mFilmMsgList.size();
                mFilmMsgList.addAll(o);
                adapter.notifyItemChanged(start, mFilmMsgList.size());
                mBinding.refresh.finishRefresh();
                mBinding.refresh.finishLoadMore();
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    mBinding.refresh.setEnableLoadMore(false);
                    mBinding.refresh.finishRefresh();
                    mBinding.refresh.finishLoadMore();
                }
            }
        }, activity).setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void readCameraFilmMsg(long cameraFilmResourceReviewMsgId) {
        readCameraFilmMsgApi api = new readCameraFilmMsgApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {

            }
        }, activity).setCameraFilmResourceReviewMsgId(cameraFilmResourceReviewMsgId);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
