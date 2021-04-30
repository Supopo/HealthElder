package com.xaqinren.healthyelders.moduleLiteav.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.nostra13.dcloudimageloader.utils.L;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityLiteAvLookModeBinding;
import com.xaqinren.healthyelders.moduleLiteav.adapter.ChooseUnLookAdapter;
import com.xaqinren.healthyelders.moduleLiteav.adapter.ChooseUserAdapter;
import com.xaqinren.healthyelders.moduleLiteav.bean.LiteAvUserBean;
import com.xaqinren.healthyelders.moduleLiteav.viewModel.ChooseUnLookViewModel;
import com.xaqinren.healthyelders.widget.UnLookSearchLayout;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.utils.StringUtils;

/**
 * 选择不给谁看
 */
public class ChooseUnLookActivity extends BaseActivity<ActivityLiteAvLookModeBinding, ChooseUnLookViewModel> implements UnLookSearchLayout.OnSearchChangeListener, OnItemClickListener {

    private List<LiteAvUserBean> liteSelAvUserBeans = new ArrayList<>();
    private List<LiteAvUserBean> liteAvUserBeans = new ArrayList<>();
    private ChooseUserAdapter userAdapter;
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_lite_av_look_mode;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        rlTitle.setVisibility(View.GONE);
        initView();
    }

    private void initView() {

        for (int i = 0; i < 30; i++) {
            LiteAvUserBean bean = new LiteAvUserBean("name", "avatar", i);
            if (i % 10 == 0) {
                bean.viewType = 0;
            }else{
                bean.viewType = 1;
            }
            liteAvUserBeans.add(bean);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.searchBar.setOnSearchChangeListener(this);
        userAdapter = new ChooseUserAdapter();
        binding.friendList.setLayoutManager(new LinearLayoutManager(this));
        binding.friendList.setAdapter(userAdapter);
        userAdapter.setList(liteAvUserBeans);
        userAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onTextChange(String text) {
        if (!StringUtils.isEmpty(text)) {
            //展示搜索页
        }
    }

    @Override
    public void onUserRemove(int uid) {
        //通知列表刷新选中状态
        for (int i = 0; i < liteSelAvUserBeans.size(); i++) {
            int id = liteSelAvUserBeans.get(i).id;
            if (id == uid) {
                liteSelAvUserBeans.remove(i);
                break;
            }
        }
        for (int i = 0; i < liteAvUserBeans.size(); i++) {
            int id = liteAvUserBeans.get(i).id;
            if (id == uid) {
                liteAvUserBeans.get(i).isSel = false;
                userAdapter.setData(i , liteAvUserBeans.get(i));
            }
        }
    }

    @Override
    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
        LiteAvUserBean liteAvUserBean = liteAvUserBeans.get(position);
        if (liteAvUserBean.viewType == 1) {
            liteAvUserBean.isSel = !liteAvUserBean.isSel;
            if (liteAvUserBean.isSel) {
                liteSelAvUserBeans.add(liteAvUserBean);
                binding.searchBar.addData(liteAvUserBean);
            }else {
                liteSelAvUserBeans.remove(liteAvUserBean);
                binding.searchBar.removeUser(liteAvUserBean.id);
            }
        }

        userAdapter.notifyDataSetChanged();
    }
}
