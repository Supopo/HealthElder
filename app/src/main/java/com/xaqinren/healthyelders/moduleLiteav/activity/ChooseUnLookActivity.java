package com.xaqinren.healthyelders.moduleLiteav.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.nostra13.dcloudimageloader.utils.L;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityLiteAvLookModeBinding;
import com.xaqinren.healthyelders.moduleLiteav.adapter.ChooseUserAdapter;
import com.xaqinren.healthyelders.moduleLiteav.bean.LiteAvUserBean;
import com.xaqinren.healthyelders.moduleLiteav.liteAv.LiteAvConstant;
import com.xaqinren.healthyelders.moduleLiteav.viewModel.ChooseUnLookViewModel;
import com.xaqinren.healthyelders.widget.UnLookSearchLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.utils.StringUtils;

/**
 * 选择不给谁看
 */
public class ChooseUnLookActivity extends BaseActivity<ActivityLiteAvLookModeBinding, ChooseUnLookViewModel> implements UnLookSearchLayout.OnSearchChangeListener, OnItemClickListener {

    private static final String TAG = "ChooseUnLookActivity";
    private List<LiteAvUserBean> liteSelAvUserBeans = new ArrayList<>();
    private List<LiteAvUserBean> liteAvUserBeans = new ArrayList<>();
    private List<LiteAvUserBean> searchUserBeans = new ArrayList<>();
    private ChooseUserAdapter userAdapter;
    /**
     * 请求单一的数据，请求返回数量 == 0 时，切换请求模式
     */
    private String FRIEND = "FRIEND";//朋友
    private String FANS = "FANS";//粉丝
    private String ATTENTION = "ATTENTION";//关注的人
    private String STRANGER = "STRANGER";//陌生人
    private String currentRequestType = FRIEND;
    private int currentPage = 1;
    private int pageSize = 10;

    /**
     * 搜索用的
     */
    private int searchCurrentPage;
    private int searchPageSize = 10;


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
        List<LiteAvUserBean> temp = (List<LiteAvUserBean>) getIntent().getSerializableExtra(LiteAvConstant.UnLookList);
        if (temp != null) {
            mergeList(temp);
        }
        initView();
        viewModel.getUserList(currentPage , pageSize , currentRequestType);
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.requestSuccess.observe(this,a ->{
            dismissDialog();
        });
        viewModel.userList.observe(this, liteAvUserBeans -> {
            if (liteAvUserBeans.isEmpty()) {
                if (next()) {
                    //进去下一次请求
                    currentPage = 1;
                    viewModel.getUserList(currentPage , pageSize , currentRequestType);
                }else{
                    //TODO 无更多用户了,需显示无更多数据footer

                }
                return;
            }
            if (currentRequestType.equals(FRIEND)) {
                if (this.liteAvUserBeans.isEmpty()) {
                    LiteAvUserBean bean = new LiteAvUserBean();
                    bean.viewType = 1;
                    bean.nickname = "好友";
                    this.liteAvUserBeans.add(bean);
                }
            } else if (currentRequestType.equals(FANS)) {
                if (this.liteAvUserBeans.isEmpty() ||
                        this.liteAvUserBeans.get(this.liteAvUserBeans.size() - 1).viewType == 0) {
                    LiteAvUserBean bean = new LiteAvUserBean();
                    bean.viewType = 1;
                    bean.nickname = "粉丝";
                    this.liteAvUserBeans.add(bean);
                }
            } else {
                if (this.liteAvUserBeans.isEmpty()
                || this.liteAvUserBeans.get(this.liteAvUserBeans.size() - 1).viewType == 0) {
                    LiteAvUserBean bean = new LiteAvUserBean();
                    bean.viewType = 1;
                    bean.nickname = "关注";
                    this.liteAvUserBeans.add(bean);
                }
            }
            this.liteAvUserBeans.addAll(liteAvUserBeans);
            userAdapter.setList(this.liteAvUserBeans);
        });
        viewModel.searchUserList.observe(this, liteAvUserBeans -> {
            //搜索列表
            searchUserBeans.clear();
            mergerSearch(liteAvUserBeans);
            searchUserBeans.addAll(liteAvUserBeans);
        });
    }
    private boolean next() {
        if (currentRequestType.equals(FRIEND)) {
            currentRequestType = FANS;
            return true;
        } else if (currentRequestType.equals(FANS)) {
            currentRequestType = ATTENTION;
            return true;
        } else {
            currentRequestType = ATTENTION;
            return false;
        }
    }

    /**
     * @param temp
     */
    private void mergeList(List<LiteAvUserBean> temp) {
        for (int i = 0; i < temp.size(); i++) {
            LiteAvUserBean bean = temp.get(i);
            liteSelAvUserBeans.add(bean);
            for (LiteAvUserBean user : liteAvUserBeans) {
                if (user.viewType == 1) continue;
                if (user.userId == bean.userId) {
                    user.isSel = true;
                }
            }
        }
        int i = 0;
        for (LiteAvUserBean bean : temp) {
            if (!bean.isSel) {
                liteAvUserBeans.add(i, bean);
            }
            i++;
        }
    }
    private void mergerSearch(List<LiteAvUserBean> temp) {
        for (int i = 0; i < temp.size(); i++) {
            LiteAvUserBean bean = temp.get(i);
            for (LiteAvUserBean user : liteSelAvUserBeans) {
                if (user.userId == bean.userId) {
                    bean.isSel = true;
                }
            }
        }
    }
    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.searchBar.setOnSearchChangeListener(this);
        userAdapter = new ChooseUserAdapter();
        binding.friendList.setLayoutManager(new LinearLayoutManager(this));
        binding.friendList.setAdapter(userAdapter);
        userAdapter.setAnimationEnable(false);
        userAdapter.setList(liteAvUserBeans);
        userAdapter.setOnItemClickListener(this);

        binding.confirmButton.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.putExtra("list", (Serializable) liteSelAvUserBeans);
            setResult(Activity.RESULT_OK,intent);
            finish();
        });
        binding.searchBar.addData(liteSelAvUserBeans);

    }

    @Override
    public void onTextChange(String text) {
        if (!StringUtils.isEmpty(text)) {
            //展示搜索页
        }
    }

    @Override
    public void onUserRemove(long uid) {
        //通知列表刷新选中状态
        for (int i = 0; i < liteSelAvUserBeans.size(); i++) {
            long id = liteSelAvUserBeans.get(i).userId;
            if (id == uid) {
                liteSelAvUserBeans.remove(i);
                break;
            }
        }
        for (int i = 0; i < liteAvUserBeans.size(); i++) {
            long id = liteAvUserBeans.get(i).userId;
            if (id == uid) {
                liteAvUserBeans.get(i).isSel = false;
                userAdapter.setData(i , liteAvUserBeans.get(i));
            }
        }
        binding.confirmButton.setEnabled(!liteSelAvUserBeans.isEmpty());
    }

    @Override
    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
        LiteAvUserBean liteAvUserBean = liteAvUserBeans.get(position);
        if (liteAvUserBean.viewType == 0) {
            liteAvUserBean.isSel = !liteAvUserBean.isSel;
            if (liteAvUserBean.isSel) {
                liteSelAvUserBeans.add(liteAvUserBean);
                binding.searchBar.addData(liteAvUserBean);
            }else {
                liteSelAvUserBeans.remove(liteAvUserBean);
                binding.searchBar.removeUser(liteAvUserBean.userId);
            }
        }
        userAdapter.setData(position, liteAvUserBean);
    }


}
