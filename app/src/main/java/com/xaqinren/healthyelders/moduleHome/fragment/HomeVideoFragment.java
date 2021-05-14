package com.xaqinren.healthyelders.moduleHome.fragment;

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
import com.xaqinren.healthyelders.databinding.FragmentHomeVideoBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.bean.VideoEvent;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleHome.viewModel.HomeVideoModel;
import com.xaqinren.healthyelders.utils.AnimUtils;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.io.File;

import cc.ibooker.ztextviewlib.AutoVerticalScrollTextViewUtil;
import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.bus.RxBus;

/**
 * Created by Lee. on 2021/5/13.
 * 视频播放Fragment
 */
public class HomeVideoFragment extends BaseFragment<FragmentHomeVideoBinding, HomeVideoModel> implements ITXVodPlayListener, TelephonyUtil.OnTelephoneListener {
    private VideoInfo videoInfo;
    private int position;
    private String type;//区分是从哪里进来的
    private TXVodPlayer vodPlayer;
    private Disposable disposable;
    private Animation amRotate;

    public HomeVideoFragment(VideoInfo videoInfo, String type, int position) {
        this.videoInfo = videoInfo;
        this.type = type;
        this.position = position;
        videoInfo.resourceUrl = Constant.setVideoSigUrl(videoInfo.resourceUrl);
    }

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_home_video;
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
        //音乐播放器旋转动画
        amRotate = AnimationUtils.loadAnimation(getActivity(), R.anim.music_rotate_anim);
        viewModel.videoInfo.setValue(videoInfo);

        //TODO 判断是不是正在直播
        //直播头像背景动画
        AnimationDrawable animationDrawable = (AnimationDrawable) binding.avatarBg.getBackground();
        animationDrawable.start();
        //头像缩小动画
        Animation animation = AnimUtils.getAnimation(getActivity(), R.anim.avatar_start_zb);
        binding.rlAvatar.clearAnimation();
        binding.rlAvatar.startAnimation(animation);



        initVideo();
    }

    private void initVideo() {
        //加载进度
        binding.mainLoadView.setVisibility(View.VISIBLE);
        binding.mainLoadView.start();
        //视频播放
        vodPlayer = new TXVodPlayer(getActivity());
        //RENDER_MODE_FULL_FILL_SCREEN 将图像等比例铺满整个屏幕，多余部分裁剪掉，此模式下画面不会留黑边，但可能因为部分区域被裁剪而显示不全。
        //RENDER_MODE_ADJUST_RESOLUTION 将图像等比例缩放，适配最长边，缩放后的宽和高都不会超过显示区域，居中显示，画面可能会留有黑边。
        vodPlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
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

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        disposable = RxBus.getDefault().toObservable(VideoEvent.class).subscribe(bean -> {
            if (bean != null) {

                //上下切换
                LogUtils.v(Constant.TAG_LIVE, AppApplication.get().getTjPlayPosition() + type + position + bean.toString());
                if (bean.msgId == 1) {
                    stopPlay(true);

                    if (bean.fragmentId.equals("home-tj") && type.equals("home-tj")) {
                        startTjVideo();
                    }

                    if (bean.fragmentId.equals("home-gz") && type.equals("home-gz")) {
                        LogUtils.v(Constant.TAG_LIVE, AppApplication.get().getGzPlayPosition() + type + position + bean.toString());
                        startGzVideo();
                    }

                } else if (bean.msgId == 101) {//左右切换
                    stopPlay(true);
                    if ((bean.position == 0 && type.equals("home-tj"))) {
                        startTjVideo();
                    } else if ((bean.position == 1 && type.equals("home-gz"))) {
                        startGzVideo();
                    }
                }
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


    private void startPlay(boolean b) {
        vodPlayer.setAutoPlay(b);
        vodPlayer.startPlay(videoInfo.resourceUrl);
    }

    private void pausePlay() {
        binding.mainVideoView.onPause();
        if (vodPlayer != null) {
            vodPlayer.pause();
        }
    }

    private void restartPlay() {
        binding.mainVideoView.onResume();
        if (vodPlayer != null) {
            vodPlayer.resume();
        }
    }

    private void stopPlay(boolean clearLastFrame) {
        if (vodPlayer != null) {
            vodPlayer.stopPlay(clearLastFrame);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        pausePlay();
        LogUtils.v(Constant.TAG_LIVE, type + position + "onPause()");
    }

    @Override
    public void onResume() {
        super.onResume();

        if (type.equals("home-tj")) {
            if (AppApplication.get().getTjPlayPosition() == position) {
                restartPlay();
            } else {
                pausePlay();
            }
        }

        if (type.equals("home-gz")) {
            if (AppApplication.get().getGzPlayPosition() == position) {
                restartPlay();
            } else {
                pausePlay();
            }
        }


        LogUtils.v(Constant.TAG_LIVE, type + position + "onResume()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.mainVideoView.onDestroy();
        stopPlay(true);
        TelephonyUtil.getInstance().uninitPhoneListener();
        disposable.dispose();
    }

    @Override
    public void onPlayEvent(TXVodPlayer txVodPlayer, int event, Bundle param) {
        if (event == TXLiveConstants.PLAY_EVT_CHANGE_RESOLUTION) {
            //param.getInt(TXLiveConstants.EVT_PARAM1); //视频宽度
            //param.getInt(TXLiveConstants.EVT_PARAM2); //视频高度
        } else if (event == TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME) {// 收到首帧数据，越快收到此消息说明链路质量越好
        } else if (event == TXLiveConstants.PLAY_EVT_VOD_PLAY_PREPARED) {//播放器已准备完成,可以播放
            LogUtils.v(Constant.TAG_LIVE, type + position + "PLAY_EVT_VOD_PLAY_PREPARED");
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_LOADING) {//视频播放loading，如果能够恢复，之后会有BEGIN事件
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {//视频播放开始
            showStartLayout();
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_PROGRESS) {//视频播放进度，会通知当前进度和总体进度，仅在点播时有效
            //EVT_PLAY_DURATION 总时间  EVT_PLAY_PROGRESS 当前进度
            //有此回调说明是点播
            if (param.getInt(TXLiveConstants.EVT_PLAY_PROGRESS_MS) > 0) {

                binding.progressBar.setVisibility(View.VISIBLE);
                binding.progressBar.setMax(param.getInt(TXLiveConstants.EVT_PLAY_DURATION_MS));
                binding.progressBar.setProgress(param.getInt(TXLiveConstants.EVT_PLAY_PROGRESS_MS));

                if (!isStart) {
                    showStartLayout();
                }
            }

        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_END) {//视频播放结束
            LogUtils.v(Constant.TAG_LIVE, type + position + "播放结束了");

            restartPlay();
        } else if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT) {//网络断连,且经多次重连抢救无效,可以放弃治疗,更多重试请自行重启播放
        } else if (event == TXLiveConstants.PLAY_EVT_RTMP_STREAM_BEGIN) {//已经连接服务器，开始拉流（仅播放RTMP地址时会抛送）
        }
    }

    private boolean isStart;//是否开始了

    private void showStartLayout() {
        binding.mainLoadView.stop();
        binding.mainLoadView.setVisibility(View.GONE);
        binding.coverImageView.setVisibility(View.GONE);
        if (amRotate != null) {
            binding.musicImageView.clearAnimation();
            binding.musicImageView.startAnimation(amRotate);
        }
        isStart = true;
    }


    @Override
    public void onNetStatus(TXVodPlayer txVodPlayer, Bundle bundle) {

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
}
