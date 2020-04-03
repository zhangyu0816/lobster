package com.zb.lib_base.utils;

import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class StaggeredDividerItemDecoration extends RecyclerView.ItemDecoration {
    private AppCompatActivity context;
    private int interval;

    public StaggeredDividerItemDecoration(AppCompatActivity context, int interval) {
        this.context = context;
        this.interval = interval;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
        // 获取item在span中的下标
        int spanIndex = params.getSpanIndex();
        int interval = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                this.interval, context.getResources().getDisplayMetrics());
        // 中间间隔
        if (spanIndex % 2 == 0) {
            outRect.left = 0;
        } else {
            // item为奇数位，设置其左间隔为5dp
            outRect.left = interval;
        }
        // 下方间隔
        outRect.bottom = interval;
    }
}
