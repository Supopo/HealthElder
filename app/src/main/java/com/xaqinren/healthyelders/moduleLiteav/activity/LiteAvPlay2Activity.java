package com.xaqinren.healthyelders.moduleLiteav.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tencent.qcloud.ugckit.utils.Signature;
import com.tencent.qcloud.xiaoshipin.mainui.list.TCVideoInfo;
import com.tencent.qcloud.xiaoshipin.play.PlayerInfo;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXVodPlayConfig;
import com.tencent.rtmp.TXVodPlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityLiteAvPlay2Binding;
import com.xaqinren.healthyelders.databinding.ItemVideoListBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.moduleHome.adapter.FragmentPagerAdapter;
import com.xaqinren.healthyelders.moduleHome.bean.VideoEvent;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleHome.fragment.HomeVideoFragment;
import com.xaqinren.healthyelders.moduleLiteav.fragment.LiteAvPlayFragment;
import com.xaqinren.healthyelders.moduleLiteav.viewModel.LiteAvPlayViewModel;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.bus.RxBus;

public class LiteAvPlay2Activity extends BaseActivity<ActivityLiteAvPlay2Binding, LiteAvPlayViewModel> {

    private static final String TAG = "LiteAvPlayActivity2";
    private int page = 1;
    private int pageSize = 10;
    private List<Fragment> fragmentList = new ArrayList<>();
    private int homePosition;
    private List<VideoInfo> mVideoInfoList;
    private FragmentPagerAdapter homeAdapter;
    public static final int PLAY_SIGNAL = 10038;


    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_lite_av_play_2;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        rlTitle.setVisibility(View.GONE);
        setStatusBarTransparent();
        mVideoInfoList = new ArrayList<>();
        testData();
        homePosition = 0;
        homeAdapter = new FragmentPagerAdapter(this, fragmentList);
        for (int i = 0; i < mVideoInfoList.size(); i++) {
            fragmentList.add(new LiteAvPlayFragment(mVideoInfoList.get(i), "VIDEO", homePosition));
            homePosition++;
        }
        binding.viewPager2.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        binding.viewPager2.setAdapter(homeAdapter);
        binding.viewPager2.setOffscreenPageLimit(5);
        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                AppApplication.get().setPlayPosition(position);
                RxBus.getDefault().post(new VideoEvent(PLAY_SIGNAL));
            }
        });

    }
    private void testData() {
        int time = (int) (System.currentTimeMillis() / 1000 + (24 * 60 * 60 * 1));
        String t = Signature.getTimeExpire(time);
        String[] urls = {
                "http://1302448977.vod2.myqcloud.com/2734970fvodcq1302448977/96104fa55285890818301219505/HuATs9Bs4wwA.mp4",
                "http://1302448977.vod2.myqcloud.com/2734970fvodcq1302448977/9f646aec5285890818301618671/u8wAGSjnujsA.mp4",
                "http://1302448977.vod2.myqcloud.com/2734970fvodcq1302448977/5dd176fa5285890818294517667/RaxZKTDhg8AA.mp4",
        };
        for (int i = 0; i < 3; i++) {
            try {
                String playToken = Signature.singVideo(urls[i], t);
                VideoInfo info = new VideoInfo();
                info.resourceUrl = urls[i] + "?t=" + t + "&sign=" + playToken;
                info.resourceType = "VIDEO";
                mVideoInfoList.add(info);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}
