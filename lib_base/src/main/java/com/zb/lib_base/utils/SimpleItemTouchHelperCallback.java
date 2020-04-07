package com.zb.lib_base.utils;


import com.zb.lib_base.R;
import com.zb.lib_base.adapter.BindingItemAdapter;
import com.zb.lib_base.adapter.ItemTouchHelperAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private ItemTouchHelperAdapter mAdapter;
    private boolean sort = false;
    private int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;        //允许上下左右的拖动
    private int swipeFlags = 0;   //不允许侧滑
    private boolean firstUnMoving = false;
    private List<String> images = new ArrayList<>();

    public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
        mAdapter = adapter;
    }

    public void setDragFlags(int dragFlags) {
        this.dragFlags = dragFlags;
    }

    public void setSwipeFlags(int swipeFlags) {
        this.swipeFlags = swipeFlags;
    }

    public void setFirstUnMoving(boolean firstUnMoving) {
        this.firstUnMoving = firstUnMoving;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    /*
     * 用于返回可以滑动的方向
     * */
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

        return makeMovementFlags(dragFlags, swipeFlags);

    }

    /**
     * 长按选中Item的时候开始调用
     *
     * @param viewHolder
     * @param actionState
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
//        if (sort)
//            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
//                viewHolder.itemView.setBackgroundResource(R.drawable.img_selected_bg);
//            }
        super.onSelectedChanged(viewHolder, actionState);
    }

    /**
     * 手指松开的时候还原
     *
     * @param recyclerView
     * @param viewHolder
     */
    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (sort) {
            if (images.get(viewHolder.getAdapterPosition()).isEmpty())
                ((BindingItemAdapter) mAdapter).notifyDataSetChanged();
        }

    }

    /*
     * 当用户拖动一个Item进行上下移动从旧的位置到新的位置的时候会调用该方法
     * */
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        if (images.get(viewHolder.getAdapterPosition()).isEmpty()) {
            if (viewHolder.getAdapterPosition() != 0 && target.getAdapterPosition() != 0)
                mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        } else {
            mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        }
        return true;

    }

    /*
     * 当用户左右滑动Item达到删除条件时，会调用该方法
     * */
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    //设置item是否可以拖动
    public void setSort(boolean sort) {
        this.sort = sort;
    }

    /*
     * 该方法返回true时，表示支持长按拖动，即长按ItemView后才可以拖动  默认返回true
     * */
    @Override
    public boolean isLongPressDragEnabled() {
        return sort;
    }

    /*
     * 该方法返回true时，表示如果用户触摸并左右滑动了View，
     * 那么可以执行滑动删除操作，即可以调用到onSwiped()方法。默认是返回true
     * */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }


}
