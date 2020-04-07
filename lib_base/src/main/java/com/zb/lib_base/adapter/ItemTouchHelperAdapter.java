package com.zb.lib_base.adapter;

public interface ItemTouchHelperAdapter {
    //数据交换前后位置
    void onItemMove(int fromPosition, int toPosition);

    //滑动删除
    void onItemDelete(int position);
}
