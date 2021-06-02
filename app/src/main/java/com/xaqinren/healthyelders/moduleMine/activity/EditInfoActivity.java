package com.xaqinren.healthyelders.moduleMine.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.ActivityEditInfoBinding;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleMine.adapter.EditInfoAdapter;
import com.xaqinren.healthyelders.moduleMine.bean.EditMenuBean;
import com.xaqinren.healthyelders.moduleMine.viewModel.EditInfoViewModel;
import com.xaqinren.healthyelders.utils.GlideUtil;
import com.xaqinren.healthyelders.utils.UrlUtils;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.utils.StringUtils;

public class EditInfoActivity extends BaseActivity<ActivityEditInfoBinding, EditInfoViewModel> {
    private List<EditMenuBean> editMenuBeans = new ArrayList<>();
    private UserInfoBean userInfoBean;
    private EditInfoAdapter editInfoAdapter;
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_edit_info;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        setTitle("编辑资料");
        userInfoBean = UserInfoMgr.getInstance().getUserInfo();
        createEditList();
        editInfoAdapter = new EditInfoAdapter();
        editInfoAdapter.setList(editMenuBeans);
        binding.menuList.setLayoutManager(new LinearLayoutManager(this));
        binding.menuList.setNestedScrollingEnabled(false);
        binding.menuList.setAdapter(editInfoAdapter);
        editInfoAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (position == 0){
                changeName();
            }else if (position == 1){
            }else if (position == 2){
                changeInfo();
            }else if (position == 3){
                changeSex();
            }else if (position == 4){
                changeBirth();
            }else if (position == 5){
                changeCity();
            }
        });
        int dp94 = (int) getResources().getDimension(R.dimen.dp_94);
        GlideUtil.intoImageView(this, UrlUtils.resetImgUrl(userInfoBean.getAvatarUrl(), dp94, dp94), binding.avatar);
    }
    private void createEditList() {
        editMenuBeans.add(new EditMenuBean("名字", userInfoBean.getNickname(), true, ""));
        editMenuBeans.add(new EditMenuBean("健康号", userInfoBean.getRecommendedCode(), false, ""));
        editMenuBeans.add(new EditMenuBean("简介", getValue(userInfoBean.getIntroduce(),"点击设置") , true, ""));
        editMenuBeans.add(new EditMenuBean("性别", getValue(getSex( userInfoBean.getSex()),"不展示"), true, ""));
        editMenuBeans.add(new EditMenuBean("生日", getValue(null,"不展示"), true, ""));
        editMenuBeans.add(new EditMenuBean("所在地", getValue(userInfoBean.getCityAddress(),"暂不设置"), true, ""));
    }

    private String getSex(String sex) {
        if (sex == null) {
            return null;
        }
        return sex.equals("MALE") ? "男" : "女";
    }

    private String getValue(String value,String defaultValue) {
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }
        return value;
    }

    private void changeName() {
        startActivityForResult(new Intent(this, EditNameActivity.class), 0x0010);
    }

    private void changeInfo() {
        startActivityForResult(new Intent(this, EditBriefActivity.class), 0x0020);
    }

    private void changeSex() {

    }

    private void changeBirth() {

    }

    private void changeCity() {

    }
}
