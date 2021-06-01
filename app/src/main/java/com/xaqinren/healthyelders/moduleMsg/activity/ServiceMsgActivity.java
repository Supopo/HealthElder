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
import com.xaqinren.healthyelders.moduleMsg.adapter.provider.SysProvider;
import com.xaqinren.healthyelders.moduleMsg.bean.MessageDetailBean;
import com.xaqinren.healthyelders.moduleMsg.viewModel.InteractiveViewModel;
import com.xaqinren.healthyelders.utils.LogUtils;

import me.goldze.mvvmhabit.base.BaseActivity;

/**
 * 服务消息
 */
public class ServiceMsgActivity extends BaseActivity<ActivityInteractiveBinding, InteractiveViewModel> {

    private InteractiveAdapter interactiveAdapter;
    private String TAG = getClass().getSimpleName();

    private int page = 1;
    private int pageSize = 5;

    private String messageGroup = com.xaqinren.healthyelders.moduleMsg.Constant.SERVICE;


    private String messageType = "";
    private int friendPage = 1;
    private int friendPageSize = 20;
    private boolean enableFriend = true;
    private boolean hasLoadMore = false;
    private boolean isSelShow;
    private int messageCount = 0;
    private int friendCount = 0;



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
        setTitle("服务通知");
        ImManager.getInstance().clearUnreadById(Constant.CONVERSATION_SERVICE_ID);
        binding.titleLayout.setVisibility(View.GONE);
        binding.rlContainer.setBackgroundColor(getResources().getColor(R.color.color_FFF8F8F8));
        interactiveAdapter = new InteractiveAdapter();

        interactiveAdapter.addItemProvider(new ServiceProvider());

        binding.content.setLayoutManager(new LinearLayoutManager(this));
        binding.content.setAdapter(interactiveAdapter);

        interactiveAdapter.setOnItemClickListener((adapter, view, position) -> {
            MessageDetailBean bean = (MessageDetailBean) adapter.getData().get(position);
            LogUtils.e(TAG, "position->" + position + "\t type ->" + bean.getItemType());
            if (bean.getItemType() == MessageDetailBean.TYPE_LOAD_MORE) {
                page++;
                showDialog();
                viewModel.getMessage(page, pageSize, messageGroup, messageType);
            } else if (bean.getItemType() == MessageDetailBean.TYPE_TOP) {

            }
        });


        interactiveAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            MessageDetailBean bean = (MessageDetailBean) adapter.getData().get(position);
            switch (view.getId()){
                case R.id.avatar:{
                    //用户详情
                }break;
                case R.id.attention_btn: {
                    //粉丝消息,关注
                }break;
                case R.id.favorite: {
                    //推荐列表,关注
                }break;
                case R.id.close: {
                    //推荐列表,删除
                }break;
            }
        });



        if (enableFriend) {
            interactiveAdapter.getLoadMoreModule().setEnableLoadMore(true);
            interactiveAdapter.getLoadMoreModule().setAutoLoadMore(false);
            interactiveAdapter.getLoadMoreModule().setOnLoadMoreListener(() -> {
                if (friendCount>0)
                    viewModel.getRecommendFriend();
            });
        }


        viewModel.getMessage(page, pageSize, messageGroup, "");
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.requestSuccess.observe(this, aBoolean -> {
            dismissDialog();
        });
        viewModel.musicListData.observe(this, interactiveBeans -> {
            if (page == 1) {
                interactiveAdapter.getData().clear();
                messageCount = 0;
                friendCount = 0;
                friendPage = 1;
            }
            interactiveAdapter.addData(messageCount, interactiveBeans);
            messageCount += interactiveBeans.size();
            if (interactiveBeans.size() >= pageSize) {
                if (isSelShow)return;
                isSelShow = true;
                interactiveAdapter.addData(messageCount, new MessageDetailBean() {
                    @Override
                    public int getItemType() {
                        return MessageDetailBean.TYPE_LOAD_MORE;
                    }
                });
            }else{
                //加载完成，隐藏loadmore
                if (isSelShow) {
                    interactiveAdapter.removeAt(messageCount);
                }
            }
            if (!enableFriend)return;
            if (friendCount==0)
                viewModel.getRecommendFriend();
        });

        viewModel.friendListData.observe(this, friendBeans -> {
            if (friendCount == 0 && !friendBeans.isEmpty()) {
                interactiveAdapter.addData(new MessageDetailBean() {
                    @Override
                    public int getItemType() {
                        return MessageDetailBean.TYPE_TEXT;
                    }
                });
            }
            interactiveAdapter.addData(friendBeans);
            if (friendBeans.size() >= friendPageSize) {
                interactiveAdapter.getLoadMoreModule().loadMoreEnd(false);
            }else{
                interactiveAdapter.getLoadMoreModule().loadMoreEnd(false);
            }
        });
    }
}
