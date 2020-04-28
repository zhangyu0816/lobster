package com.zb.module_mine.adapter;

import com.zb.lib_base.adapter.BindingItemAdapter;
import com.zb.lib_base.adapter.ItemTouchHelperAdapter;
import com.zb.lib_base.adapter.RecyclerHolder;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.BR;

import java.util.Collections;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;

public class MineAdapter<T> extends BindingItemAdapter<T>  implements ItemTouchHelperAdapter {

    private BaseViewModel viewModel;
    private int selectIndex = -1;

    public MineAdapter(AppCompatActivity activity, int layoutId, List<T> list, BaseViewModel viewModel) {
        super(activity, layoutId, list);
        this.viewModel = viewModel;
    }

    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
    }

    public int getSelectIndex() {
        return selectIndex;
    }

    @Override
    protected void onBind(RecyclerHolder<ViewDataBinding> holder, T t, int position) {
        holder.binding.setVariable(BR.item, t);
        holder.binding.setVariable(BR.position, position);
        holder.binding.setVariable(BR.isSelect,position == selectIndex);
        if (viewModel != null) {
            holder.binding.setVariable(BR.viewModel, viewModel);
        }
        holder.binding.executePendingBindings();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (!getList().get(fromPosition).equals("") && !getList().get(toPosition).equals("")) {
            if (fromPosition < toPosition) {
                //从上往下拖动，每滑动一个item，都将list中的item向下交换，向上滑同理。
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(getList(), i, i + 1);//交换数据源两个数据的位置
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(getList(), i, i - 1);//交换数据源两个数据的位置
                }
            }
            //更新视图
            notifyItemMoved(fromPosition, toPosition);
        }
    }

    @Override
    public void onItemDelete(int position) {

    }
}
