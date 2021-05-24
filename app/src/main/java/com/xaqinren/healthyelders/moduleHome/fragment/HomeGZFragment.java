package com.xaqinren.healthyelders.moduleHome.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.tencent.qcloud.tim.uikit.utils.ScreenUtil;
import com.tencent.qcloud.ugckit.utils.ScreenUtils;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.FragmentHomeGzBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.LockableNestedScrollView;
import com.xaqinren.healthyelders.moduleHome.adapter.FragmentPagerAdapter;
import com.xaqinren.healthyelders.moduleHome.adapter.ZhiBoingAvatarAdapter;
import com.xaqinren.healthyelders.moduleHome.bean.VideoEvent;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleHome.viewModel.HomeGZViewModel;
import com.xaqinren.healthyelders.moduleZhiBo.activity.LiveGuanzhongActivity;
import com.xaqinren.healthyelders.utils.MScreenUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;

/**
 * Created by Lee. on 2021/5/11.
 * 首页关注列表Fragment
 */
public class HomeGZFragment extends BaseFragment<FragmentHomeGzBinding, HomeGZViewModel> {
    private static final String TAG = "home-gz";
    private List<VideoInfo> mVideoInfoList = new ArrayList<>();
    private Disposable subscribe;
    private int page = 1;
    private List<Fragment> fragmentList = new ArrayList<>();
    private FragmentPagerAdapter videoAdapter;
    private int fragmentPosition;//视频Fragment在list中的位置
    private FragmentActivity fragmentActivity;

    public HomeGZFragment(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
    }

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_home_gz;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    public void setVP2Enabled(boolean enabled) {
        if (enabled) {
            if (binding.rlTop.getVisibility() == View.GONE) {//顶部未展示
                binding.nsv.setScrollingEnabled(false);
                binding.viewPager2.setUserInputEnabled(true);
            } else {
                binding.nsv.setScrollingEnabled(true);
                binding.viewPager2.setUserInputEnabled(false);
            }
        } else {
            binding.nsv.setScrollingEnabled(false);
            binding.viewPager2.setUserInputEnabled(false);
        }
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        subscribe = RxBus.getDefault().toObservable(EventBean.class).subscribe(event -> {
            if (event != null) {
                if (event.msgId == 101 && event.msgType == 1) {
                    //判断是不是第一次切换到关注列表
                    if (!isInit) {
                        //在切过来之后设置不然会导致HomeFragment里面的NSV滑动
                        resetVVPHeight();
                        initVideoViews();
                    }
                }
            }
        });
        RxSubscriptions.add(subscribe);
        viewModel.datas.observe(this, datas -> {

            closeLoadView();

            if (datas != null && datas.size() > 0) {
                if (page == 1) {
                    viewModel.getLiveFiends();

                    mVideoInfoList.clear();
                    fragmentList.clear();
                    fragmentPosition = 0;
                    //需要重new否者会出现缓存
                    videoAdapter = new FragmentPagerAdapter(fragmentActivity, fragmentList);
                    binding.viewPager2.setAdapter(videoAdapter);
                }

                mVideoInfoList.addAll(datas);

                for (int i = 0; i < datas.size(); i++) {
                    fragmentList.add(new HomeVideoFragment(datas.get(i), TAG, fragmentPosition));
                    fragmentPosition++;
                }
                videoAdapter.notifyDataSetChanged();

                if (page == 1 && binding.srl.isRefreshing()) {
                    binding.srl.setRefreshing(false);
                    AppApplication.get().setTjPlayPosition(0);
                    RxBus.getDefault().post(new VideoEvent(1, TAG));
                }
            } else {
                page--;
            }
        });
        viewModel.firendDatas.observe(this, list -> {
            if (list != null && list.size() > 0) {
                binding.rlTop.setVisibility(View.VISIBLE);
                binding.llShowTop.setVisibility(View.GONE);
                binding.tvShowZb.setText(list.size() + "个直播");
                binding.nsv.smoothScrollTo(0, 0);
                zbingAdapter.setNewInstance(list);
                binding.viewPager2.setUserInputEnabled(false);
                binding.nsv.setScrollingEnabled(true);
            } else {
                binding.rlTop.setVisibility(View.GONE);
                binding.llShowTop.setVisibility(View.VISIBLE);
                binding.viewPager2.setUserInputEnabled(true);
                binding.nsv.setScrollingEnabled(false);
            }
        });
        viewModel.liveInfo.observe(this, liveInfo -> {
            if (liveInfo != null) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constant.LiveInitInfo, liveInfo);
                startActivity(LiveGuanzhongActivity.class, bundle);
            }
        });
        viewModel.dismissDialog.observe(this, dismiss -> {
            if (dismiss != null) {
                if (dismiss) {
                    dismissDialog();
                }
            }
        });
        viewModel.closeRsl.observe(this, closeRsl -> {
            if (closeRsl != null && closeRsl) {
                if (binding.srl.isRefreshing()) {
                    binding.srl.setRefreshing(false);
                }
            }
        });
    }

    private void closeLoadView() {
        binding.loadView.stop();
        binding.loadView.setVisibility(View.GONE);
    }

    public void resetVVPHeight() {
        ViewGroup.LayoutParams layoutParams = binding.viewPager2.getLayoutParams();
        layoutParams.height = MScreenUtil.getScreenHeight(getActivity());
    }

    private ZhiBoingAvatarAdapter zbingAdapter;

    private void initZBingAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.rvZbList.setLayoutManager(linearLayoutManager);
        zbingAdapter = new ZhiBoingAvatarAdapter(R.layout.item_zbing_avatar);
        binding.rvZbList.setAdapter(zbingAdapter);

        zbingAdapter.setOnItemClickListener(((adapter, view, position) -> {
            viewModel.joinLive(zbingAdapter.getData().get(position).liveRoomId);
        }));
    }

    @Override
    public void initData() {
        super.initData();
        //开始时候可能有头布局所以禁止滑动
        binding.viewPager2.setUserInputEnabled(false);
    }


    private boolean isInit;//设置懒加载，点到关注才开始加载

    private void initVideoViews() {

        videoAdapter = new FragmentPagerAdapter(fragmentActivity, fragmentList);


        for (int i = 0; i < mVideoInfoList.size(); i++) {
            fragmentList.add(new HomeVideoFragment(mVideoInfoList.get(i), TAG, fragmentPosition));
            fragmentPosition++;
        }

        showLoadView();
        //请求数据
        viewModel.getVideoData(page);

        binding.viewPager2.setAdapter(videoAdapter);
        binding.viewPager2.setOffscreenPageLimit(Constant.loadVideoSize);
        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                AppApplication.get().setGzPlayPosition(position);
                RxBus.getDefault().post(new VideoEvent(1, TAG));
                //判断数据数量滑动到倒数第三个时候去进行加载
                if ((position + 1) == fragmentList.size()) {
                    //加载更多数据
                    page++;
                    viewModel.getVideoData(page);
                }
            }
        });

        initZBingAdapter();


        binding.nsv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY >= (int) getResources().getDimension(R.dimen.dp_218)) {
                    binding.rlTop.setVisibility(View.GONE);
                    binding.llShowTop.setVisibility(View.VISIBLE);
                    binding.nsv.setScrollingEnabled(false);
                    binding.viewPager2.setUserInputEnabled(true);
                }
            }
        });

        binding.srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                showLoadView();
                viewModel.getVideoData(page);
                //判断
                if (binding.rlTop.getVisibility() == View.GONE) {
                    binding.nsv.setScrollingEnabled(false);
                    binding.viewPager2.setUserInputEnabled(true);
                } else {
                    binding.nsv.setScrollingEnabled(true);
                    binding.viewPager2.setUserInputEnabled(false);
                }
            }
        });


        binding.llShowTop.setOnClickListener(lis -> {
            binding.rlTop.setVisibility(View.VISIBLE);
            binding.llShowTop.setVisibility(View.GONE);
            binding.nsv.setScrollingEnabled(true);
            binding.viewPager2.setUserInputEnabled(false);
        });

        isInit = true;
    }

    private void showLoadView() {
        binding.loadView.setVisibility(View.VISIBLE);
        binding.loadView.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        subscribe.dispose();
    }
}
