package com.zb.module_home.vm;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.umeng.socialize.media.UMImage;
import com.zb.lib_base.api.dynCancelLikeApi;
import com.zb.lib_base.api.dynDoLikeApi;
import com.zb.lib_base.api.dynPiazzaListApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.AreaDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.CollectID;
import com.zb.lib_base.model.DiscoverInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DouYinLayoutManager;
import com.zb.lib_base.utils.OnViewPagerListener;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.SharePW;
import com.zb.module_home.R;
import com.zb.module_home.adapter.HomeAdapter;
import com.zb.module_home.databinding.HomeVideoListBinding;
import com.zb.module_home.databinding.ItemVideoBinding;
import com.zb.module_home.iv.VideoListVMInterface;
import com.zb.module_home.windows.GiftPW;
import com.zb.module_home.windows.GiftPayPW;
import com.zb.module_home.windows.ReviewPW;

import java.util.List;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.OrientationHelper;
import io.realm.Realm;

public class VideoListViewModel extends BaseViewModel implements VideoListVMInterface {
    public int position;
    public int pageNo;
    private HomeAdapter adapter;
    private DouYinLayoutManager douYinLayoutManager;
    private HomeVideoListBinding mBinding;
    private AreaDb areaDb;
    private boolean isUp = false;
    private boolean isOver = false;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (HomeVideoListBinding) binding;
        areaDb = new AreaDb(Realm.getDefaultInstance());
        setAdapter();
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void setAdapter() {
        adapter = new HomeAdapter<>(activity, R.layout.item_video, MineApp.discoverInfoList, this);

        douYinLayoutManager = new DouYinLayoutManager(activity, OrientationHelper.VERTICAL, false);
        mBinding.videoList.setLayoutManager(douYinLayoutManager);
        mBinding.videoList.setAdapter(adapter);
        mBinding.videoList.scrollToPosition(position);
        adapter.notifyDataSetChanged();
        douYinLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {
            @Override
            public void onPageRelease(boolean isNest, View view) {
//                releaseVideo(position);
            }

            @Override
            public void onPageSelected(boolean isButton, View view) {
//                playVideo(position);
                isUp = douYinLayoutManager.getDrift() >= 0;
                position = douYinLayoutManager.findFirstCompletelyVisibleItemPosition();
                SCToastUtil.showToast(activity, "方向" + (isUp ? "向上" : "向下") + position, true);
                if (!isOver && ((position < 3 && !isUp) || position == MineApp.discoverInfoList.size() - 1)) {
                    pageNo++;
                    dynPiazzaList();
                }
            }
        });

    }

    @Override
    public void dynPiazzaList() {
        dynPiazzaListApi api = new dynPiazzaListApi(new HttpOnNextListener<List<DiscoverInfo>>() {
            @Override
            public void onNext(List<DiscoverInfo> o) {
                if (isUp) {
                    // 上滑  底部加载更多
                    int start = MineApp.discoverInfoList.size();
                    MineApp.discoverInfoList.addAll(o);
                    adapter.notifyItemRangeChanged(start, MineApp.discoverInfoList.size());
                } else {
                    // 下滑  顶部加载更多
                    MineApp.discoverInfoList.addAll(0, o);
//                    mBinding.videoList.scrollToPosition(o.size());
                    adapter.notifyItemRangeChanged(0, o.size());
//                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    SCToastUtil.showToast(activity, "视频已加载完", true);
                    isOver = true;
                }
            }
        }, activity)
                .setCityId(areaDb.getCityId(MineApp.cityName))
                .setDynType(2)
                .setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void toMemberDetail(ViewDataBinding binding, DiscoverInfo discoverInfo) {
        ItemVideoBinding mBinding = (ItemVideoBinding) binding;
        mBinding.videoView.pause();
        ActivityUtils.getCardMemberDetail(discoverInfo.getUserId(), false);
    }

    @Override
    public void toReviews(int position) {
        DiscoverInfo discoverInfo = MineApp.discoverInfoList.get(position);
        new ReviewPW(activity, mBinding.getRoot(), discoverInfo.getFriendDynId(), discoverInfo.getReviews(), () -> {
            discoverInfo.setReviews(discoverInfo.getReviews() + 1);
            adapter.notifyItemChanged(position);
        });
    }

    @Override
    public void doGood(int position) {
        DiscoverInfo discoverInfo = MineApp.discoverInfoList.get(position);
        if (goodDb.hasGood(discoverInfo.getFriendDynId())) {
            dynCancelLike(discoverInfo, position);
        } else {
            dynDoLike(discoverInfo, position);
        }
    }

    @Override
    public void doShare(DiscoverInfo discoverInfo) {
        String sharedName = discoverInfo.getNick();
        String content = discoverInfo.getText();
        String sharedUrl = HttpManager.BASE_URL + "mobile/Dyn_dynDetail?friendDynId=" + discoverInfo.getFriendDynId();
        UMImage umImage = new UMImage(activity, discoverInfo.getImage().replace("YM0000", "430X430"));
        new SharePW(activity, mBinding.getRoot(), umImage, sharedName, content, sharedUrl);
    }

    @Override
    public void doReward(DiscoverInfo discoverInfo) {
        new GiftPW(activity, mBinding.getRoot(), giftInfo ->
                new GiftPayPW(activity, mBinding.getRoot(), giftInfo, discoverInfo.getFriendDynId(), () -> {
                }));
    }


    private void dynDoLike(DiscoverInfo discoverInfo, int position) {
        dynDoLikeApi api = new dynDoLikeApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                goodDb.saveGood(new CollectID(discoverInfo.getFriendDynId()));
                adapter.notifyItemChanged(position);

                Intent data = new Intent("lobster_doGood");
                data.putExtra("goodNum", discoverInfo.getGoodNum() + 1);
                data.putExtra("friendDynId", discoverInfo.getFriendDynId());
                activity.sendBroadcast(data);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == 0) {
                    if (TextUtils.equals(e.getMessage(), "已经赞过了")) {
                        goodDb.saveGood(new CollectID(discoverInfo.getFriendDynId()));
                        adapter.notifyItemChanged(position);

                        Intent data = new Intent("lobster_doGood");
                        data.putExtra("friendDynId", discoverInfo.getFriendDynId());
                        activity.sendBroadcast(data);
                    }
                }
            }
        }, activity).setFriendDynId(discoverInfo.getFriendDynId());
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void dynCancelLike(DiscoverInfo discoverInfo, int position) {
        dynCancelLikeApi api = new dynCancelLikeApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                goodDb.deleteGood(discoverInfo.getFriendDynId());
                adapter.notifyItemChanged(position);

                Intent data = new Intent("lobster_doGood");
                data.putExtra("goodNum", discoverInfo.getGoodNum() - 1);
                data.putExtra("friendDynId", discoverInfo.getFriendDynId());
                activity.sendBroadcast(data);
            }
        }, activity).setFriendDynId(discoverInfo.getFriendDynId());
        HttpManager.getInstance().doHttpDeal(api);
    }
}
