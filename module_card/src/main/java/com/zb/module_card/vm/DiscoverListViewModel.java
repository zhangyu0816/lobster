package com.zb.module_card.vm;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.adapter.ViewPagerAdapter;
import com.zb.lib_base.api.attentionOtherApi;
import com.zb.lib_base.api.cancelAttentionApi;
import com.zb.lib_base.api.contactNumApi;
import com.zb.lib_base.api.makeEvaluateApi;
import com.zb.lib_base.api.memberInfoConfApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.AttentionDb;
import com.zb.lib_base.db.LikeTypeDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.AttentionInfo;
import com.zb.lib_base.model.ContactNum;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.model.ShareInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.FragmentUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.FunctionPW;
import com.zb.lib_base.windows.SuperLikePW;
import com.zb.lib_base.windows.VipAdPW;
import com.zb.module_card.R;
import com.zb.module_card.databinding.CardDiscoverListBinding;
import com.zb.module_card.iv.DiscoverListVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

public class DiscoverListViewModel extends BaseViewModel implements DiscoverListVMInterface {
    public long otherUserId;
    private CardDiscoverListBinding mBinding;
    private List<Fragment> fragments = new ArrayList<>();
    public MemberInfo memberInfo;
    private BaseReceiver attentionReceiver;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (CardDiscoverListBinding) binding;
        contactNum();
        initFragments();

        attentionReceiver = new BaseReceiver(activity, "lobster_attention") {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isAttention = intent.getBooleanExtra("isAttention", false);
                mBinding.setIsAttention(isAttention);
            }
        };
    }

    public void onStart() {
        adapter.notifyItemChanged(mBinding.viewPage.getCurrentItem());
    }

    private ViewPagerAdapter adapter;

    private void initFragments() {
        fragments.clear();
        fragments.add(FragmentUtils.getCardMemberDiscoverFragment(otherUserId));
        fragments.add(FragmentUtils.getCardMemberVideoFragment(otherUserId));
        adapter = new ViewPagerAdapter(activity, fragments);
        mBinding.viewPage.setSaveEnabled(false);
        mBinding.viewPage.setAdapter(adapter);
        initTabLayout(new String[]{"动态", "小视频"}, mBinding.tabLayout, mBinding.viewPage, R.color.black_252, R.color.black_827, 0, false);
    }

    @Override
    public void back(View view) {
        super.back(view);
        try {
            attentionReceiver.unregisterReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
        activity.finish();
    }

    @Override
    public void more(View view) {
        super.more(view);
        memberInfoConf();
    }

    @Override
    public void follow(View view) {
        super.follow(view);
        if (!mBinding.getIsAttention()) {
            attentionOther();
        } else {
            cancelAttention();
        }
    }

    @Override
    public void attentionOther() {
        attentionOtherApi api = new attentionOtherApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                mBinding.setIsAttention(true);
                AttentionDb.getInstance().saveAttention(new AttentionInfo(otherUserId, mBinding.getMemberInfo().getNick(), mBinding.getMemberInfo().getImage(), true, BaseActivity.userId));
                activity.sendBroadcast(new Intent("lobster_attentionList"));
                Intent intent = new Intent("lobster_attention");
                intent.putExtra("isAttention", mBinding.getIsAttention());
                activity.sendBroadcast(intent);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.ERROR) {
                    if (e.getMessage().equals("已经关注过")) {
                        mBinding.setIsAttention(true);
                        AttentionDb.getInstance().saveAttention(new AttentionInfo(otherUserId, mBinding.getMemberInfo().getNick(), mBinding.getMemberInfo().getImage(), true, BaseActivity.userId));
                        activity.sendBroadcast(new Intent("lobster_attentionList"));
                    }
                }
            }
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void cancelAttention() {
        cancelAttentionApi api = new cancelAttentionApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                mBinding.setIsAttention(false);
                AttentionDb.getInstance().saveAttention(new AttentionInfo(otherUserId, mBinding.getMemberInfo().getNick(), mBinding.getMemberInfo().getImage(), false, BaseActivity.userId));
                activity.sendBroadcast(new Intent("lobster_attentionList"));
                Intent intent = new Intent("lobster_attention");
                intent.putExtra("isAttention", mBinding.getIsAttention());
                activity.sendBroadcast(intent);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.ERROR) {
                    if (e.getMessage().equals("你还没关注我啊")) {
                        mBinding.setIsAttention(false);
                        AttentionDb.getInstance().saveAttention(new AttentionInfo(otherUserId, mBinding.getMemberInfo().getNick(), mBinding.getMemberInfo().getImage(), false, BaseActivity.userId));
                        activity.sendBroadcast(new Intent("lobster_attentionList"));
                        Intent intent = new Intent("lobster_attention");
                        intent.putExtra("isAttention", mBinding.getIsAttention());
                        activity.sendBroadcast(intent);
                    }
                }
            }
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void contactNum() {
        contactNumApi api = new contactNumApi(new HttpOnNextListener<ContactNum>() {
            @Override
            public void onNext(ContactNum o) {
                mBinding.setContactNum(o);
            }
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void memberInfoConf() {
        memberInfoConfApi api = new memberInfoConfApi(new HttpOnNextListener<ShareInfo>() {
            @Override
            public void onNext(ShareInfo o) {
                String sharedUrl = HttpManager.BASE_URL + "render/" + otherUserId + ".html?sharetextId="
                        + o.getSharetextId();
                String sharedName = o.getText().replace("{userId}", memberInfo.getUserId() + "");
                sharedName = sharedName.replace("{nick}", memberInfo.getNick());
                String content;
                if (memberInfo.getServiceTags().isEmpty()) {
                    content = o.getText();
                } else {
                    content = memberInfo.getServiceTags().substring(1, memberInfo.getServiceTags().length() - 1);
                    content = "兴趣：" + content.replace("#", ",");
                }

                new FunctionPW(mBinding.getRoot(), memberInfo.getImage().replace("YM0000", "430X430"), sharedName, content, sharedUrl,
                        otherUserId == BaseActivity.userId, false, false, false, new FunctionPW.CallBack() {
                    @Override
                    public void gift() {

                    }

                    @Override
                    public void delete() {
                    }

                    @Override
                    public void report() {
                        ActivityUtils.getHomeReport(otherUserId);
                    }

                    @Override
                    public void download() {

                    }

                    @Override
                    public void like() {
                        superLike(null);
                    }
                });
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void superLike(View view) {
        super.superLike(view);
        if (MineApp.mineInfo.getMemberType() == 2) {
            makeEvaluate();
        } else {
            if (memberInfo != null)
                new VipAdPW(mBinding.getRoot(), 3, memberInfo.getImage());
        }
    }

    @Override
    public void makeEvaluate() {
        //  likeOtherStatus  0 不喜欢  1 喜欢  2.超级喜欢 （非会员提示开通会员）
        makeEvaluateApi api = new makeEvaluateApi(new HttpOnNextListener<Integer>() {
            @Override
            public void onNext(Integer o) {
                String myHead = MineApp.mineInfo.getImage();
                String otherHead = memberInfo.getImage();
                // 1喜欢成功 2匹配成功 3喜欢次数用尽
                if (o == 1) {
                    LikeTypeDb.getInstance().setType(otherUserId, 2);
                    new SuperLikePW(mBinding.getRoot(), myHead, otherHead, MineApp.mineInfo.getSex(), memberInfo.getSex());
                } else if (o == 4) {
                    // 超级喜欢时，非会员或超级喜欢次数用尽
                    if (MineApp.mineInfo.getMemberType() == 2) {
                        SCToastUtil.showToast(activity, "今日超级喜欢次数已用完", true);
                    } else {
                        new VipAdPW(mBinding.getRoot(), 3, otherHead);
                    }
                } else {
                    LikeTypeDb.getInstance().setType(otherUserId, 2);
                    SCToastUtil.showToast(activity, "你已超级喜欢过对方", true);
                }

            }
        }, activity).setOtherUserId(otherUserId).setLikeOtherStatus(2);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void contactNumDetail(int position) {
        ActivityUtils.getMineFCL(position, otherUserId);
    }

    @Override
    public void update(View view) {
        SCToastUtil.showToast(activity, "已发送您的求更请求", true);
    }
}
