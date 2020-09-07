package com.zb.module_card.views;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import com.zb.lib_base.db.MineInfoDb;
import com.zb.lib_base.model.MineInfo;
import com.zb.module_card.R;
import com.zb.module_card.databinding.ProgressBinding;

import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import io.realm.Realm;

public class ProgressView extends RelativeLayout {
    private ProgressBinding mBinding;
    private MineInfo mineInfo;
    private MineInfoDb mineInfoDb;
    private static ObjectAnimator pvh;
    private PropertyValuesHolder pvhSY, pvhSX;

    public ProgressView(Context context) {
        super(context);
        init(context);
    }

    public ProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mineInfoDb = new MineInfoDb(Realm.getDefaultInstance());
        mineInfo = mineInfoDb.getMineInfo();
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.progress, null, false);
        addView(mBinding.getRoot());

        mBinding.setMineInfo(mineInfo);
        pvhSY = PropertyValuesHolder.ofFloat("scaleY", 1, 2.3f);
        pvhSX = PropertyValuesHolder.ofFloat("scaleX", 1, 2.3f);
        pvh = ObjectAnimator.ofPropertyValuesHolder(mBinding.ivProgress, pvhSY, pvhSX).setDuration(1000);
        pvh.setRepeatCount(Animation.INFINITE);
        pvh.start();
    }

    public static void play() {
        pvh.start();
    }

    public static void stop() {
        if (pvh != null)
            pvh.cancel();
    }

    @BindingAdapter("isPlay")
    public static void onClick(final ProgressView view, boolean isPlay) {
        if (isPlay) {
            play();
        } else {
            stop();
        }
    }
}
