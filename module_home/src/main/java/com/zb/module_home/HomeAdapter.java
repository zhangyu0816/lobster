package com.zb.module_home;

import com.zb.lib_base.adapter.BindingItemAdapter;
import com.zb.lib_base.adapter.RecyclerHolder;
import com.zb.lib_base.vm.BaseViewModel;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;

public class HomeAdapter<T> extends BindingItemAdapter<T> {

    private BaseViewModel viewModel;

    public HomeAdapter(AppCompatActivity activity, int layoutId, List<T> list,BaseViewModel viewModel) {
        super(activity, layoutId, list);
        this.viewModel=viewModel;
    }

    @Override
    protected void onBind(RecyclerHolder<ViewDataBinding> holder, T t, int position) {
        holder.binding.setVariable(BR.item, t);
        holder.binding.setVariable(BR.position, position);
        if (viewModel != null) {
            holder.binding.setVariable(BR.viewModel, viewModel);
        }
        holder.binding.executePendingBindings();
    }
}
