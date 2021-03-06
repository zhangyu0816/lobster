package com.zb.lib_base.views.card;

import android.graphics.Canvas;
import android.view.View;

import com.zb.lib_base.views.SuperLikeView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class CardItemTouchHelperCallback<T> extends ItemTouchHelper.Callback {

    private final RecyclerView.Adapter adapter;
    private List<T> dataList;
    private OnSwipeListener<T> mListener;

    public CardItemTouchHelperCallback(@NonNull RecyclerView.Adapter adapter, @NonNull List<T> dataList) {
        this.adapter = checkIsNull(adapter);
        this.dataList = checkIsNull(dataList);
    }

    private <T> T checkIsNull(T t) {
        if (t == null) {
            throw new NullPointerException();
        }
        return t;
    }

    public void setOnSwipedListener(OnSwipeListener<T> mListener) {
        this.mListener = mListener;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        swiped(viewHolder.itemView, direction);
    }

    public void swiped(View view, int direction) {
        view.setOnTouchListener(null);
        int layoutPosition = 0;
        SuperLikeView.stop();
        T remove = dataList.remove(layoutPosition);
        adapter.notifyDataSetChanged();
        if (mListener != null) {
            mListener.onSwiped(view, remove, direction == ItemTouchHelper.LEFT ? CardConfig.SWIPED_LEFT : CardConfig.SWIPED_RIGHT);
        }
        view.setAlpha(1.0f);
        // 当没有数据时回调 mListener
        if (adapter.getItemCount() == 5 || adapter.getItemCount() == 3 || adapter.getItemCount() == 0) {
            if (mListener != null) {
                mListener.onSwipedClear();
            }
        }
    }

    /**
     * 是否可以删除
     *
     * @return
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            float ratio = dX / getThreshold(recyclerView);

            // ratio 最大为 1 或 -1
            if (ratio > 1) {
                ratio = 1;
            } else if (ratio < -1) {
                ratio = -1;
            }
            itemView.setRotation(ratio * CardConfig.DEFAULT_ROTATE_DEGREE);
            if (ratio == 1 || ratio == -1)
                itemView.setAlpha(0f);
            if (mListener != null) {
                if (ratio != 0) {
                    mListener.onSwiping(itemView, ratio, ratio < 0 ? CardConfig.SWIPING_LEFT : CardConfig.SWIPING_RIGHT);
                } else {
                    mListener.onSwiping(itemView, ratio, CardConfig.SWIPING_NONE);
                }
            }
        }
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setRotation(0f);
        viewHolder.itemView.setAlpha(1f);
        if (mListener != null) {
            mListener.onReset();
        }
    }

    private float getThreshold(RecyclerView recyclerView) {
        return recyclerView.getWidth() * 0.6f;
    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return 0.1f;
    }


}
