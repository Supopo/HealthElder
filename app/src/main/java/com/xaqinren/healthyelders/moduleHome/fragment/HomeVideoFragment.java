package com.xaqinren.healthyelders.moduleHome.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
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
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.ViewSkeletonScreen;
import com.tencent.qcloud.ugckit.utils.TelephonyUtil;
import com.tencent.qcloud.ugckit.utils.ToastUtil;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.ITXVodPlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.TXVodPlayConfig;
import com.tencent.rtmp.TXVodPlayer;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.MainActivity;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.FragmentHomeVideoBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.global.InfoCache;
import com.xaqinren.healthyelders.moduleHome.bean.CommentListBean;
import com.xaqinren.healthyelders.moduleHome.bean.VideoEvent;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleHome.viewModel.HomeVideoModel;
import com.xaqinren.healthyelders.moduleLiteav.bean.LiteAvUserBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishDesBean;
import com.xaqinren.healthyelders.moduleLogin.activity.PhoneLoginActivity;
import com.xaqinren.healthyelders.moduleLogin.activity.SelectLoginActivity;
import com.xaqinren.healthyelders.moduleMine.activity.UserInfoActivity;
import com.xaqinren.healthyelders.moduleZhiBo.activity.LiveGuanzhongActivity;
import com.xaqinren.healthyelders.moduleZhiBo.activity.VideoEditTextDialogActivity;
import com.xaqinren.healthyelders.moduleZhiBo.activity.ZhiboOverGZActivity;
import com.xaqinren.healthyelders.utils.AnimUtil;
import com.xaqinren.healthyelders.utils.AnimUtils;
import com.xaqinren.healthyelders.utils.GlideUtil;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.utils.Num2TextUtil;
import com.xaqinren.healthyelders.utils.UrlUtils;
import com.xaqinren.healthyelders.widget.InputPwdDialog;
import com.xaqinren.healthyelders.widget.LiteAvOpenModePopupWindow;
import com.xaqinren.healthyelders.widget.YesOrNoDialog;
import com.xaqinren.healthyelders.widget.comment.CommentDialog;
import com.xaqinren.healthyelders.widget.share.ShareDialog;

import java.io.File;
import java.util.List;
import java.util.Random;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.AppManager;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.bus.RxBus;

/**
 * Created by Lee. on 2021/5/13.
 * ????????????Fragment
 */
public class HomeVideoFragment extends BaseFragment<FragmentHomeVideoBinding, HomeVideoModel> implements TelephonyUtil.OnTelephoneListener, ITXLivePlayListener, ITXVodPlayListener {
    private VideoInfo videoInfo;
    private int position;
    private String type;//??????????????????????????? home-tj home-gz home-list
    private TXVodPlayer vodPlayer;
    private Disposable disposable;
    private Animation musicRotateAnim;//??????Icon????????????
    private Animation avatarAnim;//????????????????????????
    private AnimationDrawable avatarBgAnim;//?????????????????????
    private ObjectAnimator objectAnimator;//??????Icon???????????? ?????????
    private int playStatus;//?????????????????? 0????????? 1?????? 2????????????
    private boolean hasPlaying;//????????????????????? ?????????????????????????????????????????????????????????????????????????????????
    private AnimationDrawable avatarAddAnim;
    private AnimationDrawable zbingAnim;

    private String commentId;//???????????????ID ?????????id????????????id??????
    private CommentListBean mCommentListBean;//??????????????????
    private String TAG = getClass().getSimpleName();
    private Disposable commentDisposable;
    private boolean editTextOpen;//?????????????????????EditTextActivity
    private TXLivePlayConfig mPlayerConfig;
    private TXLivePlayer mLivePlayer;
    private boolean isMineOpen;
    private int videoOpenType;//1-???????????? 2-????????? 3-????????????
    private long timeTag;//??????list-video???????????????????????????????????????????????????????????????????????????????????????????????????????????????
    private ViewSkeletonScreen skeletonScreen1;
    private int mRenderMode;
    private LiteAvOpenModePopupWindow openModePop;
    private int publishMode;
    private InputPwdDialog pwdDialog;
    private String commentText;
    private boolean isCaching;//???????????????

    //Unable to instantiate fragment xxx: could not find Fragment constructor
    //??????Fragment???????????????????????????????????????????????????????????????????????????????????????????????????????????????
    //Fragment?????????????????????public??????????????????
    public HomeVideoFragment() {
    }

    public HomeVideoFragment(VideoInfo videoInfo, String type, int position, boolean isMineOpen) {
        this.videoInfo = videoInfo;
        this.type = type;
        this.position = position;
        this.isMineOpen = isMineOpen;
        if (videoInfo.resourceType.equals(Constant.REQ_TAG_SP)) {
            videoInfo.oldResourceUrl = videoInfo.resourceUrl;
            videoInfo.resourceUrl = Constant.setVideoSigUrl(videoInfo.resourceUrl);
            Log.v(Constant.TAG_LIVE, videoInfo.resourceUrl);
        }
    }

    public HomeVideoFragment(VideoInfo videoInfo, String type, int position, boolean isMineOpen, int videoOpenType, long timeTag) {
        this.videoInfo = videoInfo;
        this.type = type;
        this.position = position;
        this.isMineOpen = isMineOpen;
        this.videoOpenType = videoOpenType;
        this.timeTag = timeTag;
        if (videoInfo != null && !TextUtils.isEmpty(videoInfo.resourceType) && videoInfo.resourceType.equals(Constant.REQ_TAG_SP)) {
            videoInfo.oldResourceUrl = videoInfo.resourceUrl;
            videoInfo.resourceUrl = Constant.setVideoSigUrl(videoInfo.resourceUrl);
            Log.v(Constant.TAG_LIVE, videoInfo.resourceUrl);
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


    public static final int STATE_PLAYING = 1;//????????????
    public static final int STATE_PAUSE = 2;//??????
    public static final int STATE_STOP = 3;//??????
    public int state;

    private boolean isHP;

    @SuppressLint("ObjectAnimatorBinding")
    @Override
    public void initData() {
        super.initData();
        TelephonyUtil.getInstance().setOnTelephoneListener(this);
        TelephonyUtil.getInstance().initPhoneListener();

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        if (AppApplication.get().getLayoutPos() == 0 && AppApplication.get().getTjPlayPosition() == -1) {
            //???????????????
            showSkeleton1();
            binding.rlView.setVisibility(View.GONE);
        } else {
            binding.rlView.setVisibility(View.VISIBLE);
        }

        //????????????
        if (videoInfo == null || TextUtils.isEmpty(videoInfo.resourceId)) {
            return;
        }
        commentId = videoInfo.resourceId;
        if (type.equals("home-list")) {
            if (videoInfo.getVideoType() == 1) {
                binding.bottom.setVisibility(View.VISIBLE);//????????????view????????????
                if (isMineOpen) {
                    if (videoOpenType == 3) {
                        binding.commentLayout.setVisibility(View.VISIBLE);//??????????????????????????????
                        binding.llSetting.setVisibility(View.GONE);
                        binding.ivShare.setBackground(getActivity().getResources().getDrawable(R.mipmap.icon_play_share));
                    } else {
                        binding.commentLayout.setVisibility(View.GONE);//??????????????????????????????
                        binding.llSetting.setVisibility(View.VISIBLE);
                        binding.tvShareNum.setVisibility(View.GONE);
                        binding.ivShare.setBackground(getActivity().getResources().getDrawable(R.mipmap.icon_video_more));
                    }

                } else {
                    binding.commentLayout.setVisibility(View.VISIBLE);//??????????????????????????????
                    binding.llSetting.setVisibility(View.GONE);
                    binding.ivShare.setBackground(getActivity().getResources().getDrawable(R.mipmap.icon_play_share));
                }
                binding.viewMenu.setVisibility(View.GONE);//?????????????????????????????????
            } else {
                binding.bottom.setVisibility(View.GONE);
                binding.llSetting.setVisibility(View.GONE);
                binding.commentLayout.setVisibility(View.GONE);
                binding.viewMenu.setVisibility(View.GONE);
            }

        } else {
            binding.bottom.setVisibility(View.GONE);
            binding.llSetting.setVisibility(View.GONE);
            binding.commentLayout.setVisibility(View.GONE);
            binding.viewMenu.setVisibility(View.VISIBLE);
        }

        viewModel.videoInfo.setValue(videoInfo);

        binding.commentLayout.setOnClickListener(lis -> {
            Bundle bundle = new Bundle();
            bundle.putInt("pos", position);
            bundle.putString("type", type);
            bundle.putString("commentText", commentText);
            commentType = 0;
            editTextOpen = true;
            startActivity(VideoEditTextDialogActivity.class, bundle);
        });
        binding.ivFace.setOnClickListener(lis -> {
            Bundle bundle = new Bundle();
            bundle.putInt("pos", position);
            bundle.putString("type", type);
            bundle.putInt("openType", 1);
            commentType = 0;
            editTextOpen = true;
            startActivity(VideoEditTextDialogActivity.class, bundle);
        });

        //????????????
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


            //??????????????????????????????
            if (videoInfo.resourceType.equals("LIVE") && videoInfo.hasPassword) {
                GlideUtil.intoGaoSiImageView(getActivity(), videoInfo.coverUrl, binding.coverImageView, 25);
                binding.ivZbJm.setVisibility(View.VISIBLE);
            } else {
                GlideUtil.intoImageView(getActivity(), videoInfo.coverUrl, binding.coverImageView);
            }

            //??????????????????????????????????????????????????? ??????????????????CenterInside
            if (!isHP) {
                binding.coverImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }

            binding.coverImageView.setVisibility(View.VISIBLE);
        }

        if (videoInfo.resourceType.equals("VIDEO")) {
            //??????
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

            //???????????????????????????
            musicRotateAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.music_rotate_anim);

            createObjAnim();


            //????????????
            if (!TextUtils.isEmpty(videoInfo.musicIcon)) {
                Glide.with(getActivity()).load(videoInfo.musicIcon).diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.musicImageView);
            }

            //??????
            if (!TextUtils.isEmpty(videoInfo.avatarUrl)) {
                Glide.with(getActivity()).load(videoInfo.avatarUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.avatarImageView);
            }

            if (videoInfo.hasLive) {
                //????????????????????????
                avatarBgAnim = (AnimationDrawable) binding.avatarBg.getBackground();
                avatarBgAnim.start();
                //??????????????????
                avatarAnim = AnimUtils.getAnimation(getActivity(), R.anim.avatar_start_zb);
                //??????????????????
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
        objectAnimator = ObjectAnimator.ofFloat(binding.rlMusicImageView, "rotation", 0f, 360f);//??????????????????????????????????????????????????????
        objectAnimator.setDuration(10000);//??????????????????
        objectAnimator.setInterpolator(new LinearInterpolator());//????????????????????????
        objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        objectAnimator.setRepeatMode(ObjectAnimator.RESTART);
    }

    public void playMusicAnim() {
        if (objectAnimator == null) {
            createObjAnim();
        }
        if (state == STATE_STOP) {
            objectAnimator.start();//????????????
            binding.mainMusicalNoteLayout.start(true);
            state = STATE_PLAYING;
        } else if (state == STATE_PAUSE) {
            objectAnimator.resume();//??????????????????
            state = STATE_PLAYING;
            binding.mainMusicalNoteLayout.start(true);
        } else if (state == STATE_PLAYING) {
            objectAnimator.pause();//????????????
            state = STATE_PAUSE;
            binding.mainMusicalNoteLayout.start(false);
        }
    }

    public void stopMusicAnim() {
        if (objectAnimator != null) {
            getActivity().runOnUiThread(() -> {
                objectAnimator.end();//????????????
                state = STATE_STOP;
            });
        }
        binding.mainMusicalNoteLayout.start(false);
    }

    private void initVideo() {
        //??????????????????????????????????????????????????????????????????
        if ((AppApplication.get().getTjPlayPosition() == -1 && position == 0 && type.equals("home-tj"))) {
            dismissLoading();
        } else {
            showLoading();
        }

        //????????????
        vodPlayer = new TXVodPlayer(getActivity());
        //RENDER_MODE_FULL_FILL_SCREEN ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        //RENDER_MODE_ADJUST_RESOLUTION ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        vodPlayer.setRenderMode(isHP ? TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION : TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);

        //RENDER_ROTATION_PORTRAIT ???????????????Home ????????????????????????
        //RENDER_ROTATION_LANDSCAPE ????????????????????? 270 ??????Home ????????????????????????
        vodPlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
        vodPlayer.setVodListener(this);
        TXVodPlayConfig config = new TXVodPlayConfig();
        //????????????
        File sdcardDir = getActivity().getCacheDir();
        if (sdcardDir != null) {
            config.setCacheFolderPath(sdcardDir.getAbsolutePath() + "/JKZLcache");
        }
        config.setMaxCacheItems(10);
        vodPlayer.setConfig(config);
        vodPlayer.setPlayerView(binding.mainVideoView);
        isCaching = true;
        startPlay(false);
    }

    public static final float CACHE_TIME_FAST = 1.0f;
    public static final float CACHE_TIME_SMOOTH = 5.0f;

    private void initLive() {
        //??????????????????????????????????????????????????????????????????
        if ((AppApplication.get().getTjPlayPosition() == -1 && position == 0 && type.equals("home-tj"))) {
            dismissLoading();
        } else {
            showLoading();
        }


        binding.mainVideoView.showLog(false);
        mPlayerConfig = new TXLivePlayConfig();
        mLivePlayer = new TXLivePlayer(getActivity());
        mPlayerConfig.setAutoAdjustCacheTime(true);
        mPlayerConfig.setMinAutoAdjustCacheTime(CACHE_TIME_FAST);
        mPlayerConfig.setMaxAutoAdjustCacheTime(CACHE_TIME_FAST);

        mLivePlayer.setConfig(mPlayerConfig);


        mLivePlayer.setPlayerView(binding.mainVideoView);
        mLivePlayer.setPlayListener(this);

        mLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
        mLivePlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
        mPlayerConfig.setEnableMessage(true);
        mLivePlayer.setConfig(mPlayerConfig);
    }

    private void showLoading() {
        //????????????
        binding.rlLoadingView.setVisibility(View.VISIBLE);
        binding.loadingView.playAnimation();
    }


    private double firstLikeTime;

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        disposable = RxBus.getDefault().toObservable(VideoEvent.class).subscribe(bean -> {
            if (bean != null) {
                //????????????
                LogUtils.v(Constant.TAG_LIVE, "App: " + AppApplication.get().getTjPlayPosition() + "-" + type + "-" + position + "-" + bean.toString());
                if (bean.msgId == 1) {
                    showFollow();

                    //??????????????????????????????????????????????????????
                    if (!isCaching) {
                        stopPlay(false);
                    }

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
                    //????????????
                    pauseMsg();
                } else if (bean.msgId == 3) {
                    //????????????
                    resumeMsg();
                } else if (bean.msgId == 101) {//????????????
                    stopPlay(true);

                    if ((bean.position == 0 && type.equals("home-tj"))) {
                        startTjVideo();
                    } else if ((bean.position == 1 && type.equals("home-gz"))) {
                        startGzVideo();
                    }
                } else if (bean.msgId == 10010) {//???????????????
                    stopPlay(true);
                    //??????
                    if (AppApplication.get().getLayoutPos() == 0 && type.equals("home-tj")) {
                        if (AppApplication.get().getTjPlayPosition() == position || (AppApplication.get().getTjPlayPosition() == -1 && position == 0)) {
                            //??????
                            binding.rlView.setVisibility(View.GONE);
                        }
                    }
                } else if (bean.msgId == 10011) {//???????????????
                    //?????????????????????????????????????????????
                    dismissLoading();
                    //??????
                    if (AppApplication.get().getLayoutPos() == 0 && type.equals("home-tj")) {
                        if (AppApplication.get().getTjPlayPosition() == position || (AppApplication.get().getTjPlayPosition() == 0 && position == 0)) {
                            //??????
                            binding.rlView.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });


        commentDisposable = RxBus.getDefault().toObservable(EventBean.class).subscribe(bean -> {
            if (bean != null) {
                if (bean.msgId == CodeTable.VIDEO_SEND_COMMENT && bean.pos == position && bean.type.equals(type)) {
                    String content = bean.content;
                    if (commentType == 0) {
                        //????????????
                        viewModel.toComment(commentId, content);
                    } else if (commentType == 1) {
                        //????????????
                        viewModel.toCommentReply(mCommentListBean, content, 0);
                    } else if (commentType == 2) {
                        //????????????
                        viewModel.toCommentReply(mCommentListBean, content, 1);
                    }
                } else if (bean.msgId == CodeTable.VIDEO_SEND_COMMENT_OVER && bean.pos == position && bean.type.equals(type)) {
                    commentText = bean.content;
                    editTextOpen = false;
                }
            }
        });

        //??????????????????/??????
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
                                //????????????
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

            //?????????????????????
            if (!InfoCache.getInstance().checkLogin()) {
                startActivity(SelectLoginActivity.class);
                return;
            }
            if (UserInfoMgr.getInstance().getUserInfo() == null) {
                startActivity(SelectLoginActivity.class);
                return;
            }

            //????????????????????????
            if (!UserInfoMgr.getInstance().getUserInfo().hasMobileNum()) {
                startActivity(PhoneLoginActivity.class);
                return;
            }

            long secondTime = System.currentTimeMillis();
            if (secondTime - firstLikeTime < 500) {
                return;
            }

            viewModel.toLikeVideo(videoInfo.resourceId, !videoInfo.hasFavorite, position);

            firstLikeTime = secondTime;
        });

        binding.authorTextView.setOnClickListener(lis -> {
            if (AppApplication.isToLogin()) {
                return;
            }

            //????????????????????????????????????????????????????????????????????? ????????????
            if (videoOpenType != 0 && videoInfo.userId.equals(UserInfoMgr.getInstance().getUserInfo().getId())) {
                //?????????????????????
                RxBus.getDefault().post(new EventBean(CodeTable.FINISH_ACT, "video-list"));
                return;
            }

            Bundle bundle = new Bundle();
            bundle.putString("userId", videoInfo.userId);
            startActivity(UserInfoActivity.class, bundle);


        });


        binding.authorTextView2.setOnClickListener(lis -> {
            if (AppApplication.isToLogin()) {
                return;
            }

            //????????????????????????????????????????????????????????????????????? ????????????
            if (videoOpenType != 0 && videoInfo.userId.equals(UserInfoMgr.getInstance().getUserInfo().getId())) {
                //?????????????????????
                RxBus.getDefault().post(new EventBean(CodeTable.FINISH_ACT, "video-list"));
                return;
            }


            //????????????????????????????????????
            Bundle bundle = new Bundle();
            bundle.putString("userId", videoInfo.userId);
            startActivity(UserInfoActivity.class, bundle);

        });


        binding.avatarImageView.setOnClickListener(lis -> {
            if (AppApplication.isToLogin()) {
                return;
            }

            //????????????????????????????????????????????????????????????????????? ????????????
            if (videoOpenType != 0 && videoInfo.userId.equals(UserInfoMgr.getInstance().getUserInfo().getId())) {
                //?????????????????????
                RxBus.getDefault().post(new EventBean(CodeTable.FINISH_ACT, "video-list"));
                return;
            }


            //????????????????????????????????????
            if (videoInfo.hasLive) {
                //?????????????????????????????????????????????
                if (videoInfo.hasPassword) {
                    pwdDialog = new InputPwdDialog(getActivity());
                    pwdDialog.setRightBtnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (TextUtils.isEmpty(pwdDialog.code)) {
                                ToastUtil.toastShortMessage("???????????????");
                                return;
                            }
                            viewModel.joinLive(videoInfo.liveRoomId, pwdDialog.code);
                            pwdDialog.dismissDialog();
                        }
                    });
                    pwdDialog.showDialog();
                } else {
                    viewModel.joinLive(videoInfo.liveRoomId, "");
                }
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("userId", videoInfo.userId);
                startActivity(UserInfoActivity.class, bundle);
            }


        });
        //??????
        binding.followImageView.setOnClickListener(lis -> {
            //?????????????????????
            if (!InfoCache.getInstance().checkLogin()) {
                startActivity(SelectLoginActivity.class);
                return;
            }
            if (UserInfoMgr.getInstance().getUserInfo() == null) {
                startActivity(SelectLoginActivity.class);
                return;
            }
            //????????????????????????
            if (!UserInfoMgr.getInstance().getUserInfo().hasMobileNum()) {
                startActivity(PhoneLoginActivity.class);
                return;
            }

            avatarAddAnim = (AnimationDrawable) binding.followImageView.getBackground();
            avatarAddAnim.start();
            //?????????????????? ???????????????????????????
            AppApplication.get().followList.put(videoInfo.userId, true);
            viewModel.toFollow(videoInfo.userId);
            videoInfo.hasAttention = !videoInfo.hasAttention;
        });
        binding.ivComment.setOnClickListener(lis -> {
            showCommentDialog(videoInfo.resourceId);
        });
        binding.ivShare.setOnClickListener(lis -> {
            //??????
            showShareDialog(videoInfo);
        });
        //????????????????????? ???????????????
        /*if (!videoInfo.isPassVideo() || videoInfo.isPrivate()) {
            binding.ivShare.setVisibility(View.GONE);
        }*/
        viewModel.commentSuccess.observe(this, commentListBean -> {
            if (commentListBean != null && commentDialog != null) {
                //??????????????????????????????????????????1
                try {
                    videoInfo.commentCount = (Integer.parseInt(videoInfo.commentCount) + 1) + "";
                    binding.tvComment.setText(Num2TextUtil.sNum2Text2(videoInfo.commentCount));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //????????????
                if (commentType == 0) {
                    //???????????????????????????
                    commentDialog.addMCommentData(commentListBean);
                } else if (commentType == 1 || commentType == 2) {
                    //???????????????????????????
                    commentDialog.addMReplyData(commentListBean);
                }

                commentType = 0;
                RxBus.getDefault().post(new EventBean(CodeTable.VIDEO_PL, videoInfo.resourceId, isMineOpen ? 1 : 0));
            }
        });

        binding.rlClick.setOnClickListener(lis -> {
            //???????????????????????????????????????
            if (AppApplication.get().isShowTopMenu()) {
                return;
            }


            //??????????????????
            if (videoInfo.getVideoType() == 2 || videoInfo.getVideoType() == 4) {
                dismissLoading();
                if (AppApplication.get().isShowTopMenu()) {
                    return;
                }
                //???????????????
                if (videoInfo.hasPassword) {
                    pwdDialog = new InputPwdDialog(getActivity());
                    pwdDialog.setRightBtnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (TextUtils.isEmpty(pwdDialog.code)) {
                                ToastUtil.toastShortMessage("???????????????");
                                return;
                            }
                            viewModel.joinLive(videoInfo.liveRoomId, pwdDialog.code);
                            pwdDialog.dismissDialog();
                        }
                    });
                    pwdDialog.showDialog();
                } else {
                    viewModel.joinLive(videoInfo.liveRoomId, "");
                }
            }
        });
        binding.llZhiBoTip.setOnClickListener(lis -> {
            //??????????????????
            if (videoInfo.getVideoType() == 2 || videoInfo.getVideoType() == 4) {
                dismissLoading();

                //???????????????
                if (videoInfo.hasPassword) {
                    pwdDialog = new InputPwdDialog(getActivity());
                    pwdDialog.setRightBtnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (TextUtils.isEmpty(pwdDialog.code)) {
                                ToastUtil.toastShortMessage("???????????????");
                                return;
                            }
                            viewModel.joinLive(videoInfo.liveRoomId, pwdDialog.code);
                            pwdDialog.dismissDialog();
                        }
                    });
                    pwdDialog.showDialog();
                } else {
                    viewModel.joinLive(videoInfo.liveRoomId, "");
                }
            }
        });
        viewModel.liveInfo.observe(this, liveInfo -> {
            if (liveInfo != null) {

                if (liveInfo.liveRoomStatus.equals("LIVE_OVER")) {
                    //???????????? ????????????????????????
                    //??????????????????
                    Bundle bundle = new Bundle();
                    bundle.putString("liveRoomRecordId", liveInfo.liveRoomRecordId);
                    bundle.putString("liveRoomId", videoInfo.liveRoomId);
                    startActivity(ZhiboOverGZActivity.class, bundle);
                } else {
                    //???????????????
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
                    RxBus.getDefault().post(new EventBean(CodeTable.VIDEO_DZ, videoInfo.hasFavorite ? 1 : 0, videoInfo.resourceId, isMineOpen ? 1 : 0));
                }
            }
        });

        viewModel.delSuccess.observe(this, bls -> {
            Bundle bundle = new Bundle();
            bundle.putBoolean(Constant.PUBLISH_SUCCESS, true);
            startActivity(MainActivity.class, bundle);
            getActivity().overridePendingTransition(R.anim.activity_push_none, R.anim.activity_right_2exit);
        });

        binding.tvSetting.setOnClickListener(lis -> {
            showOpenModeDialog();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            if (data != null) {
                String roomPassword = data.getStringExtra("pwd");
                viewModel.joinLive(videoInfo.liveRoomId, roomPassword);
            }
        }
    }


    //??????????????????????????????
    private void showFollow() {
        Boolean aBoolean = AppApplication.get().followList.get(videoInfo.userId);
        if (aBoolean != null) {
            videoInfo.hasAttention = aBoolean;
        }

        if (!videoInfo.showFollow()) {
            binding.followImageView.setVisibility(View.INVISIBLE);
        } else {
            binding.followImageView.clearAnimation();
            binding.followImageView.setBackground(getActivity().getResources().getDrawable(R.drawable.avatar_gz_zb_anim));
            binding.followImageView.setVisibility(View.VISIBLE);
        }
    }


    private void dismissLoading() {
        if (binding.loadingView.isAnimating() || binding.rlLoadingView.getVisibility() == View.VISIBLE) {
            binding.loadingView.cancelAnimation();
            binding.rlLoadingView.setVisibility(View.GONE);
        }
    }


    //????????????
    private ShareDialog shareDialog;

    private void showShareDialog(VideoInfo videoInfo) {
        if (!InfoCache.getInstance().checkLogin()) {
            //??????????????????
            startActivity(SelectLoginActivity.class);
            return;
        }
        if (UserInfoMgr.getInstance().getUserInfo() == null) {
            startActivity(SelectLoginActivity.class);
            return;
        }
        //????????????????????????
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
            shareDialog.isMineOpen(videoOpenType == 3 ? false : isMineOpen);
            shareDialog.setOnDelClickListener(new ShareDialog.OnDelClickListener() {

                private YesOrNoDialog yesOrNoDialog;

                @Override
                public void onDelClick() {
                    yesOrNoDialog = new YesOrNoDialog(getActivity());
                    yesOrNoDialog.setMessageText("?????????????????????");
                    yesOrNoDialog.showDialog();
                    yesOrNoDialog.setRightBtnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog();
                            viewModel.delVideo(videoInfo.resourceId);
                            yesOrNoDialog.dismissDialog();
                        }
                    });

                }
            });
        }
        shareDialog.show(binding.mainRelativeLayout);
    }

    //????????????
    private CommentDialog commentDialog;

    private int commentType;//??????????????????0-?????? 1-?????? 2-????????????

    private void showCommentDialog(String videoId) {
        if (commentDialog == null)
            commentDialog = new CommentDialog(getContext(), videoId, getActivity());
        commentDialog.setOnChildClick(new CommentDialog.OnChildClick() {
            @Override
            public void toComment(CommentListBean iCommentBean) {
                //????????????
                commentType = 1;
                commentId = iCommentBean.id;
                mCommentListBean = iCommentBean;
                showPublishCommentDialog("?????? @" + iCommentBean.nickname + " :");
            }

            @Override
            public void toCommentReply(CommentListBean iCommentBean) {
                //????????????
                commentType = 2;
                commentId = iCommentBean.id;
                mCommentListBean = iCommentBean;
                showPublishCommentDialog("?????? @" + iCommentBean.fromUsername + " :");
            }

            @Override
            public void toCommentVideo(String videoId) {
                commentType = 0;
                commentId = videoId;
                //??????????????????
                showPublishCommentDialog("???????????????????????????");
            }

            @Override
            public void toOpenFeace(String videoId) {
                commentType = 0;
                commentId = videoId;
                //??????????????????
                showPublishCommentDialog("???????????????????????????", 1);
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
        commentDialog.show(binding.mainRelativeLayout, videoInfo.commentCount);
    }

    /**
     * ????????????
     */
    private void showPublishCommentDialog(String nickName) {
        showPublishCommentDialog(nickName, 0);
    }

    private void showPublishCommentDialog(String nickName, int openType) {
        //?????????????????????
        if (!InfoCache.getInstance().checkLogin()) {
            startActivity(SelectLoginActivity.class);
            return;
        }
        if (UserInfoMgr.getInstance().getUserInfo() == null) {
            startActivity(SelectLoginActivity.class);
            return;
        }
        //????????????????????????
        if (!UserInfoMgr.getInstance().getUserInfo().hasMobileNum()) {
            startActivity(PhoneLoginActivity.class);
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString("hint", nickName);
        bundle.putString("commentText", commentText);
        bundle.putInt("pos", position);
        bundle.putString("type", type);
        bundle.putInt("openType", openType);
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
                    //???????????????????????????????????????
                    if (!videoInfo.hasFavorite) {
                        //?????????????????????
                        if (!InfoCache.getInstance().checkLogin()) {
                            startActivity(SelectLoginActivity.class);
                            return;
                        }
                        if (UserInfoMgr.getInstance().getUserInfo() == null) {
                            startActivity(SelectLoginActivity.class);
                            return;
                        }
                        //????????????????????????
                        if (!UserInfoMgr.getInstance().getUserInfo().hasMobileNum()) {
                            startActivity(PhoneLoginActivity.class);
                            return;
                        }
                        viewModel.toLikeVideo(videoInfo.resourceId, !videoInfo.hasFavorite, position);
                    }
                    break;
            }
        }
    };

    //????????????
    private double before_press_Y;
    private double before_press_X;
    private double now_press_y;
    private double now_press_x;
    private long firstClickTime;
    private float[] num = {-30, -20, 0, 20, 30};//????????????

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

        //??????????????????
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
                //??????????????????ImageView
                binding.mainLikeLayout.removeView(iv);
            }
        });

    }

    private void startTjVideo() {
        if (AppApplication.get().getTjPlayPosition() == position) {
            startPlay(true);
            isCaching = false;
        }
    }

    private void startGzVideo() {
        if (AppApplication.get().getGzPlayPosition() == position) {
            startPlay(true);
            isCaching = false;
        }
    }

    private void startListVideo() {
        if (AppApplication.get().getTimeTag() == timeTag) {
            Integer integer = AppApplication.get().listPos.get(timeTag);
            if (integer != null && integer == position) {
                startPlay(true);
                isCaching = false;
            }
        }
    }

    private void startPlay(boolean b) {


        if (videoInfo.getVideoType() == 1) {

            if (vodPlayer != null) {
                vodPlayer.setAutoPlay(b);
                vodPlayer.startPlay(videoInfo.resourceUrl);
            }

            //?????????????????????????????????
            if (b && binding.rlView.getVisibility() == View.GONE) {
                binding.rlView.setVisibility(View.VISIBLE);
            }


        } else if (videoInfo.getVideoType() == 2 || videoInfo.getVideoType() == 4) {
            zbingAnim.start();

            //?????????????????????????????????????????????
            if (videoInfo.hasPassword) {
                dismissLoading();
                //?????????????????????
            } else {
                if (mLivePlayer != null) {
                    mLivePlayer.startPlay(videoInfo.resourceUrl, videoInfo.getVideoType() == 4 ? TXLivePlayer.PLAY_TYPE_VOD_HLS : TXLivePlayer.PLAY_TYPE_LIVE_RTMP);
                }
            }

        }

        LogUtils.v(Constant.TAG_LIVE, "---------------------startPlay" + position);
    }

    private void clickPauseVideo() {
        //????????????
        if (videoInfo.getVideoType() == 1) {
            if (hasPlaying) {


                if (playStatus == 1) {
                    playStatus = 2;
                } else if (playStatus == 2) {
                    playStatus = 1;
                }
                if (playStatus == 1) {
                    vodPlayer.resume();
                    binding.playImageView.setVisibility(View.GONE);
                } else if (playStatus == 2) {
                    vodPlayer.pause();
                    binding.playImageView.setVisibility(View.VISIBLE);
                }
                playMusicAnim();
            }
        }

    }

    private void pausePlay() {
        binding.mainVideoView.onPause();

        if (videoInfo.getVideoType() == 1) {
            if (vodPlayer != null) {
                vodPlayer.pause();
            }
        } else if (videoInfo.getVideoType() == 2 || videoInfo.getVideoType() == 4) {
            if (mLivePlayer != null) {
                mLivePlayer.pause();
            }
        }
        LogUtils.v(Constant.TAG_LIVE, "---------------------pausePlay" + position);
    }

    private void resumePlay() {
        //?????????????????? ????????????-???????????????????????????-????????????????????????
        if ((AppApplication.get().bottomMenu == 0) || (AppApplication.get().bottomMenu == 2 && type.equals("home-list")) || (AppApplication.get().bottomMenu == 3 && type.equals("home-list"))) {
            binding.mainVideoView.onResume();

            //????????????????????????
            if (hasPlaying) {
                //??????????????????????????????
                if (playStatus == 1) {
                    if (videoInfo.getVideoType() == 1) {
                        if (vodPlayer != null) {
                            vodPlayer.resume();
                        }
                    } else if (videoInfo.getVideoType() == 2 || videoInfo.getVideoType() == 4) {
                        if (mLivePlayer != null) {
                            mLivePlayer.resume();
                        }
                    }
                } else if (playStatus == 0) {
                    startPlay(true);//??????????????????????????????
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
        if (videoInfo.getVideoType() == 2 || videoInfo.getVideoType() == 4) {
            if (zbingAnim != null) {
                zbingAnim.stop();
            }
            if (mLivePlayer != null) {
                mLivePlayer.stopPlay(false);
            }
        } else if (videoInfo.getVideoType() == 1) {
            if (videoInfo.getVideoType() == 1) {
                stopMusicAnim();
                binding.playImageView.setVisibility(View.GONE);
            }
            if (vodPlayer != null) {
                vodPlayer.stopPlay(clearLastFrame);
            }
        }


        binding.coverImageView.setVisibility(View.VISIBLE);//--??????
        hasPlaying = false;
        playStatus = 0;


        LogUtils.v(Constant.TAG_LIVE, "---------------------stopPlay" + position);

    }


    private boolean isPause;

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.v(Constant.TAG_LIVE, "---------------------onPause" + position);
        isPause = true;
        pauseMsg();
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtils.v(Constant.TAG_LIVE, "---------------------onStop" + position);
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
                if (AppApplication.get().getLayoutPos() == 0 && AppApplication.get().getTjPlayPosition() == position) {
                    resumePlay();
                } else {
                    pausePlay();
                }
            } else if (type.equals("home-gz")) {
                if (AppApplication.get().getLayoutPos() == 1 && AppApplication.get().getGzPlayPosition() == position) {
                    resumePlay();
                } else {
                    pausePlay();
                }
            } else if (type.equals("home-list")) {

                Integer integer = AppApplication.get().listPos.get(timeTag);
                if (integer != null && integer == position) {
                    resumePlay();
                } else {
                    pausePlay();
                }

            }
        }
    }


    private boolean isFirstResume = true;

    @Override
    public void onResume() {
        super.onResume();
        isPause = false;
        //???????????????????????????????????????
        if (!isFirstResume) {
            resumeMsg();
        }
        showFollow();
        isFirstResume = false;
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
        if (disposable != null) {
            disposable.dispose();
        }
        handler.removeCallbacksAndMessages(null);
    }

    //???????????????
    @Override
    public void onPlayEvent(TXVodPlayer txVodPlayer, int event, Bundle param) {
        if (event == TXLiveConstants.PLAY_EVT_CHANGE_RESOLUTION) {
            //param.getInt(TXLiveConstants.EVT_PARAM1); //????????????
            //param.getInt(TXLiveConstants.EVT_PARAM2); //????????????
        } else if (event == TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME) {// ??????????????????????????????????????????????????????????????????
            LogUtils.v(Constant.TAG_LIVE, type + position + "PLAY_EVT_RCV_FIRST_I_FRAME");
        } else if (event == TXLiveConstants.PLAY_EVT_VOD_PLAY_PREPARED) {//????????????????????????,????????????
            LogUtils.v(Constant.TAG_LIVE, type + position + "PLAY_EVT_VOD_PLAY_PREPARED");
            dismissLoading();
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_LOADING) {//????????????loading????????????????????????????????????BEGIN??????
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {//??????????????????
            LogUtils.v(Constant.TAG_LIVE, type + position + "PLAY_EVT_PLAY_BEGIN");
            showStartLayout();
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_PROGRESS) {//?????????????????????????????????????????????????????????????????????????????????
            //???????????????????????????????????????????????????PLAY_EVT_PLAY_BEGIN
            //EVT_PLAY_DURATION ?????????  EVT_PLAY_PROGRESS ????????????
            //???????????????????????????
            if (param.getInt(TXLiveConstants.EVT_PLAY_PROGRESS_MS) > 0) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.progressBar.setMax(param.getInt(TXLiveConstants.EVT_PLAY_DURATION_MS));
                binding.progressBar.setProgress(param.getInt(TXLiveConstants.EVT_PLAY_PROGRESS_MS));
                showStartLayout();
            }

        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_END) {//??????????????????
            resumePlay();
        } else if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT) {//????????????,??????????????????????????????,??????????????????,?????????????????????????????????
            LogUtils.v(Constant.TAG_LIVE, type + position + "PLAY_ERR_NET_DISCONNECT");
        }
    }

    private void showStartLayout() {
        //????????????????????????????????????????????????
        if (hasPlaying) {
            return;
        }
        hasPlaying = true;//???????????????????????????????????????

        //???????????????????????????????????????????????????????????????
        //???????????????????????????????????????????????????????????????????????????
        if (AppApplication.get().isShowTopMenu() && (type.equals("home-tj") || type.equals("home-gz"))) {
            //????????????
            stopPlay(true);
            return;
        }

        //??????????????????????????????????????????????????????
        if (AppApplication.get().getBottomMenu() != 0 && (type.equals("home-tj") || type.equals("home-gz"))) {
            //??????
            pauseMsg();
            return;
        }

        //?????????????????????????????????????????????????????????
        if (isPause) {
            //??????????????????MainAct?????????VideoList?????? onPause???????????????????????????onStop???????????????
            Activity activity = AppManager.getAppManager().currentActivity();
            if (!activity.getLocalClassName().contains("MainActivity") && !activity.getLocalClassName().contains("VideoListActivity")) {
                pauseMsg();
                return;
            }
        }


        binding.coverImageView.setVisibility(View.GONE);

        dismissLoading();
        if (videoInfo.getVideoType() == 1) {
            //????????????Icon??????
            if (musicRotateAnim != null) {
                playMusicAnim();
            }
        } else {
            zbingAnim.start();
        }
        playStatus = 1;
    }

    @Override
    public void onNetStatus(TXVodPlayer txVodPlayer, Bundle status) {
        //???????????????
        if (status.getInt(TXLiveConstants.NET_STATUS_VIDEO_WIDTH) > status.getInt(TXLiveConstants.NET_STATUS_VIDEO_HEIGHT)) {
            //????????????
            if (mRenderMode != TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION) {
                mRenderMode = TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION;
                txVodPlayer.setRenderMode(mRenderMode);
            }
        } else {
            //????????????
            if (mRenderMode != TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN) {
                mRenderMode = TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN;
                txVodPlayer.setRenderMode(mRenderMode);
            }
        }
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

    //???????????????
    @Override
    public void onPlayEvent(int event, Bundle param) {
        LogUtils.v(Constant.TAG_LIVE, type + position + "ZB_PARAM" + event);

        if (event == TXLiveConstants.PLAY_EVT_CHANGE_RESOLUTION) {
            //param.getInt(TXLiveConstants.EVT_PARAM1); //????????????
            //param.getInt(TXLiveConstants.EVT_PARAM2); //????????????
        } else if (event == TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME) {// ??????????????????????????????????????????????????????????????????
            LogUtils.v(Constant.TAG_LIVE, type + position + "ZB_PLAY_EVT_RCV_FIRST_I_FRAME");
        } else if (event == TXLiveConstants.PLAY_EVT_VOD_PLAY_PREPARED) {//????????????????????????,????????????
            LogUtils.v(Constant.TAG_LIVE, type + position + "ZB_PLAY_EVT_VOD_PLAY_PREPARED");
            showStartLayout();
        } else if (event == TXLiveConstants.PLAY_EVT_RTMP_STREAM_BEGIN) {//???????????????????????????????????????????????? RTMP ?????????????????????
            LogUtils.v(Constant.TAG_LIVE, type + position + "ZB_PLAY_EVT_RTMP_STREAM_BEGIN");
            showStartLayout();
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {//??????????????????
            LogUtils.v(Constant.TAG_LIVE, type + position + "ZB_PLAY_EVT_PLAY_BEGIN");
            showStartLayout();
        } else if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT) {//????????????,??????????????????????????????,??????????????????,?????????????????????????????????
            LogUtils.v(Constant.TAG_LIVE, type + position + "ZB_PLAY_ERR_NET_DISCONNECT");
        }

    }

    @Override
    public void onNetStatus(Bundle status) {
        //???????????????
        if (status.getInt(TXLiveConstants.NET_STATUS_VIDEO_WIDTH) > status.getInt(TXLiveConstants.NET_STATUS_VIDEO_HEIGHT)) {
            //????????????
            if (mRenderMode != TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION) {
                mRenderMode = TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION;
                mLivePlayer.setRenderMode(mRenderMode);
            }
        } else {
            //????????????
            if (mRenderMode != TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN) {
                mRenderMode = TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN;
                mLivePlayer.setRenderMode(mRenderMode);
            }
        }
    }


    public void showSkeleton1() {
        if (AppApplication.get().isFirstLoadVideo) {
            skeletonScreen1 = Skeleton.bind(binding.coverImageView)
                    .shimmer(true)//??????????????????
                    .color(R.color.flashColor)//shimmer?????????
                    .angle(Constant.flashAngle)//shimmer???????????????
                    .duration(Constant.flashDuration)//?????????????????????????????????
                    .load(R.layout.def_vp)//?????????UI
                    .show();
            binding.mainRelativeLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    skeletonScreen1.hide();
                }
            }, Constant.flashCloseDuration);
        }
        AppApplication.get().isFirstLoadVideo = false;
    }

    //??????????????????
    private List<LiteAvUserBean> unLookUserList;//??????????????????

    private void showOpenModeDialog() {
        if (openModePop == null) {
            openModePop = new LiteAvOpenModePopupWindow(getActivity());

            if (videoInfo.creationViewAuth.equals(Constant.ZP_STATUS_GK)) {
                openModePop.setMode(0);
            } else if (videoInfo.creationViewAuth.equals(Constant.ZP_STATUS_HY)) {
                openModePop.setMode(1);
            } else if (videoInfo.creationViewAuth.equals(Constant.ZP_STATUS_SM)) {
                openModePop.setMode(2);
            }

            openModePop.setOnItemSelListener(new LiteAvOpenModePopupWindow.OnItemSelListener() {
                @Override
                public void onItemSel(int mode) {
                    publishMode = mode;
                    showDialog();
                    viewModel.setVideoStatus(videoInfo.resourceId, publishMode);
                    //????????????????????????

                    String content = "";
                    if (videoOpenType == 1) {
                        content = "mine-zp";
                    } else if (videoOpenType == 2) {
                        content = "mine-sm";
                    }
                    RxBus.getDefault().post(new EventBean(CodeTable.CODE_SUCCESS, content));
                }

                @Override
                public void onSwitchChange(boolean comment) {

                }
            });

        }
        openModePop.setUnLookUserList(this.unLookUserList);
        openModePop.showPopupWindow();
    }

}
