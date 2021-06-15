package com.zb.module_camera.activity;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.model.Film;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_camera.BR;
import com.zb.module_camera.R;
import com.zb.module_camera.vm.FilmDetailViewModel;

@Route(path = RouteUtils.Camera_Film_Detail)
public class FilmDetailActivity extends WhiteCameraBaseActivity {
    @Autowired(name = "film")
    Film film;

    @Override
    public int getRes() {
        return R.layout.ac_film_detail;
    }

    @Override
    public void initUI() {
        FilmDetailViewModel viewModel = new FilmDetailViewModel();
        viewModel.mFilm = film;
        mBinding.setVariable(BR.viewModel, viewModel);
        viewModel.setBinding(mBinding);
    }
}
