package com.zb.module_card.adapter;

import android.view.View;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.adapter.BindingItemAdapter;
import com.zb.lib_base.adapter.RecyclerHolder;
import com.zb.lib_base.iv.SuperLikeInterface;
import com.zb.lib_base.model.PairInfo;
import com.zb.lib_base.views.SuperLikeView;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_card.BR;
import com.zb.module_card.R;
import com.zb.module_card.databinding.ItemCardBinding;
import com.zb.module_card.vm.MemberDetailViewModel;

import java.util.List;
import java.util.Random;

import androidx.databinding.ViewDataBinding;

public class CardAdapter<T> extends BindingItemAdapter<T> {

    private BaseViewModel viewModel;
    private RxAppCompatActivity activity;
    private int selectImageIndex = 0;
    private View currentView;
    private Random ra = new Random();

    public View getCurrentView() {
        return currentView;
    }

    public void setCurrentView(View currentView) {
        this.currentView = currentView;
    }


    public CardAdapter(RxAppCompatActivity activity, int layoutId, List<T> list, BaseViewModel viewModel) {
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
            ItemCardBinding mBinding = (ItemCardBinding) holder.binding;
            CardAdapter adapter = new CardAdapter<>(activity, R.layout.item_card_image, ((PairInfo) t).getImageList(), viewModel);
            adapter.setCurrentView(holder.binding.getRoot());
            holder.binding.setVariable(BR.adapter, adapter);
            holder.binding.setVariable(BR.currentView, holder.binding.getRoot());
            mBinding.likeLayout.removeAllViews();
            if (position == 0) {
                SuperLikeView superLikeView = new SuperLikeView(mContext);
                SuperLikeView.superLike(superLikeView, (SuperLikeInterface) viewModel, (PairInfo) t, currentView, true, true);
                mBinding.likeLayout.addView(superLikeView);
            }
        }

        if (viewModel != null) {
            holder.binding.setVariable(BR.viewModel, viewModel);
        }

        if (viewModel instanceof MemberDetailViewModel) {
            holder.binding.setVariable(BR.raIndex, ra.nextInt(5));
        }
        holder.binding.executePendingBindings();
    }
}
