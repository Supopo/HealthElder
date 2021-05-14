package com.xaqinren.healthyelders.moduleHome.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.FragmentHomeBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.adapter.HomeVP2Adapter;
import com.xaqinren.healthyelders.moduleHome.adapter.MenuAdapter;
import com.xaqinren.healthyelders.moduleHome.bean.GirlsBean;
import com.xaqinren.healthyelders.moduleHome.bean.VideoEvent;
import com.xaqinren.healthyelders.moduleHome.viewModel.HomeViewModel;
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
    private String[] menuTitles = {"美食厨房", "民间偏方", "运动健身", "家有良医", "美食厨房", "民间偏方", "运动健身"};
    public ViewPager2 vp2;

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
                        binding.tabLayout.setVisibility(View.GONE);
                        binding.rlTop.setVisibility(View.VISIBLE);
                        binding.nsv.setScrollingEnabled(true);
                        //滚回到顶部
                        binding.nsv.fling(0);
                        binding.nsv.smoothScrollTo(0, 0);
                    }
                }
            }
        });
        RxSubscriptions.add(subscribe);
    }

    private boolean isFirst = true;

    public void initData() {
        super.initData();
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new HomeTJFragment(getActivity()));
        fragments.add(new HomeGZFragment(getActivity()));
        fragments.add(new HomeFJFragment());
        HomeVP2Adapter vp2Adapter = new HomeVP2Adapter(getActivity(), fragments);
        vp2 = binding.viewPager2;
        binding.viewPager2.setAdapter(vp2Adapter);
        binding.viewPager2.setOffscreenPageLimit(1);
        binding.tabLayout.setViewPager2(binding.viewPager2, titles);
        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                AppApplication.get().setLayoutPos(position);
                //第一次加载完所有Fragment会触发
                if (!isFirst) {
                    LogUtils.v(Constant.TAG_LIVE, "左右滑动：" + position);
                    //通知HomeVideoFragment做出左右滑动相应操作
                    RxBus.getDefault().post(new VideoEvent(101, position));
                    //通知关注列表页面开始加载数据
                    RxBus.getDefault().post(new EventBean(101, position));
                }
                isFirst = false;
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.nsv.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY >= getResources().getDimension(R.dimen.dp_237)) {
                        //通知首页底部菜单栏变透明
                        RxBus.getDefault().post(new EventBean(CodeTable.EVENT_HOME, CodeTable.SET_MENU_TOUMING));
                        binding.nsv.setScrollingEnabled(false);
                        binding.rlTop.setVisibility(View.GONE);
                        binding.tabLayout.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        initTopMenu();

    }

    private void initTopMenu() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.rlMenu1.setLayoutManager(linearLayoutManager);
        BaseQuickAdapter<String, BaseViewHolder> menu1Adapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_home_menu) {

            @Override
            protected void convert(@NotNull BaseViewHolder holder, String s) {
                holder.setText(R.id.tv_menu, s);
            }
        };
        binding.rlMenu1.setAdapter(menu1Adapter);
        menu1Adapter.setNewInstance(new ArrayList<String>(Arrays.asList(menuTitles)));

        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getActivity());
        linearLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.rvMenu2.setLayoutManager(linearLayoutManager2);
        MenuAdapter menuAdapter = new MenuAdapter(R.layout.item_home_menu2);
        binding.rvMenu2.setAdapter(menuAdapter);

        List<GirlsBean> menu = new ArrayList();
        for (int i = 0; i < 8; i++) {
            GirlsBean girlsBean = new GirlsBean();
            menu.add(girlsBean);
        }
        menuAdapter.setNewInstance(menu);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        subscribe.dispose();
    }


}
