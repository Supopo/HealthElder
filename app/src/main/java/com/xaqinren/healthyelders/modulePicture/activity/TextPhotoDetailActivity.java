package com.xaqinren.healthyelders.modulePicture.activity;

import android.app.Dialog;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.tencent.bugly.proguard.T;
import com.tencent.qcloud.tim.uikit.utils.ScreenUtil;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.ActivityTextPhotoDetailBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.moduleHome.bean.CommentListBean;
import com.xaqinren.healthyelders.moduleLiteav.Constant;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishDesBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishFocusItemBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.VideoCommentBean;
import com.xaqinren.healthyelders.moduleLogin.activity.SelectLoginActivity;
import com.xaqinren.healthyelders.modulePicture.bean.DiaryInfoBean;
import com.xaqinren.healthyelders.modulePicture.viewModel.TextPhotoDetailViewModel;
import com.xaqinren.healthyelders.moduleZhiBo.activity.VideoEditTextDialogActivity;
import com.xaqinren.healthyelders.utils.GlideUtil;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.utils.Num2TextUtil;
import com.xaqinren.healthyelders.widget.VideoPublishEditTextView;
import com.xaqinren.healthyelders.widget.comment.CommentAdapter;
import com.xaqinren.healthyelders.widget.comment.CommentDialog;
import com.xaqinren.healthyelders.widget.comment.CommentListAdapter;
import com.xaqinren.healthyelders.widget.comment.CommentPublishDialog;
import com.xaqinren.healthyelders.widget.comment.ICommentBean;
import com.xaqinren.healthyelders.widget.share.ShareDialog;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerAdapter;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.listener.OnPageChangeListener;

import java.net.URI;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxBusSubscriber;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.utils.ConvertUtils;
import me.goldze.mvvmhabit.utils.StringUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.goldze.mvvmhabit.utils.Utils;

/**
 * 图文详情页
 */
public class TextPhotoDetailActivity extends BaseActivity<ActivityTextPhotoDetailBinding, TextPhotoDetailViewModel> implements View.OnClickListener {

    private CommentListAdapter commentAdapter;
    private List<CommentListBean> dataList;
    private BaseLoadMoreModule loadMoreModule;
    private CommentPublishDialog commentDialog;
    private ShareDialog shareDialog;
    private TextView commentCountTv;
    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;
    private String TAG = "TextPhotoDetailActivity";
    private VideoPublishEditTextView publishEditTextView;

    private int page = 1;
    private int mCommentCount;
    private String videoId;


    private int openType;
    private String commentId;//评论时候的ID 有内容id或者评论id区别
    private CommentListBean mCommentListBean;//当前评论对象
    private TextView diaryTitle;
    private TextView diaryTime;
    private DiaryInfoBean diaryInfoBean;
    private Disposable disposable;
    private String diaryType = "diaryType";
    private Banner banner;
    private TextView banneCount;

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
        videoId = getIntent().getStringExtra(Constant.VIDEO_ID);

        rlTitle.setVisibility(View.GONE);
        View headerBanner = View.inflate(this, R.layout.header_text_photo_banner, null);
        View headerText = View.inflate(this, R.layout.header_text_photo_text, null);
        commentAdapter = new CommentListAdapter(R.layout.item_comment_list, new CommentListAdapter.OnChildLoadMoreCommentListener() {
            @Override
            public void onLoadMore(int position, CommentListBean iCommentBean, int page, int pageSize) {
                iCommentBean.parentPos = position;
                viewModel.getCommentReplyList(iCommentBean);
            }

            @Override
            public void onPackUp(int position, CommentListBean iCommentBean, int page, int pageSize) {
                iCommentBean.itemPage = 1;
                iCommentBean.replyList.clear();
                commentAdapter.notifyItemChanged(position);
            }
        }, new CommentListAdapter.OnOperationItemClickListener() {
            @Override
            public void toComment(CommentListBean iCommentBean) {
                //回复评论
                openType = 1;
                commentId = iCommentBean.id;
                mCommentListBean = iCommentBean;
                showPublishCommentDialog("回复 @" + iCommentBean.nickname + " :");
            }

            @Override
            public void toCommentReply(CommentListBean iCommentBean) {
                //回复回复
                openType = 2;
                commentId = iCommentBean.id;
                mCommentListBean = iCommentBean;
                showPublishCommentDialog("回复 @" + iCommentBean.fromUsername + " :");
            }

            @Override
            public void toLike(CommentListBean iCommentBean) {
                if (AppApplication.get().isToLogin())
                    return;
                int position = iCommentBean.parentPos - 1;
                //点赞评论
                viewModel.setCommentLike(videoId, iCommentBean.id, !iCommentBean.hasFavorite, false);

                CommentListBean commentBean = commentAdapter.getData().get(position);

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
                if (AppApplication.get().isToLogin())
                    return;
                int position = iCommentBean.parentPos - 1;
                //点赞回复评论
                viewModel.setCommentLike(videoId, iCommentBean.id, !iCommentBean.hasFavorite, true);

                CommentListBean commentBean = commentAdapter.getData().get(position);
                commentBean.itemPos = iCommentBean.itemPos;
                if (commentBean.allReply != null) {
                    CommentListBean commentChild = commentBean.allReply.get(iCommentBean.itemPos);
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
//                onChildClick.toUser(iCommentBean);
                //TODO 打开用户主页
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
            //评论视频本体
            openType = 0;
            commentId = videoId;
            showPublishCommentDialog("说点什么吧");
        });
//        dataList = new ArrayList<>();
//        commentAdapter.setList(this.dataList);

        loadMoreModule = commentAdapter.getLoadMoreModule();
        loadMoreModule.setAutoLoadMore(true);
        loadMoreModule.setEnableLoadMore(true);
        loadMoreModule.setOnLoadMoreListener(() -> {
            page++;
            viewModel.getCommentList(page , videoId);
        });
        binding.commentIv.setOnClickListener(view -> {
            //滚动到评论部分
            scrollComment();
        });
        binding.commentTv.setOnClickListener(view -> {
            //滚动到评论部分
            scrollComment();
        });
        binding.likeIv.setOnClickListener(this);
        binding.likeTv.setOnClickListener(this);

        binding.commentIv.setOnClickListener(this);
        binding.commentTv.setOnClickListener(this);

        binding.shareIv.setOnClickListener(this);
        binding.shareTv.setOnClickListener(this);

        binding.guanzhu.setOnClickListener(this);
        viewModel.diaryInfo(videoId);
        viewModel.getCommentList(page , videoId);
    }

    /**
     * 发表评论
     */
    private void showPublishCommentDialog(String nickName) {
        if (AppApplication.get().isToLogin())
            return;

        Bundle bundle = new Bundle();
        bundle.putString("hint", nickName);
        bundle.putInt("pos", -10);
        bundle.putString("type", diaryType);
        startActivity(VideoEditTextDialogActivity.class, bundle);
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.commentList.observe(this, dataList -> {
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

        viewModel.commentReplyList.observe(this, replyDatas -> {
            if (replyDatas != null) {
                if (replyDatas.replyList != null && replyDatas.replyList.size() > 0) {
                    int position = replyDatas.parentPos - 1;
                    CommentListBean bean = commentAdapter.getData().get(position);
                     commentAdapter.getData().get(position).itemPage++;
                    //加载更多加载完成
                    replyDatas.replyList.get(replyDatas.replyList.size() - 1).lodaState = 1;
                    replyDatas.replyList.get(replyDatas.replyList.size() - 1).itemPage = commentAdapter.getData().get(position).itemPage;


                    int index = commentAdapter.getData().get(position).replyList.size() > 0 ? commentAdapter.getData().get(position).replyList.size() : 0;
                    commentAdapter.getData().get(position)
                            .replyList.addAll(index,
                            replyDatas.replyList);
                    commentAdapter.notifyItemChanged(replyDatas.parentPos);
                }
            }
        });

        viewModel.commentSuccess.observe(this, commentListBean -> {
            if (commentListBean != null) {
                //本地刷新
                if (openType == 0) {
                    //往评论列表查插数据
                    addMCommentData(commentListBean);
                } else if (openType == 1 || openType == 2) {
                    //往回复列表查插数据
                    addMReplyData(commentListBean);
                }
                openType = 0;
            }
        });

        viewModel.diaryInfo.observe(this,diaryInfoBean -> {
            this.diaryInfoBean = diaryInfoBean;
            setBannerData(diaryInfoBean.bannerImages);
            setContentData(this.diaryInfoBean);
        });

        viewModel.follow.observe(this,follow->{
            if (follow) {
                diaryInfoBean.hasFavorite = !diaryInfoBean.hasFavorite;
                follow();
            }
        });

        viewModel.favorite.observe(this, value ->{
            diaryInfoBean.hasFavorite = !diaryInfoBean.hasFavorite;
            binding.likeIv.setImageResource(diaryInfoBean.hasFavorite ? R.mipmap.icon_zan_red : R.mipmap.icon_zan_gray);
            diaryInfoBean.favoriteCount = diaryInfoBean.hasFavorite ? diaryInfoBean.favoriteCount + 1 : diaryInfoBean.favoriteCount - 1;
            binding.likeTv.setText(Num2TextUtil.num2Text(diaryInfoBean.favoriteCount));
        });

        //TODO 发表评论弹窗的接口
        disposable = RxBus.getDefault().toObservable(EventBean.class).subscribe(bean -> {
            if (bean != null) {
                if (bean.msgId == CodeTable.VIDEO_SEND_COMMENT && bean.type.equals(diaryType)) {
                    String content = bean.content;
                    if (openType == 0) {
                        //发表评论
                        viewModel.toComment(commentId, content);
                    } else if (openType == 1) {
                        //回复评论
                        viewModel.toCommentReply(mCommentListBean, content, 0);
                    } else if (openType == 2) {
                        //回复回复
                        viewModel.toCommentReply(mCommentListBean, content, 1);
                    }
                }
            }
        });
        RxSubscriptions.add(disposable);

    }

    private void setContentData(DiaryInfoBean diaryInfoBean) {
        diaryTitle.setText(diaryInfoBean.title);
        PublishDesBean desBean = new PublishDesBean();
        desBean.content = diaryInfoBean.summary.content;
        desBean.publishFocusItemBeans = diaryInfoBean.summary.publishFocusItemBeans;
        publishEditTextView.initDesStr(desBean);
        commentCountTv.setText("共" + diaryInfoBean.commentCount+"条评论");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd");
        try {
            Date date = simpleDateFormat.parse(diaryInfoBean.createdAt);
            String format = simpleDateFormat.format(date);
            diaryTime.setText("编辑于 " + format);
        } catch (ParseException e) {
            diaryTime.setText("编辑于 " + diaryInfoBean.createdAt);
            e.printStackTrace();
        }

        GlideUtil.intoImageView(this,diaryInfoBean.avatarUrl,binding.avatar);
        binding.nickname.setText(diaryInfoBean.nickname);

        binding.likeTv.setText(Num2TextUtil.num2Text(diaryInfoBean.favoriteCount));
        binding.commentTv.setText(Num2TextUtil.num2Text(diaryInfoBean.commentCount));
        binding.shareTv.setText(Num2TextUtil.num2Text(diaryInfoBean.shareCount));
        binding.likeIv.setImageResource(diaryInfoBean.hasFavorite ? R.mipmap.icon_zan_red : R.mipmap.icon_zan_gray);
        follow();
    }

    private void setBannerData(List<DiaryInfoBean.BannerImagesDTO> bannerImages) {
        //计算banner最大的比例图
        int height = mathScale(bannerImages);
        ViewGroup.LayoutParams params = banner.getLayoutParams();
        params.height = height;
        banner.setLayoutParams(params);
        if (bannerImages.size() > 1) {
            banneCount.setVisibility(View.VISIBLE);
            banner.addOnPageChangeListener(new OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    banneCount.setText((position + 1) + "/" + bannerImages.size());
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            banneCount.setText("1/" + bannerImages.size());
        }
        banner.setDatas(bannerImages);
        banner.setAdapter(new BannerImageAdapter<DiaryInfoBean.BannerImagesDTO>(bannerImages) {
            @Override
            public void onBindView(BannerImageHolder holder, DiaryInfoBean.BannerImagesDTO data, int position, int size) {
                holder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                Glide.with(holder.itemView)
                        .load(data.url)
                        .into(holder.imageView);
            }
        });
        banner.setIndicator(new CircleIndicator(this));
    }

    private int mathScale(List<DiaryInfoBean.BannerImagesDTO> bannerImages) {
        float scale = Integer.MAX_VALUE;
        for (DiaryInfoBean.BannerImagesDTO dto : bannerImages) {
            Uri uri = Uri.parse(dto.url);
            String query = uri.getQuery();
            String[] wh = query.split("&");
            float w = Float.parseFloat(wh[0].split("=")[1]);
            float h = Float.parseFloat(wh[1].split("=")[1]);
            LogUtils.e(TAG, "w = " + w + "\th = " + h);
            float s = w / h;
            if (s < scale) {
                scale = s;
            }
        }
        int screenW = ScreenUtil.getScreenWidth(this);
        return (int) (screenW / scale);
    }

    //添加自己的评论数据
    public void addMCommentData(CommentListBean commentListBean) {
        mCommentCount++;
        commentCountTv.setText(mCommentCount + "条评论");
        commentAdapter.addData(0, commentListBean);
        commentAdapter.notifyItemChanged(1);
        binding.content.scrollToPosition(1);
    }

    public void addMReplyData(CommentListBean commentListBean) {
        mCommentCount++;
        commentCountTv.setText(mCommentCount + "条评论");
        int position = commentListBean.parentPos - 1;
        CommentListBean adapterCommentBean = commentAdapter.getData().get(position );

        adapterCommentBean.shortVideoCommentReplyList.add(0, commentListBean);
        adapterCommentBean.commentCount++;
        adapterCommentBean.lodaState = 1;//展示  展开回复

        commentAdapter.notifyItemChanged(commentListBean.parentPos);
    }

    public void rushData() {
        page = 1;
        viewModel.getCommentList(page, videoId);
    }


    public void show(View Parent, String commentCount) {
        if (TextUtils.isEmpty(commentCount)) {
            commentCount = "0";
        }
        mCommentCount = Integer.parseInt(commentCount);
        commentCountTv.setText(commentCount + "条评论");
    }


    private void scrollComment() {
        int top = commentCountTv.getTop();
        LogUtils.e(TAG, "top -> " + top);
        binding.content.scrollBy(0, top);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxSubscriptions.remove(disposable);
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
        banner = headerBanner.findViewById(R.id.banner);
        banneCount = headerBanner.findViewById(R.id.banner_count);
        banner.addBannerLifecycleObserver(this);
    }

    /**
     * 初始化文详情
     * @param headerText
     */
    private void initContent(View headerText) {
        publishEditTextView = headerText.findViewById(R.id.text_content);
        commentCountTv = headerText.findViewById(R.id.comment_count_tv);
        diaryTitle = headerText.findViewById(R.id.text_title);
        diaryTime = headerText.findViewById(R.id.edit_time);
    }

    @Override
    public void onClick(View view) {
        //判断登录
        if (!checkLogin()) {
            return;
        }
        switch (view.getId()) {
            case R.id.like_iv: case R.id.like_tv:
                viewModel.toFavorite(videoId, !diaryInfoBean.hasFavorite);
                break;
            case R.id.comment_iv: case R.id.comment_tv:
                showPublishCommentDialog(null);
                break;
            case R.id.share_iv: case R.id.share_tv:
                showShare();
                break;
            case R.id.guanzhu: {
                viewModel.toFollow(diaryInfoBean.userId);
            }break;
        }
    }

    private void follow() {
        binding.guanzhu.setText(diaryInfoBean.hasFavorite ? "已关注" : "关注");
    }


    private boolean checkLogin() {
        if (!StringUtils.isEmpty(UserInfoMgr.getInstance().getAccessToken())) {
            return true;
        }
        ToastUtils.showShort(R.string.un_login);
        startActivity(SelectLoginActivity.class);
        return false;
    }
}
