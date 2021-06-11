package com.zb.lib_base.windows;

import android.view.View;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.R;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.databinding.DfFilmRinseBinding;
import com.zb.lib_base.db.FilmResourceDb;
import com.zb.lib_base.model.Film;

import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentManager;

public class FilmRinseDF extends BaseDialogFragment {

    private DfFilmRinseBinding mBinding;
    private Film mFilm;
    private FilmRinseCallBack mFilmRinseCallBack;

    public FilmRinseDF(RxAppCompatActivity activity) {
        super(activity, false, false);
    }

    public void show(FragmentManager manager) {
        show(manager, "FilmRinseDF");
    }

    public FilmRinseDF setFilm(Film film) {
        mFilm = film;
        return this;
    }

    public FilmRinseDF setFilmRinseCallBack(FilmRinseCallBack filmRinseCallBack) {
        mFilmRinseCallBack = filmRinseCallBack;
        return this;
    }

    @Override
    public void onStart() {
        super.onStart();
        center(0.9f);
    }

    @Override
    public int getLayoutId() {
        return R.layout.df_film_rinse;
    }

    @Override
    public void setDataBinding(ViewDataBinding viewDataBinding) {
        mBinding = (DfFilmRinseBinding) viewDataBinding;
    }

    @Override
    public void initUI() {
        mBinding.setDialog(this);
        mBinding.setFilm(mFilm);
        mBinding.setHasFilm(FilmResourceDb.getInstance().getImageSize(mFilm.getId()) < MineApp.filmMaxSize);
    }

    public void cancel(View view) {
        dismiss();
    }

    public void wash(View view) {
        mFilmRinseCallBack.rinse();
        dismiss();
    }

    public interface FilmRinseCallBack {
        void rinse();
    }
}
