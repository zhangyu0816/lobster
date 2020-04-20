package com.zb.module_bottle.adapter;

import com.zb.lib_base.adapter.BindingItemAdapter;
import com.zb.lib_base.adapter.RecyclerHolder;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.BasePopupWindow;
import com.zb.module_bottle.BR;
import com.zb.module_bottle.databinding.ItemBottleContentBinding;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;

public class BottleAdapter<T> extends BindingItemAdapter<T> {
    private BaseViewModel viewModel;
    private BasePopupWindow pw;
    private int selectIndex = -1;

    public BottleAdapter(AppCompatActivity activity, int layoutId, List<T> list) {
        super(activity, layoutId, list);
    }

    public BottleAdapter(AppCompatActivity activity, int layoutId, List<T> list, BaseViewModel viewModel) {
        super(activity, layoutId, list);
        this.viewModel = viewModel;
    }

    public BottleAdapter(AppCompatActivity activity, int layoutId, List<T> list, BasePopupWindow pw) {
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
        if (holder.binding instanceof ItemBottleContentBinding) {
            ((ItemBottleContentBinding) holder.binding).tvBottle.setTypeface(MineApp.type);
        }
        if (viewModel != null) {
            holder.binding.setVariable(BR.viewModel, viewModel);
        }
        if (pw != null) {
            holder.binding.setVariable(BR.pw, pw);
        }
        holder.binding.executePendingBindings();
    }
}
