package com.xaqinren.healthyelders.moduleLiteav.fragment;

import android.animation.ObjectAnimator;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;

import com.tencent.qcloud.ugckit.utils.TelephonyUtil;
import com.tencent.rtmp.ITXVodPlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXVodPlayConfig;
import com.tencent.rtmp.TXVodPlayer;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.FragmentPlayLiteAvBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.bean.VideoEvent;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleLiteav.activity.LiteAvPlay2Activity;
import com.xaqinren.healthyelders.moduleLiteav.activity.MusicDetailsActivity;
import com.xaqinren.healthyelders.moduleLiteav.bean.LiteAvUserBean;
import com.xaqinren.healthyelders.moduleLiteav.viewModel.LiteAvPlayViewModel;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.widget.comment.CommentDialog;
import com.xaqinren.healthyelders.widget.comment.CommentPublishDialog;
import com.xaqinren.healthyelders.widget.comment.ICommentBean;
import com.xaqinren.healthyelders.widget.share.IShareUser;
import com.xaqinren.healthyelders.widget.share.ShareDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
    private boolean hasPlaying;//是否已经开始了 因为视频播放第一次播放只走进度，滑动后播放先走开始回调
    private AnimationDrawable avatarAddAnim;
    private AnimationDrawable zbingAnim;
    private Animation musicRotateAnim;//音乐Icon旋转动画
    private Animation avatarAnim;//头像放大缩小动画
    private AnimationDrawable avatarBgAnim;//头像背景圈动画
    private ObjectAnimator objectAnimator;//音乐Icon旋转动画 可暂停

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

        //加载进度
//        binding.mainLoadView.setVisibility(View.VISIBLE);
//        binding.mainLoadView.start();
        //视频播放
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
        binding.playerCloudView.setOnClickListener(view -> {
            //暂停
            if (vodPlayer.isPlaying())
            pausePlay();
            else restartPlay();
        });
        binding.plGroup.setOnClickListener(view -> {
            showCommentDialog(videoInfo.id);
        });
        binding.shareGroup.setOnClickListener(view -> {
            showShareDialog();
        });
        binding.musicBtn.setOnClickListener(view -> {
            startActivity(MusicDetailsActivity.class);
        });

    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        disposable = RxBus.getDefault().toObservable(VideoEvent.class).subscribe(bean -> {
            stopPlay(false);
            if (bean != null) {
                if (bean.msgId == LiteAvPlay2Activity.PLAY_SIGNAL) {
                    //判断当前滑动到的postion是不是当前页，若是则播放
                    if (AppApplication.get().getPlayPosition() == position) {
                        vodPlayer.setAutoPlay(true);
                        vodPlayer.startPlay(videoInfo.resourceUrl);
                    }
                }
            }
        });
        RxSubscriptions.add(disposable);
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

    private void stopPlay(boolean clearLastFrame) {
        if (vodPlayer != null) {
            vodPlayer.stopPlay(clearLastFrame);
        }
    }

    private void pausePlay() {
        binding.playerCloudView.onPause();
        if (vodPlayer != null) {
            vodPlayer.pause();
        }
        binding.playBtn.setVisibility(View.VISIBLE);
        binding.musicBtn.clearAnimation();
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
        LogUtils.e(Constant.TAG_LIVE, "Fragment" + position + "\tevent\t=\t" + event);
        if (event == TXLiveConstants.PLAY_EVT_CHANGE_RESOLUTION) {
            //判断设置是否满屏显示
            int width = param.getInt(TXLiveConstants.EVT_PARAM1);
            int height = param.getInt(TXLiveConstants.EVT_PARAM2);
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_END) {
//            restartPlay();
        } else if (event == TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME) {// 视频I帧到达，开始播放

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
            binding.playBtn.setVisibility(View.GONE);
            binding.mainLoadView.setVisibility(View.GONE);
            binding.mainMusicalNoteLayout.start(true);
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

       /* binding.coverImageView.setVisibility(View.GONE);
        binding.mainLoadView.stop();
        binding.mainLoadView.setVisibility(View.GONE);
        if (videoInfo.getVideoType() == 1) {
            //开启音乐Icon动画
            if (musicRotateAnim != null) {
                playMusicAnim();
            }
        }else {
            zbingAnim.start();
        }
        hasPlaying = true;
        isPlaying = true;*/
    }

    @Override
    public void onNetStatus(TXVodPlayer txVodPlayer, Bundle bundle) {

    }


    //分享弹窗
    ShareDialog shareDialog;
    private void showShareDialog(){
        if (shareDialog == null)
            shareDialog = new ShareDialog(getActivity());
        shareDialog.setData(getShareData());
        shareDialog.show(binding.rlContainer);
    }
    private List<? extends IShareUser> getShareData() {
        List<LiteAvUserBean> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            LiteAvUserBean bean = new LiteAvUserBean();
            bean.avatarUrl = "";
            bean.nickname = "";
            bean.userId = i;
            list.add(bean);
        }
        return list;
    }

    //评论弹窗
    CommentDialog commentDialog;
    private void showCommentDialog(String videoId) {
        if (commentDialog == null)
            commentDialog = new CommentDialog(getContext(), videoId);
        commentDialog.setOnChildClick(new CommentDialog.OnChildClick() {
            @Override
            public void toComment(ICommentBean iCommentBean) {
                //评论评论
                LogUtils.e(TAG,"准备评论");
                showPublishCommentDialog();
            }

            @Override
            public void toCommentVideo(String videoId) {
                //评论视频本体
                showPublishCommentDialog();
            }

            @Override
            public void toLike(ICommentBean iCommentBean) {
                LogUtils.e(TAG,"准备点赞");
            }

            @Override
            public void toUser(ICommentBean iCommentBean) {
                LogUtils.e(TAG,"准备查看用户");
            }
        });
        commentDialog.show(binding.rlContainer);
    }

    CommentPublishDialog publishDialog;
    /**
     * 发表评论
     */
    private void showPublishCommentDialog() {
        if (publishDialog == null) {
            publishDialog = new CommentPublishDialog(getActivity(), null);
        }
        publishDialog.show(binding.rlContainer);
    }
}

