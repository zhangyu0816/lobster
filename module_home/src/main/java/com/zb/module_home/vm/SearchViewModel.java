package com.zb.module_home.vm;

import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.zb.lib_base.api.searchApi;
import com.zb.lib_base.db.AreaDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_home.R;
import com.zb.module_home.adapter.HomeAdapter;
import com.zb.module_home.databinding.HomeSearchBinding;
import com.zb.module_home.iv.SearchVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import io.realm.Realm;

public class SearchViewModel extends BaseViewModel implements SearchVMInterface, OnLoadMoreListener {
    private HomeSearchBinding mBinding;
    public HomeAdapter adapter;
    private AreaDb areaDb;
    private List<MemberInfo> memberInfoList = new ArrayList<>();
    private int pageNo = 1;
    private String keyWord = "";

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (HomeSearchBinding) binding;
        areaDb = new AreaDb(Realm.getDefaultInstance());
        mBinding.setNoData(false);
        setAdapter();

        // 发送
        mBinding.edSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (mBinding.getSearchKey().isEmpty()) {
                    SCToastUtil.showToast(activity, "请输入搜索内容", true);
                    return true;
                }
                pageNo = 1;
                memberInfoList.clear();
                adapter.notifyDataSetChanged();
                keyWord = mBinding.getSearchKey();
                search();
            }
            return true;
        });
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void setAdapter() {
        adapter = new HomeAdapter<>(activity, R.layout.item_search, memberInfoList, this);
        mBinding.recyclerView.addItemDecoration(new DividerItemDecoration(mBinding.getRoot().getContext(), LinearLayout.VERTICAL));
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(mBinding.getRoot().getContext()));
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        pageNo++;
        search();
    }

    @Override
    public void search() {
        searchApi api = new searchApi(new HttpOnNextListener<List<MemberInfo>>() {
            @Override
            public void onNext(List<MemberInfo> o) {
                mBinding.setNoData(false);
                int start = memberInfoList.size();
                memberInfoList.addAll(o);
                adapter.notifyItemRangeChanged(start, memberInfoList.size());
                mBinding.refresh.finishRefresh();
                mBinding.refresh.finishLoadMore();
                mBinding.setSearchKey("");
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    mBinding.refresh.setEnableLoadMore(false);
                    mBinding.refresh.finishRefresh();
                    mBinding.refresh.finishLoadMore();
                    mBinding.setSearchKey("");
                    if (memberInfoList.size() == 0) {
                        mBinding.setNoData(true);
                    }
                }
            }
        }, activity)
                .setKeyWord(keyWord)
                .setPageNo(pageNo)
                .setMaxAge(100)
                .setMinAge(0)
                .setSex(-1);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void toMemberDetail(long userId) {
        ActivityUtils.getCardMemberDetail(userId, false);
    }
}
