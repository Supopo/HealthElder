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
    private List<VideoInfo> mVideoInfoList;
    private List<VideoInfo> temp;
    private Disposable subscribe;
    private int page = 1;
    private int pageSize = 4;
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

    private String[] pics = {
            "http://qinren.oss-cn-hangzhou.aliyuncs.com/20200804/6bc9be6ee22343beb6d78f7e24204992.png",
            "http://qinren.oss-cn-hangzhou.aliyuncs.com/20200727/608a1b18c21a40e0ad678761d4a0ed17.png",
            "http://qinren.oss-cn-hangzhou.aliyuncs.com/20200804/86791a92d09f4d1595e0124d09156d6f.png",
            "http://qinren.oss-cn-hangzhou.aliyuncs.com/20200727/608a1b18c21a40e0ad678761d4a0ed17.png",
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
        getData();
    }

    private void getData() {
        temp = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            VideoInfo info = new VideoInfo();
            int random = (int) (Math.random() * 7);
            info.resourceUrl = vidoes[random];
            temp.add(info);
        }
    }

    private boolean isInit;

    private void initVideoViews() {
        binding.homeLoadView.setVisibility(View.VISIBLE);
        binding.homeLoadView.start();
        //请求数据
        viewModel.getVideoData(page, pageSize);

        homeAdapter = new FragmentPagerAdapter(fragmentActivity, fragmentList);

        getData();
        for (int i = 0; i < temp.size(); i++) {
            fragmentList.add(new HomeVideoFragment(temp.get(i), TAG, fragmentPosition));
            fragmentPosition++;
        }
        binding.viewPager2.setAdapter(homeAdapter);
        binding.viewPager2.setOffscreenPageLimit(fragmentList.size());
        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                AppApplication.get().setGzPlayPosition(position);
                RxBus.getDefault().post(new VideoEvent(1, TAG));
                //判断数据数量滑动到倒数第三个时候去进行加载
                if ((position + 1) == fragmentList.size()) {
                    //TODO 加载更多数据
                    getData();
                    //加载数据
                    for (int i = 0; i < temp.size(); i++) {
                        fragmentList.add(new HomeVideoFragment(temp.get(i), TAG, fragmentPosition));
                        fragmentPosition++;
                    }
                    homeAdapter.notifyDataSetChanged();
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
