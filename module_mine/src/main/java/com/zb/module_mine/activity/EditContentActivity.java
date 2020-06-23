package com.zb.module_mine.activity;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.databinding.MineEditContentBinding;
import com.zb.module_mine.vm.EditContentViewModel;

@Route(path = RouteUtils.Mine_Edit_Content)
public class EditContentActivity extends MineBaseActivity {
    @Autowired(name = "type")
    int type;
    @Autowired(name = "lines")
    int lines;
    @Autowired(name = "title")
    String title;
    @Autowired(name = "content")
    String content;
    @Autowired(name = "hint")
    String hint;

    @Override
    public int getRes() {
        return R.layout.mine_edit_content;
    }

    @Override
    public void initUI() {
        EditContentViewModel viewModel = new EditContentViewModel();
        viewModel.type = type;
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.title, title);
        mBinding.setVariable(BR.content, content);
        mBinding.setVariable(BR.lines, lines);
        mBinding.setVariable(BR.type, type);
        mBinding.setVariable(BR.hint, hint);

        MineEditContentBinding binding = (MineEditContentBinding) mBinding;
        binding.edContent.setText(content);
        binding.edContent.setSelection(content.length());
    }
}
