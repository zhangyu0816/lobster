package com.zb.lib_base.adapter;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.BR;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.BasePopupWindow;

import java.util.List;

import androidx.databinding.ViewDataBinding;

public class BaseAdapter<T> extends BindingItemAdapter<T> {
    private BasePopupWindow pw;
    private BaseViewModel viewModel;
    private int selectIndex = -1;

    public BaseAdapter(RxAppCompatActivity activity, int layoutId, List<T> list, BaseViewModel viewModel) {
        super(activity, layoutId, list);
        this.viewModel = viewModel;
    }

    public BaseAdapter(RxAppCompatActivity activity, int layoutId, List<T> list, BasePopupWindow pw) {
        super(activity, layoutId, list);
        this.pw = pw;
    }

    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
    }

    @Override
    protected void onBind(RecyclerHolder<ViewDataBinding> holder, T t, int position) {
        holder.binding.setVariable(BR.item, t);
        holder.binding.setVariable(BR.position, position);
        holder.binding.setVariable(BR.isSelect, position == selectIndex);
        if (pw != null) {
            holder.binding.setVariable(BR.pw, pw);
        }
        if (viewModel != null) {
            holder.binding.setVariable(BR.viewModel, viewModel);
        }
        holder.binding.executePendingBindings();
    }
}
