package com.zb.module_bottle.activity;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_bottle.BR;
import com.zb.module_bottle.R;
import com.zb.module_bottle.vm.BottleChatViewModel;

import java.lang.reflect.Field;

@Route(path = RouteUtils.Bottle_Chat)
public class BottleChatActivity extends BottleBaseActivity {
    @Autowired(name = "driftBottleId")
    long driftBottleId;
    @Autowired(name = "nick")
    String nick = "";

    @Override
    public int getRes() {
        return R.layout.bottle_chat;
    }

    @Override
    public void initUI() {
        BottleChatViewModel viewModel = new BottleChatViewModel();
        viewModel.driftBottleId = driftBottleId;
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.nick, nick);
        mBinding.setVariable(BR.viewModel, viewModel);

        final Context context = getApplicationContext();
        final RelativeLayout parentLayout = (RelativeLayout) findViewById(R.id.parent);
        final View myLayout = getWindow().getDecorView();
        parentLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                // r will be populated with the coordinates of your view that area still visible.
                parentLayout.getWindowVisibleDisplayFrame(r);
                int screenHeight = myLayout.getRootView().getHeight();
                int heightDiff = screenHeight - (r.bottom - r.top);
                if (heightDiff > 100) {
                    // if more than 100 pixels, its probably a keyboard
                    // get status bar height
                    int statusBarHeight = 0;
                    try {
                        Class<?> c = Class.forName("com.android.internal.R$dimen");
                        Object obj = c.newInstance();
                        Field field = c.getField("status_bar_height");
                        int x = Integer.parseInt(field.get(obj).toString());
                        statusBarHeight = context.getResources().getDimensionPixelSize(x);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    int realKeyboardHeight = heightDiff - statusBarHeight;
                    Log.i("","keyboard height = " + realKeyboardHeight);
                }
            }
        });
    }
}
