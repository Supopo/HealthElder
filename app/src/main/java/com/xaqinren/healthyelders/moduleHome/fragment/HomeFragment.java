package com.xaqinren.healthyelders.moduleHome.fragment;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnMultiPurposeListener;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.FragmentHomeBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.adapter.HomeVP2Adapter;
import com.xaqinren.healthyelders.moduleHome.adapter.MenuAdapter;
import com.xaqinren.healthyelders.moduleHome.adapter.ZhiBoingAvatarAdapter;
import com.xaqinren.healthyelders.moduleHome.bean.GirlsBean;
import com.xaqinren.healthyelders.moduleHome.bean.MenuBean;
import com.xaqinren.healthyelders.moduleHome.bean.VideoEvent;
import com.xaqinren.healthyelders.moduleHome.viewModel.HomeViewModel;
import com.xaqinren.healthyelders.moduleLiteav.service.LocationService;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBUserListBean;
import com.xaqinren.healthyelders.utils.LogUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;

/**
 * Created by Lee. on 2021/5/11.
 * 首页Fragment
 */
public class HomeFragment extends BaseFragment<FragmentHomeBinding, HomeViewModel> {
    private static final String TAG = "HomeFragment";
    private Disposable subscribe;
    private String[] titles = {"推荐", "关注", "附近"};
    public ViewPager2 vp2;
    private BaseQuickAdapter<MenuBean, BaseViewHolder> menu1Adapter;
    private MenuAdapter menu2Adapter;
    private HomeGZFragment gzFragment;
    private ZhiBoingAvatarAdapter zbingAdapter;
    private HomeTJFragment tjFragment;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_home;
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
                    if (event.msgType == CodeTable.SET_MENU_WHITE) {
                        scrollTop();
                    }
                }
            }
        });
        RxSubscriptions.add(subscribe);

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

    private boolean isFirst = true;

    public void initData() {
        super.initData();
        initFragment();
        initTopMenu();
        viewModel.getHomeInfo();
        LocationService.startService(getActivity());
    }

    private void initFragment() {
        List<Fragment> fragments = new ArrayList<>();
        tjFragment = new HomeTJFragment(getActivity());
        gzFragment = new HomeGZFragment(getActivity());
        fragments.add(tjFragment);
        fragments.add(gzFragment);
        fragments.add(new HomeFJFragment());
        HomeVP2Adapter vp2Adapter = new HomeVP2Adapter(getActivity(), fragments);
        vp2 = binding.viewPager2;

        binding.viewPager2.setAdapter(vp2Adapter);
        binding.viewPager2.setOffscreenPageLimit(2);
        binding.tabLayout.setViewPager2(binding.viewPager2, titles);
        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                AppApplication.get().setLayoutPos(position);
                //第一次加载完所有Fragment会触发
                if (!isFirst) {
                    //通知HomeVideoFragment做出左右滑动相应操作
                    RxBus.getDefault().post(new VideoEvent(101, position));
                    //通知关注列表页面开始加载数据
                    RxBus.getDefault().post(new EventBean(101, position));

                    if (position == 1) {
                        //判断有没有直播中
                        scrollTop();
                        binding.tvShowZb.setText("个直播");
                    } else if (position == 2) {
                        binding.tvShowZb.setText("回到首页");
                        binding.rlZbList.setVisibility(View.GONE);
                        RxBus.getDefault().post(new EventBean(CodeTable.EVENT_HOME, CodeTable.SET_MENU_WHITE));
                    } else {
                        binding.rlZbList.setVisibility(View.GONE);
                        binding.tvShowZb.setText("回到首页");
                        RxBus.getDefault().post(new EventBean(CodeTable.EVENT_HOME, CodeTable.SET_MENU_TOUMING));
                    }
                }
                isFirst = false;
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.nsv.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    LogUtils.v(Constant.TAG_LIVE, "滑动：" + scrollY);
                    if (AppApplication.get().getLayoutPos() == 0) {
                        if (scrollY >= (int) getResources().getDimension(R.dimen.dp_237)) {
                            LogUtils.v(Constant.TAG_LIVE, "滑动1：" + getResources().getDimension(R.dimen.dp_237));
                            //通知首页底部菜单栏变透明
                            RxBus.getDefault().post(new EventBean(CodeTable.EVENT_HOME, CodeTable.SET_MENU_TOUMING));
                            binding.nsv.setScrollingEnabled(false);
                            binding.rlTop.setVisibility(View.GONE);
                            binding.rlTabMenu.setVisibility(View.VISIBLE);
                        } else {
                            binding.llShowTop.setVisibility(View.VISIBLE);
                        }
                    } else if (AppApplication.get().getLayoutPos() == 1) {
                        if (scrollY >= (int) getResources().getDimension(R.dimen.dp_218)) {
                            LogUtils.v(Constant.TAG_LIVE, "滑动2：" + getResources().getDimension(R.dimen.dp_218));
                            binding.nsv.setScrollingEnabled(false);
                            gzFragment.gzViewPager2.setUserInputEnabled(true);
                            binding.rlZbList.setVisibility(View.GONE);
                        } else {
                            binding.llShowTop.setVisibility(View.VISIBLE);
                        }
                    }

                }
            });
        }

        binding.llShowTop.setOnClickListener(lis -> {
            scrollTop();
        });

        initZBingAdapter();
    }

    private void initZBingAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.rvZbList.setLayoutManager(linearLayoutManager);
        zbingAdapter = new ZhiBoingAvatarAdapter(R.layout.item_zbing_avatar);
        binding.rvZbList.setAdapter(zbingAdapter);


        List<ZBUserListBean> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ZBUserListBean bean = new ZBUserListBean();
            list.add(bean);
        }
        zbingAdapter.setNewInstance(list);
    }

    //滚回到顶部
    private void scrollTop() {
        binding.nsv.setScrollingEnabled(true);
        binding.nsv.fling(0);
        binding.nsv.smoothScrollTo(0, 0);

        if (AppApplication.get().getLayoutPos() == 0) {
            tjFragment.tjViewPager2.setUserInputEnabled(false);
            binding.rlTabMenu.setVisibility(View.GONE);
            binding.rlTop.setVisibility(View.VISIBLE);
            binding.rlZbList.setVisibility(View.GONE);
        } else if (AppApplication.get().getLayoutPos() == 1) {
            gzFragment.gzViewPager2.setUserInputEnabled(false);
            binding.rlTabMenu.setVisibility(View.VISIBLE);
            binding.rlTop.setVisibility(View.GONE);
            binding.rlZbList.setVisibility(View.VISIBLE);
        }
        binding.llShowTop.setVisibility(View.GONE);

    }

    private void initTopMenu() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.rlMenu1.setLayoutManager(linearLayoutManager);
        menu1Adapter = new BaseQuickAdapter<MenuBean, BaseViewHolder>(R.layout.item_home_menu) {

            @Override
            protected void convert(@NotNull BaseViewHolder holder, MenuBean item) {
                TextView tvMenu = holder.getView(R.id.tv_menu);
                tvMenu.setText(item.menuName);
                tvMenu.setTextColor(android.graphics.Color.parseColor(item.fontColor));
            }
        };
        binding.rlMenu1.setAdapter(menu1Adapter);

        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getActivity());
        linearLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.rvMenu2.setLayoutManager(linearLayoutManager2);
        menu2Adapter = new MenuAdapter(R.layout.item_home_menu2);
        binding.rvMenu2.setAdapter(menu2Adapter);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        subscribe.dispose();
    }


}
