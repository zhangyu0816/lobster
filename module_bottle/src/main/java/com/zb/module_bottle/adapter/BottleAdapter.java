package com.zb.module_bottle.adapter;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.adapter.BindingItemAdapter;
import com.zb.lib_base.adapter.ItemTouchHelperAdapter;
import com.zb.lib_base.adapter.RecyclerHolder;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.BasePopupWindow;
import com.zb.module_bottle.BR;
import com.zb.module_bottle.databinding.ItemBottleContentBinding;
import com.zb.module_bottle.vm.BottleListViewModel;

import java.util.List;

import androidx.databinding.ViewDataBinding;

public class BottleAdapter<T> extends BindingItemAdapter<T> implements ItemTouchHelperAdapter {
    private BaseViewModel viewModel;
    private BasePopupWindow pw;
    private int selectIndex = -1;

    public BottleAdapter(RxAppCompatActivity activity, int layoutId, List<T> list) {
        super(activity, layoutId, list);
    }

    public BottleAdapter(RxAppCompatActivity activity, int layoutId, List<T> list, BaseViewModel viewModel) {
        super(activity, layoutId, list);
        this.viewModel = viewModel;
    }

    public BottleAdapter(RxAppCompatActivity activity, int layoutId, List<T> list, BasePopupWindow pw) {
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

    @Override
    public void onItemMove(int fromPosition, int toPosition) {

    }

    @Override
    public void onItemDelete(int position) {
        if (viewModel instanceof BottleListViewModel) {
            ((BottleListViewModel) viewModel).deleteBottle(position);
        }
    }
}
