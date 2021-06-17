package com.xaqinren.healthyelders.moduleHome.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tencent.qcloud.ugckit.utils.TelephonyUtil;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.ITXVodPlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.TXVodPlayConfig;
import com.tencent.rtmp.TXVodPlayer;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.FragmentHomeVideoBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.global.InfoCache;
import com.xaqinren.healthyelders.moduleHome.bean.CommentListBean;
import com.xaqinren.healthyelders.moduleHome.bean.VideoEvent;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleHome.viewModel.HomeVideoModel;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishDesBean;
import com.xaqinren.healthyelders.moduleLogin.activity.SelectLoginActivity;
import com.xaqinren.healthyelders.moduleMine.activity.UserInfoActivity;
import com.xaqinren.healthyelders.moduleZhiBo.activity.LiveGuanzhongActivity;
import com.xaqinren.healthyelders.moduleZhiBo.activity.VideoEditTextDialogActivity;
import com.xaqinren.healthyelders.moduleZhiBo.activity.ZhiboOverGZActivity;
import com.xaqinren.healthyelders.utils.AnimUtil;
import com.xaqinren.healthyelders.utils.AnimUtils;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.utils.UrlUtils;
import com.xaqinren.healthyelders.widget.comment.CommentDialog;
import com.xaqinren.healthyelders.widget.share.ShareDialog;

import java.io.File;
import java.util.Random;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.bus.RxBus;

/**
 * Created by Lee. on 2021/5/13.
 * 视频播放Fragment
 */
public class HomeVideoFragment extends BaseFragment<FragmentHomeVideoBinding, HomeVideoModel> implements TelephonyUtil.OnTelephoneListener, ITXLivePlayListener, ITXVodPlayListener {
    private VideoInfo videoInfo;
    private int position;
    private String type;//区分是从哪里进来的
    private TXVodPlayer vodPlayer;
    private Disposable disposable;
    private Animation musicRotateAnim;//音乐Icon旋转动画
    private Animation avatarAnim;//头像放大缩小动画
    private AnimationDrawable avatarBgAnim;//头像背景圈动画
    private ObjectAnimator objectAnimator;//音乐Icon旋转动画 可暂停
    private boolean noPlayPause;//判断播放状态
    private boolean hasPlaying;//是否已经开始了 因为视频播放第一次播放只走进度，滑动后播放先走开始回调
    private AnimationDrawable avatarAddAnim;
    private AnimationDrawable zbingAnim;

    private String commentId;//评论时候的ID 有内容id或者评论id区别
    private CommentListBean mCommentListBean;//当前评论对象
    private String TAG = getClass().getSimpleName();
    private Disposable commentDisposable;
    private boolean editTextOpen;//判断是否打开了EditTextActivity
    private TXLivePlayConfig mPlayerConfig;
    private TXLivePlayer mLivePlayer;

    public HomeVideoFragment(VideoInfo videoInfo, String type, int position) {
        this.videoInfo = videoInfo;
        this.type = type;
        this.position = position;
        if (videoInfo.resourceType.equals("VIDEO")) {
            videoInfo.resourceUrl = Constant.setVideoSigUrl(videoInfo.resourceUrl);
        }
    }

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_home_video;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }


    public static final int STATE_PLAYING = 1;//正在播放
    public static final int STATE_PAUSE = 2;//暂停
    public static final int STATE_STOP = 3;//停止
    public int state;

    private boolean isHP;

    @SuppressLint("ObjectAnimatorBinding")
    @Override
    public void initData() {
        super.initData();
        TelephonyUtil.getInstance().setOnTelephoneListener(this);
        TelephonyUtil.getInstance().initPhoneListener();

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        commentId = videoInfo.resourceId;
        if (type.equals("home-list")) {
            if (videoInfo.getVideoType() != 2) {
                binding.bottom.setVisibility(View.VISIBLE);//影响视频view是否整页
                binding.commentLayout.setVisibility(View.VISIBLE);//列表页面需要展示评论
                binding.viewMenu.setVisibility(View.GONE);//首页列表需要撑起标题等
            } else {
                binding.bottom.setVisibility(View.GONE);
                binding.commentLayout.setVisibility(View.GONE);
                binding.viewMenu.setVisibility(View.GONE);
            }

        } else {
            binding.bottom.setVisibility(View.GONE);
            binding.commentLayout.setVisibility(View.GONE);
            binding.viewMenu.setVisibility(View.VISIBLE);
        }

        viewModel.videoInfo.setValue(videoInfo);

        binding.commentLayout.setOnClickListener(lis -> {
            Bundle bundle = new Bundle();
            bundle.putInt("pos", position);
            bundle.putString("type", type);
            openType = 0;
            editTextOpen = true;
            startActivity(VideoEditTextDialogActivity.class, bundle);
        });

        //视频封面
        if (!TextUtils.isEmpty(videoInfo.coverUrl)) {
            try {
                String w = UrlUtils.getUrlQueryByTag(videoInfo.coverUrl, "w");
                String h = UrlUtils.getUrlQueryByTag(videoInfo.coverUrl, "h");
                if (Integer.parseInt(w) > Integer.parseInt(h)) {
                    isHP = true;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            //判单横竖屏设置不同模式封面图片加载
            if (isHP) {
                binding.coverImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            } else {
                binding.coverImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            Glide.with(getActivity()).load(videoInfo.coverUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.coverImageView);
            binding.coverImageView.setVisibility(View.VISIBLE);
        }

        if (videoInfo.resourceType.equals("VIDEO")) {
            //内容
            PublishDesBean publishDesBean = new PublishDesBean();
            publishDesBean.content = videoInfo.content;
            publishDesBean.publishFocusItemBeans = videoInfo.publishFocusItemBeans;
            binding.descTextView.setColorBlock(getResources().getColor(R.color.white));
            binding.descTextView.setColorNormal(getResources().getColor(R.color.white));
            binding.descTextView.setColorTopic(getResources().getColor(R.color.white));

            int[] textStyle = {
                    Typeface.BOLD,
                    Typeface.BOLD,
                    Typeface.NORMAL};
            binding.descTextView.setTextStyle(textStyle);
            binding.descTextView.initDesStr(publishDesBean);

            //音乐播放器旋转动画
            musicRotateAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.music_rotate_anim);

            createObjAnim();


            //音乐封面
            if (!TextUtils.isEmpty(videoInfo.musicIcon)) {
                Glide.with(getActivity()).load(videoInfo.musicIcon).diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.musicImageView);
            }

            //头像
            if (!TextUtils.isEmpty(videoInfo.avatarUrl)) {
                Glide.with(getActivity()).load(videoInfo.avatarUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.avatarImageView);
            }

            if (videoInfo.hasLive) {
                //直播头像背景动画
                avatarBgAnim = (AnimationDrawable) binding.avatarBg.getBackground();
                avatarBgAnim.start();
                //头像缩小动画
                avatarAnim = AnimUtils.getAnimation(getActivity(), R.anim.avatar_start_zb);
                //直播头像动画
                binding.rlAvatar.clearAnimation();
                binding.rlAvatar.startAnimation(avatarAnim);
            }

            initVideo();
        } else if (videoInfo.resourceType.equals("LIVE")) {
            zbingAnim = (AnimationDrawable) binding.ivZBing.getBackground();
            initLive();
        }

    }

    private void createObjAnim() {
        state = STATE_STOP;
        objectAnimator = ObjectAnimator.ofFloat(binding.rlMusicImageView, "rotation", 0f, 360f);//添加旋转动画，旋转中心默认为控件中点
        objectAnimator.setDuration(10000);//设置动画时间
        objectAnimator.setInterpolator(new LinearInterpolator());//动画时间线性渐变
        objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        objectAnimator.setRepeatMode(ObjectAnimator.RESTART);
    }

    public void playMusicAnim() {
        if (objectAnimator == null) {
            createObjAnim();
        }
        if (state == STATE_STOP) {
            objectAnimator.start();//动画开始
            binding.mainMusicalNoteLayout.start(true);
            state = STATE_PLAYING;
        } else if (state == STATE_PAUSE) {
            objectAnimator.resume();//动画重新开始
            state = STATE_PLAYING;
            binding.mainMusicalNoteLayout.start(true);
        } else if (state == STATE_PLAYING) {
            objectAnimator.pause();//动画暂停
            state = STATE_PAUSE;
            binding.mainMusicalNoteLayout.start(false);
        }
    }

    public void stopMusicAnim() {
        if (objectAnimator != null) {
            objectAnimator.end();//动画结束
            state = STATE_STOP;
        }
        binding.mainMusicalNoteLayout.start(false);
    }

    private void initVideo() {
        //判断是推荐第一条开始未滑动时候不显示加载进度
        if ((AppApplication.get().getTjPlayPosition() == -1 && position == 0 && type.equals("home-tj"))) {
            dismissLoading();
        } else {
            showLoading();
        }


        //视频播放
        vodPlayer = new TXVodPlayer(getActivity());
        //RENDER_MODE_FULL_FILL_SCREEN 将图像等比例铺满整个屏幕，多余部分裁剪掉，此模式下画面不会留黑边，但可能因为部分区域被裁剪而显示不全。
        //RENDER_MODE_ADJUST_RESOLUTION 将图像等比例缩放，适配最长边，缩放后的宽和高都不会超过显示区域，居中显示，画面可能会留有黑边。
        vodPlayer.setRenderMode(isHP ? TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION : TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);

        //RENDER_ROTATION_PORTRAIT 正常播放（Home 键在画面正下方）
        //RENDER_ROTATION_LANDSCAPE 画面顺时针旋转 270 度（Home 键在画面正左方）
        vodPlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
        vodPlayer.setVodListener(this);
        TXVodPlayConfig config = new TXVodPlayConfig();
        //缓存设置
        File sdcardDir = getActivity().getExternalFilesDir(null);
        if (sdcardDir != null) {
            config.setCacheFolderPath(sdcardDir.getAbsolutePath() + "/JKZLcache");
        }
        config.setMaxCacheItems(4);
        vodPlayer.setConfig(config);
        vodPlayer.setPlayerView(binding.mainVideoView);
        startPlay(false);
    }

    public static final float CACHE_TIME_FAST = 1.0f;
    public static final float CACHE_TIME_SMOOTH = 5.0f;

    private void initLive() {
        binding.mainVideoView.showLog(false);
        mPlayerConfig = new TXLivePlayConfig();
        mLivePlayer = new TXLivePlayer(getActivity());
        mPlayerConfig.setAutoAdjustCacheTime(true);
        mPlayerConfig.setMinAutoAdjustCacheTime(CACHE_TIME_FAST);
        mPlayerConfig.setMaxAutoAdjustCacheTime(CACHE_TIME_FAST);
        //        //缓存设置
        //        File sdcardDir = getActivity().getExternalFilesDir(null);
        //        if (sdcardDir != null) {
        //            mPlayerConfig.setCacheFolderPath(sdcardDir.getAbsolutePath() + "/JKZLcache");
        //        }
        //        mPlayerConfig.setMaxCacheItems(4);

        mLivePlayer.setConfig(mPlayerConfig);


        mLivePlayer.setPlayerView(binding.mainVideoView);
        mLivePlayer.setPlayListener(this);

        mLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
        mLivePlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
        mPlayerConfig.setEnableMessage(true);
        mLivePlayer.setConfig(mPlayerConfig);
    }

    private void showLoading() {
        //加载进度
        binding.loadingView.setVisibility(View.VISIBLE);
        binding.loadingView.playAnimation();
    }


    private double firstLikeTime;

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        disposable = RxBus.getDefault().toObservable(VideoEvent.class).subscribe(bean -> {
            if (bean != null) {
                //上下切换
                //                LogUtils.v(Constant.TAG_LIVE, "App: " + AppApplication.get().getTjPlayPosition() + "-" + type + "-" + position + "-" + bean.toString());
                if (bean.msgId == 1) {
                    showFollow();

                    stopPlay(true);
                    if (bean.fragmentId.equals("home-tj") && type.equals("home-tj")) {
                        startTjVideo();
                    }
                    if (bean.fragmentId.equals("home-gz") && type.equals("home-gz")) {
                        startGzVideo();
                    }
                    if (bean.fragmentId.equals("home-list") && type.equals("home-list")) {
                        startListVideo();
                    }

                } else if (bean.msgId == 2) {
                    //暂停播放
                    pauseMsg();
                } else if (bean.msgId == 3) {
                    //继续播放
                    resumeMsg();
                } else if (bean.msgId == 101) {//左右切换
                    stopPlay(true);
                    if ((bean.position == 0 && type.equals("home-tj"))) {
                        startTjVideo();
                    } else if ((bean.position == 1 && type.equals("home-gz"))) {
                        startGzVideo();
                    }
                } else if (bean.msgId == 10010) {//回到了顶部
                    stopPlay(true);
                    //判断
                    if (AppApplication.get().getLayoutPos() == 0 && type.equals("home-tj")) {
                        if (AppApplication.get().getTjPlayPosition() == position || (AppApplication.get().getTjPlayPosition() == -1 && position == 0)) {
                            //隐藏
                            binding.rlView.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });


        commentDisposable = RxBus.getDefault().toObservable(EventBean.class).subscribe(bean -> {
            if (bean != null) {
                if (bean.msgId == CodeTable.VIDEO_SEND_COMMENT && bean.pos == position && bean.type.equals(type)) {
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
                } else if (bean.msgId == CodeTable.VIDEO_SEND_COMMENT_OVER && bean.pos == position && bean.type.equals(type)) {
                    editTextOpen = false;
                }
            }
        });

        //双击点赞事件/暂停
        binding.mainLikeLayout.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        before_press_Y = event.getY();
                        before_press_X = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        long secondTime = System.currentTimeMillis();
                        if (secondTime - firstClickTime < 200) {
                            now_press_y = event.getY();
                            now_press_x = event.getX();
                            if (now_press_y - before_press_Y <= 50 && now_press_x - before_press_X <= 50) {
                                //双击点赞
                                handler.removeMessages(1);
                                handler.sendEmptyMessage(2);
                            }
                        } else {
                            handler.sendEmptyMessageDelayed(1, 250);
                        }

                        firstClickTime = secondTime;
                        break;

                }
                return true;
            }
        });
        binding.rlLike.setOnClickListener(lis -> {

            if (AppApplication.isToLogin()) {
                return;
            }

            long secondTime = System.currentTimeMillis();
            if (secondTime - firstLikeTime < 500) {
                return;
            }

            viewModel.toLikeVideo(videoInfo.resourceId, !videoInfo.hasFavorite, position);

            firstLikeTime = secondTime;
        });

        binding.avatarImageView.setOnClickListener(lis -> {
            if (AppApplication.isToLogin()) {
                return;
            }

            Bundle bundle = new Bundle();
            bundle.putString("userId", videoInfo.userId);
            startActivity(UserInfoActivity.class, bundle);
        });
        //关注
        binding.followImageView.setOnClickListener(lis -> {
            //先判断是否登录
            if (!InfoCache.getInstance().checkLogin()) {
                startActivity(SelectLoginActivity.class);
                return;
            }

            avatarAddAnim = (AnimationDrawable) binding.followImageView.getBackground();
            avatarAddAnim.start();
            //缓存关注列表 用户视频列表中判断
            AppApplication.get().followList.put(videoInfo.userId, true);
            viewModel.toFollow(videoInfo.userId);
            videoInfo.hasAttention = !videoInfo.hasAttention;
        });
        binding.ivComment.setOnClickListener(lis -> {
            showCommentDialog(videoInfo.resourceId);
        });
        binding.ivShare.setOnClickListener(lis -> {
            showShareDialog(videoInfo);
        });
        viewModel.commentSuccess.observe(this, commentListBean -> {
            if (commentListBean != null && commentDialog != null) {

                //本地刷新
                if (openType == 0) {
                    //往评论列表查插数据
                    commentDialog.addMCommentData(commentListBean);
                } else if (openType == 1 || openType == 2) {
                    //往回复列表查插数据
                    commentDialog.addMReplyData(commentListBean);
                }

                openType = 0;
            }
        });

        binding.rlClick.setOnClickListener(lis -> {
            //判断是直播间
            if (videoInfo.getVideoType() == 2) {
                dismissLoading();

                //进入直播间
                viewModel.joinLive(videoInfo.liveRoomId);
            }
        });
        binding.llZhiBoTip.setOnClickListener(lis -> {
            //判断是直播间
            if (videoInfo.getVideoType() == 2) {
                dismissLoading();

                //进入直播间
                viewModel.joinLive(videoInfo.liveRoomId);
            }
        });
        viewModel.liveInfo.observe(this, liveInfo -> {
            if (liveInfo != null) {

                if (liveInfo.liveRoomStatus.equals("LIVE_OVER")) {
                    //直播结束 直接跳转结束页面
                    //跳转结算页面
                    Bundle bundle = new Bundle();
                    bundle.putString("liveRoomRecordId", liveInfo.liveRoomRecordId);
                    bundle.putString("liveRoomId", videoInfo.liveRoomId);
                    startActivity(ZhiboOverGZActivity.class, bundle);
                } else {
                    //进入直播间
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constant.LiveInitInfo, liveInfo);
                    startActivity(LiveGuanzhongActivity.class, bundle);
                }

            }
        });
        viewModel.dismissDialog.observe(this, dismiss -> {
            if (dismiss != null) {
                if (dismiss) {
                    dismissDialog();
                }
            }
        });

        viewModel.dzSuccess.observe(this, dzRes -> {
            if (dzRes != null) {
                if (dzRes.isSuccess) {
                    videoInfo.hasFavorite = !videoInfo.hasFavorite;
                    if (videoInfo.hasFavorite) {
                        videoInfo.favoriteCount = String.valueOf(videoInfo.getFavoriteCount() + 1);
                    } else {
                        videoInfo.favoriteCount = String.valueOf(videoInfo.getFavoriteCount() - 1);
                    }
                    viewModel.videoInfo.setValue(videoInfo);
                }
            }
        });
    }

    //判断自己账号隐藏关注
    private void showFollow() {

        Boolean aBoolean = AppApplication.get().followList.get(videoInfo.userId);
        if (aBoolean != null) {
            videoInfo.hasAttention = aBoolean;
        }

        if (!videoInfo.showFollow()) {
            binding.followImageView.setVisibility(View.GONE);
        } else {
            binding.followImageView.setVisibility(View.VISIBLE);
        }
    }


    private void dismissLoading() {
        if (binding.loadingView.isAnimating() || binding.loadingView.getVisibility() == View.VISIBLE) {
            binding.loadingView.cancelAnimation();
            binding.loadingView.setVisibility(View.GONE);
        }
    }


    //分享弹窗
    private ShareDialog shareDialog;

    private void showShareDialog(VideoInfo videoInfo) {
        if (shareDialog == null)
            videoInfo.share.downUrl = videoInfo.resourceUrl;
        shareDialog = new ShareDialog(getActivity(), videoInfo.share, 0);
        shareDialog.show(binding.mainRelativeLayout);
    }

    //评论弹窗
    private CommentDialog commentDialog;

    private int openType;

    private void showCommentDialog(String videoId) {
        if (commentDialog == null)
            commentDialog = new CommentDialog(getContext(), videoId, getActivity());
        commentDialog.setOnChildClick(new CommentDialog.OnChildClick() {
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
            public void toCommentVideo(String videoId) {
                openType = 0;
                commentId = videoId;
                //评论视频本体
                showPublishCommentDialog("说点什么吧");
            }

            @Override
            public void toLike(CommentListBean iCommentBean) {
            }

            @Override
            public void toUser(CommentListBean iCommentBean) {
            }
        });
        commentDialog.show(binding.mainRelativeLayout, videoInfo.commentCount);
    }

    /**
     * 发表评论
     */
    private void showPublishCommentDialog(String nickName) {
        if (AppApplication.get().isToLogin())
            return;

        Bundle bundle = new Bundle();
        bundle.putString("hint", nickName);
        bundle.putInt("pos", position);
        bundle.putString("type", type);
        editTextOpen = true;
        startActivity(VideoEditTextDialogActivity.class, bundle);
    }


    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    clickPauseVideo();
                    break;
                case 2:
                    double2DianZan();
                    break;
            }
        }
    };

    //双击点赞
    private double before_press_Y;
    private double before_press_X;
    private double now_press_y;
    private double now_press_x;
    private long firstClickTime;
    private float[] num = {-30, -20, 0, 20, 30};//随机路径

    private void double2DianZan() {
        ImageView iv = new ImageView(getActivity());
        iv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_video_like));

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(150, 150);
        lp.leftMargin = (int) now_press_x - 100;
        lp.topMargin = (int) now_press_y - 200;

        iv.setLayoutParams(lp);
        iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        try {
            binding.mainLikeLayout.addView(iv);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //双击点赞动画
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(AnimUtil.scale(iv, "scaleX", 1.5f, 0.9f, 100, 0))
                .with(AnimUtil.scale(iv, "scaleY", 1.5f, 0.9f, 100, 0))
                .with(AnimUtil.rotation(iv, 0, 0, num[new Random().nextInt(4)]))
                .with(AnimUtil.alpha(iv, 0, 1, 100, 0))
                .with(AnimUtil.scale(iv, "scaleX", 0.9f, 1, 50, 150))
                .with(AnimUtil.scale(iv, "scaleY", 0.9f, 1, 50, 150))
                .with(AnimUtil.translationY(iv, 0, -600, 800, 400))
                .with(AnimUtil.alpha(iv, 1, 0, 300, 400))
                .with(AnimUtil.scale(iv, "scaleX", 1, 1.5f, 700, 400))
                .with(AnimUtil.scale(iv, "scaleY", 1, 1.5f, 700, 400));
        animatorSet.start();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //只移除当前的ImageView
                binding.mainLikeLayout.removeView(iv);
            }
        });

    }

    private void startTjVideo() {
        if (AppApplication.get().getTjPlayPosition() == position) {
            startPlay(true);
        }
    }

    private void startGzVideo() {
        if (AppApplication.get().getGzPlayPosition() == position) {
            startPlay(true);
        }
    }

    private void startListVideo() {
        if (AppApplication.get().getPlayPosition() == position) {
            startPlay(true);
        }
    }

    private void startPlay(boolean b) {

        if (binding.rlView.getVisibility() == View.GONE) {
            binding.rlView.setVisibility(View.VISIBLE);
        }
        if (videoInfo.getVideoType() == 2) {
            zbingAnim.start();
            if (mLivePlayer != null) {
                mLivePlayer.startPlay(videoInfo.resourceUrl, TXLivePlayer.PLAY_TYPE_LIVE_RTMP);
            }
        } else {
            if (vodPlayer != null) {
                vodPlayer.setAutoPlay(b);
                vodPlayer.startPlay(videoInfo.resourceUrl);
            }

        }

        LogUtils.v(Constant.TAG_LIVE, "---------------------startPlay" + position);
    }

    private void clickPauseVideo() {
        //单击暂停
        if (videoInfo.getVideoType() == 1) {
            if (hasPlaying) {
                noPlayPause = !noPlayPause;
                if (noPlayPause) {
                    vodPlayer.resume();
                    binding.playImageView.setVisibility(View.GONE);
                } else {
                    vodPlayer.pause();
                    binding.playImageView.setVisibility(View.VISIBLE);
                }
                playMusicAnim();
            }
        }

    }

    private void pausePlay() {
        binding.mainVideoView.onPause();

            if (videoInfo.getVideoType() == 2) {
                if (mLivePlayer != null) {
                    mLivePlayer.pause();
                }
            } else {
                if (vodPlayer != null) {
                    vodPlayer.pause();
                }
            }
        LogUtils.v(Constant.TAG_LIVE, "---------------------pausePlay" + position);
    }

    private void resumePlay() {
        if (AppApplication.get().bottomMenu == 0) {
            binding.mainVideoView.onResume();

            //是否开启播放状态
            if (hasPlaying) {
                //判断之前不是暂停状态
                if (noPlayPause) {
                    if (videoInfo.getVideoType() == 2) {
                        if (mLivePlayer != null) {
                            mLivePlayer.resume();
                        }
                    } else {
                        if (vodPlayer != null) {
                            vodPlayer.resume();
                        }
                    }
                }
            } else {
                if (!AppApplication.get().isShowTopMenu()) {
                    startPlay(true);
                }
            }
        }

        LogUtils.v(Constant.TAG_LIVE, "---------------------resumePlay" + position);

    }

    private void stopPlay(boolean clearLastFrame) {
        if (videoInfo.getVideoType() == 2) {
            if (zbingAnim != null) {
                zbingAnim.stop();
            }
            if (mLivePlayer != null) {
                mLivePlayer.stopPlay(false);
            }
        } else {
            stopMusicAnim();
            binding.playImageView.setVisibility(View.GONE);

            if (vodPlayer != null) {
                vodPlayer.stopPlay(clearLastFrame);
            }
        }


        binding.coverImageView.setVisibility(View.VISIBLE);//--展示
        hasPlaying = false;
        noPlayPause = false;


        LogUtils.v(Constant.TAG_LIVE, "---------------------stopPlay" + position);

    }

    @Override
    public void onPause() {
        super.onPause();
        pauseMsg();
    }

    public void pauseMsg() {
        if (hasPlaying && !editTextOpen) {
            pausePlay();
        }
    }

    public void resumeMsg() {
        if (!editTextOpen) {
            if (type.equals("home-tj")) {
                if (AppApplication.get().getTjPlayPosition() == position) {
                    resumePlay();
                } else {
                    pausePlay();
                }
            }

            if (type.equals("home-gz")) {
                if (AppApplication.get().getGzPlayPosition() == position) {
                    resumePlay();
                } else {
                    pausePlay();
                }
            }

            if (type.equals("home-list")) {
                if (AppApplication.get().getPlayPosition() == position) {
                    resumePlay();
                } else {
                    pausePlay();
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        resumeMsg();
        showFollow();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (avatarAnim != null) {
            avatarAnim.cancel();
            avatarAnim = null;
        }
        if (avatarBgAnim != null) {
            avatarBgAnim.stop();
            avatarBgAnim = null;
        }
        if (musicRotateAnim != null) {
            musicRotateAnim.cancel();
            musicRotateAnim = null;
        }
        if (commentDisposable != null) {
            commentDisposable.dispose();
        }

        binding.mainVideoView.onDestroy();
        stopPlay(true);
        TelephonyUtil.getInstance().uninitPhoneListener();
        disposable.dispose();
        handler.removeCallbacksAndMessages(null);
    }

    //视频播放器
    @Override
    public void onPlayEvent(TXVodPlayer txVodPlayer, int event, Bundle param) {
        if (event == TXLiveConstants.PLAY_EVT_CHANGE_RESOLUTION) {
            //param.getInt(TXLiveConstants.EVT_PARAM1); //视频宽度
            //param.getInt(TXLiveConstants.EVT_PARAM2); //视频高度
        } else if (event == TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME) {// 收到首帧数据，越快收到此消息说明链路质量越好
            LogUtils.v(Constant.TAG_LIVE, type + position + "PLAY_EVT_RCV_FIRST_I_FRAME");
        } else if (event == TXLiveConstants.PLAY_EVT_VOD_PLAY_PREPARED) {//播放器已准备完成,可以播放
            LogUtils.v(Constant.TAG_LIVE, type + position + "PLAY_EVT_VOD_PLAY_PREPARED");
            dismissLoading();
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_LOADING) {//视频播放loading，如果能够恢复，之后会有BEGIN事件
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {//视频播放开始
            LogUtils.v(Constant.TAG_LIVE, type + position + "PLAY_EVT_PLAY_BEGIN");
            showStartLayout();
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_PROGRESS) {//视频播放进度，会通知当前进度和总体进度，仅在点播时有效
            //第一次播放时候先会出现进度不会先走PLAY_EVT_PLAY_BEGIN
            //EVT_PLAY_DURATION 总时间  EVT_PLAY_PROGRESS 当前进度
            //有此回调说明是点播
            if (param.getInt(TXLiveConstants.EVT_PLAY_PROGRESS_MS) > 0) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.progressBar.setMax(param.getInt(TXLiveConstants.EVT_PLAY_DURATION_MS));
                binding.progressBar.setProgress(param.getInt(TXLiveConstants.EVT_PLAY_PROGRESS_MS));
                showStartLayout();
            }

        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_END) {//视频播放结束
            resumePlay();
        } else if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT) {//网络断连,且经多次重连抢救无效,可以放弃治疗,更多重试请自行重启播放
            LogUtils.v(Constant.TAG_LIVE, type + position + "PLAY_ERR_NET_DISCONNECT");
        }

    }

    private void showStartLayout() {

        if (hasPlaying) {
            return;
        }

        binding.coverImageView.setVisibility(View.GONE);

        dismissLoading();
        if (videoInfo.getVideoType() == 1) {
            //开启音乐Icon动画
            if (musicRotateAnim != null) {
                playMusicAnim();
            }
        } else {
            zbingAnim.start();
        }
        hasPlaying = true;
        noPlayPause = true;
    }

    @Override
    public void onNetStatus(TXVodPlayer txVodPlayer, Bundle bundle) {

    }

    @Override
    public void onRinging() {
        if (videoInfo.getVideoType() == 2) {
            if (mLivePlayer != null) {
                mLivePlayer.setMute(true);
            }
        } else {
            if (vodPlayer != null) {
                vodPlayer.setMute(true);
            }
        }


    }

    @Override
    public void onOffhook() {
        if (videoInfo.getVideoType() == 2) {
            if (mLivePlayer != null) {
                mLivePlayer.setMute(true);
            }
        } else {
            if (vodPlayer != null) {
                vodPlayer.setMute(true);
            }
        }
    }

    @Override
    public void onIdle() {
        if (videoInfo.getVideoType() == 2) {
            if (mLivePlayer != null) {
                mLivePlayer.setMute(false);
            }
        } else {
            if (vodPlayer != null) {
                vodPlayer.setMute(false);
            }
        }
    }

    //直播播放器
    @Override
    public void onPlayEvent(int event, Bundle param) {
        if (event == TXLiveConstants.PLAY_EVT_CHANGE_RESOLUTION) {
            //param.getInt(TXLiveConstants.EVT_PARAM1); //视频宽度
            //param.getInt(TXLiveConstants.EVT_PARAM2); //视频高度
        } else if (event == TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME) {// 收到首帧数据，越快收到此消息说明链路质量越好
            LogUtils.v(Constant.TAG_LIVE, type + position + "PLAY_EVT_RCV_FIRST_I_FRAME");
        } else if (event == TXLiveConstants.PLAY_EVT_VOD_PLAY_PREPARED) {//播放器已准备完成,可以播放
            LogUtils.v(Constant.TAG_LIVE, type + position + "PLAY_EVT_VOD_PLAY_PREPARED");
            showStartLayout();
        } else if (event == TXLiveConstants.PLAY_EVT_RTMP_STREAM_BEGIN) {//已经连接服务器，开始拉流（仅播放 RTMP 地址时会抛送）
            LogUtils.v(Constant.TAG_LIVE, type + position + "PLAY_EVT_RTMP_STREAM_BEGIN");
            showStartLayout();
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {//视频播放开始
            LogUtils.v(Constant.TAG_LIVE, type + position + "PLAY_EVT_PLAY_BEGIN");
            showStartLayout();
        } else if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT) {//网络断连,且经多次重连抢救无效,可以放弃治疗,更多重试请自行重启播放
            LogUtils.v(Constant.TAG_LIVE, type + position + "PLAY_ERR_NET_DISCONNECT");
        }
        LogUtils.v(Constant.TAG_LIVE, type + position + "XXXXXXXXXXXXXXXX" + event);

    }

    @Override
    public void onNetStatus(Bundle bundle) {

    }
}
