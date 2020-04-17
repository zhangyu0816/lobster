package com.zb.module_home.adapter;

import com.zb.lib_base.adapter.BindingItemAdapter;
import com.zb.lib_base.adapter.RecyclerHolder;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.BasePopupWindow;
import com.zb.module_home.BR;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;

public class HomeAdapter<T> extends BindingItemAdapter<T> {

    private BaseViewModel viewModel;
    private BasePopupWindow pw;
    private int selectIndex = -1;

    public HomeAdapter(AppCompatActivity activity, int layoutId, List<T> list, BasePopupWindow pw) {
        super(activity, layoutId, list);
        this.pw = pw;
    }

    public HomeAdapter(AppCompatActivity activity, int layoutId, List<T> list, BaseViewModel viewModel) {
        super(activity, layoutId, list);
        this.viewModel = viewModel;
    }

    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
    }

    @Override
    protected void onBind(RecyclerHolder<ViewDataBinding> holder, T t, int position) {
        holder.binding.setVariable(BR.item, t);
        holder.binding.setVariable(BR.position, position);
        holder.binding.setVariable(BR.isLast, position == getItemCount() - 1);
        holder.binding.setVariable(BR.isSelect, position == selectIndex);

        if (viewModel != null) {
            holder.binding.setVariable(BR.viewModel, viewModel);
        }
        if (pw != null) {
            holder.binding.setVariable(BR.pw, pw);
        }
        holder.binding.executePendingBindings();
    }
}
