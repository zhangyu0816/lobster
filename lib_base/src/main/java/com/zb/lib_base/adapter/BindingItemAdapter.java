package com.zb.lib_base.adapter;

import java.util.List;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;

public abstract class BindingItemAdapter<T> extends RecyclerAdapter<T, ViewDataBinding> {


    public BindingItemAdapter(AppCompatActivity activity, @LayoutRes int layoutId, List<T> list) {
        super(activity, list, layoutId);

    }
}
