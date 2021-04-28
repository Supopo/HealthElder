package com.xaqinren.healthyelders.moduleZhiBo.activity;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityZhiboOverBinding;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.ZhiboOverAdapter;
import com.xaqinren.healthyelders.moduleZhiBo.popupWindow.ZB2LinkSettingPop;
import com.xaqinren.healthyelders.moduleZhiBo.popupWindow.ZBUserInfoPop;
import com.xaqinren.healthyelders.moduleZhiBo.viewModel.ZhiboOverViewModel;

import me.goldze.mvvmhabit.base.BaseActivity;

/**
 * 直播结束页面
 */
//注意ActivityBaseBinding换成自己activity_layout对应的名字 ActivityXxxBinding
public class ZhiboOverActivity extends BaseActivity<ActivityZhiboOverBinding, ZhiboOverViewModel> {

    private ZhiboOverAdapter mAdapter;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_zhibo_over;

    }

    @Override
    public int initVariableId() {
        return com.xaqinren.healthyelders.BR.viewModel;
    }


    //页面数据初始化方法
    @Override
    public void initData() {
        rlTitle.setVisibility(View.GONE);
        setStatusBarTransparent();
        initAdapter();
        viewModel.getTopUserList();
        binding.ivClose.setOnClickListener(lis -> {
            ZB2LinkSettingPop zb2LinkSettingPop = new ZB2LinkSettingPop(this);
            zb2LinkSettingPop.showPopupWindow();
        });
        binding.tvTitle.setOnClickListener(lis -> {
            ZBUserInfoPop zbUserInfoPop = new ZBUserInfoPop(this);
            zbUserInfoPop.showPopupWindow();
        });
    }


    //页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
    @Override
    public void initViewObservable() {

        //往adapter里面加载数据
        viewModel.dataList.observe(this, dataList -> {
            if (dataList != null) {
                mAdapter.setList(dataList);
                if (dataList.size() == 0) {
                    //创建适配器.空布局，没有数据时候默认展示的
                    mAdapter.setEmptyView(R.layout.list_empty);
                }
            }
        });


    }

    private void initAdapter() {
        mAdapter = new ZhiboOverAdapter(R.layout.item_zhibo_user_bean);
        //设置不可滑动
        binding.rvContent.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        binding.rvContent.setAdapter(mAdapter);

        viewModel.getTopUserList();


        //Item点击事件
        mAdapter.setOnItemClickListener((adapter, view, position) -> {

        });


    }

}
