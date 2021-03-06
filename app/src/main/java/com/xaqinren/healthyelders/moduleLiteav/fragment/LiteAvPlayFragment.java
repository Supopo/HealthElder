package com.xaqinren.healthyelders.moduleLiteav.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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
import com.tencent.rtmp.ITXVodPlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXVodPlayConfig;
import com.tencent.rtmp.TXVodPlayer;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.FragmentPlayLiteAvBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.moduleHome.bean.CommentListBean;
import com.xaqinren.healthyelders.moduleHome.bean.VideoEvent;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleLiteav.activity.LiteAvPlay2Activity;
import com.xaqinren.healthyelders.moduleLiteav.activity.MusicDetailsActivity;
import com.xaqinren.healthyelders.moduleLiteav.bean.LiteAvUserBean;
import com.xaqinren.healthyelders.moduleLiteav.viewModel.LiteAvPlayViewModel;
import com.xaqinren.healthyelders.utils.AnimUtil;
import com.xaqinren.healthyelders.utils.AnimUtils;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.widget.comment.CommentDialog;
import com.xaqinren.healthyelders.widget.comment.CommentPublishDialog;
import com.xaqinren.healthyelders.widget.share.IShareUser;
import com.xaqinren.healthyelders.widget.share.ShareDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;

public class LiteAvPlayFragment extends BaseFragment<FragmentPlayLiteAvBinding, LiteAvPlayViewModel> implements TelephonyUtil.OnTelephoneListener, ITXVodPlayListener {
    private final String TAG = "LiteAvPlayFragment";
    private final String type;
    private final int position;
    private TXVodPlayer vodPlayer;
    private VideoInfo videoInfo;
    private Disposable disposable;
    private Animation amRotate;
    private boolean hasPlaying;//????????????????????? ?????????????????????????????????????????????????????????????????????????????????
    private AnimationDrawable avatarAddAnim;
    private AnimationDrawable zbingAnim;
    private Animation musicRotateAnim;//??????Icon????????????
    private Animation avatarAnim;//????????????????????????
    private AnimationDrawable avatarBgAnim;//?????????????????????
    private ObjectAnimator objectAnimator;//??????Icon???????????? ?????????
    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;

    public LiteAvPlayFragment(VideoInfo videoInfo, String type, int position) {
        this.videoInfo = videoInfo;
        this.type = type;
        this.position = position;
    }

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_play_lite_av;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        TelephonyUtil.getInstance().setOnTelephoneListener(this);
        TelephonyUtil.getInstance().initPhoneListener();

        //????????????
//        binding.mainLoadView.setVisibility(View.VISIBLE);
//        binding.mainLoadView.start();
        //????????????
        vodPlayer = new TXVodPlayer(getActivity());
        vodPlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
        vodPlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
        vodPlayer.setLoop(true);
        vodPlayer.setVodListener(this);
        TXVodPlayConfig config = new TXVodPlayConfig();

        File sdcardDir = getActivity().getExternalFilesDir(null);
        if (sdcardDir != null) {
            config.setCacheFolderPath(sdcardDir.getAbsolutePath() + "/JKZLcache");
        }
        config.setMaxCacheItems(5);

        vodPlayer.setConfig(config);
        vodPlayer.setAutoPlay(false);
        vodPlayer.setPlayerView(binding.playerCloudView);
        amRotate = AnimationUtils.loadAnimation(getActivity(), R.anim.music_rotate_anim);

        createObjAnim();

        //????????????
        if (!TextUtils.isEmpty(videoInfo.musicIcon)) {
            Glide.with(getActivity()).load(videoInfo.musicIcon).diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.musicBtn);
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


        /*binding.playerCloudView.setOnClickListener(view -> {
            //??????
            if (vodPlayer.isPlaying())
            pausePlay();
            else restartPlay();
        });*/
        binding.plGroup.setOnClickListener(view -> {
            showCommentDialog(videoInfo.id);
        });
        binding.shareGroup.setOnClickListener(view -> {
            showShareDialog();
        });
        binding.musicBtn.setOnClickListener(view -> {
            startActivity(MusicDetailsActivity.class);
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
                if (publishDialog != null && publishDialog.isShow()) {
                    publishDialog.keyBoardClosed();
                }
            }
        };
        binding.rlContainer.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
    }



    //????????????
    private double before_press_Y;
    private double before_press_X;
    private double now_press_y;
    private double now_press_x;
    private long firstClickTime;
    private float[] num = {-30, -20, 0, 20, 30};//????????????
    private boolean isPlaying;//??????????????????
    public static final int STATE_PLAYING = 1;//????????????
    public static final int STATE_PAUSE = 2;//??????
    public static final int STATE_STOP = 3;//??????
    public int state;


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


    private void clickPauseVideo() {
        //????????????
        if (videoInfo.getVideoType() == 1) {
            if (hasPlaying) {
                isPlaying = !isPlaying;
                if (isPlaying) {
                    vodPlayer.resume();
                    binding.playBtn.setVisibility(View.GONE);
                } else {
                    vodPlayer.pause();
                    binding.playBtn.setVisibility(View.VISIBLE);
                }
                playMusicAnim();
            }
        }

    }

    private void createObjAnim() {
        state = STATE_STOP;
        objectAnimator = ObjectAnimator.ofFloat(binding.musicBtn, "rotation", 0f, 360f);//??????????????????????????????????????????????????????
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

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        disposable = RxBus.getDefault().toObservable(VideoEvent.class).subscribe(bean -> {
            stopPlay(false);
            if (bean != null) {
                if (bean.msgId == LiteAvPlay2Activity.PLAY_SIGNAL) {
                    //????????????????????????postion????????????????????????????????????
                    if (AppApplication.get().getPlayPosition() == position) {
                        vodPlayer.setAutoPlay(true);
                        vodPlayer.startPlay(videoInfo.resourceUrl);
                    }
                }
            }
        });
        RxSubscriptions.add(disposable);

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
    }
    private void restartPlay() {
        binding.playerCloudView.onResume();
        if (vodPlayer != null) {
            vodPlayer.resume();
        }
        binding.mainMusicalNoteLayout.start(true);
        binding.musicBtn.clearAnimation();
        binding.musicBtn.startAnimation(amRotate);
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

    private void startPlay(boolean b) {
        vodPlayer.setAutoPlay(b);
        vodPlayer.startPlay(videoInfo.resourceUrl);
    }

    private void pausePlay() {
        binding.playerCloudView.onPause();
        if (vodPlayer != null) {
            vodPlayer.pause();
        }
    }

    private void resumePlay() {
        binding.playerCloudView.onResume();
        if (vodPlayer != null) {
            //??????????????????????????????
            if (isPlaying) {
                vodPlayer.resume();
            } else {
                vodPlayer.pause();
            }
        }
    }

    private void stopPlay(boolean clearLastFrame) {
        if (!hasPlaying) {
            return;
        }
        if (videoInfo.getVideoType() == 1) {
            stopMusicAnim();
        }else {
            zbingAnim.stop();
        }
        binding.playerIvCover.setVisibility(View.VISIBLE);
        hasPlaying = false;
        isPlaying = false;
        binding.playBtn.setVisibility(View.GONE);
        if (vodPlayer != null) {
            vodPlayer.stopPlay(clearLastFrame);
        }

    }

    public void stopMusicAnim() {
        if (objectAnimator != null) {
            objectAnimator.end();//????????????
            state = STATE_STOP;
        }
        binding.mainMusicalNoteLayout.start(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        vodPlayer.startPlay(videoInfo.resourceUrl);
    }

    @Override
    public void onPause() {
        super.onPause();
        pausePlay();
        binding.playBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        restartPlay();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.rlContainer.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
        binding.playerCloudView.onDestroy();
        stopPlay(true);
        TelephonyUtil.getInstance().uninitPhoneListener();
        disposable.dispose();
        RxSubscriptions.remove(disposable);
    }


    @Override
    public void onRinging() {
        if (vodPlayer != null) {
            vodPlayer.setMute(true);
        }
    }

    @Override
    public void onOffhook() {
        if (vodPlayer != null) {
            vodPlayer.setMute(true);
        }
    }

    @Override
    public void onIdle() {
        if (vodPlayer != null) {
            vodPlayer.setMute(false);
        }
    }

    @Override
    public void onPlayEvent(TXVodPlayer txVodPlayer, int event, Bundle param) {
//        LogUtils.e(Constant.TAG_LIVE, "Fragment" + position + "\tevent\t=\t" + event);
        if (event == TXLiveConstants.PLAY_EVT_CHANGE_RESOLUTION) {
            //??????????????????????????????
            int width = param.getInt(TXLiveConstants.EVT_PARAM1);
            int height = param.getInt(TXLiveConstants.EVT_PARAM2);
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_END) {
            resumePlay();
        } else if (event == TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME) {// ??????I????????????????????????

        } else if (event == TXLiveConstants.PLAY_EVT_VOD_PLAY_PREPARED) {
            /*binding.playBtn.setVisibility(View.GONE);
            binding.mainLoadView.setVisibility(View.GONE);
            if (amRotate != null) {
                binding.musicBtn.clearAnimation();
                binding.musicBtn.startAnimation(amRotate);
                binding.mainMusicalNoteLayout.start(true);
            }*/
        }else if (event == TXLiveConstants.PLAY_EVT_RTMP_STREAM_BEGIN){
            showStartLayout();
        }
        else if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {
            showStartLayout();
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_PROGRESS) {
            if (param.getInt(TXLiveConstants.EVT_PLAY_PROGRESS_MS) > 0) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.progressBar.setMax(param.getInt(TXLiveConstants.EVT_PLAY_DURATION_MS));
                binding.progressBar.setProgress(param.getInt(TXLiveConstants.EVT_PLAY_PROGRESS_MS));
                showStartLayout();
            }
        }
    }

    private void showStartLayout() {
        if (hasPlaying) {
            return;
        }

        binding.playerIvCover.setVisibility(View.GONE);
        binding.mainLoadView.stop();
        binding.mainLoadView.setVisibility(View.GONE);
        if (videoInfo.getVideoType() == 1) {
            //????????????Icon??????
            if (musicRotateAnim != null) {
                playMusicAnim();
            }
        }else {
            zbingAnim.start();
        }
        hasPlaying = true;
        isPlaying = true;
    }

    @Override
    public void onNetStatus(TXVodPlayer txVodPlayer, Bundle bundle) {

    }


    //????????????
    ShareDialog shareDialog;
    private void showShareDialog(){
        if (shareDialog == null)
            shareDialog = new ShareDialog(getActivity());
        shareDialog.setShareData(getShareData());
        shareDialog.show(binding.rlContainer);
    }
    private List<? extends IShareUser> getShareData() {
        List<LiteAvUserBean> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            LiteAvUserBean bean = new LiteAvUserBean();
            bean.setAvatar("");
            bean.setName("");
            bean.setId(i+"");
            list.add(bean);
        }
        return list;
    }

    //????????????
    CommentDialog commentDialog;
    private void showCommentDialog(String videoId) {
        if (commentDialog == null)
            commentDialog = new CommentDialog(getContext(), videoId);
        commentDialog.setOnChildClick(new CommentDialog.OnChildClick() {
            @Override
            public void toComment(CommentListBean iCommentBean) {
                //????????????
                LogUtils.e(TAG,"????????????");
                showPublishCommentDialog();
            }

            @Override
            public void toCommentReply(CommentListBean iCommentBean) {
                
            }

            @Override
            public void toOpenFeace(String videoId) {
                //??????????????????
                showPublishCommentDialog();
            }

            @Override
            public void toCommentVideo(String videoId) {
                //??????????????????
                showPublishCommentDialog();
            }

            @Override
            public void toLike(CommentListBean iCommentBean) {
                LogUtils.e(TAG,"????????????");
            }

            @Override
            public void toUser(CommentListBean iCommentBean) {
                LogUtils.e(TAG,"??????????????????");
            }
        });
        commentDialog.show(binding.rlContainer,"0");
    }

    CommentPublishDialog publishDialog;
    /**
     * ????????????
     */
    private void showPublishCommentDialog() {
        if (publishDialog == null) {
            publishDialog = new CommentPublishDialog(getActivity(), null);
        }
        publishDialog.show(binding.rlContainer);
    }
}

