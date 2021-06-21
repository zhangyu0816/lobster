package com.zb.module_camera.activity;

import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.lib_base.utils.SoftHideKeyBoardUtil;
import com.zb.module_camera.BR;
import com.zb.module_camera.R;
import com.zb.module_camera.vm.FilmResourceDetailViewModel;

@Route(path = RouteUtils.Camera_Film_Resource_Detail)
public class FilmResourceDetailActivity extends WhiteCameraBaseActivity {

    @Autowired(name = "cameraFilmResourceId")
    long cameraFilmResourceId;
    @Autowired(name = "filmName")
    String filmName;

    private FilmResourceDetailViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.ac_film_resource_detail;
    }

    @Override
    public void initUI() {
        fitComprehensiveScreen();
        viewModel = new FilmResourceDetailViewModel();
        viewModel.cameraFilmResourceId = cameraFilmResourceId;
        viewModel.filmName = filmName;
        mBinding.setVariable(BR.viewModel, viewModel);
        viewModel.setBinding(mBinding);
        SoftHideKeyBoardUtil.assistActivity(activity, true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (viewModel != null)
                viewModel.back(null);
            return true;
        }
        return false;
    }
}
