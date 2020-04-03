package com.zb.module_register;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.KeyBroadUtils;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_register.databinding.RegisterCodeBinding;
import com.zb.module_register.vm.CodeViewModel;

@Route(path = RouteUtils.Register_Code)
public class CodeActivity extends RegisterBaseActivity implements KeyBroadUtils.OnSoftKeyboardChangeListener {
    private RegisterCodeBinding binding;
    private TextView[] array = new TextView[4];

    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener;

    @Override
    public int getRes() {
        return R.layout.register_code;
    }

    @Override
    public void initUI() {
        CodeViewModel viewModel = new CodeViewModel();
        mBinding.setVariable(BR.viewModel, viewModel);
        viewModel.setBinding(mBinding);
        binding = (RegisterCodeBinding) mBinding;

        array[0] = binding.tvCode1;
        array[1] = binding.tvCode2;
        array[2] = binding.tvCode3;
        array[3] = binding.tvCode4;
        binding.edCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String content = binding.edCode.getText().toString();
                for (int i = 0; i < 4; i++) {
                    if (i < content.length()) {
                        array[i].setText(String.valueOf(content.charAt(i)));
                    } else {
                        array[i].setText("");
                    }
                    if (content.length() == 4) {
                        binding.tvNext.setBackgroundResource(R.drawable.btn_bg_white_radius60);
                        binding.tvNext.setTextColor(MineApp.getInstance().getResources().getColor(R.color.purple_7a4));
                        binding.tvNext2.setBackgroundResource(R.drawable.btn_bg_white_radius60);
                        binding.tvNext2.setTextColor(MineApp.getInstance().getResources().getColor(R.color.purple_7a4));
                    }else{
                        binding.tvNext.setBackgroundResource(R.drawable.btn_bg_purple_af9_radius60);
                        binding.tvNext.setTextColor(MineApp.getInstance().getResources().getColor(R.color.purple_cab));
                        binding.tvNext2.setBackgroundResource(R.drawable.btn_bg_purple_af9_radius60);
                        binding.tvNext2.setTextColor(MineApp.getInstance().getResources().getColor(R.color.purple_cab));
                    }
                }
            }
        });
        ViewGroup.LayoutParams lp = binding.includeLayout.whiteView.getLayoutParams();
        lp.width = MineApp.W * 2 / 3;
        binding.includeLayout.whiteView.setLayoutParams(lp);

        mOnGlobalLayoutListener = KeyBroadUtils.observeSoftKeyboard(this, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        KeyBroadUtils.removeSoftKeyboardObserver(this, mOnGlobalLayoutListener);
    }

    @Override
    public void onSoftKeyBoardChange(int softKeyboardHeight, boolean visible) {
        binding.tvNext.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
