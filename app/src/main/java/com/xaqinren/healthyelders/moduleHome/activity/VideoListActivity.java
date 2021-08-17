package com.xaqinren.healthyelders.moduleHome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.ActivityVideoListBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.adapter.FragmentPagerAdapter;
import com.xaqinren.healthyelders.moduleHome.bean.VideoEvent;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleHome.bean.VideoListBean;
import com.xaqinren.healthyelders.moduleHome.fragment.HomeVideoFragment;
import com.xaqinren.healthyelders.moduleHome.viewModel.VideoListViewModel;
import com.xaqinren.healthyelders.moduleMine.bean.DZVideoInfo;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.bus.RxBus;

/**
 * Created by Lee. on 2021/5/18.
 * 视频列表播放页面
 */
public class VideoListActivity extends BaseActivity<ActivityVideoListBinding, VideoListViewModel> {
    private static final String TAG = "home-list";

    private List<VideoInfo> mVideoInfoList = new ArrayList<>();
    private int page = 1;
    private int position;
    private List<Fragment> fragmentList = new ArrayList<>();
    private FragmentPagerAdapter homeAdapter;
    private int fragmentPosition;//视频Fragment在list中的位置
    private VideoListBean videos;
    private Handler handler;
    private boolean isSingle;
    private Disposable subscribe;
    private boolean isMineOpen;
    private int openType;//打开方式 1 从某个用户作品列表打开 2私密作品 3赞过
    private long timeTag;
    private String userId;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_video_list;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initParam() {
        super.initParam();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        videos = (VideoListBean) bundle.getSerializable("key");
        isSingle = bundle.getBoolean("key1", false);

        isMineOpen = bundle.getBoolean(Constant.MINE_OPEN, false);
        openType = bundle.getInt("openType", 0);

        userId = bundle.getString("userId", "");

        //1-从首页直播列表打开 2-从附近（首页菜单视频页面）打开 3 我的-作品 4 我的-私密 5 我的-点赞 6别人的作品 7 别人的点赞
        if (videos.openType == 2 || videos.openType == 3 || videos.openType == 4 || videos.openType == 5 || videos.openType == 6 || videos.openType == 7) {
            position = videos.position;
            page = videos.page;
            if (isSingle) {
                if (videos.videoInfos.size() >= position) {
                    mVideoInfoList.add(videos.videoInfos.get(position));
                    position = 0;
                }
            } else {
                mVideoInfoList.addAll(videos.videoInfos);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppApplication.get().listPos.remove(timeTag);
        handler.removeCallbacksAndMessages(null);
        if (subscribe != null) {
            subscribe.dispose();
        }
    }


    private int lastPos = -1;

    @Override
    public void initData() {
        super.initData();
        handler = new Handler();
        setStatusBarTransparent();

        timeTag = System.currentTimeMillis();
        AppApplication.get().setTimeTag(timeTag);


        homeAdapter = new FragmentPagerAdapter(this, fragmentList);

        for (int i = 0; i < mVideoInfoList.size(); i++) {
            addFragment(mVideoInfoList, i);
        }


        binding.viewPager2.setAdapter(homeAdapter);
        binding.viewPager2.setOffscreenPageLimit(Constant.loadVideoSize);

        if (videos.openType == 2 || videos.openType == 3 || videos.openType == 4 || videos.openType == 5 || videos.openType == 6 || videos.openType == 7) {
            //从附近打开-我的作品-我的私密作品
            if (!isSingle) {
                binding.viewPager2.setCurrentItem(position, false);
            }
            //给列表序号加标记
            AppApplication.get().listPos.put(timeTag, position);
            AppApplication.get().setPlayPosition(position);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //播放指令
                    RxBus.getDefault().post(new VideoEvent(1, TAG));
                }
            }, 500);
        } else if (videos.openType == 1) {
            //请求数据  推荐打开 主播列表
            viewModel.getVideoData(page, videos, userId);
        }

        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (isSingle)
                    return;

                //防止创建新Fragment时候多走一次
                if (position != 0 && position == lastPos) {
                    return;
                }
                AppApplication.get().listPos.put(timeTag, position);
                AppApplication.get().setPlayPosition(position);
                RxBus.getDefault().post(new VideoEvent(1, TAG));
                //判断数据数量滑动到倒数第三个时候去进行加载
                if ((position + 2) == fragmentList.size()) {
                    //加载更多数据
                    page++;
                    viewModel.getVideoData(page, videos, userId);
                }

                lastPos = position;
            }
        });


        binding.srl.setEnabled(!isSingle);

        //        if (openType == 1 && !isMineOpen) {
        //            binding.srl.setEnabled(false);
        //        }

        binding.srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        binding.ivBack.setOnClickListener(lis -> {
            finish();
        });


    }

    public void refreshData() {
        page = 1;
        binding.srl.setRefreshing(false);
        viewModel.getVideoData(page, videos, userId);
    }

    private boolean needRefreshData;

    @Override
    public void onStart() {
        super.onStart();
        if (needRefreshData) {
            needRefreshData = false;
            refreshData();
        }
    }


    @Override
    public void initViewObservable() {
        super.initViewObservable();
        subscribe = RxBus.getDefault().toObservable(EventBean.class).subscribe(event -> {
            if (event != null) {
                if (event.msgId == CodeTable.CODE_SUCCESS && event.content.equals("overLive")) {
                    //判断刷新
                    needRefreshData = true;
                } else if (event.msgId == CodeTable.FINISH_ACT && event.content.equals("video-list")) {
                    finish();
                }
            }
        });

        //接受数据
        viewModel.datas.observe(this, datas -> {
            //            closeLoadView();

            if (datas != null && datas.size() > 0) {
                binding.rlEmpty.setVisibility(View.GONE);
                binding.viewPager2.setVisibility(View.VISIBLE);

                List<VideoInfo> tempList = new ArrayList<>();

                if (videos.openType == 1) {
                    tempList.addAll(datas);
                } else {
                    //先判断是否包含文章有先移除
                    for (VideoInfo data : datas) {
                        if (!data.isArticle()) {
                            tempList.add(data);
                        }
                    }
                }

                if (page == 1) {
                    mVideoInfoList.clear();
                    fragmentList.clear();
                    fragmentPosition = 0;
                }

                mVideoInfoList.addAll(tempList);

                //ViewPage添加
                for (int i = 0; i < tempList.size(); i++) {
                    addFragment(tempList, i);
                }

                if (page == 1) {
                    //需要重new否者会出现缓存
                    homeAdapter = new FragmentPagerAdapter(this, fragmentList);
                    binding.viewPager2.setAdapter(homeAdapter);
                }

            } else {
                if (page > 1) {
                    page--;
                } else {
                    //展示空布局
                    binding.rlEmpty.setVisibility(View.VISIBLE);
                    binding.viewPager2.setVisibility(View.GONE);
                    homeAdapter = new FragmentPagerAdapter(this, fragmentList);
                    binding.viewPager2.setAdapter(homeAdapter);
                }
            }
        });

        //点赞视频列表接受数据
        viewModel.dzDatas.observe(this, datas -> {
            //            closeLoadView();

            if (datas != null && datas.size() > 0) {
                List<VideoInfo> tempList = new ArrayList<>();

                for (DZVideoInfo dzVideoInfo : datas) {
                    if (dzVideoInfo.homeComprehensiveHall != null && !dzVideoInfo.homeComprehensiveHall.isArticle()) {
                        tempList.add(dzVideoInfo.homeComprehensiveHall);
                    }
                }

                if (page == 1) {
                    mVideoInfoList.clear();
                    fragmentList.clear();
                    fragmentPosition = 0;
                }

                mVideoInfoList.addAll(tempList);

                //ViewPage添加
                for (int i = 0; i < tempList.size(); i++) {
                    addFragment(tempList, i);
                }

                if (page == 1) {
                    //需要重new否者会出现缓存
                    homeAdapter = new FragmentPagerAdapter(this, fragmentList);
                    binding.viewPager2.setAdapter(homeAdapter);
                }

            } else {
                if (page > 1) {
                    page--;
                }
            }
        });
    }

    public void addFragment(List<VideoInfo> tempList, int i) {
        if (tempList.get(i) != null) {
            fragmentList.add(new HomeVideoFragment(tempList.get(i), TAG, fragmentPosition, isMineOpen, openType, timeTag));
            fragmentPosition++;
        }

    }
}
