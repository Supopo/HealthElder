package com.xaqinren.healthyelders.moduleHome.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.tencent.qcloud.ugckit.utils.ScreenUtils;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.FragmentHomeTjBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.CodeTable;
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
                    if (event.msgType == CodeTable.SET_MENU_TOUMING) {
                        //底部菜单变透明 头布局隐藏 开启vp2滑动
                        binding.viewPager2.setUserInputEnabled(true);
                        //第一次 开始播放第一条
                        if (AppApplication.get().getTjPlayPosition() < 0) {
                            AppApplication.get().setTjPlayPosition(0);
                        }
                        RxBus.getDefault().post(new VideoEvent(1, TAG));
                    } else if (event.msgType == CodeTable.SET_MENU_WHITE) {
                        //底部菜单变白 说明头布局出现
                        binding.viewPager2.setUserInputEnabled(false);

                    }
                }
            }
        });
        RxSubscriptions.add(subscribe);

        //接受数据
        viewModel.datas.observe(this, datas -> {
            binding.homeLoadView.stop();
            binding.homeLoadView.setVisibility(View.GONE);

            if (datas != null && datas.size() > 0) {
                mVideoInfoList.addAll(datas);

                for (int i = 0; i < datas.size(); i++) {
                    fragmentList.add(new HomeVideoFragment(datas.get(i), TAG, fragmentPosition));
                    fragmentPosition++;
                }
                homeAdapter.notifyDataSetChanged();
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
        initVideoViews();
    }

    private void initVideoViews() {
        binding.homeLoadView.setVisibility(View.VISIBLE);
        binding.homeLoadView.start();
        //请求数据
        viewModel.getVideoData(page);

        homeAdapter = new FragmentPagerAdapter(fragmentActivity, fragmentList);

        for (int i = 0; i < mVideoInfoList.size(); i++) {
            fragmentList.add(new HomeVideoFragment(mVideoInfoList.get(i), TAG, fragmentPosition));
            fragmentPosition++;
        }
        binding.viewPager2.setAdapter(homeAdapter);
        binding.viewPager2.setOffscreenPageLimit(Constant.loadVideoSize);

        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                //第一次加载所有Fragment完会触发
                if (!firstInit) {
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
    }

    private boolean firstInit = true;


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        subscribe.dispose();
    }
}
