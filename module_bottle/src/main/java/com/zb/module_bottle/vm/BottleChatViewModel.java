package com.zb.module_bottle.vm;

import android.view.View;

import com.zb.lib_base.api.myBottleApi;
import com.zb.lib_base.api.replyBottleApi;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.BottleInfo;
import com.zb.lib_base.model.BottleMsg;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_bottle.BR;
import com.zb.module_bottle.R;
import com.zb.module_bottle.adapter.BottleAdapter;
import com.zb.module_bottle.databinding.BottleChatBinding;
import com.zb.module_bottle.iv.BottleChatVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.ViewDataBinding;

public class BottleChatViewModel extends BaseViewModel implements BottleChatVMInterface {
    public long driftBottleId;
    public BottleAdapter adapter;
    private List<BottleMsg> bottleMsgList = new ArrayList<>();
    private BottleChatBinding mBinding;
    public MineInfo mineInfo;
    public BottleInfo bottleInfo;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (BottleChatBinding) binding;
        mineInfo = mineInfoDb.getMineInfo();
        setAdapter();
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void setAdapter() {
        adapter = new BottleAdapter<>(activity, R.layout.item_bottle_chat, bottleMsgList, this);
        myBottle();
    }

    @Override
    public void myBottle() {
        myBottleApi api = new myBottleApi(new HttpOnNextListener<BottleInfo>() {
            @Override
            public void onNext(BottleInfo o) {
                bottleInfo = o;
                bottleMsgList.addAll(o.getMessageList());
                adapter.notifyDataSetChanged();
                mBinding.chatList.scrollToPosition(adapter.getItemCount() - 1);
                mBinding.setVariable(BR.nick, bottleInfo.getOtherNick());
            }
        }, activity).setDriftBottleId(driftBottleId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void sendBottle(View view) {
        if (mBinding.getContent().isEmpty()) {
            SCToastUtil.showToastBlack(activity, "请输入回复内容");
            return;
        }
        replyBottleApi api = new replyBottleApi(new HttpOnNextListener<BottleMsg>() {
            @Override
            public void onNext(BottleMsg o) {
                if (o == null) {
                    SCToastUtil.showToastBlack(activity, "此漂流瓶已被销毁");
                } else {
                    bottleMsgList.add(o);
                    adapter.notifyDataSetChanged();
                    mBinding.chatList.scrollToPosition(adapter.getItemCount() - 1);
                }
            }
        }, activity).setDriftBottleId(driftBottleId).setText(mBinding.getContent());
        HttpManager.getInstance().doHttpDeal(api);
    }
}
