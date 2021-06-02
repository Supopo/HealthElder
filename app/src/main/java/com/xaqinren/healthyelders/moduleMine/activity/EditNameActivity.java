package com.xaqinren.healthyelders.moduleMine.activity;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;

import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.ActivityEditNameBinding;
import com.xaqinren.healthyelders.impl.TextWatcherImpl;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleMine.viewModel.EditInfoViewModel;

import me.goldze.mvvmhabit.base.BaseActivity;

public class EditNameActivity extends BaseActivity <ActivityEditNameBinding, EditInfoViewModel>{
    private UserInfoBean userInfoBean;
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_edit_name;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        setTitle("修改名字");
        setTvRight("保存");
        tvRight.setTextColor(getResources().getColor(R.color.color_F81E4D));
        userInfoBean = UserInfoMgr.getInstance().getUserInfo();
        binding.nameEt.setText(userInfoBean.getNickname());
        showClose();
        showCount();
        binding.nameEt.addTextChangedListener(new TextWatcherImpl(){
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                showClose();
                showCount();
            }
        });
        binding.clear.setOnClickListener(view -> binding.nameEt.setText(null));
        tvRight.setOnClickListener(view -> {
            //TODO 修改名字接口

        });
    }

    private void showClose() {
        int count = binding.nameEt.getText().length();
        binding.clear.setVisibility( count > 0 ? View.VISIBLE : View.INVISIBLE );
    }

    private void showCount() {
        int count = binding.nameEt.getText().length();
        binding.textCount.setText(count + "/20");
    }
}
