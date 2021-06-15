package com.zb.module_camera.vm;

import android.view.View;

import com.zb.lib_base.api.findCameraFilmsResourceListApi;
import com.zb.lib_base.api.otherInfoApi;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.Film;
import com.zb.lib_base.model.FilmInfo;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_camera.R;
import com.zb.module_camera.adapter.CameraAdapter;
import com.zb.module_camera.databinding.AcFilmDetailBinding;
import com.zb.module_camera.iv.FilmDetailVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.ViewDataBinding;

public class FilmDetailViewModel extends BaseViewModel implements FilmDetailVMInterface {
    private AcFilmDetailBinding mBinding;
    public Film mFilm;
    private List<FilmInfo> mFilmInfoList = new ArrayList<>();
    public CameraAdapter adapter;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (AcFilmDetailBinding) binding;
        mBinding.setTitle(mFilm.getTitle());
        if (!mFilm.getImages().isEmpty()) {
            String[] temp = mFilm.getImages().split("#");
            for (int i = 0; i < temp.length; i++) {
                if (i == 0)
                    mFilm.setImage1(temp[0]);
                else if (i == 1)
                    mFilm.setImage2(temp[1]);
                else if (i == 2)
                    mFilm.setImage3(temp[2]);
                else if (i == 3)
                    mFilm.setImage4(temp[3]);
                else if (i == 4)
                    mFilm.setImage5(temp[4]);
            }
        }
        mBinding.setFilm(mFilm);
        setAdapter();
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void setAdapter() {
        adapter = new CameraAdapter<>(activity, R.layout.item_film_info,mFilmInfoList,this);
        findCameraFilmsResourceList();
        otherInfo();
    }

    @Override
    public void findCameraFilmsResourceList() {
        findCameraFilmsResourceListApi api = new findCameraFilmsResourceListApi(new HttpOnNextListener<List<FilmInfo>>() {
            @Override
            public void onNext(List<FilmInfo> o) {
                mFilmInfoList = o;
            }
        }, activity).setCameraFilmId(mFilm.getId());
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void otherInfo() {
        otherInfoApi api = new otherInfoApi(new HttpOnNextListener<MemberInfo>() {
            @Override
            public void onNext(MemberInfo o) {
                mBinding.setOtherUserInfo(o);
            }
        },activity).setOtherUserId(mFilm.getUserId());
        HttpManager.getInstance().doHttpDeal(api);
    }
}
