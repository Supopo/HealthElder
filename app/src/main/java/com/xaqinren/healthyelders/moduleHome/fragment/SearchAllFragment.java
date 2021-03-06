package com.xaqinren.healthyelders.moduleHome.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.FragmentAllSearchBinding;
import com.xaqinren.healthyelders.databinding.HeaderAllSearchBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.InfoCache;
import com.xaqinren.healthyelders.moduleHome.activity.VideoListActivity;
import com.xaqinren.healthyelders.moduleHome.adapter.AllSearchAdapter;
import com.xaqinren.healthyelders.moduleHome.adapter.SearchUserAdapter;
import com.xaqinren.healthyelders.moduleHome.bean.CommentListBean;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleHome.bean.VideoListBean;
import com.xaqinren.healthyelders.moduleHome.viewModel.SearchAllViewModel;
import com.xaqinren.healthyelders.moduleLogin.activity.PhoneLoginActivity;
import com.xaqinren.healthyelders.moduleLogin.activity.SelectLoginActivity;
import com.xaqinren.healthyelders.moduleMine.activity.UserInfoActivity;
import com.xaqinren.healthyelders.modulePicture.activity.TextPhotoDetailActivity;
import com.xaqinren.healthyelders.moduleZhiBo.activity.VideoEditTextDialogActivity;
import com.xaqinren.healthyelders.uniApp.UniService;
import com.xaqinren.healthyelders.uniApp.UniUtil;
import com.xaqinren.healthyelders.uniApp.bean.UniEventBean;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.utils.Num2TextUtil;
import com.xaqinren.healthyelders.widget.SpeacesItemDecoration;
import com.xaqinren.healthyelders.widget.YesOrNoDialog;
import com.xaqinren.healthyelders.widget.comment.CommentDialog;
import com.xaqinren.healthyelders.widget.share.ShareDialog;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.utils.ToastUtils;

/**
 * Created by Lee. on 2021/5/27.
 */
public class SearchAllFragment extends BaseFragment<FragmentAllSearchBinding, BaseViewModel> {

    private AllSearchAdapter mAdapter;
    private BaseLoadMoreModule mLoadMore;
    public int page = 1;
    private SearchAllViewModel searchAllViewModel;
    private Disposable subscribe;
    private SearchUserAdapter userAdapter;
    private HeaderAllSearchBinding headBinding;
    private Disposable uniSubscribe;
    private String commentId;
    private CommentListBean mCommentListBean;
    private boolean editTextOpen;
    private String commentText;
    private int nowPos;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_all_search;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        //获取别的ViewModel
        searchAllViewModel = ViewModelProviders.of(getActivity()).get(SearchAllViewModel.class);
        mAdapter = new AllSearchAdapter();

        binding.rvContent.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvContent.setAdapter(mAdapter);
        binding.rvContent.addItemDecoration(new SpeacesItemDecoration(getActivity(), 1, 3, 0));
        showDialog();
        mLoadMore = mAdapter.getLoadMoreModule();//创建适配器.上拉加载
        mLoadMore.setEnableLoadMore(true);//打开上拉加载
        mLoadMore.setAutoLoadMore(true);//自动加载
        mLoadMore.setPreLoadNumber(1);//设置滑动到倒数第几个条目时自动加载，默认为1
        mLoadMore.setEnableLoadMoreIfNotFullPage(true);//当数据不满一页时继续自动加载
        mLoadMore.setOnLoadMoreListener(new OnLoadMoreListener() {//设置加载更多监听事件
            @Override
            public void onLoadMore() {
                binding.srlContent.setRefreshing(false);
                page++;
                searchAllViewModel.searchDatas(page, 0);
            }
        });
        binding.srlContent.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                userRes = false;
                contentRes = false;
                mLoadMore.setEnableLoadMore(false);
                page = 1;
                searchAllViewModel.searchUsers(page, 3);
                searchAllViewModel.searchDatas(page, 0);
            }
        });

        mAdapter.setOnItemClickListener(((adapter, view, position) -> {
            //视频
            if (mAdapter.getData().get(position).getItemType() == 0) {
                List<VideoInfo> tempList = new ArrayList<>();
                VideoInfo videoInfo = mAdapter.getData().get(position);
                tempList.add(videoInfo);
                toVideoList(tempList);
            } else if (mAdapter.getData().get(position).getItemType() == 4) {
                //跳转图文详情
                Intent intent = new Intent(getContext(), TextPhotoDetailActivity.class);
                intent.putExtra(com.xaqinren.healthyelders.moduleLiteav.Constant.VIDEO_ID, mAdapter.getData().get(position).resourceId);
                startActivity(intent);
            } else if (mAdapter.getData().get(position).getItemType() == 3) {
                //进入直播
                searchAllViewModel.joinLive(mAdapter.getData().get(position).liveRoomId);
            } else if (mAdapter.getData().get(position).getItemType() == 2) {
                //进入商品
                VideoInfo info = mAdapter.getData().get(position);
                UniService.startService(getContext(), info.appId, 0x20056, info.jumpUrl);
            }
        }));

        mAdapter.setOnItemChildClickListener(((adapter, view, position) -> {
            nowPos = position;
            videoInfo = mAdapter.getData().get(position);
            if (view.getId() == R.id.iv_zan) {
                //视频点赞
                searchAllViewModel.toLike(0, mAdapter.getData().get(position).resourceId, !mAdapter.getData().get(position).hasFavorite, position);
            } else if (view.getId() == R.id.iv_avatar) {
                //进入用户信息
                UserInfoActivity.startActivity(getActivity(), mAdapter.getData().get(position).userId);
            } else if (view.getId() == R.id.iv_comment) {
                if (!InfoCache.getInstance().checkLogin()) {
                    //跳转登录页面
                    startActivity(SelectLoginActivity.class);
                    return;
                }
                if (UserInfoMgr.getInstance().getUserInfo() == null) {
                    startActivity(SelectLoginActivity.class);
                    return;
                }
                //判断是否绑手机号
                if (!UserInfoMgr.getInstance().getUserInfo().hasMobileNum()) {
                    startActivity(PhoneLoginActivity.class);
                    return;
                }
                showCommentDialog(videoInfo.resourceId);
            } else if (view.getId() == R.id.iv_share) {
                if (!InfoCache.getInstance().checkLogin()) {
                    //跳转登录页面
                    startActivity(SelectLoginActivity.class);
                    return;
                }
                if (UserInfoMgr.getInstance().getUserInfo() == null) {
                    startActivity(SelectLoginActivity.class);
                    return;
                }
                //判断是否绑手机号
                if (!UserInfoMgr.getInstance().getUserInfo().hasMobileNum()) {
                    startActivity(PhoneLoginActivity.class);
                    return;
                }
                showShareDialog(videoInfo);
            }
        }));

        initHead();
        ((BaseActivity) getActivity()).showDialog();
    }

    private boolean hasHead;

    private void initHead() {
        if (hasHead) {
            return;
        }

        View headView = LinearLayout.inflate(getActivity(), R.layout.header_all_search, null);
        headBinding = DataBindingUtil.bind(headView);
        userAdapter = new SearchUserAdapter(R.layout.item_search_user);
        headBinding.rvUser.setLayoutManager(new LinearLayoutManager(getActivity()));
        headBinding.rvUser.setAdapter(userAdapter);

        headBinding.tvMore.setOnClickListener(lis -> {
            //通知页面切换到
            searchAllViewModel.toUsers.postValue(true);
        });

        mAdapter.addHeaderView(headView);
        hasHead = true;

        userAdapter.setOnItemChildClickListener(((adapter, view, position) -> {
            followPosition = position;
            //判断是否登录
            if (AppApplication.isToLogin()) {
                return;
            }

            if (view.getId() == R.id.rl_avatar) {

                Bundle bundle = new Bundle();
                bundle.putString("userId", userAdapter.getData().get(position).id);
                Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, 10086);

            } else if (view.getId() == R.id.tv_follow) {
                searchAllViewModel.toFollow(userAdapter.getData().get(position).id);
            }
        }));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10086) {
            Boolean aBoolean = AppApplication.get().followList.get(userAdapter.getData().get(followPosition).id);
            if (aBoolean == null) {
                aBoolean = false;
            }
            if (aBoolean) {
                userAdapter.getData().get(followPosition).hasAttention = aBoolean;
            }
            userAdapter.notifyItemChanged(followPosition, 99);
        }
    }

    private int followPosition;

    private void toVideoList(List<VideoInfo> tempList) {
        //跳页 传入数据 pos page list
        VideoListBean listBean = new VideoListBean();

        listBean.page = 0;
        listBean.position = 0;
        listBean.videoInfos = tempList;
        listBean.openType = 2;

        Bundle bundle = new Bundle();
        bundle.putSerializable("key", listBean);
        bundle.putBoolean("isSingle", true);
        startActivity(VideoListActivity.class, bundle);
    }


    //分享弹窗
    private ShareDialog shareDialog;

    private void showShareDialog(VideoInfo videoInfo) {
        if (!InfoCache.getInstance().checkLogin()) {
            //跳转登录页面
            startActivity(SelectLoginActivity.class);
            return;
        }
        if (UserInfoMgr.getInstance().getUserInfo() == null) {
            startActivity(SelectLoginActivity.class);
            return;
        }
        //判断是否绑手机号
        if (!UserInfoMgr.getInstance().getUserInfo().hasMobileNum()) {
            startActivity(PhoneLoginActivity.class);
            return;
        }
        if (videoInfo.share != null) {
            videoInfo.share.downUrl = videoInfo.resourceUrl;
            videoInfo.share.oldUrl = videoInfo.oldResourceUrl;
        }
        shareDialog = new ShareDialog(getActivity(), videoInfo.share, videoInfo, ShareDialog.VIDEO_TYPE);
        shareDialog.setRxPermissions(permissions);
        if (videoInfo.getVideoType() != 2) {
            shareDialog.isMineOpen(false);
        }
        shareDialog.show(binding.srlContent);
    }

    //评论弹窗
    private CommentDialog commentDialog;
    private VideoInfo videoInfo;
    private int commentType;//评论打开方式0-评论 1-回复 2-回复回复

    private void showCommentDialog(String videoId) {
        if (commentDialog == null)
            commentDialog = new CommentDialog(getContext(), videoId, getActivity());
        commentDialog.setOnChildClick(new CommentDialog.OnChildClick() {
            @Override
            public void toComment(CommentListBean iCommentBean) {
                //回复评论
                commentType = 1;
                commentId = iCommentBean.id;
                mCommentListBean = iCommentBean;
                showPublishCommentDialog("回复 @" + iCommentBean.nickname + " :");
            }

            @Override
            public void toCommentReply(CommentListBean iCommentBean) {
                //回复回复
                commentType = 2;
                commentId = iCommentBean.id;
                mCommentListBean = iCommentBean;
                showPublishCommentDialog("回复 @" + iCommentBean.fromUsername + " :");
            }

            @Override
            public void toCommentVideo(String videoId) {
                commentType = 0;
                commentId = videoId;
                //评论视频本体
                showPublishCommentDialog("留下你的精彩评论吧");
            }

            @Override
            public void toOpenFeace(String videoId) {
                commentType = 0;
                commentId = videoId;
                //评论视频本体
                showPublishCommentDialog("留下你的精彩评论吧", 1);
            }

            @Override
            public void toLike(CommentListBean iCommentBean) {
            }

            @Override
            public void toUser(CommentListBean iCommentBean) {
                String id = iCommentBean.fromUserId == null ? iCommentBean.userId : iCommentBean.fromUserId;
                UserInfoActivity.startActivity(getActivity(), id);
            }
        });
        commentDialog.show(binding.srlContent, videoInfo.commentCount);
    }

    /**
     * 发表评论
     */
    private void showPublishCommentDialog(String nickName) {
        showPublishCommentDialog(nickName, 0);
    }

    private void showPublishCommentDialog(String nickName, int openType) {
        //先判断是否登录
        if (!InfoCache.getInstance().checkLogin()) {
            startActivity(SelectLoginActivity.class);
            return;
        }
        if (UserInfoMgr.getInstance().getUserInfo() == null) {
            startActivity(SelectLoginActivity.class);
            return;
        }
        //判断是否绑手机号
        if (!UserInfoMgr.getInstance().getUserInfo().hasMobileNum()) {
            startActivity(PhoneLoginActivity.class);
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString("hint", nickName);
        bundle.putString("commentText", commentText);
        bundle.putInt("openType", openType);
        editTextOpen = true;
        startActivity(VideoEditTextDialogActivity.class, bundle);
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        searchAllViewModel.commentSuccess.observe(this, commentListBean -> {
            if (commentListBean != null && commentDialog != null) {
                //评论成功通知视频页面评论数加1
                try {
                    mAdapter.getData().get(nowPos).commentCount = (Integer.parseInt(mAdapter.getData().get(nowPos).commentCount) + 1) + "";
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mAdapter.notifyDataSetChanged();
                //本地刷新
                if (commentType == 0) {
                    //往评论列表查插数据
                    commentDialog.addMCommentData(commentListBean);
                } else if (commentType == 1 || commentType == 2) {
                    //往回复列表查插数据
                    commentDialog.addMReplyData(commentListBean);
                }
                commentType = 0;
            }
        });

        searchAllViewModel.dzSuccess.observe(this, dzSuccess -> {
            if (dzSuccess != null && dzSuccess.type == 0 && dzSuccess.isSuccess) {
                mAdapter.getData().get(dzSuccess.position).hasFavorite = !mAdapter.getData().get(dzSuccess.position).hasFavorite;
                if (mAdapter.getData().get(dzSuccess.position).hasFavorite) {
                    mAdapter.getData().get(dzSuccess.position).favoriteCount = String.valueOf(mAdapter.getData().get(dzSuccess.position).getFavoriteCount() + 1);
                } else {
                    mAdapter.getData().get(dzSuccess.position).favoriteCount = String.valueOf(mAdapter.getData().get(dzSuccess.position).getFavoriteCount() - 1);
                }
                //加1是因为设置了头布局
                mAdapter.notifyItemChanged(dzSuccess.position + 1, 99);
            }

        });
        uniSubscribe = RxBus.getDefault().toObservable(UniEventBean.class).subscribe(event -> {
            if (event != null) {
                if (event.msgId == CodeTable.UNI_RELEASE) {
                    if (event.taskId == 0x20056) {
                        UniUtil.openUniApp(getContext(), event.appId, event.jumpUrl, null, event.isSelfUni);
                    }
                } else if (event.msgId == CodeTable.UNI_RELEASE_FAIL) {
                    ToastUtils.showShort("打开小程序失败");
                }
            }
        });
        searchAllViewModel.dismissDialog.observe(this, disDialog -> {
            dismissDialog();
        });

        searchAllViewModel.followSuccess.observe(this, dismissDialog -> {
            userAdapter.getData().get(followPosition).hasAttention = !userAdapter.getData().get(followPosition).hasAttention;
            userAdapter.notifyItemChanged(followPosition, 99);
        });


        searchAllViewModel.userDatas.observe(this, dataList -> {
            if (dataList != null) {
                if (userAdapter.getData().size() > 0) {
                    userAdapter.getData().clear();
                }
                userAdapter.setNewInstance(dataList);
                userRes = true;
                if (dataList.size() > 0) {
                    headBinding.llHead.setVisibility(View.VISIBLE);
                } else {
                    headBinding.llHead.setVisibility(View.GONE);
                    showNodata();
                }

            }
        });

        searchAllViewModel.allDatas.observe(this, dataList -> {
            binding.srlContent.setRefreshing(false);
            if (dataList != null) {
                if (page == 1 && mAdapter.getData().size() > 0) {
                    mAdapter.getData().clear();
                }
                contentRes = true;
                dismissDialog();

                if (dataList.size() > 0) {
                    mLoadMore.loadMoreComplete();
                    if (page == 1) {
                        mAdapter.setNewInstance(dataList);
                    } else {
                        mAdapter.addData(dataList);
                    }
                } else {
                    mLoadMore.loadMoreEnd(true);
                    if (page == 1) {
                        showNodata();
                    }
                    page--;
                }

            }
        });

        subscribe = RxBus.getDefault().toObservable(EventBean.class).subscribe(event -> {
            if (event != null) {
                if (event.msgType == CodeTable.SEARCH_TAG) {
                    page = 1;
                } else if (event.msgId == CodeTable.VIDEO_DZ) {
                    //找出adapter中对应pos
                    int temp = -1;
                    for (int i = 0; i < mAdapter.getData().size(); i++) {
                        if (mAdapter.getData().get(i).resourceId.equals(event.content)) {
                            temp = i;
                        }
                    }
                    if (temp != -1) {
                        //局部刷新
                        int favoriteCount = mAdapter.getData().get(temp).getFavoriteCount();

                        if (event.msgType == 1) {
                            favoriteCount++;
                        } else {
                            favoriteCount--;
                        }
                        mAdapter.getData().get(temp).favoriteCount = String.valueOf(favoriteCount);
                        mAdapter.getData().get(temp).hasFavorite = event.msgType == 1 ? true : false;
                        mAdapter.notifyDataSetChanged();
                    }
                } else if (event.msgId == CodeTable.VIDEO_PL) {
                    //找出adapter中对应pos
                    int temp = -1;
                    for (int i = 0; i < mAdapter.getData().size(); i++) {
                        if (mAdapter.getData().get(i).resourceId.equals(event.content)) {
                            temp = i;
                        }
                    }
                    if (temp != -1) {
                        try {
                            mAdapter.getData().get(temp).commentCount = (Integer.parseInt(mAdapter.getData().get(temp).commentCount) + 1) + "";
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                } else if (event.msgId == CodeTable.VIDEO_SEND_COMMENT_OVER) {
                    commentText = event.content;
                    editTextOpen = false;
                } else if (event.msgId == CodeTable.VIDEO_SEND_COMMENT) {
                    //不为空说明是从VideoFragment打开的
                    if (!TextUtils.isEmpty(event.type)) {
                        return;
                    }

                    String content = event.content;
                    if (commentType == 0) {
                        //发表评论
                        searchAllViewModel.toComment(commentId, content);
                    } else if (commentType == 1) {
                        //回复评论
                        searchAllViewModel.toCommentReply(mCommentListBean, content, 0);
                    } else if (commentType == 2) {
                        //回复回复
                        searchAllViewModel.toCommentReply(mCommentListBean, content, 1);
                    }
                }
            }

        });
    }

    public boolean userRes;
    public boolean contentRes;

    private void showNodata() {
        if (userRes && contentRes) {
            if (headBinding.llHead.getVisibility() == View.GONE) {
                mAdapter.setEmptyView(R.layout.item_empty);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (subscribe != null) {
            subscribe.dispose();
        }
        if (uniSubscribe != null) {
            uniSubscribe.dispose();
        }
    }
}
