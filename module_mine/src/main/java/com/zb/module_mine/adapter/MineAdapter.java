package com.zb.module_mine.adapter;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.adapter.BindingItemAdapter;
import com.zb.lib_base.adapter.ItemTouchHelperAdapter;
import com.zb.lib_base.adapter.RecyclerHolder;
import com.zb.lib_base.api.contactNumApi;
import com.zb.lib_base.api.otherInfoApi;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.ContactNum;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.BasePopupWindow;
import com.zb.module_mine.BR;
import com.zb.module_mine.vm.FCLViewModel;

import java.util.Collections;
import java.util.List;

import androidx.databinding.ViewDataBinding;

public class MineAdapter<T> extends BindingItemAdapter<T> implements ItemTouchHelperAdapter {

    private BaseViewModel viewModel;
    private BasePopupWindow pw;
    private int selectIndex = -1;

    public MineAdapter(RxAppCompatActivity activity, int layoutId, List<T> list, BaseViewModel viewModel) {
        super(activity, layoutId, list);
        this.viewModel = viewModel;
    }

    public MineAdapter(RxAppCompatActivity activity, int layoutId, List<T> list, BasePopupWindow pw) {
        super(activity, layoutId, list);
        this.pw = pw;
    }

    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
    }

    public int getSelectIndex() {
        return selectIndex;
    }

    @Override
    protected void onBind(RecyclerHolder<ViewDataBinding> holder, T t, int position) {
        holder.binding.setVariable(BR.item, t);
        holder.binding.setVariable(BR.position, position);
        holder.binding.setVariable(BR.isSelect, position == selectIndex);
        if (viewModel != null) {
            holder.binding.setVariable(BR.viewModel, viewModel);
        }
        if (pw != null) {
            holder.binding.setVariable(BR.pw, pw);
        }
        if (viewModel instanceof FCLViewModel) {
            ((FCLViewModel) viewModel).getContactNum((MemberInfo) t, position);
        }
        if (viewModel instanceof FCLViewModel) {
            if (((FCLViewModel) viewModel).position == 2) {
                otherInfoApi api = new otherInfoApi(new HttpOnNextListener<MemberInfo>() {
                    @Override
                    public void onNext(MemberInfo o) {
                        ((MemberInfo) t).setPersonalitySign(o.getPersonalitySign());
                        notifyItemChanged(position);
                    }
                }, mContext).setOtherUserId(((MemberInfo) t).getUserId());
                HttpManager.getInstance().doHttpDeal(api);
            } else {
                contactNumApi api = new contactNumApi(new HttpOnNextListener<ContactNum>() {
                    @Override
                    public void onNext(ContactNum o) {
                        ((MemberInfo) t).setBeLikeQuantity(o.getBeLikeCount());
                        ((MemberInfo) t).setFansQuantity(o.getFansCount());
                        notifyItemChanged(position);
                    }
                }, mContext).setOtherUserId(((MemberInfo) t).getUserId());
                HttpManager.getInstance().doHttpDeal(api);
            }
        }
        holder.binding.executePendingBindings();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (!getList().get(fromPosition).equals("") && !getList().get(toPosition).equals("")) {
            if (fromPosition < toPosition) {
                //从上往下拖动，每滑动一个item，都将list中的item向下交换，向上滑同理。
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(getList(), i, i + 1);//交换数据源两个数据的位置
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(getList(), i, i - 1);//交换数据源两个数据的位置
                }
            }
            //更新视图
            notifyItemMoved(fromPosition, toPosition);
        }
    }

    @Override
    public void onItemDelete(int position) {

    }
}
