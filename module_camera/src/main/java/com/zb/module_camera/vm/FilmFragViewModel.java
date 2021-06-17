package com.zb.module_camera.vm;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.api.findCameraFilmsApi;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.Film;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_camera.R;
import com.zb.module_camera.adapter.CameraAdapter;
import com.zb.module_camera.databinding.FragFilmBinding;
import com.zb.module_camera.iv.FilmFragVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

public class FilmFragViewModel extends BaseViewModel implements FilmFragVMInterface, OnRefreshListener, OnLoadMoreListener {
    private FragFilmBinding mBinding;
    private List<Film> mFilmList = new ArrayList<>();
    private int pageNo = 1;
    public CameraAdapter adapter;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (FragFilmBinding) binding;
        setAdapter();
    }

    @Override
    public void setAdapter() {
        adapter = new CameraAdapter<>(activity, R.layout.item_my_film, mFilmList, this);
        findCameraFilms();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        pageNo++;
        findCameraFilms();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        mBinding.refresh.setEnableLoadMore(true);
        mFilmList.clear();
        adapter.notifyDataSetChanged();
        pageNo = 1;
        findCameraFilms();
    }

    @Override
    public void findCameraFilms() {
        findCameraFilmsApi api = new findCameraFilmsApi(new HttpOnNextListener<List<Film>>() {
            @Override
            public void onNext(List<Film> o) {
                int start = mFilmList.size();
                for (Film film : o) {
                    if (!film.getImages().isEmpty()) {
                        String[] temp = film.getImages().split("#");
                        for (int i = 0; i < temp.length; i++) {
                            if (i == 0)
                                film.setImage1(temp[0]);
                            else if (i == 1)
                                film.setImage2(temp[1]);
                            else if (i == 2)
                                film.setImage3(temp[2]);
                            else if (i == 3)
                                film.setImage4(temp[3]);
                            else if (i == 4)
                                film.setImage5(temp[4]);
                        }
                    }
                }
                mFilmList.addAll(o);
                adapter.notifyItemChanged(start, mFilmList.size());
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
        }, activity).setIsEnable(1).setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void toDetail(Film film) {
        ActivityUtils.getCameraFilmDetail(film);
    }
}
