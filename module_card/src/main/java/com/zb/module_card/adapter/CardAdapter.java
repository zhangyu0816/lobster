package com.zb.module_card.adapter;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.adapter.BindingItemAdapter;
import com.zb.lib_base.adapter.RecyclerHolder;
import com.zb.lib_base.model.PairInfo;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.BasePopupWindow;
import com.zb.module_card.BR;
import com.zb.module_card.R;

import java.util.List;

import androidx.databinding.ViewDataBinding;

public class CardAdapter<T> extends BindingItemAdapter<T> {

    private BaseViewModel viewModel;
    private RxAppCompatActivity activity;
    private BasePopupWindow pw;
    private int selectImageIndex = 0;
    private int selectIndex = -1;
    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
    }

    public CardAdapter(RxAppCompatActivity activity, int layoutId, List<T> list, BaseViewModel viewModel) {
        super(activity, layoutId, list);
        this.activity = activity;
        this.viewModel = viewModel;
    }

    public CardAdapter(RxAppCompatActivity activity, int layoutId, List<T> list, BasePopupWindow pw) {
        super(activity, layoutId, list);
        this.activity = activity;
        this.pw = pw;
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
        holder.binding.setVariable(BR.isSelect, position == selectIndex);
        holder.binding.setVariable(BR.isImageSelect, position == selectImageIndex);
        if (t instanceof PairInfo) {
            CardAdapter adapter = new CardAdapter<>(activity, R.layout.item_card_image, ((PairInfo) t).getImageList(), viewModel);
            holder.binding.setVariable(BR.adapter, adapter);
            holder.binding.setVariable(BR.currentView, holder.binding.getRoot());
        }

        if (viewModel != null) {
            holder.binding.setVariable(BR.viewModel, viewModel);
        }
        if (pw != null) {
            holder.binding.setVariable(BR.pw, pw);
        }
        holder.binding.executePendingBindings();
    }
}
