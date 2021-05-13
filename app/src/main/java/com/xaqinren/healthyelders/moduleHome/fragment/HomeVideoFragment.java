package com.xaqinren.healthyelders.moduleHome.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import androidx.annotation.Nullable;

import com.tencent.liteav.txcvodplayer.TXCVodVideoView;
import com.tencent.qcloud.ugckit.utils.LogReport;
import com.tencent.qcloud.ugckit.utils.TelephonyUtil;
import com.tencent.qcloud.xiaoshipin.mainui.list.TCVideoInfo;
import com.tencent.qcloud.xiaoshipin.play.PlayerInfo;
import com.tencent.qcloud.xiaoshipin.play.TCVodPlayerActivity;
import com.tencent.rtmp.ITXVodPlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLog;
import com.tencent.rtmp.TXVodPlayConfig;
import com.tencent.rtmp.TXVodPlayer;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.FragmentHomeVideoBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.bean.VideoEvent;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleHome.viewModel.HomeVideoModel;
import com.xaqinren.healthyelders.moduleHome.widget.LoadingView;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.io.File;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.bus.RxBus;

/**
 * Created by Lee. on 2021/5/13.
 */
public class HomeVideoFragment extends BaseFragment<FragmentHomeVideoBinding, HomeVideoModel> implements ITXVodPlayListener, TelephonyUtil.OnTelephoneListener {
    private VideoInfo videoInfo;
    private String type;
    private int position;
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
        vodPlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
        vodPlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
        vodPlayer.setVodListener(this);
        TXVodPlayConfig config = new TXVodPlayConfig();

        File sdcardDir = getActivity().getExternalFilesDir(null);
        if (sdcardDir != null) {
            config.setCacheFolderPath(sdcardDir.getAbsolutePath() + "/JKZLcache");
        }
        config.setMaxCacheItems(5);

        vodPlayer.setConfig(config);
        vodPlayer.setAutoPlay(false);
        vodPlayer.setPlayerView(binding.mainVideoView);
        vodPlayer.startPlay(videoInfo.resourceUrl);
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
                        vodPlayer.setAutoPlay(true);
                        vodPlayer.startPlay(videoInfo.resourceUrl);
                    }
                }
            }
        });
    }

    private void restartPlay() {
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
        binding.mainVideoView.onPause();
        stopPlay(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.mainVideoView.onResume();
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
            //判断设置是否满屏显示
            int width = param.getInt(TXLiveConstants.EVT_PARAM1);
            int height = param.getInt(TXLiveConstants.EVT_PARAM2);
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_END) {
            restartPlay();
        } else if (event == TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME) {// 视频I帧到达，开始播放

        } else if (event == TXLiveConstants.PLAY_EVT_VOD_PLAY_PREPARED) {

        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {
            binding.mainLoadView.setVisibility(View.GONE);
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
