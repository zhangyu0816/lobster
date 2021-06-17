package com.zb.module_camera.vm;

import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_camera.databinding.FragFilmNewsBinding;
import com.zb.module_camera.iv.FilmNewsFragVMInterface;

import androidx.databinding.ViewDataBinding;

public class FilmNewsFragViewModel extends BaseViewModel implements FilmNewsFragVMInterface {
    private FragFilmNewsBinding mBinding;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (FragFilmNewsBinding) binding;
        setAdapter();
    }

    @Override
    public void setAdapter() {

    }
}
