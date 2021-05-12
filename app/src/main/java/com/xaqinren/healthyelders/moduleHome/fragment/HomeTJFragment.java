package com.xaqinren.healthyelders.moduleHome.fragment;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.tencent.liteav.basic.log.TXCLog;
import com.tencent.qcloud.ugckit.utils.BitmapUtils;
import com.tencent.qcloud.ugckit.utils.LogReport;
import com.tencent.qcloud.ugckit.utils.ScreenUtils;
import com.tencent.qcloud.ugckit.utils.TelephonyUtil;
import com.tencent.qcloud.ugckit.utils.TelephonyUtil.OnTelephoneListener;
import com.tencent.qcloud.ugckit.utils.ToastUtil;
import com.tencent.qcloud.xiaoshipin.mainui.list.TCVideoInfo;
import com.tencent.qcloud.xiaoshipin.play.PlayerInfo;
import com.tencent.rtmp.ITXVodPlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLog;
import com.tencent.rtmp.TXVodPlayConfig;
import com.tencent.rtmp.TXVodPlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.FragmentHomeBinding;
import com.xaqinren.healthyelders.databinding.FragmentHomeTjBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.moduleHome.LockableNestedScrollView;
import com.xaqinren.healthyelders.moduleHome.VerticalViewPager2;
import com.xaqinren.healthyelders.moduleHome.viewModel.HomeTJViewModel;
import com.xaqinren.healthyelders.moduleHome.viewModel.HomeViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;

/**
 * Created by Lee. on 2021/5/11.
 */
public class HomeTJFragment extends BaseFragment<FragmentHomeTjBinding, HomeTJViewModel> implements ITXVodPlayListener, OnTelephoneListener {
    private static final String TAG = "HomeFragment";
    private VerticalViewPager2 mVerticalViewPager;
    private HomePageAdapter mPagerAdapter;
    private TXCloudVideoView mTXCloudVideoView;
    // 发布者id 、视频地址、 发布者名称、 头像URL、 封面URL
    private List<TCVideoInfo> mTCLiveInfoList;
    private int mCurrentPosition;
    private TXVodPlayer mTXVodPlayer;
    private ImageView mIvCover;
    private boolean canRefresh;//是否可以下拉刷新
    private Disposable subscribe;
    public LockableNestedScrollView mNestedScrollView;
    private View.OnScrollChangeListener changeListener;


    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_home_tj;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    private String[] pics = {
            "http://qinren.oss-cn-hangzhou.aliyuncs.com/20200727/608a1b18c21a40e0ad678761d4a0ed17.png",
            "http://qinren.oss-cn-hangzhou.aliyuncs.com/20200804/86791a92d09f4d1595e0124d09156d6f.png",
            "http://qinren.oss-cn-hangzhou.aliyuncs.com/20200804/6bc9be6ee22343beb6d78f7e24204992.png",
    };
    private String[] vidoes = {
            "http://qinren.oss-cn-hangzhou.aliyuncs.com/20200727/cbac2f83b8cd41aeab99f330c9149eab.mp4",
            "http://qinren.oss-cn-hangzhou.aliyuncs.com/20200804/cf913ed075eb4b9bb0dfd0ef17255167.mp4",
            "http://qinren.oss-cn-hangzhou.aliyuncs.com/20200804/0a7cab4596374f06a8cf0481f754b302.mp4",
    };

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        subscribe = RxBus.getDefault().toObservable(EventBean.class).subscribe(event -> {
            if (event != null) {

            }
        });
        RxSubscriptions.add(subscribe);
    }

    public void resetVVPHeight() {
        ViewGroup.LayoutParams layoutParams = mVerticalViewPager.getLayoutParams();
        layoutParams.height = ScreenUtils.getScreenHeight(getActivity());
    }

    @Override
    public void initData() {
        super.initData();

        //        binding.srlContent.setEnabled(false);
        mVerticalViewPager = binding.verticalViewPager;
//        mNestedScrollView = binding.nsv;
        resetVVPHeight();

        mTCLiveInfoList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            TCVideoInfo info = new TCVideoInfo();
            info.review_status = TCVideoInfo.REVIEW_STATUS_NORMAL;
            info.frontcover = pics[i];
            info.hlsPlayUrl = vidoes[i];
            info.playurl = info.hlsPlayUrl;
            mTCLiveInfoList.add(info);
        }


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            changeListener = new View.OnScrollChangeListener() {
//                @Override
//                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                    Log.e(TAG, "onScrollChange: " + scrollX + "---" + scrollY + "----" + oldScrollX + "---" + oldScrollY + "---" + binding.verticalViewPager.getTop() + "---" + ScreenUtils.getScreenHeight(getActivity()));
//                    if (scrollY >= binding.verticalViewPager.getTop()) {
//                        //通知首页底部菜单栏变透明
//                        RxBus.getDefault().post(new EventBean(CodeTable.EVENT_HOME, CodeTable.SET_MENU_TOUMING));
//                        binding.nsv.setScrollingEnabled(false);
//                        binding.rlTop.setVisibility(View.GONE);
//                    }
//                }
//            };
//            binding.nsv.setOnScrollChangeListener(changeListener);
//        }
        initViewPage();
    }

    private void initViewPage() {
        initVideoViews();
        initPlayerSDK();
        TelephonyUtil.getInstance().setOnTelephoneListener(this);
        TelephonyUtil.getInstance().initPhoneListener();

        //在这里停留，让列表界面卡住几百毫秒，给sdk一点预加载的时间，形成秒开的视觉效果
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initVideoViews() {
        mIvCover = (ImageView) getActivity().findViewById(R.id.player_iv_cover);
        mVerticalViewPager.setOffscreenPageLimit(2);
        mVerticalViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                TXLog.d(TAG, "mVerticalViewPager, onPageScrolled position = " + position);
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    canRefresh = true;
                }
                TXLog.d(TAG, "mVerticalViewPager, onPageSelected position = " + position);
                mCurrentPosition = position;
                // 滑动界面，首先让之前的播放器暂停，并seek到0
                TXLog.d(TAG, "滑动后，让之前的播放器暂停，mTXVodPlayer = " + mTXVodPlayer);
                if (mTXVodPlayer != null) {
                    mTXVodPlayer.seek(0);
                    mTXVodPlayer.pause();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mVerticalViewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                TXLog.d(TAG, "mVerticalViewPager, transformPage pisition = " + position + " mCurrentPosition" + mCurrentPosition);
                if (position != 0) {
                    return;
                }

                ViewGroup viewGroup = (ViewGroup) page;
                mIvCover = (ImageView) viewGroup.findViewById(com.hjyy.liteav.R.id.player_iv_cover);
                mTXCloudVideoView = (TXCloudVideoView) viewGroup.findViewById(R.id.player_cloud_view);


                PlayerInfo playerInfo = mPagerAdapter.findPlayerInfo(mCurrentPosition);
                if (playerInfo != null) {
                    playerInfo.vodPlayer.resume();
                    mTXVodPlayer = playerInfo.vodPlayer;
                }
            }
        });

        mPagerAdapter = new HomePageAdapter();
        mVerticalViewPager.setAdapter(mPagerAdapter);
    }

    private void initPlayerSDK() {
        mVerticalViewPager.setCurrentItem(0);
    }

    private void restartPlay() {
        if (mTXVodPlayer != null) {
            mTXVodPlayer.resume();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.onDestroy();
            mTXCloudVideoView = null;
        }

        mPagerAdapter.onDestroy();
        stopPlay(true);
        mTXVodPlayer = null;

        TelephonyUtil.getInstance().uninitPhoneListener();
        subscribe.dispose();
    }


    protected void stopPlay(boolean clearLastFrame) {
        if (mTXVodPlayer != null) {
            mTXVodPlayer.stopPlay(clearLastFrame);
        }
    }

    @Override
    public void onPlayEvent(TXVodPlayer player, int event, Bundle param) {
        if (event == TXLiveConstants.PLAY_EVT_CHANGE_RESOLUTION) {
            int width = param.getInt(TXLiveConstants.EVT_PARAM1);
            int height = param.getInt(TXLiveConstants.EVT_PARAM2);
            //FIXBUG:不能修改为横屏，合唱会变为横向的
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_END) {
            restartPlay();
        } else if (event == TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME) {// 视频I帧到达，开始播放

            PlayerInfo playerInfo = mPagerAdapter.findPlayerInfo(player);
            if (playerInfo != null) {
                playerInfo.isBegin = true;
            }
            if (mTXVodPlayer == player) {
                TXLog.i(TAG, "onPlayEvent, event I FRAME, player = " + player);
                mIvCover.setVisibility(View.GONE);

                LogReport.getInstance().reportVodPlaySucc(event);
            }
        } else if (event == TXLiveConstants.PLAY_EVT_VOD_PLAY_PREPARED) {
            if (mTXVodPlayer == player) {
                TXLog.i(TAG, "onPlayEvent, event prepared, player = " + player);
                mTXVodPlayer.resume();
            }
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {
            PlayerInfo playerInfo = mPagerAdapter.findPlayerInfo(player);
            if (playerInfo != null && playerInfo.isBegin) {
                mIvCover.setVisibility(View.GONE);
                TXCLog.i(TAG, "onPlayEvent, event begin, cover remove");
            }
        } else if (event < 0) {
            if (mTXVodPlayer == player) {
                TXLog.i(TAG, "onPlayEvent, event prepared, player = " + player);

                LogReport.getInstance().reportVodPlayFail(event);
            }

            ToastUtil.toastShortMessage("event:" + event);
        }
    }

    @Override
    public void onNetStatus(TXVodPlayer player, Bundle status) {

    }

    @Override
    public void onRinging() {
        if (mTXVodPlayer != null) {
            mTXVodPlayer.setMute(true);
        }
    }

    @Override
    public void onOffhook() {
        if (mTXVodPlayer != null) {
            mTXVodPlayer.setMute(true);
        }
    }

    @Override
    public void onIdle() {
        if (mTXVodPlayer != null) {
            mTXVodPlayer.setMute(false);
        }
    }

    class HomePageAdapter extends PagerAdapter {
        private List<PlayerInfo> playerInfoList = new ArrayList<>();

        protected PlayerInfo instantiatePlayerInfo(int position) {

            PlayerInfo playerInfo = new PlayerInfo();
            TXVodPlayer vodPlayer = new TXVodPlayer(getActivity());
            vodPlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
            //FIXBUG:FULL_SCREEN 合唱显示不全，ADJUST_RESOLUTION黑边
            vodPlayer.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION);
            vodPlayer.setVodListener(HomeTJFragment.this);
            TXVodPlayConfig config = new TXVodPlayConfig();

            File sdcardDir = getActivity().getExternalFilesDir(null);
            if (sdcardDir != null) {
                config.setCacheFolderPath(sdcardDir.getAbsolutePath() + "/txcache");
            }
            config.setMaxCacheItems(5);
            vodPlayer.setConfig(config);
            vodPlayer.setAutoPlay(false);

            TCVideoInfo tcLiveInfo = mTCLiveInfoList.get(position);
            playerInfo.playURL = TextUtils.isEmpty(tcLiveInfo.hlsPlayUrl) ? tcLiveInfo.playurl : tcLiveInfo.hlsPlayUrl;
            playerInfo.vodPlayer = vodPlayer;
            playerInfo.reviewstatus = tcLiveInfo.review_status;
            playerInfo.pos = position;
            playerInfoList.add(playerInfo);

            return playerInfo;
        }

        protected void destroyPlayerInfo(int position) {
            while (true) {
                PlayerInfo playerInfo = findPlayerInfo(position);
                if (playerInfo == null) {
                    break;
                }
                playerInfo.vodPlayer.stopPlay(true);
                playerInfoList.remove(playerInfo);

            }
        }

        public PlayerInfo findPlayerInfo(int position) {
            for (int i = 0; i < playerInfoList.size(); i++) {
                PlayerInfo playerInfo = playerInfoList.get(i);
                if (playerInfo.pos == position) {
                    return playerInfo;
                }
            }
            return null;
        }

        public PlayerInfo findPlayerInfo(TXVodPlayer player) {
            for (int i = 0; i < playerInfoList.size(); i++) {
                PlayerInfo playerInfo = playerInfoList.get(i);
                if (playerInfo.vodPlayer == player) {
                    return playerInfo;
                }
            }
            return null;
        }

        public void onDestroy() {
            for (PlayerInfo playerInfo : playerInfoList) {
                playerInfo.vodPlayer.stopPlay(true);
            }
            playerInfoList.clear();
        }

        @Override
        public int getCount() {
            return mTCLiveInfoList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TCVideoInfo videoInfo = mTCLiveInfoList.get(position);

            View view = LayoutInflater.from(container.getContext()).inflate(R.layout.lite_view_player_content, null);
            view.setId(position);

            // 封面
            ImageView coverImageView = (ImageView) view.findViewById(R.id.player_iv_cover);
            if (videoInfo.review_status == TCVideoInfo.REVIEW_STATUS_PORN) { //涉黄的图片不显示
                coverImageView.setImageResource(R.drawable.bg);
            } else {
                BitmapUtils.blurBgPic(getActivity(), coverImageView, videoInfo.frontcover, R.drawable.bg);
            }

            TextView tvStatus = (TextView) view.findViewById(R.id.tx_video_review_status);
            if (videoInfo.review_status == TCVideoInfo.REVIEW_STATUS_NOT_REVIEW) {
                tvStatus.setVisibility(View.VISIBLE);
                tvStatus.setText(R.string.video_not_review);
            } else if (videoInfo.review_status == TCVideoInfo.REVIEW_STATUS_PORN) {
                tvStatus.setVisibility(View.VISIBLE);
                tvStatus.setText(R.string.video_porn);
            } else if (videoInfo.review_status == TCVideoInfo.REVIEW_STATUS_NORMAL) {
                tvStatus.setVisibility(View.GONE);
            }

            // 获取此player
            TXCloudVideoView playView = (TXCloudVideoView) view.findViewById(R.id.player_cloud_view);
            PlayerInfo playerInfo = instantiatePlayerInfo(position);
            playerInfo.playerView = playView;
            playerInfo.vodPlayer.setPlayerView(playView);

            if (playerInfo.reviewstatus == TCVideoInfo.REVIEW_STATUS_NORMAL) {
                playerInfo.vodPlayer.startPlay(playerInfo.playURL);
            } else if (playerInfo.reviewstatus == TCVideoInfo.REVIEW_STATUS_NOT_REVIEW) { // 审核中

            } else if (playerInfo.reviewstatus == TCVideoInfo.REVIEW_STATUS_PORN) {       // 涉黄

            }
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            destroyPlayerInfo(position);

            container.removeView((View) object);
        }
    }
}
