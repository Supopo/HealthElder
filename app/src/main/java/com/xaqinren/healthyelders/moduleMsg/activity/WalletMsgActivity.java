package com.xaqinren.healthyelders.moduleMsg.activity;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityInteractiveBinding;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleMsg.ImManager;
import com.xaqinren.healthyelders.moduleMsg.adapter.InteractiveAdapter;
import com.xaqinren.healthyelders.moduleMsg.adapter.provider.ServiceProvider;
import com.xaqinren.healthyelders.moduleMsg.adapter.provider.WalletProvider;
import com.xaqinren.healthyelders.moduleMsg.bean.MessageDetailBean;
import com.xaqinren.healthyelders.moduleMsg.viewModel.InteractiveViewModel;
import com.xaqinren.healthyelders.utils.LogUtils;

import me.goldze.mvvmhabit.base.BaseActivity;

public class WalletMsgActivity extends BaseActivity<ActivityInteractiveBinding, InteractiveViewModel> {

    private InteractiveAdapter interactiveAdapter;
    private String TAG = getClass().getSimpleName();

    private int page = 1;
    private int pageSize = 5;

    private String messageGroup = com.xaqinren.healthyelders.moduleMsg.Constant.WALLET;

    private String messageType = "";


    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_interactive;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        setTitle("钱包通知");
        ImManager.getInstance().clearUnreadById(Constant.CONVERSATION_WALLET_ID);
        binding.titleLayout.setVisibility(View.GONE);
        binding.rlContainer.setBackgroundColor(getResources().getColor(R.color.color_FFF8F8F8));
        interactiveAdapter = new InteractiveAdapter();

        interactiveAdapter.addItemProvider(new WalletProvider());

        binding.content.setLayoutManager(new LinearLayoutManager(this));
        binding.content.setAdapter(interactiveAdapter);

        interactiveAdapter.setOnItemClickListener((adapter, view, position) -> {
            MessageDetailBean bean = (MessageDetailBean) adapter.getData().get(position);
            LogUtils.e(TAG, "position->" + position + "\t type ->" + bean.getItemType());
            if (bean.getItemType() == MessageDetailBean.TYPE_LOAD_MORE) {
                page++;
                viewModel.getMessage(page, pageSize, messageGroup, messageType);
            } else if (bean.getItemType() == MessageDetailBean.TYPE_TOP) {

            }
        });

        interactiveAdapter.getLoadMoreModule().setEnableLoadMore(true);
        interactiveAdapter.getLoadMoreModule().setAutoLoadMore(true);
        interactiveAdapter.getLoadMoreModule().setOnLoadMoreListener(() -> {
            viewModel.getMessage(page, pageSize, messageGroup, "");
        });
        viewModel.getMessage(page, pageSize, messageGroup, "");
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.musicListData.observe(this, interactiveBeans -> {
            if (page == 1) {
                interactiveAdapter.getData().clear();
            }
            interactiveAdapter.addData(interactiveBeans);
            if (interactiveBeans.size() >= pageSize) {
                interactiveAdapter.getLoadMoreModule().loadMoreComplete();
                page++;
            }else{
                interactiveAdapter.getLoadMoreModule().loadMoreEnd(false);
            }
        });

    }
}
