package com.xaqinren.healthyelders.modulePicture.activity;

import android.app.Dialog;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityTextPhotoDetailBinding;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishDesBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishFocusItemBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.VideoCommentBean;
import com.xaqinren.healthyelders.modulePicture.viewModel.TextPhotoDetailViewModel;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.widget.VideoPublishEditTextView;
import com.xaqinren.healthyelders.widget.comment.CommentAdapter;
import com.xaqinren.healthyelders.widget.comment.CommentDialog;
import com.xaqinren.healthyelders.widget.comment.CommentPublishDialog;
import com.xaqinren.healthyelders.widget.comment.ICommentBean;
import com.xaqinren.healthyelders.widget.share.ShareDialog;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;

/**
 * 图文详情页
 */
public class TextPhotoDetailActivity extends BaseActivity<ActivityTextPhotoDetailBinding, TextPhotoDetailViewModel> {

    private CommentAdapter commentAdapter;
    private List<ICommentBean> dataList;
    private BaseLoadMoreModule loadMoreModule;
    private CommentPublishDialog commentDialog;
    private ShareDialog shareDialog;
    private TextView commentCountTv;
    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;
    private String TAG = "TextPhotoDetailActivity";
    private VideoPublishEditTextView publishEditTextView;


    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_text_photo_detail;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        rlTitle.setVisibility(View.GONE);
        View headerBanner = View.inflate(this, R.layout.header_text_photo_banner, null);
        View headerText = View.inflate(this, R.layout.header_text_photo_text, null);
        commentAdapter = new CommentAdapter(R.layout.item_comment, new CommentAdapter.OnChildLoadMoreCommentListener() {
            @Override
            public void onLoadMore(int position, ICommentBean iCommentBean, int page, int pageSize) {
                //TODO 掉接口 查询当前评论的更多评论
                List<VideoCommentBean> list = new ArrayList<>();
                for (int j = 0; j < 1; j++) {
                    VideoCommentBean bean1 = new VideoCommentBean();
                    bean1.avatar = "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.juimg.com%2Ftuku%2Fyulantu%2F140703%2F330746-140f301555752.jpg&refer=http%3A%2F%2Fimg.juimg.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1623312032&t=dd12bc18844a0c4cfd47510b2ec89c4b";
                    bean1.nickName = "你好呀";
                    bean1.canComment = true;
                    bean1.comment = "评论内容评论内容评论内容评论内容评论内容评论内容评论内容";
                    bean1.commentTime = "12小时前";
                    bean1.likeCount = "666";
                    bean1.isLike = false;
                    list.add(bean1);
                }
                iCommentBean.childComment.addAll(list.size() - 1, list);
                commentAdapter.notifyItemChanged(position);
            }

            @Override
            public void onPackUp(int position, ICommentBean iCommentBean, int page, int pageSize) {

            }
        }, new CommentAdapter.OnOperationItemClickListener() {
            @Override
            public void toComment(ICommentBean iCommentBean) {
                showComment();
            }

            @Override
            public void toLike(ICommentBean iCommentBean) {

            }

            @Override
            public void toUser(ICommentBean iCommentBean) {

            }
        });
        commentAdapter.addHeaderView(headerBanner);
        commentAdapter.addHeaderView(headerText);
        initBanner(headerBanner);
        initContent(headerText);
        commentAdapter.setAnimationEnable(false);
        binding.content.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.content.setAdapter(commentAdapter);
        binding.commentLayout.setOnClickListener(view -> {

        });
        dataList = getCommentData();
        commentAdapter.setList(this.dataList);

        loadMoreModule = commentAdapter.getLoadMoreModule();
        loadMoreModule.setAutoLoadMore(true);
        loadMoreModule.setEnableLoadMore(true);
        loadMoreModule.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //TODO 调用 获取本详情更多评论
            }
        });
        binding.commentLayout.setOnClickListener(view -> {
            showComment();
        });
        binding.shareIv.setOnClickListener(view -> {
            showShare();
        });
        binding.shareTv.setOnClickListener(view -> {
            showShare();
        });
        binding.commentIv.setOnClickListener(view -> {
            //滚动到评论部分
            scrollComment();
        });
        binding.commentTv.setOnClickListener(view -> {
            //滚动到评论部分
            scrollComment();
        });
        onGlobalLayoutListener = () -> {
            final Rect r = new Rect();
            binding.rlContainer.getWindowVisibleDisplayFrame(r);
            final int screenHeight = binding.rlContainer.getRootView().getHeight();
            final int heightDifference = screenHeight - r.bottom;
            boolean visible = heightDifference > screenHeight / 3;
            if (visible) {
                LogUtils.e(TAG, "keyboardHeightInPx \t->\t" + heightDifference);
            } else {
                if (commentDialog != null && commentDialog.isShow()) {
                    commentDialog.keyBoardClosed();
                }
            }
        };
        binding.rlContainer.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
    }

    private void scrollComment() {
        int top = commentCountTv.getTop();
        LogUtils.e(TAG, "top -> " + top);
        binding.content.scrollBy(0, top);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.rlContainer.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
    }

    /**
     * 展示评论弹窗
     */
    private void showComment() {
        if (commentDialog == null) {
            commentDialog = new CommentPublishDialog(this, "");
        }
        commentDialog.show(binding.rlTitle);
    }
    /**
     * 展示分享弹窗
     */
    private void showShare() {
        if (shareDialog == null) {
            shareDialog = new ShareDialog(this);
            shareDialog.setShowType(ShareDialog.TP_TYPE);
        }
        shareDialog.show(binding.rlTitle);
    }

    /**
     * 初始化banner
     * @param headerBanner
     */
    private void initBanner(View headerBanner) {

    }

    /**
     * 初始化文详情
     * @param headerText
     */
    private void initContent(View headerText) {
        PublishFocusItemBean itemBean = new PublishFocusItemBean();
        itemBean.type = 1;
        itemBean.start = 0;
        itemBean.end = 4;
        itemBean.uid = 1243;
        PublishDesBean desBean = new PublishDesBean();
        desBean.publishFocusItemBeans = new ArrayList<>();
        desBean.publishFocusItemBeans.add(itemBean);
        desBean.content = "#你好鸭 今天分享一道超级简单又快手的下饭菜！肥牛卷下水一焯水，各式调味一拌，就完成了，酸辣的酱汁包裹着贤能的肥牛卷，超级下饭！大家快安排上吧！\n" +
                "【食材准备】\n" +
                "肥牛300g、先交、小米蕉、辣椒面、香菜、熟芝麻、生抽、陈醋、蚝油\n" +
                "1、调酱汁：碗中放入蒜末、辣椒面、青红椒圈、报纸吗淋上热油+两勺生抽+两勺陈醋+一勺蚝油+少许盐和白糖拌匀。\n" +
                "2、冷水放入肥牛和1勺了牛煮至肥牛变色捞出放入碗里，倒入酱料、香菜拌匀即可～";
        publishEditTextView = headerText.findViewById(R.id.text_content);
        commentCountTv = headerText.findViewById(R.id.comment_count_tv);
        publishEditTextView.setTopicEnable(false);
        publishEditTextView.setAtEnable(false);
        publishEditTextView.initDesStr(desBean);

    }

    private List<ICommentBean> getCommentData() {
        List<ICommentBean> list = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            VideoCommentBean bean = new VideoCommentBean();
            bean.avatar = "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.juimg.com%2Ftuku%2Fyulantu%2F140703%2F330746-140f301555752.jpg&refer=http%3A%2F%2Fimg.juimg.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1623312032&t=dd12bc18844a0c4cfd47510b2ec89c4b";
            bean.nickName = "你好呀";
            bean.canComment = true;
            bean.comment = "评论内容评论内容评论内容评论内容评论内容评论内容评论内容";
            bean.commentTime = "12小时前";
            bean.likeCount = "666";
            bean.isLike = true;
            bean.commentCount = 3;
            bean.childComment = new ArrayList<>();
            list.add(bean);
        }
        return list;
    }

}
