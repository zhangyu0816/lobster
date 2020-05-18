package com.zb.module_chat.adapter;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.adapter.BindingItemAdapter;
import com.zb.lib_base.adapter.RecyclerHolder;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_chat.BR;

import java.util.List;

import androidx.databinding.ViewDataBinding;

public class ChatAdapter<T> extends BindingItemAdapter<T> {

    private BaseViewModel viewModel;

    public ChatAdapter(RxAppCompatActivity activity, int layoutId, List<T> list, BaseViewModel viewModel) {
        super(activity, layoutId, list);
        this.viewModel = viewModel;
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
