package com.zb.lib_base.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.List;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

public abstract class RecyclerAdapter<T, B extends ViewDataBinding> extends RecyclerView.Adapter<RecyclerHolder<B>> {

    private RxAppCompatActivity mContext;
    private List<T> mList;
    private int layoutId;

   public RecyclerAdapter(RxAppCompatActivity context, List<T> list, @LayoutRes int layoutId) {
        this.mContext = context;
        this.mList = list;
        this.layoutId = layoutId;
    }

    @NonNull
    @Override
    public RecyclerHolder<B> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        B bing = DataBindingUtil.inflate(LayoutInflater.from(mContext), layoutId, parent, false);
        return new RecyclerHolder<>(bing);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerHolder<B> holder, int position) {
        final T t = mList.get(position);
        onBind(holder, t, position);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public List<T> getList() {
        return mList;
    }

    protected abstract void onBind(RecyclerHolder<B> holder, T t, int position);

    /*
     Recylerview的item是 ImageView 和  TextView构成，当数据改变时，我们会调用 notifyDataSetChanged，这个时候列表会刷新，
     为了使 url 没变的 ImageView 不重新加载（图片会一闪）我们可以用 setHasStableIds(true);
   */
    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    /*
      使用这个，相当于给ImageView加了一个tag，tag不变的话，不用重新加载图片。
      但是加了这句话，会使得 列表的 数据项 重复！！ 我们需要在我们的Adapter里面重写 getItemId就好了。
    */
    @Override
    public long getItemId(int position) {
        return position;
    }
}
