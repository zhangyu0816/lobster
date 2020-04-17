package com.zb.module_bottle.adapter;

import com.zb.lib_base.adapter.BindingItemAdapter;
import com.zb.lib_base.adapter.RecyclerHolder;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;

public class BottleAdapter<T> extends BindingItemAdapter<T> {

    public BottleAdapter(AppCompatActivity activity, int layoutId, List<T> list) {
        super(activity, layoutId, list);
    }

    @Override
    protected void onBind(RecyclerHolder<ViewDataBinding> holder, T t, int position) {

    }
}
