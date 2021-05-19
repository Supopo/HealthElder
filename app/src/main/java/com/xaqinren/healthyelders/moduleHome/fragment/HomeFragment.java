package com.xaqinren.healthyelders.moduleHome.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.FragmentHomeBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.moduleHome.adapter.HomeVP2Adapter;
import com.xaqinren.healthyelders.moduleHome.adapter.MenuAdapter;
import com.xaqinren.healthyelders.moduleHome.bean.MenuBean;
import com.xaqinren.healthyelders.moduleHome.bean.VideoEvent;
import com.xaqinren.healthyelders.moduleHome.viewModel.HomeViewModel;
import com.xaqinren.healthyelders.moduleLiteav.service.LocationService;

import java.util.ArrayList;
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
    private HomeGZFragment gzFragment;
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
                    if (event.msgType == CodeTable.SHOW_TAB_LAYOUT) {
                        //展示TabLayout
                        binding.rlTabMenu.setVisibility(View.VISIBLE);
                    } else if (event.msgType == CodeTable.SHOW_HOME1_TOP) {
                        //隐藏TabLayout
                        binding.rlTabMenu.setVisibility(View.GONE);
                    }
                }
            }
        });
        RxSubscriptions.add(subscribe);


    }

    private boolean isFirst = true;

    public void initData() {
        super.initData();
        initFragment();
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


                    if (binding.rlTabMenu.getVisibility() == View.GONE) {
                        binding.rlTabMenu.setVisibility(View.VISIBLE);
                    }
                    if (position == 0 && tjFragment.getTopShow()) {
                        binding.rlTabMenu.setVisibility(View.GONE);
                    }

                    //通知HomeVideoFragment做出左右滑动相应操作
                    RxBus.getDefault().post(new VideoEvent(101, position));
                    //通知关注列表页面开始加载数据
                    RxBus.getDefault().post(new EventBean(101, position));

                    if (position == 0) {//推荐
                        RxBus.getDefault().post(new EventBean(CodeTable.EVENT_HOME, CodeTable.SET_MENU_TOUMING));
                    } else if (position == 1) {//关注
                        RxBus.getDefault().post(new EventBean(CodeTable.EVENT_HOME, CodeTable.SET_MENU_TOUMING));
                    } else {//附近
                        RxBus.getDefault().post(new EventBean(CodeTable.EVENT_HOME, CodeTable.SET_MENU_WHITE));
                    }
                }
                isFirst = false;
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        subscribe.dispose();
    }


}
