package com.zb.module_card.vm;

import android.content.Intent;
import android.view.View;

import com.umeng.socialize.media.UMImage;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.adapter.FragmentAdapter;
import com.zb.lib_base.api.attentionOtherApi;
import com.zb.lib_base.api.cancelAttentionApi;
import com.zb.lib_base.api.contactNumApi;
import com.zb.lib_base.api.memberInfoConfApi;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.AttentionInfo;
import com.zb.lib_base.model.ContactNum;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.model.ShareInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.FragmentUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.FunctionPW;
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

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (CardDiscoverListBinding) binding;
        contactNum();
        initFragments();
    }

    private void initFragments() {
        fragments.clear();
        fragments.add(FragmentUtils.getCardMemberDiscoverFragment(otherUserId));
        fragments.add(FragmentUtils.getCardMemberVideoFragment(otherUserId));
        mBinding.viewPage.setAdapter(new FragmentAdapter(activity.getSupportFragmentManager(), fragments));
        initTabLayout(new String[]{"动态", "小视频"}, mBinding.tabLayout, mBinding.viewPage, R.color.black_252, R.color.black_827, 0);
    }

    @Override
    public void back(View view) {
        super.back(view);
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
                attentionDb.saveAttention(new AttentionInfo(otherUserId, mBinding.getMemberInfo().getNick(), mBinding.getMemberInfo().getImage(), true, BaseActivity.userId));
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
                        attentionDb.saveAttention(new AttentionInfo(otherUserId, mBinding.getMemberInfo().getNick(), mBinding.getMemberInfo().getImage(), true, BaseActivity.userId));
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
                attentionDb.saveAttention(new AttentionInfo(otherUserId, mBinding.getMemberInfo().getNick(), mBinding.getMemberInfo().getImage(), false, BaseActivity.userId));
                activity.sendBroadcast(new Intent("lobster_attentionList"));
                Intent intent = new Intent("lobster_attention");
                intent.putExtra("isAttention", mBinding.getIsAttention());
                activity.sendBroadcast(intent);
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
                UMImage umImage = new UMImage(activity, memberInfo.getImage().replace("YM0000", "430X430"));
                String content = "";
                if (memberInfo.getServiceTags().isEmpty()) {
                    content = o.getText();
                } else {
                    content = memberInfo.getServiceTags().substring(1, memberInfo.getServiceTags().length() - 1);
                    content = "兴趣：" + content.replace("#", ",");
                }

                new FunctionPW(activity, mBinding.getRoot(), umImage, sharedName, content, sharedUrl,
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
}
