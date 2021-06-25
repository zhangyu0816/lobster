package com.zb.lib_base.windows;

import android.view.View;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.R;
import com.zb.lib_base.adapter.BaseAdapter;
import com.zb.lib_base.api.saveCameraFilmApi;
import com.zb.lib_base.databinding.DfFilmBinding;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.Film;
import com.zb.lib_base.utils.SCToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentManager;

public class FilmDF extends BaseDialogFragment {
    private DfFilmBinding mBinding;
    private int filmType = 1;
    private boolean isSet = false;
    private List<Film> mFilms = new ArrayList<>();
    private Film mFilm;
    private FilmCallBack mFilmCallBack;
    public BaseAdapter adapter;
    private String[][] filmTypeName = {{"#怀旧", "#室内", "#人像"}, {"#复古", "#室外", "#情感"}, {"#胶片", "#人像", "#风景"}, {"#灰调", "#情绪", "#室外"}};
    private Map<Integer, List<Integer>> filmImageMap = new HashMap<>();
    private List<Integer> dataList = new ArrayList<>();

    public FilmDF(RxAppCompatActivity activity) {
        super(activity, false, false);
    }

    public FilmDF setFilmType(int filmType) {
        this.filmType = filmType;
        return this;
    }

    public FilmDF setSet(boolean set) {
        isSet = set;
        return this;
    }

    public FilmDF setFilms(List<Film> films) {
        mFilms = films;
        return this;
    }

    public FilmDF setFilmCallBack(FilmCallBack filmCallBack) {
        mFilmCallBack = filmCallBack;
        return this;
    }

    public void show(FragmentManager manager) {
        show(manager, "FilmDF");
    }

    @Override
    public void onStart() {
        super.onStart();
        cleanPadding();
    }

    @Override
    public int getLayoutId() {
        return R.layout.df_film;
    }

    @Override
    public void setDataBinding(ViewDataBinding viewDataBinding) {
        mBinding = (DfFilmBinding) viewDataBinding;
    }

    @Override
    public void initUI() {
        List<Integer> list1 = new ArrayList<>();
        list1.add(R.mipmap.monochrome1);
        list1.add(R.mipmap.monochrome2);
        list1.add(R.mipmap.monochrome3);
        List<Integer> list2 = new ArrayList<>();
        list2.add(R.mipmap.color_balance1);
        list2.add(R.mipmap.color_balance2);
        list2.add(R.mipmap.color_balance3);
        List<Integer> list3 = new ArrayList<>();
        list3.add(R.mipmap.contrast1);
        list3.add(R.mipmap.contrast2);
        list3.add(R.mipmap.contrast3);
        List<Integer> list4 = new ArrayList<>();
        list4.add(R.mipmap.saturation1);
        list4.add(R.mipmap.saturation2);
        list4.add(R.mipmap.saturation3);
        filmImageMap.put(1, list1);
        filmImageMap.put(2, list2);
        filmImageMap.put(3, list3);
        filmImageMap.put(4, list4);

        adapter = new BaseAdapter<>(activity, R.layout.item_film_image, dataList, this);
        mBinding.setDialog(this);
        mBinding.setFilmType(filmType);
        mBinding.setIsSet(isSet);
        setFilm();
    }

    private void setFilm() {
        mFilm = null;
        for (Film film : mFilms) {
            if (film.getCamerafilmType() == filmType) {
                mFilm = film;
            }
        }

        if (mFilm == null) {
            mFilm = new Film();
            mFilm.setCamerafilmType(filmType);
            mFilm.setAuthority(1);
        }
        dataList.clear();
        adapter.notifyDataSetChanged();
        dataList.addAll(filmImageMap.get(filmType));
        adapter.notifyDataSetChanged();
        mBinding.setAuthority(mFilm.getAuthority());
        mBinding.setFilmName(mFilm.getTitle());
        mBinding.setFilmTypeName1(filmTypeName[filmType - 1][0]);
        mBinding.setFilmTypeName2(filmTypeName[filmType - 1][1]);
        mBinding.setFilmTypeName3(filmTypeName[filmType - 1][2]);
    }

    public void cancel(View view) {
        dismiss();
    }

    public void selectFilm(int filmType) {
        this.filmType = filmType;
        mBinding.setFilmType(filmType);
        setFilm();
    }

    public void selectAuthority(int author) {
        mBinding.setAuthority(author);
        mFilm.setAuthority(author);
    }

    public void useFilm(View view) {
        if (mBinding.getFilmName().trim().isEmpty()) {
            SCToastUtil.showToast(activity, "请设置胶卷名称", true);
            return;
        }
        saveCameraFilmApi api = new saveCameraFilmApi(new HttpOnNextListener<Film>() {
            @Override
            public void onNext(Film o) {
                mFilmCallBack.selectFilm(filmType, o);
                dismiss();
            }
        }, activity)
                .setAuthority(mFilm.getAuthority())
                .setTitle(mBinding.getFilmName())
                .setCamerafilmType(filmType)
                .setCameraFilmId(mFilm.getId());
        HttpManager.getInstance().doHttpDeal(api);
    }

    public interface FilmCallBack {
        void selectFilm(int filmType, Film film);
    }
}
