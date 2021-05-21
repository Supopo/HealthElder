package com.xaqinren.healthyelders.moduleHome.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.tencent.qcloud.tim.uikit.utils.ScreenUtil;
import com.tencent.qcloud.ugckit.utils.ScreenUtils;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.FragmentHomeTjBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.adapter.FragmentPagerAdapter;
import com.xaqinren.healthyelders.moduleHome.bean.VideoEvent;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleHome.viewModel.HomeTJViewModel;

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
    private static final String TAG = "home-tj";
    private List<VideoInfo> mVideoInfoList = new ArrayList<>();
    private Disposable subscribe;
    private int page = 1;
    private List<Fragment> fragmentList = new ArrayList<>();
    private FragmentPagerAdapter homeAdapter;
    private int fragmentPosition;//视频Fragment在list中的位置
    private FragmentActivity fragmentActivity;
    public ViewPager2 tjViewPager2;
    public CardView tjCardView;
    private int screenWidth;
    private Handler handler;


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

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        subscribe = RxBus.getDefault().toObservable(EventBean.class).subscribe(event -> {
            if (event != null) {

            }
        });
        RxSubscriptions.add(subscribe);

        //接受数据
        viewModel.datas.observe(this, datas -> {
            closeLoadView();
            if (datas != null && datas.size() > 0) {

                if (page == 1) {
                    mVideoInfoList.clear();
                    fragmentList.clear();
                    fragmentPosition = 0;
                    homeAdapter.notifyDataSetChanged();
                }

                mVideoInfoList.addAll(datas);

                for (int i = 0; i < datas.size(); i++) {
                    fragmentList.add(new HomeVideoFragment(datas.get(i), TAG, fragmentPosition));
                    fragmentPosition++;
                }
                homeAdapter.notifyDataSetChanged();


                if (page == 1 && binding.srl.isRefreshing()) {
                    binding.srl.setRefreshing(false);
                    AppApplication.get().setTjPlayPosition(0);
                    RxBus.getDefault().post(new VideoEvent(1, TAG));
                }

                //判断是是不是展示首页菜单展示模式
                if (AppApplication.get().getLayoutPos() == 0 && AppApplication.get().isShowTopMenu()) {
                    //隐藏视频播放视图层
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            RxBus.getDefault().post(new VideoEvent(10010, 0));
                        }
                    }, 400);
                }



            } else {
                page--;
            }
        });

    }

    private void closeLoadView() {
        binding.loadView.stop();
        binding.loadView.setVisibility(View.GONE);
    }


    public int oldWidth;

    @Override
    public void initData() {
        super.initData();
        handler = new Handler();
        //开始时候有头布局所以禁止滑动
        tjViewPager2 = binding.viewPager2;
        tjCardView = binding.cardView;
        binding.viewPager2.setUserInputEnabled(false);
        screenWidth = ScreenUtil.getScreenWidth(getActivity());

        //变窄viewPager2
        ViewGroup.LayoutParams params = tjCardView.getLayoutParams();
        int dimension = (int) getActivity().getResources().getDimension(R.dimen.dp_20);
        params.height = ScreenUtils.getScreenHeight(getActivity());
        params.width = screenWidth - dimension;
        oldWidth = screenWidth - dimension;
        tjCardView.setLayoutParams(params);
        initVideoViews();
    }

    public void setCardWidth(int width) {
        ViewGroup.LayoutParams params = tjCardView.getLayoutParams();
        params.width = width;
        tjCardView.setLayoutParams(params);
    }

    private void initVideoViews() {

        homeAdapter = new FragmentPagerAdapter(fragmentActivity, fragmentList);

        for (int i = 0; i < mVideoInfoList.size(); i++) {
            fragmentList.add(new HomeVideoFragment(mVideoInfoList.get(i), TAG, fragmentPosition));
            fragmentPosition++;
        }

        showLoadView();
        //请求数据
        viewModel.getVideoData(page);
        binding.viewPager2.setAdapter(homeAdapter);
        binding.viewPager2.setOffscreenPageLimit(Constant.loadVideoSize);

        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                //第一次加载所有Fragment完会触发
                if (!firstInit) {
                    if (position == 0) {
                        binding.viewPager2.setUserInputEnabled(true);
                    }

                    AppApplication.get().setTjPlayPosition(position);
                    RxBus.getDefault().post(new VideoEvent(1, TAG));
                    //判断数据数量滑动到倒数第三个时候去进行加载
                    if ((position + 1) == fragmentList.size()) {
                        //加载更多数据
                        page++;
                        viewModel.getVideoData(page);
                    }
                }

                firstInit = false;
            }
        });

        binding.srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                showLoadView();
                viewModel.getVideoData(page);
            }
        });
    }

    private void showLoadView() {
        binding.loadView.setVisibility(View.VISIBLE);
        binding.loadView.start();
    }

    private boolean firstInit = true;


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        subscribe.dispose();
    }
}
