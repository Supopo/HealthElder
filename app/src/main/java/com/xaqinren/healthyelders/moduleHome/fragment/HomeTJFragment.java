package com.xaqinren.healthyelders.moduleHome.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tencent.qcloud.ugckit.utils.ScreenUtils;
import com.tencent.qcloud.xiaoshipin.play.PlayerInfo;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXVodPlayConfig;
import com.tencent.rtmp.TXVodPlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.FragmentHomeTjBinding;
import com.xaqinren.healthyelders.databinding.ItemVideoListBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.adapter.FragmentPagerAdapter;
import com.xaqinren.healthyelders.moduleHome.bean.VideoEvent;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleHome.viewModel.HomeTJViewModel;
import com.xaqinren.healthyelders.utils.LogUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;

/**
 * Created by Lee. on 2021/5/11.
 * 视频播放列表Fragment
 */
public class HomeTJFragment extends BaseFragment<FragmentHomeTjBinding, HomeTJViewModel> {
    private static final String TAG = "HomeFragment";
    private List<VideoInfo> mVideoInfoList;
    private List<VideoInfo> temp;
    private Disposable subscribe;
    private int page = 1;
    private int pageSize = 4;
    private List<Fragment> fragmentList = new ArrayList<>();
    private FragmentPagerAdapter homeAdapter;
    private int videoPosition;//视频Fragment在list中的位置
    private FragmentActivity fragmentActivity;

    public HomeTJFragment(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
    }

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
                if (event.msgId == CodeTable.EVENT_HOME) {
                    if (event.msgType == CodeTable.SET_MENU_TOUMING) {
                        //底部菜单变透明 开启vp2滑动
                        binding.viewPager2.setUserInputEnabled(true);
                    } else if (event.msgType == CodeTable.SET_MENU_WHITE) {
                        //底部菜单变白，头布局处理
                        binding.viewPager2.setUserInputEnabled(false);
                    }
                }
            }
        });
        RxSubscriptions.add(subscribe);
        viewModel.datas.observe(this, datas -> {
            if (datas != null && datas.size() > 0) {

            }
        });
    }

    public void resetVVPHeight() {
        ViewGroup.LayoutParams layoutParams = binding.viewPager2.getLayoutParams();
        layoutParams.height = ScreenUtils.getScreenHeight(getActivity());
    }

    @Override
    public void initData() {
        super.initData();
        resetVVPHeight();

        //开始时候有头布局所以禁止滑动
        binding.viewPager2.setUserInputEnabled(false);

        mVideoInfoList = new ArrayList<>();
        temp = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            VideoInfo info = new VideoInfo();
            info.coverUrl = pics[i];
            info.resourceUrl = vidoes[i];
            mVideoInfoList.add(info);
            temp.add(info);
        }
        initVideoViews();
    }


    private void initVideoViews() {
        binding.homeLoadView.setVisibility(View.VISIBLE);
        binding.homeLoadView.start();
        //请求数据
        viewModel.getVideoData(page, pageSize);

        homeAdapter = new FragmentPagerAdapter(fragmentActivity, fragmentList);

        for (int i = 0; i < mVideoInfoList.size(); i++) {
            fragmentList.add(new HomeVideoFragment(mVideoInfoList.get(i), "main", videoPosition));
            videoPosition++;
        }
        binding.viewPager2.setAdapter(homeAdapter);
        binding.viewPager2.setOffscreenPageLimit(fragmentList.size());
        binding.homeLoadView.setVisibility(View.GONE);


        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                AppApplication.get().setPlayPosition(position);
                RxBus.getDefault().post(new VideoEvent(1));
                //判断数据数量滑动到倒数第三个时候去进行加载
                if ((position + 3) == fragmentList.size()) {
                    //TODO 加载更多数据

                    //加载数据
                    for (int i = 0; i < temp.size(); i++) {
                        fragmentList.add(new HomeVideoFragment(temp.get(i), "main", videoPosition));
                        videoPosition++;
                    }
                    homeAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        subscribe.dispose();
    }


    class VideoAdapter extends BaseQuickAdapter<VideoInfo, BaseViewHolder> implements LoadMoreModule {
        private List<PlayerInfo> playerInfoList = new ArrayList<>();

        protected PlayerInfo instantiatePlayerInfo(int position) {

            PlayerInfo playerInfo = new PlayerInfo();
            TXVodPlayer vodPlayer = new TXVodPlayer(getActivity());
            vodPlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
            //设置模式 全屏-RENDER_MODE_FULL_FILL_SCREEN
            vodPlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
            //            vodPlayer.setVodListener(HomeTJFragment.this);
            TXVodPlayConfig config = new TXVodPlayConfig();

            File sdcardDir = getActivity().getExternalFilesDir(null);
            if (sdcardDir != null) {
                config.setCacheFolderPath(sdcardDir.getAbsolutePath() + "/jkzlcache");
            }
            config.setMaxCacheItems(5);
            vodPlayer.setConfig(config);
            vodPlayer.setAutoPlay(false);

            VideoInfo videoInfo = mVideoInfoList.get(position);
            playerInfo.playURL = videoInfo.resourceUrl;
            playerInfo.vodPlayer = vodPlayer;
            playerInfo.pos = position;
            playerInfoList.add(playerInfo);

            return playerInfo;
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

        public VideoAdapter(int layoutResId) {
            super(layoutResId);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder helper, VideoInfo item) {
            //注意 ItemBinding 改为自己item_layout的名字 ItemXxxBinding
            ItemVideoListBinding binding = DataBindingUtil.bind(helper.itemView);
            binding.setViewModel(item);
            binding.executePendingBindings();

            Glide.with(getActivity()).load(item.coverUrl).into((ImageView) helper.getView(R.id.iv_cover));

            // 获取此player
            TXCloudVideoView playView = (TXCloudVideoView) helper.getView(R.id.tcVideoView);
            PlayerInfo playerInfo = instantiatePlayerInfo(helper.getAdapterPosition());
            playerInfo.playerView = playView;
            playerInfo.vodPlayer.setPlayerView(playView);
        }
    }
}
