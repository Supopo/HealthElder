package com.xaqinren.healthyelders.moduleHome.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tencent.qcloud.tim.uikit.utils.ScreenUtil;
import com.tencent.qcloud.ugckit.utils.ScreenUtils;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.FragmentHomeBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.moduleHome.activity.SearchActivity;
import com.xaqinren.healthyelders.moduleHome.activity.VideoGridActivity;
import com.xaqinren.healthyelders.moduleHome.activity.VideoListActivity;
import com.xaqinren.healthyelders.moduleHome.adapter.HomeVP2Adapter;
import com.xaqinren.healthyelders.moduleHome.adapter.MenuAdapter;
import com.xaqinren.healthyelders.moduleHome.bean.MenuBean;
import com.xaqinren.healthyelders.moduleHome.bean.VideoEvent;
import com.xaqinren.healthyelders.moduleHome.bean.VideoListBean;
import com.xaqinren.healthyelders.moduleHome.viewModel.HomeViewModel;
import com.xaqinren.healthyelders.moduleLiteav.service.LocationService;
import com.xaqinren.healthyelders.moduleZhiBo.bean.GoodsBean;
import com.xaqinren.healthyelders.uniApp.UniService;
import com.xaqinren.healthyelders.uniApp.UniUtil;
import com.xaqinren.healthyelders.uniApp.bean.UniEventBean;
import com.xaqinren.healthyelders.utils.MScreenUtil;
import com.xaqinren.healthyelders.utils.UrlUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.utils.ToastUtils;

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
    private HomeFJFragment fjFragment;
    private int screenWidth;
    private BaseQuickAdapter<MenuBean, BaseViewHolder> menu1Adapter;
    private MenuAdapter menu2Adapter;
    private Disposable uniSubscribe;
    private int clickIndex;


    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_home;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }


    public boolean isShowTop = true;
    @Override
    public void initViewObservable() {
        super.initViewObservable();
        subscribe = RxBus.getDefault().toObservable(EventBean.class).subscribe(event -> {
            if (event != null) {
                if (event.msgId == CodeTable.EVENT_HOME) {
                    if (event.msgType == CodeTable.SHOW_TAB_LAYOUT) {
                        isShowTop = false;

                        //展示TabLayout
                        binding.rlTabMenu.setVisibility(View.VISIBLE);
                    } else if (event.msgType == CodeTable.SHOW_HOME1_TOP) {
                        AppApplication.get().setShowTopMenu(true);
                        isShowTop = true;

                        //隐藏视频播放视图层
                        RxBus.getDefault().post(new VideoEvent(10010, 0));

                        //隐藏TabLayout
                        binding.rlTabMenu.setVisibility(View.GONE);

                        if (AppApplication.get().getLayoutPos() == 0) {
                            //推荐viewPager2 变窄
                            int dimension = (int) getActivity().getResources().getDimension(R.dimen.dp_20);
                            tjFragment.setCardWidth(screenWidth - dimension);
                            tjFragment.tjCardView.setRadius(getResources().getDimension(R.dimen.dp_14));
                            tjFragment.tjViewPager2.setUserInputEnabled(false);
                        } else if (AppApplication.get().getLayoutPos() == 1) {
                            gzFragment.setVP2Enabled(false);
                        } else {
                            fjFragment.recyclerView.setNestedScrollingEnabled(false);
                            fjFragment.viewTop.setVisibility(View.GONE);
                        }

                        //展示头布局
                        binding.rlTop.setVisibility(View.VISIBLE);
                        binding.nsv.setScrollingEnabled(true);
                        binding.viewPager2.setUserInputEnabled(false);

                        binding.nsv.fling(0);
                        binding.nsv.smoothScrollTo(0, 0);
                    }
                }
            }
        });
        RxSubscriptions.add(subscribe);

        uniSubscribe = RxBus.getDefault().toObservable(UniEventBean.class).subscribe(event -> {
            if (event != null) {
                if (event.msgId == CodeTable.UNI_RELEASE) {
                    if (event.taskId == 0x10111) {
                        MenuBean menuBean = menu2Adapter.getData().get(clickIndex);
                        String url = menuBean.jumpUrl;
                        UniUtil.openUniApp(getContext(), menuBean.appId, url, null, true);
                    }
                } else if (event.msgId == CodeTable.UNI_RELEASE_FAIL) {
                    ToastUtils.showShort("打开小程序失败");
                }
            }
        });
        RxSubscriptions.add(uniSubscribe);

        binding.ivZhibo.setOnClickListener(lis -> {
            VideoListBean listBean = new VideoListBean();
            listBean.type = 1;

            Bundle bundle = new Bundle();
            bundle.putSerializable("key", listBean);
            startActivity(VideoListActivity.class, bundle);
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

    public void resetVVPHeight() {
        ViewGroup.LayoutParams layoutParams = binding.viewPager2.getLayoutParams();
        layoutParams.height = MScreenUtil.getScreenHeight(getActivity());
    }

    private boolean isFirst = true;

    public void initData() {
        super.initData();
        screenWidth = ScreenUtil.getScreenWidth(getActivity());
        resetVVPHeight();
        initFragment();
        initTopMenu();
        LocationService.startService(getActivity());
    }

    private void initFragment() {
        List<Fragment> fragments = new ArrayList<>();
        tjFragment = new HomeTJFragment(getActivity());
        gzFragment = new HomeGZFragment(getActivity());
        fjFragment = new HomeFJFragment();
        fragments.add(tjFragment);
        fragments.add(gzFragment);
        fragments.add(fjFragment);
        HomeVP2Adapter vp2Adapter = new HomeVP2Adapter(getActivity(), fragments);
        vp2 = binding.viewPager2;
        binding.viewPager2.setUserInputEnabled(false);


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
                    if (position == 0 && binding.rlTop.getVisibility() == View.VISIBLE) {
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

        binding.nsv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY >= (int) getResources().getDimension(R.dimen.dp_247)) {
                    AppApplication.get().setShowTopMenu(false);

                    isShowTop = false;
                    //隐藏头部菜单
                    binding.nsv.setScrollingEnabled(false);
                    binding.viewPager2.setUserInputEnabled(true);
                    binding.rlTop.setVisibility(View.GONE);

                    if (AppApplication.get().getLayoutPos() == 0) {
                        tjFragment.tjViewPager2.setUserInputEnabled(true);
                        //推荐ViewPager2变宽
                        tjFragment.setCardWidth(screenWidth);
                        tjFragment.tjCardView.setRadius(0);
                        //判断第一次
                        //设置开始播放第一条
                        if (AppApplication.get().getTjPlayPosition() < 0) {
                            AppApplication.get().setTjPlayPosition(0);
                        }
                    } else if (AppApplication.get().getLayoutPos() == 1) {
                        gzFragment.setVP2Enabled(true);
                    } else {
                        fjFragment.recyclerView.setNestedScrollingEnabled(true);
                        fjFragment.viewTop.setVisibility(View.VISIBLE);
                    }


                    String tag = "";
                    if (AppApplication.get().getLayoutPos() == 0) {
                        tag = "home-tj";
                    } else if (AppApplication.get().getLayoutPos() == 1) {
                        tag = "home-gz";
                    }
                    //通知播放页面播放
                    RxBus.getDefault().post(new VideoEvent(1, tag));

                    //通知主页底部变透明
                    RxBus.getDefault().post(new EventBean(CodeTable.EVENT_HOME, CodeTable.SET_MENU_TOUMING));
                    //通知HomeFragment展示TabLayout
                    RxBus.getDefault().post(new EventBean(CodeTable.EVENT_HOME, CodeTable.SHOW_TAB_LAYOUT));
                } else {

                    if (scrollY == 0) {
                        return;
                    }

                    if (AppApplication.get().getLayoutPos() == 0) {
                        //h滑动237 w加宽20
                        //推荐ViewPager2逐渐变宽

                        float bb = getResources().getDimension(R.dimen.dp_20) / getResources().getDimension(R.dimen.dp_247);
                        ViewGroup.LayoutParams params = tjFragment.tjCardView.getLayoutParams();
                        params.width = tjFragment.oldWidth + (int) ((float) (scrollY) * bb);

                        tjFragment.tjCardView.setLayoutParams(params);
                    } else if (AppApplication.get().getLayoutPos() == 2) {

                    }

                    //主页底部菜单背景颜色从白变透明
                    float colorBb = 10 / getResources().getDimension(R.dimen.dp_247);
                    //从白-透明 100-0
                    int colorA = 10 - (int) (scrollY * colorBb);

                    RxBus.getDefault().post(new EventBean(CodeTable.EVENT_HOME, CodeTable.SET_MENU_COLOR, "", colorA));
                }
            }
        });

        binding.ivSearch.setOnClickListener(lis ->{
            startActivity(SearchActivity.class);
        });
        binding.ivSearch2.setOnClickListener(lis ->{
            startActivity(SearchActivity.class);
        });
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

        menu1Adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putString("title", menu1Adapter.getData().get(position).menuName);
                //获取tags
                String tags = UrlUtils.getUrlQueryByTag(menu1Adapter.getData().get(position).jumpUrl, "tags");
                bundle.putString("tags", tags);
                startActivity(VideoGridActivity.class, bundle);
            }
        });


        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getActivity());
        linearLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.rvMenu2.setLayoutManager(linearLayoutManager2);
        menu2Adapter = new MenuAdapter(R.layout.item_home_menu2);
        binding.rvMenu2.setAdapter(menu2Adapter);
        viewModel.getHomeInfo();
        menu2Adapter.setOnItemClickListener((adapter, view, position) -> {
            clickIndex = position;
            MenuBean menuBean = menu2Adapter.getData().get(position);
            UniService.startService(getContext(), menuBean.appId, 0x10111);
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        subscribe.dispose();
        RxSubscriptions.remove(uniSubscribe);
    }


}
