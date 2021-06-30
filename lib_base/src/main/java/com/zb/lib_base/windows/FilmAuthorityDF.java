package com.zb.lib_base.windows;

import android.view.View;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.R;
import com.zb.lib_base.api.saveCameraFilmApi;
import com.zb.lib_base.databinding.DfFilmAuthorityBinding;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.Film;

import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentManager;

public class FilmAuthorityDF extends BaseDialogFragment {
    private DfFilmAuthorityBinding mBinding;
    private Film mFilm;
    private FilmCallBack mFilmCallBack;

    public FilmAuthorityDF(RxAppCompatActivity activity) {
        super(activity);
    }

    public FilmAuthorityDF setFilm(Film film) {
        mFilm = film;
        return this;
    }

    public FilmAuthorityDF setFilmCallBack(FilmCallBack filmCallBack) {
        mFilmCallBack = filmCallBack;
        return this;
    }

    public void show(FragmentManager manager) {
        show(manager, "FilmAuthorityDF");
    }

    @Override
    public void onStart() {
        super.onStart();
        cleanPadding();
    }

    @Override
    public int getLayoutId() {
        return R.layout.df_film_authority;
    }

    @Override
    public void setDataBinding(ViewDataBinding viewDataBinding) {
        mBinding = (DfFilmAuthorityBinding) viewDataBinding;
    }

    @Override
    public void initUI() {
        mBinding.setDialog(this);
        mBinding.setFilmName(mFilm.getTitle());
        mBinding.setAuthority(mFilm.getAuthority());
    }


    public void cancel(View view) {
        dismiss();
    }

    public void selectAuthority(int author) {
        mBinding.setAuthority(author);
        mFilm.setAuthority(author);
    }

    public void useFilm(View view) {
        saveCameraFilmApi api = new saveCameraFilmApi(new HttpOnNextListener<Film>() {
            @Override
            public void onNext(Film o) {
                mFilmCallBack.selectFilm(o);
                dismiss();
            }
        }, activity)
                .setAuthority(mFilm.getAuthority())
                .setTitle(mBinding.getFilmName())
                .setCamerafilmType(mFilm.getCamerafilmType())
                .setCameraFilmId(mFilm.getId());
        HttpManager.getInstance().doHttpDeal(api);
    }

    public interface FilmCallBack {
        void selectFilm(Film film);
    }
}
