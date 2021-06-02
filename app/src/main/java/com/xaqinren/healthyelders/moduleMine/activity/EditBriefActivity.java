package com.xaqinren.healthyelders.moduleMine.activity;

import android.os.Bundle;
import android.text.Editable;

import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.ActivityEditBriefBinding;
import com.xaqinren.healthyelders.impl.TextWatcherImpl;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleMine.viewModel.EditInfoViewModel;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.utils.ToastUtils;

public class EditBriefActivity extends BaseActivity<ActivityEditBriefBinding, EditInfoViewModel> {
    private int max = 242;
    private UserInfoBean userInfoBean;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_edit_brief;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        setTitle("修改简介");
        setTvRight("保存");
        userInfoBean = UserInfoMgr.getInstance().getUserInfo();
        tvRight.setTextColor(getResources().getColor(R.color.color_F81E4D));
        tvRight.setOnClickListener(view -> {
            //TODO 修改接口
        });
        binding.infoEt.setText(userInfoBean.getIntroduce());
        binding.infoEt.addTextChangedListener(new TextWatcherImpl(){
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                showCount();
                if (editable.length() >= max) {
                    ToastUtils.showShort("您最多输入"+max+"个字符");
                }
            }
        });
    }
    private void showCount() {
        int count = binding.infoEt.getText().length();
        if (count >= 232) {
            binding.textCount.setText((max - count)+"");
        }else{
            binding.textCount.setText(null);
        }

    }

}
