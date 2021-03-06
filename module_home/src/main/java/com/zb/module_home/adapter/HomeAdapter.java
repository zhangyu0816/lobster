package com.zb.module_home.adapter;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.adapter.BindingItemAdapter;
import com.zb.lib_base.adapter.RecyclerHolder;
import com.zb.lib_base.api.otherInfoApi;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.model.Reward;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.BasePopupWindow;
import com.zb.module_home.BR;
import com.zb.module_home.vm.RewardListViewModel;

import java.util.List;

import androidx.databinding.ViewDataBinding;

public class HomeAdapter<T> extends BindingItemAdapter<T> {

    private BaseViewModel viewModel;
    private BasePopupWindow pw;
    private int selectIndex = -1;

    public HomeAdapter(RxAppCompatActivity activity, int layoutId, List<T> list, BasePopupWindow pw) {
        super(activity, layoutId, list);
        this.pw = pw;
    }

    public HomeAdapter(RxAppCompatActivity activity, int layoutId, List<T> list, BaseViewModel viewModel) {
        super(activity, layoutId, list);
        this.viewModel = viewModel;
    }

    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
    }

    @Override
    protected void onBind(RecyclerHolder<ViewDataBinding> holder, T t, int position) {
        holder.binding.setVariable(BR.item, t);
        holder.binding.setVariable(BR.position, position);
        holder.binding.setVariable(BR.isLast, position == getItemCount() - 1);
        holder.binding.setVariable(BR.isSelect, position == selectIndex);

        if (viewModel != null) {
            holder.binding.setVariable(BR.viewModel, viewModel);
        }
        if (pw != null) {
            holder.binding.setVariable(BR.pw, pw);
        }

        if (viewModel instanceof RewardListViewModel && t instanceof Reward) {
            otherInfoApi api = new otherInfoApi(new HttpOnNextListener<MemberInfo>() {
                @Override
                public void onNext(MemberInfo o) {
                    ((Reward) t).setFaceAttest(o.getFaceAttest());
                    ((Reward) t).setMemberType(o.getMemberType());
                    notifyItemChanged(position);
                }
            }, mContext).setOtherUserId(((Reward) t).getUserId());
            HttpManager.getInstance().doHttpDeal(api);
        }
        holder.binding.executePendingBindings();
    }
}
