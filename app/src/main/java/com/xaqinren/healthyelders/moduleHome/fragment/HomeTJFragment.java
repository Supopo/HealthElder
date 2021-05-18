package com.xaqinren.healthyelders.moduleHome.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tencent.qcloud.ugckit.utils.ScreenUtils;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.FragmentHomeTjBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.adapter.FragmentPagerAdapter;
import com.xaqinren.healthyelders.moduleHome.adapter.MenuAdapter;
import com.xaqinren.healthyelders.moduleHome.bean.MenuBean;
import com.xaqinren.healthyelders.moduleHome.bean.VideoEvent;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleHome.viewModel.HomeTJViewModel;
import com.xaqinren.healthyelders.utils.LogUtils;

import org.jetbrains.annotations.NotNull;

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
    private BaseQuickAdapter<MenuBean, BaseViewHolder> menu1Adapter;
    private MenuAdapter menu2Adapter;

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
                if (event.msgId == CodeTable.EVENT_HOME) {
                    if (event.msgType == CodeTable.SHOW_HOME1_TOP) {
                        //展示头布局
                        binding.viewPager2.setUserInputEnabled(false);
                        binding.nsv.setScrollingEnabled(true);
                        binding.rlTop.setVisibility(View.VISIBLE);
                        binding.nsv.fling(0);
                        binding.nsv.smoothScrollTo(0, 0);
                    }
                }
            }
        });
        RxSubscriptions.add(subscribe);

        //接受数据
        viewModel.datas.observe(this, datas -> {
            closeLoadView();

            if (datas != null && datas.size() > 0) {
                if (page == 1) {
                    mVideoInfoList.clear();
                }

                mVideoInfoList.addAll(datas);

                for (int i = 0; i < datas.size(); i++) {
                    fragmentList.add(new HomeVideoFragment(datas.get(i), TAG, fragmentPosition));
                    fragmentPosition++;
                }
                homeAdapter.notifyDataSetChanged();
            }
        });

        viewModel.homeInfo.observe(this, homeRes -> {
            if (homeRes != null) {
                if (homeRes.commodityType != null) {
                    menu1Adapter.setNewInstance(homeRes.contentMenu);
                }
                if (homeRes.contentMenu != null) {
                    menu2Adapter.setNewInstance(homeRes.commodityType);
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
        layoutParams.height = ScreenUtils.getScreenHeight(getActivity());
    }


    @Override
    public void initData() {
        super.initData();
        resetVVPHeight();
        //开始时候有头布局所以禁止滑动
        tjViewPager2 = binding.viewPager2;
        binding.viewPager2.setUserInputEnabled(false);
        initTopMenu();
        initVideoViews();
    }

    private void initTopMenu() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.rvMenu1.setLayoutManager(linearLayoutManager);
        menu1Adapter = new BaseQuickAdapter<MenuBean, BaseViewHolder>(R.layout.item_home_menu) {

            @Override
            protected void convert(@NotNull BaseViewHolder holder, MenuBean item) {
                TextView tvMenu = holder.getView(R.id.tv_menu);
                tvMenu.setText(item.menuName);
                tvMenu.setTextColor(Color.parseColor(item.fontColor));
            }
        };
        binding.rvMenu1.setAdapter(menu1Adapter);

        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getActivity());
        linearLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.rvMenu2.setLayoutManager(linearLayoutManager2);
        menu2Adapter = new MenuAdapter(R.layout.item_home_menu2);
        binding.rvMenu2.setAdapter(menu2Adapter);

        viewModel.getHomeInfo();

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
                        binding.nsv.setScrollingEnabled(false);
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

        binding.srl.setEnabled(false);
        binding.srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                showLoadView();
                viewModel.getVideoData(page);
                binding.srl.setRefreshing(false);
            }
        });

        binding.nsv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY >= (int) getResources().getDimension(R.dimen.dp_237)) {
                    //隐藏头部菜单
                    binding.nsv.setScrollingEnabled(false);
                    binding.viewPager2.setUserInputEnabled(true);
                    binding.rlTop.setVisibility(View.GONE);

                    //判断第一次
                    //设置开始播放第一条
                    if (AppApplication.get().getTjPlayPosition() < 0) {
                        AppApplication.get().setTjPlayPosition(0);
                    }
                    //通知播放页面播放
                    RxBus.getDefault().post(new VideoEvent(1, TAG));

                    binding.srl.setEnabled(true);
                    //通知主页底部变透明
                    RxBus.getDefault().post(new EventBean(CodeTable.EVENT_HOME, CodeTable.SET_MENU_TOUMING));
                    //通知HomeFragment展示TabLayout
                    RxBus.getDefault().post(new EventBean(CodeTable.EVENT_HOME, CodeTable.SHOW_TAB_LAYOUT));
                }
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
