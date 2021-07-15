package com.xaqinren.healthyelders.moduleZhiBo.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tencent.qcloud.ugckit.utils.ToastUtil;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.ActivityZhiboOverBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.moduleMine.activity.UserInfoActivity;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.ZhiboOverAdapter;
import com.xaqinren.healthyelders.moduleZhiBo.viewModel.ZhiboOverViewModel;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.bus.RxBus;

/**
 * 直播结束页面
 */
//注意ActivityBaseBinding换成自己activity_layout对应的名字 ActivityXxxBinding
public class ZhiboOverActivity extends BaseActivity<ActivityZhiboOverBinding, ZhiboOverViewModel> {

    private ZhiboOverAdapter mAdapter;
    private String liveRoomRecordId;
    private String liveRoomId;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_zhibo_over;

    }

    @Override
    public int initVariableId() {
        return com.xaqinren.healthyelders.BR.viewModel;
    }

    @Override
    public void initParam() {
        super.initParam();
        Bundle extras = getIntent().getExtras();
        liveRoomRecordId = (String) extras.get("liveRoomRecordId");
        liveRoomId = (String) extras.get("liveRoomId");
    }

    //页面数据初始化方法
    @Override
    public void initData() {
        setStatusBarTransparent();
        initAdapter();
        viewModel.getLiveOverInfo(liveRoomRecordId);
        binding.ivClose.setOnClickListener(lis -> {
            finish();
        });
        RxBus.getDefault().post(new EventBean(CodeTable.CODE_SUCCESS, "overLive-zb"));
        binding.btnCommit.setOnClickListener(lis -> {
            if (TextUtils.isEmpty(binding.etContent.getText().toString().trim())) {
                ToastUtil.toastShortMessage("请输入内容！");
                return;
            }
            showDialog();
            viewModel.feedbackSave(binding.etContent.getText().toString().trim(), liveRoomId, liveRoomRecordId);
        });
    }


    //页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
    @Override
    public void initViewObservable() {

        //往adapter里面加载数据
        viewModel.liveOverInfo.observe(this, liveOverInfo -> {
            if (liveOverInfo != null && liveOverInfo.liveRoomUsers != null) {
                mAdapter.setNewInstance(liveOverInfo.liveRoomUsers);
            }
        });

        viewModel.dismissDialog.observe(this, dis -> {
            if (dis != null && dis) {
                dismissDialog();
            }
        });

        viewModel.commitSuccess.observe(this, success -> {
            if (success != null && success) {
                finish();
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
            UserInfoActivity.startActivity(this, mAdapter.getData().get(position).userId);
        });


    }

}
