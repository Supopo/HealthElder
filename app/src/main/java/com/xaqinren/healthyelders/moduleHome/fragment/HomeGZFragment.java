package com.xaqinren.healthyelders.moduleHome.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tencent.qcloud.ugckit.utils.ScreenUtils;
import com.tencent.qcloud.xiaoshipin.play.PlayerInfo;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXVodPlayConfig;
import com.tencent.rtmp.TXVodPlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.FragmentHomeGzBinding;
import com.xaqinren.healthyelders.databinding.FragmentHomeTjBinding;
import com.xaqinren.healthyelders.databinding.ItemVideoListBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.adapter.FragmentPagerAdapter;
import com.xaqinren.healthyelders.moduleHome.bean.VideoEvent;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleHome.viewModel.HomeGZViewModel;
import com.xaqinren.healthyelders.moduleHome.viewModel.HomeTJViewModel;
import com.xaqinren.healthyelders.utils.LogUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
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
    private FragmentPagerAdapter homeAdapter;
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


    @Override
    public void initViewObservable() {
        super.initViewObservable();
        subscribe = RxBus.getDefault().toObservable(EventBean.class).subscribe(event -> {
            if (event != null) {
                if (event.msgId == CodeTable.EVENT_HOME) {
                    if (event.msgType == CodeTable.SET_MENU_TOUMING) {
                        //底部菜单变透明 开启vp2滑动
                        binding.viewPager2.setUserInputEnabled(true);
                    } else if (event.msgType == CodeTable.SET_MENU_WHITE) {
                        //底部菜单变白，头布局处理
                        binding.viewPager2.setUserInputEnabled(false);
                    }
                } else if (event.msgId == 101 && event.msgType == 1) {
                    //判断是不是第一次切换到关注列表
                    if (!isInit) {
                        initVideoViews();
                    }
                }
            }
        });
        RxSubscriptions.add(subscribe);
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
    }


    private boolean isInit;//设置懒加载，点到关注才开始加载

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
        isInit = true;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        subscribe.dispose();
    }
}
