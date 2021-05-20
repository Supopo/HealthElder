package com.xaqinren.healthyelders.widget.comment;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.animation.BaseAnimation;
import com.chad.library.adapter.base.animation.SlideInBottomAnimation;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.tencent.qcloud.tim.uikit.utils.ScreenUtil;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.apiserver.LiveRepository;
import com.xaqinren.healthyelders.bean.BaseListRes;
import com.xaqinren.healthyelders.databinding.PopCommentBinding;
import com.xaqinren.healthyelders.databinding.PopShareBinding;
import com.xaqinren.healthyelders.moduleHome.bean.CommentListBean;
import com.xaqinren.healthyelders.moduleHome.bean.VideoListBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.VideoCommentBean;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

//评论PopUpWindow
public class CommentDialog {
    private final String TAG = this.getClass().getSimpleName();

    private PopupWindow popupWindow;
    private View contentView;
    private SoftReference<Context> context;
    private PopCommentBinding binding;
    private CommentListAdapter commentAdapter;
    private List<CommentListBean> dataList;
    private boolean isOverData;
    private CommentViewModel viewModel;
    private OnChildClick onChildClick;
    private String videoId;
    private BaseLoadMoreModule loadMoreModule;
    private Context mContext;
    private int page = 1;
    private int mCommentCount;

    public CommentDialog(Context context, String videoId) {
        this.context = new SoftReference<>(context);
        this.videoId = videoId;
        mContext = context;
        init();
    }

    public void setOnChildClick(OnChildClick onChildClick) {
        this.onChildClick = onChildClick;
    }

    private void init() {
        viewModel = ViewModelProviders.of((FragmentActivity) context.get()).get(CommentViewModel.class);
        contentView = View.inflate(context.get(), R.layout.pop_comment, null);
        binding = DataBindingUtil.bind(contentView);
        int height = (int) (ScreenUtil.getScreenHeight(context.get()) * 0.8f);
        popupWindow = new PopupWindow(binding.getRoot(), ViewGroup.LayoutParams.MATCH_PARENT, height);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setAnimationStyle(R.style.DialogBottomAnimation);
        popupWindow.setOnDismissListener(() -> {

        });
        //关闭dialog
        binding.close.setOnClickListener(view -> popupWindow.dismiss());
        //        binding.close.setOnClickListener(view -> rushData());
        commentAdapter = new CommentListAdapter(R.layout.item_comment_list, new CommentListAdapter.OnChildLoadMoreCommentListener() {
            @Override
            public void onLoadMore(int position, CommentListBean iCommentBean, int page, int pageSize) {
                iCommentBean.parentPos = position;
                getCommentReplyList(iCommentBean);
                iCommentBean.itemPage++;
            }

            @Override
            public void onPackUp(int position, CommentListBean iCommentBean, int page, int pageSize) {
                iCommentBean.itemPage = 1;
                iCommentBean.shortVideoCommentReplyList.clear();
                commentAdapter.notifyItemChanged(position);

            }
        }, new CommentListAdapter.OnOperationItemClickListener() {
            @Override
            public void toComment(CommentListBean iCommentBean) {
                onChildClick.toComment(iCommentBean);
            }

            @Override
            public void toCommentReply(CommentListBean iCommentBean) {
                onChildClick.toCommentReply(iCommentBean);
            }

            @Override
            public void toLike(CommentListBean iCommentBean) {
                onChildClick.toLike(iCommentBean);
                //点赞
                setCommentLike(videoId, iCommentBean.id, !iCommentBean.hasFavorite, false);

                CommentListBean commentBean = commentAdapter.getData().get(iCommentBean.parentPos);

                if (commentBean.hasFavorite) {
                    if (commentBean.favoriteCount > 0) {
                        commentBean.favoriteCount--;
                    }
                } else {
                    commentBean.favoriteCount++;
                }

                commentBean.hasFavorite = !commentBean.hasFavorite;

                //刷新某个Item
                commentAdapter.notifyItemChanged(iCommentBean.parentPos, 99);
            }

            @Override
            public void toLikeReply(CommentListBean iCommentBean) {
                //点赞
                setCommentLike(videoId, iCommentBean.id, !iCommentBean.hasFavorite, true);

                CommentListBean commentBean = commentAdapter.getData().get(iCommentBean.parentPos);
                commentBean.itemPos = iCommentBean.itemPos;
                if (commentBean.shortVideoCommentReplyList != null) {
                    CommentListBean commentChild = commentBean.shortVideoCommentReplyList.get(iCommentBean.itemPos);
                    if (commentChild != null) {
                        if (commentChild.hasFavorite) {
                            if (commentChild.favoriteCount > 0) {
                                commentChild.favoriteCount--;
                            }
                        } else {
                            commentChild.favoriteCount++;
                        }


                        commentChild.hasFavorite = !commentChild.hasFavorite;
                    }
                }

                commentAdapter.notifyItemChanged(iCommentBean.parentPos, 98);


            }

            @Override
            public void toUser(CommentListBean iCommentBean) {
                onChildClick.toUser(iCommentBean);
            }
        });

        commentAdapter.setAnimationEnable(false);
        binding.commentList.setLayoutManager(new LinearLayoutManager(context.get(), LinearLayoutManager.VERTICAL, false));
        binding.commentList.setAdapter(commentAdapter);
        binding.commentLayout.setOnClickListener(view -> {
            onChildClick.toCommentVideo(videoId);
        });

        loadMoreModule = commentAdapter.getLoadMoreModule();
        loadMoreModule.setAutoLoadMore(true);
        loadMoreModule.setEnableLoadMore(true);
        loadMoreModule.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                page++;
                getCommentList();
            }
        });

        getCommentList();
        commentList.observe((LifecycleOwner) mContext, dataList -> {
            if (dataList.size() > 0) {
                //加载更多加载完成
                loadMoreModule.loadMoreComplete();
            }
            if (page == 1) {
                commentAdapter.setList(dataList);
            } else {
                if (dataList.size() == 0) {
                    //加载更多加载结束
                    loadMoreModule.loadMoreEnd(true);
                    page--;
                }
                commentAdapter.addData(dataList);
            }
        });

        commentReplyList.observe((LifecycleOwner) mContext, replyDatas -> {
            if (replyDatas != null) {
                if (replyDatas.replyList != null && replyDatas.replyList.size() > 0) {
                    //加载更多加载完成
                    replyDatas.replyList.get(replyDatas.replyList.size() - 1).lodaState = 1;
                    commentAdapter.getData().get(replyDatas.parentPos)
                            .shortVideoCommentReplyList.addAll(commentAdapter.getData().get(replyDatas.parentPos).shortVideoCommentReplyList.size() - 1,
                            replyDatas.replyList);
                    commentAdapter.notifyItemChanged(replyDatas.parentPos);
                } else {
                    commentAdapter.getData().get(replyDatas.parentPos).itemPage--;
                }
            }


        });
    }


    //添加自己的评论数据
    public void addMCommentData(CommentListBean commentListBean) {

        mCommentCount++;
        binding.commentCountTv.setText(mCommentCount + "条评论");
        commentAdapter.addData(0, commentListBean);
        commentAdapter.notifyItemChanged(0);
        binding.commentList.scrollToPosition(0);

    }

    public void addMReplyData(CommentListBean commentListBean) {
        mCommentCount++;
        binding.commentCountTv.setText(mCommentCount + "条评论");

        CommentListBean adapterCommentBean = commentAdapter.getData().get(commentListBean.parentPos);
        if (adapterCommentBean.shortVideoCommentReplyList == null) {
            adapterCommentBean.shortVideoCommentReplyList = new ArrayList<>();
        }
        adapterCommentBean.shortVideoCommentReplyList.add(0, commentListBean);
        adapterCommentBean.commentCount++;
        commentAdapter.notifyItemChanged(commentListBean.parentPos);
    }

    public void rushData() {
        page = 1;
        getCommentList();
    }


    public void addDataList(List<CommentListBean> dataList) {
        this.dataList.addAll(dataList);
    }

    public void setOverData(boolean overData) {
        isOverData = overData;
        //没有更多评论,变为收起
    }

    public void show(View Parent, String commentCount) {
        if (popupWindow == null) {
            init();
        }
        if (TextUtils.isEmpty(commentCount)) {
            commentCount = "0";
        }
        mCommentCount = Integer.parseInt(commentCount);
        binding.commentCountTv.setText(commentCount + "条评论");
        popupWindow.showAsDropDown(Parent, Gravity.BOTTOM, 0, 0);
    }


    public void hide() {
        popupWindow.dismiss();
    }


    public interface OnChildClick {
        //评论评论[存在评论本身的可能性,即没有XXX 回复 XXX的可能性]
        void toComment(CommentListBean iCommentBean);

        //回复回复
        void toCommentReply(CommentListBean iCommentBean);

        //评论视频本身
        void toCommentVideo(String videoId);

        //点击喜欢
        void toLike(CommentListBean iCommentBean);

        //查看用户
        void toUser(CommentListBean iCommentBean);
    }

    public MutableLiveData<List<CommentListBean>> commentList = new MutableLiveData<>();
    public MutableLiveData<CommentListBean> commentReplyList = new MutableLiveData<>();


    public void getCommentList() {
        LiveRepository.getInstance().getCommentList(commentList, page, videoId);
    }

    public void getCommentReplyList(CommentListBean commentListBean) {
        LiveRepository.getInstance().getCommentReplyList(commentListBean, commentReplyList);
    }

    public void setCommentLike(String shortVideoId, String commentId, boolean favoriteStatus, boolean notRoot) {
        LiveRepository.getInstance().toLikeComment(shortVideoId, commentId, favoriteStatus, notRoot);
    }
}
