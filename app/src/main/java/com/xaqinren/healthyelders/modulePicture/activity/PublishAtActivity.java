package com.xaqinren.healthyelders.modulePicture.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.text.Editable;
import android.transition.Slide;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityPublishAtBinding;
import com.xaqinren.healthyelders.impl.TextWatcherImpl;
import com.xaqinren.healthyelders.moduleLiteav.bean.LiteAvUserBean;
import com.xaqinren.healthyelders.modulePicture.Constant;
import com.xaqinren.healthyelders.modulePicture.adapter.PublishAtAdapter;
import com.xaqinren.healthyelders.modulePicture.viewModel.PublishAtViewModel;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;

public class PublishAtActivity extends BaseActivity<ActivityPublishAtBinding , PublishAtViewModel> {
    private PublishAtAdapter adapter;
    boolean singleSearchAt = true;
    private int atPage = 1;
    private int atPageSize = 20;
    //@用户列表
    private List<LiteAvUserBean> liteAvUserBeans = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_publish_at;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.requestSuccess.observe(this, b ->{
            dismissDialog();
        });
        viewModel.liteAvUserList.observe(this,data ->{
            if (atPage == 1) {
                liteAvUserBeans.clear();
            }
            liteAvUserBeans.addAll(data);
            if (data.isEmpty()) {
                adapter.getLoadMoreModule().loadMoreEnd(true);
            }else{
                adapter.getLoadMoreModule().loadMoreComplete();
            }
            adapter.setList(liteAvUserBeans);
        });
    }

    @Override
    public void initData() {
        super.initData();
        setTitle("私信朋友");
        liteAvUserBeans = new ArrayList<>();
        adapter = new PublishAtAdapter(R.layout.item_puglish_at);
        adapter.setList(liteAvUserBeans);
        binding.content.setLayoutManager(new LinearLayoutManager(this));
        binding.content.setAdapter(adapter);
        binding.inputEt.addTextChangedListener(new TextWatcherImpl(){
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                if (editable.length() > 0) {
                    searchUser();
                }else{
                    getFriendData();
                }
            }
        });
        getFriendData();
        adapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent = new Intent();
            intent.putExtra(Constant.PUBLISH_AT, liteAvUserBeans.get(position));
            setResult(Activity.RESULT_OK , intent);
            finish();
        });
    }

    @Override
    public void finish() {
        super.finish();
    }

    private void getFriendData() {
        if (!singleSearchAt){
            singleSearchAt = true;
            atPage = 1;
        }
        viewModel.getMyAtList(atPage, atPageSize);
    }
    private void searchUser() {
        if (singleSearchAt){
            singleSearchAt = false;
            atPage = 1;
        }
        viewModel.searchUserList(atPage, atPageSize, getCurrentInput());
    }
    private String getCurrentInput() {
        return binding.inputEt.getText().toString();
    }
}
