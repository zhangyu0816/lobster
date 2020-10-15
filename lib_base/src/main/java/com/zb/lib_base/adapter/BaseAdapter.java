package com.zb.lib_base.adapter;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.BR;
import com.zb.lib_base.model.HistoryMsg;
import com.zb.lib_base.model.StanzaInfo;
import com.zb.lib_base.vm.BaseChatViewModel;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.BasePopupWindow;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import androidx.databinding.ViewDataBinding;

public class BaseAdapter<T> extends BindingItemAdapter<T> {
    private BasePopupWindow pw;
    private BaseViewModel viewModel;
    private int selectIndex = -1;

    public BaseAdapter(RxAppCompatActivity activity, int layoutId, List<T> list, BaseViewModel viewModel) {
        super(activity, layoutId, list);
        this.viewModel = viewModel;
    }

    public BaseAdapter(RxAppCompatActivity activity, int layoutId, List<T> list, BasePopupWindow pw) {
        super(activity, layoutId, list);
        this.pw = pw;
    }

    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
    }

    @Override
    protected void onBind(RecyclerHolder<ViewDataBinding> holder, T t, int position) {
        holder.binding.setVariable(BR.item, t);
        holder.binding.setVariable(BR.position, position);
        holder.binding.setVariable(BR.isSelect, position == selectIndex);
        if (pw != null) {
            holder.binding.setVariable(BR.pw, pw);
        }
        if (viewModel != null) {
            holder.binding.setVariable(BR.viewModel, viewModel);
        }
        if (viewModel instanceof BaseChatViewModel && t instanceof HistoryMsg) {
            HistoryMsg item = (HistoryMsg) t;
            if (item.getMsgType() == 112) {
                try {
                    JSONObject object = new JSONObject(item.getStanza());
                    StanzaInfo stanzaInfo = new StanzaInfo();
                    if (object.has("image"))
                        stanzaInfo.setImage(object.optString("image"));
                    if (object.has("styleType"))
                        stanzaInfo.setStyleType(object.optInt("styleType"));
                    if (object.has("sex"))
                        stanzaInfo.setSex(object.optInt("sex"));
                    if (object.has("link"))
                        stanzaInfo.setLink(object.optString("link"));
                    if (object.has("memberType"))
                        stanzaInfo.setMemberType(object.optInt("memberType"));
                    if (object.has("title"))
                        stanzaInfo.setTitle(object.optString("title"));
                    if (object.has("content"))
                        stanzaInfo.setContent(object.optString("content"));
                    if (object.has("linkContent"))
                        stanzaInfo.setLinkContent(object.optString("linkContent"));
                    holder.binding.setVariable(BR.stanzaInfo, stanzaInfo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        holder.binding.executePendingBindings();
    }
}
