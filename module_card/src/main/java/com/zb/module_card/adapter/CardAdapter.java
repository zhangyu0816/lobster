package com.zb.module_card.adapter;

import com.zb.lib_base.adapter.BindingItemAdapter;
import com.zb.lib_base.adapter.RecyclerHolder;
import com.zb.lib_base.model.PairInfo;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_card.BR;
import com.zb.module_card.R;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;

public class CardAdapter<T> extends BindingItemAdapter<T> {

    private BaseViewModel viewModel;
    private AppCompatActivity activity;
    private int selectImageIndex = 0;

    public CardAdapter(AppCompatActivity activity, int layoutId, List<T> list, BaseViewModel viewModel) {
        super(activity, layoutId, list);
        this.activity = activity;
        this.viewModel = viewModel;
    }

    public int getSelectImageIndex() {
        return selectImageIndex;
    }

    public void setSelectImageIndex(int selectImageIndex) {
        this.selectImageIndex = selectImageIndex;
    }

    @Override
    protected void onBind(RecyclerHolder<ViewDataBinding> holder, T t, int position) {
        holder.binding.setVariable(BR.item, t);
        holder.binding.setVariable(BR.position, position);

        holder.binding.setVariable(BR.isImageSelect, position == selectImageIndex);
        if (t instanceof PairInfo) {
            CardAdapter adapter = new CardAdapter<>(activity, R.layout.item_card_image, ((PairInfo) t).getImageList(), viewModel);
            holder.binding.setVariable(BR.adapter, adapter);
            holder.binding.setVariable(BR.currentView, holder.binding.getRoot());
        }

        if (viewModel != null) {
            holder.binding.setVariable(BR.viewModel, viewModel);
        }
        holder.binding.executePendingBindings();
    }
}
