package com.xaqinren.healthyelders.moduleHome.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.xaqinren.healthyelders.utils.LogUtils;

import java.io.File;

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

    public HomeVideoFragment(VideoInfo videoInfo, String type, int position) {
        this.videoInfo = videoInfo;
        this.type = type;
        this.position = position;
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
        vodPlayer.setAutoPlay(false);
        //        vodPlayer.startPlay(videoInfo.resourceUrl);
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        disposable = RxBus.getDefault().toObservable(VideoEvent.class).subscribe(bean -> {
            stopPlay(false);
            if (bean != null) {
                if (bean.msgId == 1) {
                    //判断当前滑动到的postion是不是当前页，若是则播放
                    if (AppApplication.get().getPlayPosition() == position) {
                        LogUtils.v(Constant.TAG_LIVE, "收到播放消息");
                        vodPlayer.setAutoPlay(true);
                        vodPlayer.startPlay(videoInfo.resourceUrl);
                    }
                }
            }
        });
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
    }

    @Override
    public void onResume() {
        super.onResume();
        restartPlay();
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
            binding.mainLoadView.setVisibility(View.GONE);
            LogUtils.v(Constant.TAG_LIVE, "Fragment" + position + "PLAY_EVT_VOD_PLAY_PREPARED");
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_LOADING) {//视频播放loading，如果能够恢复，之后会有BEGIN事件
            LogUtils.v(Constant.TAG_LIVE, "Fragment" + position + "PLAY_EVT_PLAY_LOADING");
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {//视频播放开始
            LogUtils.v(Constant.TAG_LIVE, "Fragment" + position + "开始播放了");
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_PROGRESS) {//视频播放进度，会通知当前进度和总体进度，仅在点播时有效
            //EVT_PLAY_DURATION 总时间  EVT_PLAY_PROGRESS 当前进度
            //有此回调说明是点播
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.progressBar.setMax(param.getInt(TXLiveConstants.EVT_PLAY_DURATION_MS));
            binding.progressBar.setProgress(param.getInt(TXLiveConstants.EVT_PLAY_PROGRESS_MS));
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_END) {//视频播放结束
            restartPlay();
        } else if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT) {//网络断连,且经多次重连抢救无效,可以放弃治疗,更多重试请自行重启播放
        } else if (event == TXLiveConstants.PLAY_EVT_RTMP_STREAM_BEGIN) {//已经连接服务器，开始拉流（仅播放RTMP地址时会抛送）
        }
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
