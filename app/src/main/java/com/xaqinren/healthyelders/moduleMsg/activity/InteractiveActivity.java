package com.xaqinren.healthyelders.moduleMsg.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityInteractiveBinding;
import com.xaqinren.healthyelders.moduleHome.activity.VideoListActivity;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleHome.bean.VideoListBean;
import com.xaqinren.healthyelders.moduleMine.activity.UserInfoActivity;
import com.xaqinren.healthyelders.moduleMsg.Constant;
import com.xaqinren.healthyelders.moduleMsg.ImManager;
import com.xaqinren.healthyelders.moduleMsg.adapter.AddFriendAdapter;
import com.xaqinren.healthyelders.moduleMsg.adapter.InteractiveAdapter;
import com.xaqinren.healthyelders.moduleMsg.adapter.provider.InteractiveProvider;
import com.xaqinren.healthyelders.moduleMsg.bean.FriendBean;
import com.xaqinren.healthyelders.moduleMsg.bean.InteractiveBean;
import com.xaqinren.healthyelders.moduleMsg.bean.MessageDetailBean;
import com.xaqinren.healthyelders.moduleMsg.viewModel.InteractiveViewModel;
import com.xaqinren.healthyelders.modulePicture.activity.TextPhotoDetailActivity;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.utils.ToastUtils;

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
    private boolean enableFriend = true;

    private boolean hasLoadMore = false;
    private int opIndex;

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
                showDialog();
                viewModel.getMessage(page, pageSize, messageGroup, messageType);
            } else if (bean.getItemType() == MessageDetailBean.TYPE_TOP) {
                showDialog();
                //跳转详情
                InteractiveBean interactiveBean = (InteractiveBean) bean;
                if (interactiveBean.getExtra().getCreationType().equals(Constant.SHORT_VIDEO)) {
                    viewModel.getVideoInfo(interactiveBean.getExtra().getId());
                } else {
                    diaryInfoId = interactiveBean.getExtra().getId();
                    viewModel.diaryInfo(diaryInfoId);
                }
            }
        });

        interactiveAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            MessageDetailBean bean = (MessageDetailBean) adapter.getData().get(position);
            opIndex = position;
            switch (view.getId()) {
                case R.id.avatar: {
                    //用户详情
                    if (bean.getItemType() == MessageDetailBean.TYPE_TOP) {
                        InteractiveBean bean1 = (InteractiveBean) bean;
                        UserInfoActivity.startActivity(this, bean1.getSendUser().getUserId() + "");
                    } else if (bean.getItemType() == MessageDetailBean.TYPE_FRIEND) {
                        FriendBean friendBean = (FriendBean) bean;
                        UserInfoActivity.startActivity(this, friendBean.getUserId());
                    }
                }
                break;
                case R.id.attention_btn: {
                    //粉丝消息,关注
                }
                break;
                case R.id.favorite: {
                    //推荐列表,关注
                    showDialog();
                    FriendBean friendBean = (FriendBean) bean;
                    viewModel.recommendFriend(friendBean.getUserId());
                }
                break;
                case R.id.close: {
                    //推荐列表,删除
                    adapter.removeAt(position);
                }
                break;
            }
        });

        if (enableFriend) {
            interactiveAdapter.getLoadMoreModule().setEnableLoadMore(true);
            interactiveAdapter.getLoadMoreModule().setAutoLoadMore(false);
            interactiveAdapter.getLoadMoreModule().setOnLoadMoreListener(() -> {
                if (friendCount > 0)
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
        showDialog();
        viewModel.getMessage(page, pageSize, messageGroup, messageType);
    }

    private void toVideoList(List<VideoInfo> tempList) {
        //跳页 传入数据 pos page list
        VideoListBean listBean = new VideoListBean();

        listBean.page = 0;
        listBean.position = 0;
        listBean.videoInfos = tempList;
        listBean.openType = 2;

        Bundle bundle = new Bundle();
        bundle.putSerializable("key", listBean);
        bundle.putBoolean("key1", true);
        startActivity(VideoListActivity.class, bundle);
    }

    private void changeData() {
        page = 1;
        viewModel.getMessage(page, pageSize, messageGroup, messageType);
    }

    //当前点击的作品id
    private String diaryInfoId;
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
                if (isSelShow)
                    return;
                isSelShow = true;
                interactiveAdapter.addData(messageCount, new MessageDetailBean() {
                    @Override
                    public int getItemType() {
                        return MessageDetailBean.TYPE_LOAD_MORE;
                    }
                });
            } else {
                //加载完成，隐藏loadmore
                if (isSelShow) {
                    interactiveAdapter.removeAt(messageCount);
                }
            }
            if (!enableFriend)
                return;
            if (friendCount == 0)
                viewModel.getRecommendFriend();
        });
        viewModel.friendListData.observe(this, friendBeans -> {
            if (friendCount == 0) {
                if (interactiveAdapter.getData().size() > 0) {
                    MessageDetailBean o = (MessageDetailBean) interactiveAdapter.getData().get(interactiveAdapter.getData().size() - 1);
                    if (o.getItemType() != MessageDetailBean.TYPE_TEXT) {
                        interactiveAdapter.addData(new MessageDetailBean() {
                            @Override
                            public int getItemType() {
                                return MessageDetailBean.TYPE_TEXT;
                            }
                        });
                    }
                }

                /*if (friendBeans.isEmpty()) {
                    interactiveAdapter.addData(new MessageDetailBean() {
                        @Override
                        public int getItemType() {
                            return MessageDetailBean.TYPE_EMPTY;
                        }
                    });
                }*/
            }
            friendCount += friendBeans.size();
            interactiveAdapter.addData(friendBeans);
            if (friendBeans.size() >= friendPageSize) {
                interactiveAdapter.getLoadMoreModule().loadMoreEnd(false);
            } else {
                interactiveAdapter.getLoadMoreModule().loadMoreEnd(false);
            }
        });
        viewModel.videoInfoLiveData.observe(this, videoInfo -> {
            if (videoInfo == null) {
                ToastUtils.showLong("作品已被删除");
                return;
            }
            videoInfo.resourceType = "VIDEO";
            ArrayList<VideoInfo> list = new ArrayList<>();
            list.add(videoInfo);
            toVideoList(list);
        });
        viewModel.flow.observe(this, aBoolean -> {
            dismissDialog();
            FriendBean friendBean = (FriendBean) interactiveAdapter.getData().get(opIndex);
            if (friendBean.getIdentity().equals(AddFriendAdapter.STRANGER)) {
                //陌生人
                friendBean.setIdentity(AddFriendAdapter.ATTENTION);
            } else if (friendBean.getIdentity().equals(AddFriendAdapter.FANS)) {
                //粉丝
                friendBean.setIdentity(AddFriendAdapter.FRIEND);
            } else if (friendBean.getIdentity().equals(AddFriendAdapter.ATTENTION)) {
                //关注的人
                friendBean.setIdentity(AddFriendAdapter.STRANGER);
            } else if (friendBean.getIdentity().equals(AddFriendAdapter.FRIEND)) {
                //朋友
                friendBean.setIdentity(AddFriendAdapter.FANS);
            } else if (friendBean.getIdentity().equals(AddFriendAdapter.FOLLOW)) {
                //关注的人
                friendBean.setIdentity(AddFriendAdapter.STRANGER);
            }
            interactiveAdapter.notifyItemChanged(opIndex);
        });
        viewModel.diaryInfo.observe(this, diaryInfo -> {
            if (diaryInfo == null) {
                ToastUtils.showLong("作品已被删除");
                return;
            }
            Intent intent = new Intent(this, TextPhotoDetailActivity.class);
            intent.putExtra(com.xaqinren.healthyelders.moduleLiteav.Constant.VIDEO_ID, diaryInfoId);
            startActivity(intent);

        });
    }

    private void clickTitle() {
        changeTitleDrwa();
        if (isSelShow) {
            hideSelectWindow();
        } else {
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
