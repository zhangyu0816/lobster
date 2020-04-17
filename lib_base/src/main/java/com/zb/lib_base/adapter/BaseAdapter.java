package com.zb.lib_base.adapter;

import com.zb.lib_base.BR;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.BasePopupWindow;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;

public class BaseAdapter<T> extends BindingItemAdapter<T> {
    private BaseViewModel viewModel;
    private BasePopupWindow pw;

    public BaseAdapter(AppCompatActivity activity, int layoutId, List<T> list, BasePopupWindow pw) {
        super(activity, layoutId, list);
        this.pw = pw;
    }

//    public BaseAdapter(AppCompatActivity activity, int layoutId, List<T> list, BaseViewModel viewModel) {
//        super(activity, layoutId, list);
//        this.viewModel = viewModel;
//    }

    @Override
    protected void onBind(RecyclerHolder<ViewDataBinding> holder, T t, int position) {
        holder.binding.setVariable(BR.item, t);
        holder.binding.setVariable(BR.position, position);

//        if (viewModel != null) {
//            holder.binding.setVariable(BR.viewModel, viewModel);
//        }
        if (pw != null) {
            holder.binding.setVariable(BR.pw, pw);
        }
        holder.binding.executePendingBindings();
    }
}
