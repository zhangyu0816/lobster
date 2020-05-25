package com.zb.module_chat.activity;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_chat.BR;
import com.zb.module_chat.R;
import com.zb.module_chat.vm.ChatViewModel;

import java.lang.reflect.Field;

@Route(path = RouteUtils.Chat_Activity)
public class ChatActivity extends ChatBaseActivity {
    @Autowired(name = "otherUserId")
    long otherUserId;

    @Override
    public int getRes() {
        return R.layout.chat_chat;
    }

    @Override
    public void initUI() {
        ChatViewModel viewModel = new ChatViewModel();
        viewModel.otherUserId = otherUserId;
        viewModel.setBinding(mBinding);


//        final View myLayout = getWindow().getDecorView();
//        binding.mainRelative.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
//            Rect r = new Rect();
//            // r will be populated with the coordinates of your view that area still visible.
//            binding.mainRelative.getWindowVisibleDisplayFrame(r);
//            int screenHeight = myLayout.getRootView().getHeight();
//            int heightDiff = screenHeight - (r.bottom - r.top);
//            if (heightDiff > 100) {
//                // if more than 100 pixels, its probably a keyboard
//                // get status bar height
//                int statusBarHeight = 0;
//                try {
//                    Class<?> c = Class.forName("com.android.internal.R$dimen");
//                    Object obj = c.newInstance();
//                    Field field = c.getField("status_bar_height");
//                    int x = Integer.parseInt(field.get(obj).toString());
//                    statusBarHeight = activity.getResources().getDimensionPixelSize(x);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                int realKeyboardHeight = heightDiff - statusBarHeight;
//                Log.i("","keyboard height = " + realKeyboardHeight);
//            }
//        });
    }
}
