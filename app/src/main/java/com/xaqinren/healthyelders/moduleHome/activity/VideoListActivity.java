package com.xaqinren.healthyelders.moduleHome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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
    private int page;
    private int position;
    private List<Fragment> fragmentList = new ArrayList<>();
    private FragmentPagerAdapter homeAdapter;
    private int fragmentPosition;//视频Fragment在list中的位置
    private FragmentActivity fragmentActivity;
    private VideoListBean videos;

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

        position = videos.position;
        page = videos.page;
        mVideoInfoList.addAll(videos.videoInfos);
    }

    @Override
    public void initData() {
        super.initData();

        setStatusBarTransparent();

        homeAdapter = new FragmentPagerAdapter(this, fragmentList);

        for (int i = 0; i < mVideoInfoList.size(); i++) {
            fragmentList.add(new HomeVideoFragment(mVideoInfoList.get(i), TAG, fragmentPosition));
            fragmentPosition++;
        }


        binding.viewPager2.setAdapter(homeAdapter);
        binding.viewPager2.setOffscreenPageLimit(Constant.loadVideoSize);
        binding.viewPager2.setCurrentItem(position);

        AppApplication.get().setPlayPosition(position);
        //播放指令
        RxBus.getDefault().post(new VideoEvent(1, TAG));


        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Log.e("--", "----------------------------pos: " + position);
                AppApplication.get().setPlayPosition(position);
                RxBus.getDefault().post(new VideoEvent(1, TAG));
                //判断数据数量滑动到倒数第三个时候去进行加载
                if ((position + 1) == fragmentList.size()) {
                    //加载更多数据
                    page++;
                    viewModel.getVideoData(page);
                }


            }
        });

        binding.srl.setEnabled(false);
        binding.srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page++;
                //                showLoadView();
                viewModel.getVideoData(page);
                binding.srl.setRefreshing(false);
            }
        });

    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();

        //接受数据
        viewModel.datas.observe(this, datas -> {
            //            closeLoadView();

            if (datas != null && datas.size() > 0) {

                //先判断是否包含文章有先移除
                List<VideoInfo> tempList = new ArrayList<>();
                for (VideoInfo data : datas) {
                    if (!data.isArticle()) {
                        tempList.add(data);
                    }
                }

                mVideoInfoList.addAll(tempList);

                for (int i = 0; i < datas.size(); i++) {
                    fragmentList.add(new HomeVideoFragment(datas.get(i), TAG, fragmentPosition));
                    fragmentPosition++;
                }
                homeAdapter.notifyDataSetChanged();
            } else {
                page--;
            }
        });
    }
}
