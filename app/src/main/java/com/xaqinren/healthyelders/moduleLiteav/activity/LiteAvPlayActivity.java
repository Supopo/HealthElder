package com.xaqinren.healthyelders.moduleLiteav.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.tencent.liteav.basic.log.TXCLog;
import com.tencent.qcloud.ugckit.UGCKitConstants;
import com.tencent.qcloud.ugckit.utils.LogReport;
import com.tencent.qcloud.ugckit.utils.TelephonyUtil;
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
import com.xaqinren.healthyelders.databinding.ActivityLiteAvPlayBinding;
import com.xaqinren.healthyelders.moduleHome.bean.CommentListBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.LiteAvUserBean;
import com.xaqinren.healthyelders.moduleLiteav.viewModel.LiteAvPlayViewModel;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.widget.LiteAvPlayView;
import com.xaqinren.healthyelders.widget.comment.CommentDialog;
import com.xaqinren.healthyelders.widget.comment.CommentPublishDialog;
import com.xaqinren.healthyelders.widget.share.IShareUser;
import com.xaqinren.healthyelders.widget.share.ShareDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;
import me.goldze.mvvmhabit.base.BaseActivity;

public class LiteAvPlayActivity extends BaseActivity <ActivityLiteAvPlayBinding , LiteAvPlayViewModel> implements ITXVodPlayListener, TelephonyUtil.OnTelephoneListener{
    private static final String TAG = "LiteAvPlayActivity";
    private VerticalViewPager mVerticalViewPager;
    private MyPagerAdapter mPagerAdapter;
    private TXCloudVideoView mTXCloudVideoView;

    private ImageView mIvCover;

    // ?????????id ?????????????????? ?????????????????? ??????URL??? ??????URL
    private List<TCVideoInfo> mTCLiveInfoList;
    private int mInitTCLiveInfoPosition;
    private int mCurrentPosition;

    /**
     * SDK?????????????????????
     */
    private TXVodPlayer mTXVodPlayer;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_lite_av_play;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        rlTitle.setVisibility(View.GONE);
        setStatusBar(getResources().getColor(R.color.black));
        setStatusBarTransparent();
        initDatas();
        initViews();
        initPlayerSDK();

        TelephonyUtil.getInstance().setOnTelephoneListener(this);
        TelephonyUtil.getInstance().initPhoneListener();

        //?????????????????????????????????????????????????????????sdk??????????????????????????????????????????????????????
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initDatas() {
        Intent intent = getIntent();
        mTCLiveInfoList = (List<TCVideoInfo>) intent.getSerializableExtra(UGCKitConstants.TCLIVE_INFO_LIST);
        mInitTCLiveInfoPosition = intent.getIntExtra(UGCKitConstants.TCLIVE_INFO_POSITION, 0);
    }

    private void initViews() {
        mTXCloudVideoView = (TXCloudVideoView) findViewById(com.hjyy.liteav.R.id.player_cloud_view);
        mIvCover = (ImageView) findViewById(com.hjyy.liteav.R.id.player_iv_cover);

        mVerticalViewPager = (VerticalViewPager) findViewById(com.hjyy.liteav.R.id.vertical_view_pager);
        mVerticalViewPager.setOffscreenPageLimit(2);
        mVerticalViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                TXLog.d(TAG, "mVerticalViewPager, onPageScrolled position = " + position);
            }

            @Override
            public void onPageSelected(int position) {
                TXLog.d(TAG, "mVerticalViewPager, onPageSelected position = " + position);
                mCurrentPosition = position;
                // ??????????????????????????????????????????????????????seek???0
                TXLog.d(TAG, "??????????????????????????????????????????mTXVodPlayer = " + mTXVodPlayer);
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
                mTXCloudVideoView = (TXCloudVideoView) viewGroup.findViewById(com.hjyy.liteav.R.id.player_cloud_view);


                PlayerInfo playerInfo = mPagerAdapter.findPlayerInfo(mCurrentPosition);
                if (playerInfo != null) {
                    playerInfo.vodPlayer.resume();
                    mTXVodPlayer = playerInfo.vodPlayer;
                }
            }
        });
        mPagerAdapter = new MyPagerAdapter();
        mVerticalViewPager.setAdapter(mPagerAdapter);

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

    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;

    private void initPlayerSDK() {
        mVerticalViewPager.setCurrentItem(mInitTCLiveInfoPosition);
    }

    private void restartPlay() {
        if (mTXVodPlayer != null) {
            mTXVodPlayer.resume();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.onResume();
        }
        if (mTXVodPlayer != null) {
            mTXVodPlayer.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.onPause();
        }
        if (mTXVodPlayer != null) {
            mTXVodPlayer.pause();
        }
    }


    @Override
    protected void onDestroy() {
        binding.rlContainer.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
        super.onDestroy();
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.onDestroy();
            mTXCloudVideoView = null;
        }

        mPagerAdapter.onDestroy();
        stopPlay(true);
        mTXVodPlayer = null;

        TelephonyUtil.getInstance().uninitPhoneListener();
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
            //FIXBUG:????????????????????????????????????????????????
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_END) {
            restartPlay();
        } else if (event == TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME) {// ??????I????????????????????????

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

    class MyPagerAdapter extends PagerAdapter {

        ArrayList<PlayerInfo> playerInfoList = new ArrayList<>();

        protected PlayerInfo instantiatePlayerInfo(int position) {
            TXCLog.d(TAG, "instantiatePlayerInfo " + position);

            PlayerInfo playerInfo = new PlayerInfo();
            TXVodPlayer vodPlayer = new TXVodPlayer(LiteAvPlayActivity.this);
            vodPlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
            //FIXBUG:FULL_SCREEN ?????????????????????ADJUST_RESOLUTION??????
            vodPlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
            vodPlayer.setVodListener(LiteAvPlayActivity.this);
            TXVodPlayConfig config = new TXVodPlayConfig();

            File sdcardDir = getExternalFilesDir(null);
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

                TXCLog.d(TAG, "destroyPlayerInfo " + position);
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
        public LiteAvPlayView instantiateItem(ViewGroup container, int position) {
            TXCLog.i(TAG, "MyPagerAdapter instantiateItem, position = " + position);
            //TODO ?????????????????????bean
            TCVideoInfo videoInfo = mTCLiveInfoList.get(position);
            LiteAvPlayView view = new LiteAvPlayView(LiteAvPlayActivity.this);
            view.setId(position);
            view.initView();
            view.setVideoInfo(videoInfo);
            PlayerInfo playerInfo = instantiatePlayerInfo(position);
            view.attachVideoView(playerInfo);
            view.setOnItemClickListener(new LiteAvPlayView.OnItemClickListener() {
                @Override
                public void onShareClick(String videoId) {
                    showShareDialog();
                }

                @Override
                public void onLikeClick(String videoId) {

                }

                @Override
                public void onCommentClick(String videoId) {
                    showCommentDialog(videoInfo.fileid);
                }

                @Override
                public void onAvatarClick(String videoId) {

                }

                @Override
                public void onLookClick(String videoId) {

                }
            });
            if (playerInfo.reviewstatus == TCVideoInfo.REVIEW_STATUS_NORMAL) {
                playerInfo.vodPlayer.startPlay(playerInfo.playURL);
            } else if (playerInfo.reviewstatus == TCVideoInfo.REVIEW_STATUS_NOT_REVIEW) { // ?????????
            } else if (playerInfo.reviewstatus == TCVideoInfo.REVIEW_STATUS_PORN) {       // ??????
            }
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            TXCLog.i(TAG, "MyPagerAdapter destroyItem, position = " + position);

            destroyPlayerInfo(position);

            container.removeView((View) object);
        }
    }
    //????????????
    ShareDialog shareDialog;
    private void showShareDialog(){
        if (shareDialog == null)
            shareDialog = new ShareDialog(this);
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
            commentDialog = new CommentDialog(this, videoId);
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
            public void toCommentVideo(String videoId) {
                //??????????????????
                showPublishCommentDialog();
            }

            @Override
            public void toOpenFeace(String videoId) {
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
            publishDialog = new CommentPublishDialog(this, null);
        }
        publishDialog.show(binding.rlContainer);
    }
}
