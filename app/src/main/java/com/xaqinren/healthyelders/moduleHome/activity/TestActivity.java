package com.xaqinren.healthyelders.moduleHome.activity;

import android.os.Bundle;

import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.ITXVodPlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.TXVodPlayConfig;
import com.tencent.rtmp.TXVodPlayer;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityTestBinding;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.io.File;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;

/**
 * Created by Lee. on 2021/6/17.
 */
public class TestActivity extends BaseActivity<ActivityTestBinding, BaseViewModel> implements ITXVodPlayListener , ITXLivePlayListener {
    private String url = "rtmp://liveplay.hjyiyuanjiankang.com/live/1400392607_1403162947236139008?txSecret=6ef24ac9a2eac0e349e5c920f4f6702f&txTime=60CBF48B";
    private TXVodPlayer vodPlayer;
    private TXLivePlayConfig mPlayerConfig;
    private TXLivePlayer mLivePlayer;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_test;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        binding.btnPlay.setOnClickListener(lis -> {
            initVideo();
        });
        binding.btnPlay2.setOnClickListener(lis -> {
            initLive();
        });

    }


    private void initVideo() {

        //视频播放
        vodPlayer = new TXVodPlayer(this);
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
        vodPlayer.setAutoPlay(true);
        vodPlayer.startPlay(url);
        LogUtils.v(Constant.TAG_LIVE, "----------startPlay");
    }

    public static final float CACHE_TIME_FAST   = 1.0f;
    public static final float CACHE_TIME_SMOOTH = 5.0f;

    public static final int CACHE_STRATEGY_FAST     = 0;        //极速
    public static final int CACHE_STRATEGY_SMOOTH   = 1;        //流畅
    public static final int CACHE_STRATEGY_AUTO     = 2;        //自动
    private void initLive(){
        binding.mainVideoView.showLog(true);
        mPlayerConfig = new TXLivePlayConfig();
        mLivePlayer = new TXLivePlayer(this);
        mPlayerConfig.setAutoAdjustCacheTime(true);
        mPlayerConfig.setMaxAutoAdjustCacheTime(CACHE_TIME_FAST);
        mPlayerConfig.setMinAutoAdjustCacheTime(CACHE_TIME_FAST);
        mLivePlayer.setConfig(mPlayerConfig);


        mLivePlayer.setPlayerView(binding.mainVideoView);
        mLivePlayer.setPlayListener(this);

        mLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
        mLivePlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
        mPlayerConfig.setEnableMessage(true);
        mLivePlayer.setConfig(mPlayerConfig);

        mLivePlayer.startPlay(url, TXLivePlayer.PLAY_TYPE_LIVE_RTMP);
    }

    @Override
    public void onPlayEvent(TXVodPlayer txVodPlayer, int event, Bundle bundle) {
        if (event == TXLiveConstants.PLAY_EVT_CHANGE_RESOLUTION) {
            //param.getInt(TXLiveConstants.EVT_PARAM1); //视频宽度
            //param.getInt(TXLiveConstants.EVT_PARAM2); //视频高度
            LogUtils.v(Constant.TAG_LIVE, "PLAY_EVT_CHANGE_RESOLUTION" + TXLiveConstants.EVT_PARAM1);
            LogUtils.v(Constant.TAG_LIVE, "PLAY_EVT_CHANGE_RESOLUTION" + TXLiveConstants.EVT_PARAM2);
        } else if (event == TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME) {// 收到首帧数据，越快收到此消息说明链路质量越好
            LogUtils.v(Constant.TAG_LIVE, "PLAY_EVT_RCV_FIRST_I_FRAME");
        } else if (event == TXLiveConstants.PLAY_EVT_VOD_PLAY_PREPARED) {//播放器已准备完成,可以播放
            LogUtils.v(Constant.TAG_LIVE, "PLAY_EVT_VOD_PLAY_PREPARED");
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_LOADING) {//视频播放loading，如果能够恢复，之后会有BEGIN事件
        } else if (event == TXLiveConstants.PLAY_EVT_RTMP_STREAM_BEGIN) {//已经连接服务器，开始拉流（仅播放 RTMP 地址时会抛送）
            LogUtils.v(Constant.TAG_LIVE, "PLAY_EVT_RTMP_STREAM_BEGIN");
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {//视频播放开始
            LogUtils.v(Constant.TAG_LIVE, "PLAY_EVT_PLAY_BEGIN");
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_PROGRESS) {//视频播放进度，会通知当前进度和总体进度，仅在点播时有效
            //第一次播放时候先会出现进度不会先走PLAY_EVT_PLAY_BEGIN
            //EVT_PLAY_DURATION 总时间  EVT_PLAY_PROGRESS 当前进度
            //有此回调说明是点播
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_END) {//视频播放结束

        } else if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT) {//网络断连,且经多次重连抢救无效,可以放弃治疗,更多重试请自行重启播放
            LogUtils.v(Constant.TAG_LIVE, "PLAY_ERR_NET_DISCONNECT");
        }
    }

    @Override
    public void onNetStatus(TXVodPlayer txVodPlayer, Bundle bundle) {

    }

    @Override
    public void onPlayEvent(int i, Bundle bundle) {

    }

    @Override
    public void onNetStatus(Bundle bundle) {

    }
}
