package com.xaqinren.healthyelders.moduleHome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityVideoListBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.adapter.FragmentPagerAdapter;
import com.xaqinren.healthyelders.moduleHome.bean.VideoEvent;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleHome.bean.VideoListBean;
import com.xaqinren.healthyelders.moduleHome.fragment.HomeVideoFragment;
import com.xaqinren.healthyelders.moduleHome.viewModel.VideoListViewModel;

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
    private Disposable subscribe;
    private int page = 1;
    private int position;
    private List<Fragment> fragmentList = new ArrayList<>();
    private FragmentPagerAdapter homeAdapter;
    private int fragmentPosition;//视频Fragment在list中的位置
    private FragmentActivity fragmentActivity;
    private VideoListBean videos;
    private Handler handler;

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

        //从附近打开
        if (videos.type == 2) {
            position = videos.position;
            page = videos.page;
            mVideoInfoList.addAll(videos.videoInfos);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void initData() {
        super.initData();
        handler = new Handler();
        setStatusBarTransparent();

        homeAdapter = new FragmentPagerAdapter(this, fragmentList);

        for (int i = 0; i < mVideoInfoList.size(); i++) {
            fragmentList.add(new HomeVideoFragment(mVideoInfoList.get(i), TAG, fragmentPosition));
            fragmentPosition++;
        }


        binding.viewPager2.setAdapter(homeAdapter);
        binding.viewPager2.setOffscreenPageLimit(Constant.loadVideoSize);

        if (videos.type == 2) {
            //从附近打开
            binding.viewPager2.setCurrentItem(position);
            AppApplication.get().setPlayPosition(position);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //播放指令
                    RxBus.getDefault().post(new VideoEvent(1, TAG));
                }
            }, 500);
        } else if (videos.type == 0) {
            //请求数据  推荐打开 主播列表
            viewModel.getVideoData(page, videos.type);
        }


        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                AppApplication.get().setPlayPosition(position);
                RxBus.getDefault().post(new VideoEvent(1, TAG));
                //判断数据数量滑动到倒数第三个时候去进行加载
                if ((position + 1) == fragmentList.size()) {
                    //加载更多数据
                    page++;
                    viewModel.getVideoData(page, videos.type);
                }


            }
        });

        binding.srl.setEnabled(false);
        binding.srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page++;
                //                showLoadView();
                viewModel.getVideoData(page, videos.type);
                binding.srl.setRefreshing(false);
            }
        });

        binding.ivBack.setOnClickListener(lis -> {
            finish();
        });

    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();

        //接受数据
        viewModel.datas.observe(this, datas -> {
            //            closeLoadView();

            if (datas != null && datas.size() > 0) {
                List<VideoInfo> tempList = new ArrayList<>();

                if (videos.type == 2) {

                    //先判断是否包含文章有先移除
                    for (VideoInfo data : datas) {
                        if (!data.isArticle()) {
                            tempList.add(data);
                        }
                    }

                } else if (videos.type == 0) {
                    tempList.addAll(datas);
                }


                mVideoInfoList.addAll(tempList);

                //ViewPage添加
                for (int i = 0; i < tempList.size(); i++) {
                    fragmentList.add(new HomeVideoFragment(tempList.get(i), TAG, fragmentPosition));
                    fragmentPosition++;
                }
                homeAdapter.notifyDataSetChanged();
            } else {
                if (page > 1) {
                    page--;
                }
            }
        });
    }
}