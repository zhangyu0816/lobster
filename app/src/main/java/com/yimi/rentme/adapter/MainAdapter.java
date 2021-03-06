package com.yimi.rentme.adapter;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.yimi.rentme.BR;
import com.zb.lib_base.adapter.BindingItemAdapter;
import com.zb.lib_base.adapter.RecyclerHolder;
import com.zb.lib_base.vm.BaseViewModel;

import java.util.List;

import androidx.databinding.ViewDataBinding;

public class MainAdapter<T> extends BindingItemAdapter<T>  {
    private BaseViewModel viewModel;

    public MainAdapter(RxAppCompatActivity activity, int layoutId, List<T> list, BaseViewModel viewModel) {
        super(activity, layoutId, list);
        this.viewModel = viewModel;
    }

    @Override
    protected void onBind(RecyclerHolder<ViewDataBinding> holder, T t, int position) {
        holder.binding.setVariable(BR.item, t);
        if (viewModel != null) {
            holder.binding.setVariable(BR.viewModel, viewModel);
        }
        holder.binding.executePendingBindings();
    }
}
