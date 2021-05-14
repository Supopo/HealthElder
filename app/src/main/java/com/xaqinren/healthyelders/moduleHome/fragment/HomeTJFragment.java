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
import com.xaqinren.healthyelders.utils.LogUtils;

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
    private List<VideoInfo> temp;
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

    private String[] pics = {
            "http://qinren.oss-cn-hangzhou.aliyuncs.com/20200727/608a1b18c21a40e0ad678761d4a0ed17.png",
            "http://qinren.oss-cn-hangzhou.aliyuncs.com/20200804/86791a92d09f4d1595e0124d09156d6f.png",
            "http://qinren.oss-cn-hangzhou.aliyuncs.com/20200804/6bc9be6ee22343beb6d78f7e24204992.png",
            "http://qinren.oss-cn-hangzhou.aliyuncs.com/20200804/86791a92d09f4d1595e0124d09156d6f.png",
    };
    private String[] vidoes = {
            "http://qinren.oss-cn-hangzhou.aliyuncs.com/20200727/cbac2f83b8cd41aeab99f330c9149eab.mp4",
            "http://qinren.oss-cn-hangzhou.aliyuncs.com/20200804/cf913ed075eb4b9bb0dfd0ef17255167.mp4",
            "http://qinren.oss-cn-hangzhou.aliyuncs.com/20200804/0a7cab4596374f06a8cf0481f754b302.mp4",
            "http://qinren.oss-cn-hangzhou.aliyuncs.com/20200805/89e502164fb6482d8feee24f05ac751d.mp4",
            "http://qinren.oss-cn-hangzhou.aliyuncs.com/20201225/427d7d9635a240d8b1eb7bf2a03c2e35.mp4",
            "http://qinren.oss-cn-hangzhou.aliyuncs.com/20201225/7b7d936e94424af9bc31aafa0a0de760.mp4",
            "http://qinren.oss-cn-hangzhou.aliyuncs.com/20201224/def8cf20cd3046999f247de0a1b55199.mp4",
            "http://qinren.oss-cn-hangzhou.aliyuncs.com/20201222/69504846fdaf4e86ac8c82470175e2aa.mp4",
    };


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

    private void getData() {
        temp = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            int random = (int) (Math.random() * 7);
            VideoInfo info = new VideoInfo();
            info.resourceUrl = vidoes[random];
            temp.add(info);
        }
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
        binding.viewPager2.setOffscreenPageLimit(3);

        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                //第一次加载所有Fragment完会触发
                if (!first) {
                    LogUtils.v(Constant.TAG_LIVE, "上下滑动1：" + position);
                    AppApplication.get().setTjPlayPosition(position);
                    RxBus.getDefault().post(new VideoEvent(1, TAG));
                    //判断数据数量滑动到倒数第三个时候去进行加载
                    if ((position + 1) == fragmentList.size()) {
                        //加载更多数据
                        page++;
                        viewModel.getVideoData(page);
                    }
                }

                first = false;
            }
        });
    }

    private boolean first = true;


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        subscribe.dispose();
    }
}
