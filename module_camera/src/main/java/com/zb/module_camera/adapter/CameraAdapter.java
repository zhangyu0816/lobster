package com.zb.module_camera.adapter;

import com.zb.lib_base.adapter.BindingItemAdapter;
import com.zb.lib_base.adapter.RecyclerHolder;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_camera.BR;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;

public class CameraAdapter<T> extends BindingItemAdapter<T> {
    private BaseViewModel viewModel;
    private int selectIndex = -1;

    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
        notifyDataSetChanged();
    }

    public CameraAdapter(AppCompatActivity activity, int layoutId, List<T> list, BaseViewModel viewModel) {
        super(activity, layoutId, list);
        this.viewModel = viewModel;
    }

    @Override
    protected void onBind(RecyclerHolder<ViewDataBinding> holder, T t, int position) {
        holder.binding.setVariable(BR.item, t);
        holder.binding.setVariable(BR.position, position);
        holder.binding.setVariable(BR.isSelect, selectIndex == position);
        if (viewModel != null) {
            holder.binding.setVariable(BR.viewModel, viewModel);
        }
        holder.binding.executePendingBindings();
    }
}
