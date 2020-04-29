package com.zb.lib_base.adapter;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.List;

import androidx.annotation.LayoutRes;
import androidx.databinding.ViewDataBinding;

public abstract class BindingItemAdapter<T> extends RecyclerAdapter<T, ViewDataBinding> {

    public BindingItemAdapter(RxAppCompatActivity activity, @LayoutRes int layoutId, List<T> list) {
        super(activity, list, layoutId);

    }
}
