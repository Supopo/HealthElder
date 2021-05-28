package com.xaqinren.healthyelders.moduleMsg.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityInteractiveBinding;
import com.xaqinren.healthyelders.moduleHome.activity.VideoGridActivity;
import com.xaqinren.healthyelders.moduleHome.activity.VideoListActivity;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleHome.bean.VideoListBean;
import com.xaqinren.healthyelders.moduleMsg.Constant;
import com.xaqinren.healthyelders.moduleMsg.ImManager;
import com.xaqinren.healthyelders.moduleMsg.adapter.InteractiveAdapter;
import com.xaqinren.healthyelders.moduleMsg.adapter.provider.FriendProvider;
import com.xaqinren.healthyelders.moduleMsg.adapter.provider.InteractiveProvider;
import com.xaqinren.healthyelders.moduleMsg.adapter.provider.LoadMoreProvider;
import com.xaqinren.healthyelders.moduleMsg.adapter.provider.TextProvider;
import com.xaqinren.healthyelders.moduleMsg.bean.FriendBean;
import com.xaqinren.healthyelders.moduleMsg.bean.InteractiveBean;
import com.xaqinren.healthyelders.moduleMsg.bean.MessageDetailBean;
import com.xaqinren.healthyelders.moduleMsg.viewModel.InteractiveViewModel;
import com.xaqinren.healthyelders.modulePicture.activity.TextPhotoDetailActivity;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;

/**
 * 互动消息
 */
public class InteractiveActivity extends BaseActivity<ActivityInteractiveBinding, InteractiveViewModel> {

    private InteractiveAdapter interactiveAdapter;
    private String TAG = getClass().getSimpleName();

    private boolean isSelShow;

    private int page = 1;
    private int pageSize = 5;

    private String messageGroup = Constant.INTERACTIVE_MESSAGE;

    String[] type = {
            "",//ALL
            Constant.FAVORITE,//赞
            Constant.AT,//@
            Constant.COMMENT,//评论
            Constant.REPLY,//回复
    };

    private String messageType = type[0];

    private int messageCount = 0;
    private int friendCount = 0;

    private int friendPage = 1;

    private int friendPageSize = 20;
    private boolean enableFriend = false;

    private boolean hasLoadMore = false;

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
        ImManager.getInstance().clearUnreadById(com.xaqinren.healthyelders.global.Constant.CONVERSATION_INT_ID);
        rlTitle.setVisibility(View.GONE);
        interactiveAdapter = new InteractiveAdapter();

        interactiveAdapter.addItemProvider(new InteractiveProvider());


        binding.content.setLayoutManager(new LinearLayoutManager(this));
        binding.content.setAdapter(interactiveAdapter);

        interactiveAdapter.setOnItemClickListener((adapter, view, position) -> {
            MessageDetailBean bean = (MessageDetailBean) adapter.getData().get(position);
            LogUtils.e(TAG, "position->" + position + "\t type ->" + bean.getItemType());
            if (bean.getItemType() == MessageDetailBean.TYPE_LOAD_MORE) {
                page++;
                viewModel.getMessage(page, pageSize, messageGroup, messageType);
            } else if (bean.getItemType() == MessageDetailBean.TYPE_TOP) {
                //跳转详情
                InteractiveBean interactiveBean = (InteractiveBean) bean;
                if (interactiveBean.getExtra().getCreationType().equals(Constant.SHORT_VIDEO)) {
                    viewModel.getVideoInfo(interactiveBean.getExtra().getId());
                }else{
                    Intent intent = new Intent(this, TextPhotoDetailActivity.class);
                    intent.putExtra(com.xaqinren.healthyelders.moduleLiteav.Constant.VIDEO_ID, interactiveBean.getExtra().getId());
                    startActivity(intent);
                }
            }
        });

        if (enableFriend) {
            interactiveAdapter.getLoadMoreModule().setEnableLoadMore(true);
            interactiveAdapter.getLoadMoreModule().setAutoLoadMore(true);
            interactiveAdapter.getLoadMoreModule().setOnLoadMoreListener(() -> {
                if (friendCount>0)
                    viewModel.getRecommendFriend();
            });
        }


        binding.title.setOnClickListener(view -> {
            clickTitle();
        });
        binding.mark.setOnClickListener(view -> {
            clickTitle();
        });
        binding.allMsg.setOnClickListener(view -> {
            binding.title.setText("全部消息");
            messageType = type[0];
            changeData();
            clickTitle();
        });
        binding.zanMsg.setOnClickListener(view -> {
            binding.title.setText("赞");
            messageType = type[1];
            changeData();
            clickTitle();
        });
        binding.atMsg.setOnClickListener(view -> {
            binding.title.setText("@我的");
            messageType = type[2];
            changeData();
            clickTitle();
        });
        binding.commMsg.setOnClickListener(view -> {
            binding.title.setText("评论");
            messageType = type[3];
            changeData();
            clickTitle();
        });
        binding.back.setOnClickListener(view -> finish());

        viewModel.getMessage(page, pageSize, messageGroup, messageType);
    }

    private void toVideoList(List<VideoInfo> tempList) {
        //跳页 传入数据 pos page list
        VideoListBean listBean = new VideoListBean();

        listBean.page = 0;
        listBean.position = 0;
        listBean.videoInfos = tempList;
        listBean.type = 2;

        Bundle bundle = new Bundle();
        bundle.putSerializable("key", listBean);
        bundle.putBoolean("key1", true);
        startActivity(VideoListActivity.class, bundle);
    }

    private void changeData() {
        page = 1;
        viewModel.getMessage(page, pageSize, messageGroup, messageType);
    }
    @Override
    public void initViewObservable() {
        super.initViewObservable();
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
                interactiveAdapter.addData(messageCount, new MessageDetailBean() {
                    @Override
                    public int getItemType() {
                        return MessageDetailBean.TYPE_TEXT;
                    }
                });
            }
            interactiveAdapter.addData(friendBeans);
            if (friendBeans.size() >= friendPageSize) {
                interactiveAdapter.getLoadMoreModule().loadMoreComplete();
            }else{
                interactiveAdapter.getLoadMoreModule().loadMoreEnd(false);
            }
        });
        viewModel.videoInfoLiveData.observe(this, videoInfo -> {
            videoInfo.resourceType = "VIDEO";
            ArrayList<VideoInfo> list = new ArrayList<>();
            list.add(videoInfo);
            toVideoList(list);
        });
    }

    private void clickTitle() {
        changeTitleDrwa();
        if (isSelShow) {
            hideSelectWindow();
        }else{
            showSelectWindow();
        }
    }
    private void changeTitleDrwa() {
        Drawable drawable = getResources().getDrawable(isSelShow ? R.mipmap.icon_xiaox_xial_down : R.mipmap.icon_xiaox_xial_up);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        binding.title.setCompoundDrawables(null, null, drawable, null);
    }

    //选择分类弹窗
    private void showSelectWindow() {
        Animation showAnim = AnimationUtils.loadAnimation(this, R.anim.anim_alpha_in);
        showAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isSelShow = true;
                binding.selLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        binding.selLayout.setAnimation(showAnim);
        showAnim.start();
    }
    private void hideSelectWindow() {
        Animation showAnim = AnimationUtils.loadAnimation(this, R.anim.anim_alpha_out);
        showAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isSelShow = false;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.selLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        binding.selLayout.setAnimation(showAnim);
        showAnim.start();
    }
}
