package com.xaqinren.healthyelders.moduleMall.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.ViewSkeletonScreen;
import com.google.android.material.appbar.AppBarLayout;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.MainActivity;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.FragmentMallBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.global.InfoCache;
import com.xaqinren.healthyelders.moduleHome.activity.SearchActivity;
import com.xaqinren.healthyelders.moduleHome.adapter.FragmentPagerAdapter;
import com.xaqinren.healthyelders.moduleHome.bean.MenuBean;
import com.xaqinren.healthyelders.moduleLogin.activity.PhoneLoginActivity;
import com.xaqinren.healthyelders.moduleLogin.activity.SelectLoginActivity;
import com.xaqinren.healthyelders.moduleMall.adapter.MallHotMenuAdapter;
import com.xaqinren.healthyelders.moduleMall.adapter.MallMenu1PageAdapter;
import com.xaqinren.healthyelders.moduleMall.adapter.MallMenu3Adapter;
import com.xaqinren.healthyelders.moduleMall.viewModel.MallViewModel;
import com.xaqinren.healthyelders.moduleMine.activity.OrderListActivity;
import com.xaqinren.healthyelders.uniApp.UniService;
import com.xaqinren.healthyelders.uniApp.UniUtil;
import com.xaqinren.healthyelders.uniApp.bean.UniEventBean;
import com.xaqinren.healthyelders.utils.AnimUtils;
import com.xaqinren.healthyelders.utils.GlideUtil;
import com.xaqinren.healthyelders.widget.SpeacesItemDecoration;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;

import static com.luck.picture.lib.thread.PictureThreadUtils.runOnUiThread;

/**
 * Created by Lee. on 2021/5/25.
 */
public class MallFragment extends BaseFragment<FragmentMallBinding, MallViewModel> {

    private MallMenu1PageAdapter pageAdapter;
    private MallMenu3Adapter mallMenu3Adapter;
    private MallHotMenuAdapter mallHotMenuAdapter;

    public SwipeRefreshLayout srl;
    public boolean isTop = true;
    int pageCount;//menu1菜单页数 ViewPager+RecvclerView
    int pageSize = 10;//menu1菜单页数数量
    private List<Fragment> fragments;
    private FragmentPagerAdapter goodsPagerAdapter;
    private Disposable subscribe;
    private ViewSkeletonScreen skeletonScreen1, skeletonScreen3;
    private RecyclerViewSkeletonScreen skeletonScreen2, skeletonScreen4;
    private Runnable runnable;
    private Animation ggShowAnimation;
    private Animation ggGoneAnimation;
    private Timer ggTimer;
    private TimerTask ggAnimTask;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_mall;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    private int menu3OldPos;

    @Override
    public void initData() {
        super.initData();

        srl = binding.srlTop;
        binding.srlTop.setEnabled(false);
        pageAdapter = new MallMenu1PageAdapter(R.layout.item_mall_rv, new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (UserInfoMgr.getInstance().getAccessToken() == null) {
                    Intent intent = new Intent(getContext(), SelectLoginActivity.class);
                    getContext().startActivity(intent);
                    return;
                }
                MenuBean menuBean1 = (MenuBean) adapter.getData().get(position);
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.jumpMenu(mainActivity.convertToSlideBarMenu(menuBean1));
            }
        });
        binding.vpMenu1.setAdapter(pageAdapter);

        binding.banner.addBannerLifecycleObserver(this);
        binding.banner.setIndicator(new CircleIndicator(getActivity()));

        mallHotMenuAdapter = new MallHotMenuAdapter(R.layout.item_mall_hot);
        binding.rvMenu2.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        binding.rvMenu2.addItemDecoration(new SpeacesItemDecoration(getActivity(), 1, false));

        mallHotMenuAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (UserInfoMgr.getInstance().getAccessToken() == null) {
                startActivity(SelectLoginActivity.class);
                return;
            }
            MenuBean menuBean = mallHotMenuAdapter.getData().get(position);
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.jumpMenu(mainActivity.convertToSlideBarMenu(menuBean));
        });
        binding.rvMenu2.setAdapter(mallHotMenuAdapter);


        mallMenu3Adapter = new MallMenu3Adapter(R.layout.item_mall_menu3);
        binding.rvMenu3.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        binding.rvMenu3.setAdapter(mallMenu3Adapter);

        mallMenu3Adapter.setOnItemClickListener(((adapter, view, position) -> {
            mallMenu3Adapter.getData().get(menu3OldPos).isSelect = false;
            mallMenu3Adapter.notifyItemChanged(menu3OldPos);
            mallMenu3Adapter.getData().get(position).isSelect = true;
            mallMenu3Adapter.notifyItemChanged(position);
            menu3OldPos = position;
            binding.vpContent.setCurrentItem(position);

        }));


        binding.vpContent.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mallMenu3Adapter.getData().get(menu3OldPos).isSelect = false;
                mallMenu3Adapter.notifyItemChanged(menu3OldPos);
                mallMenu3Adapter.getData().get(position).isSelect = true;
                mallMenu3Adapter.notifyItemChanged(position);
                menu3OldPos = position;

                RxBus.getDefault().post(new EventBean(CodeTable.ADD_MALL_LIST, menu3OldPos));

            }
        });
        binding.orderLayout.setOnClickListener(v -> {
            if (InfoCache.getInstance().checkLogin() && UserInfoMgr.getInstance().getUserInfo() != null) {
                if (!UserInfoMgr.getInstance().getUserInfo().hasMobileNum()) {
                    startActivity(PhoneLoginActivity.class);
                    return;
                }
                startActivity(OrderListActivity.class);
            } else {
                startActivity(SelectLoginActivity.class);
            }
        });
        binding.etSearch.setOnClickListener(v -> {
            SearchActivity.startActivity(getContext(), 1);
        });
        showSkeleton1();
        showSkeleton3();
        showSkeleton2();
        showSkeleton4();
        viewModel.getMenuInfo();
        viewModel.getMenuType();


        runnable = new Runnable() {
            @Override
            public void run() {
                showGonggao();
                mHandler.postDelayed(runnable, 6 * 1000);
            }
        };
        startHandler();
    }

    public void startHandler() {
        if (mHandler != null && runnable != null) {
            mHandler.postDelayed(runnable, 5000);
        }
    }

    public void stopHandler() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopHandler();
    }

    public Handler mHandler = new Handler(Looper.getMainLooper());

    public void freedGGTimerTask() {
        if (ggAnimTask != null) {
            ggAnimTask.cancel();
            ggAnimTask = null;
        }
        if (ggTimer != null) {
            ggTimer.cancel();
            ggTimer.purge();
            ggTimer = null;
        }

    }

    private int[] photos = {R.mipmap.def_photo_1, R.mipmap.def_photo_2, R.mipmap.def_photo_3,
            R.mipmap.def_photo_4, R.mipmap.def_photo_5, R.mipmap.def_photo_6,
            R.mipmap.def_photo_7, R.mipmap.def_photo_8, R.mipmap.def_photo_9,
            R.mipmap.def_photo_10, R.mipmap.def_photo_11, R.mipmap.def_photo_12,
    };

    private String[] names1 = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l"};
    private String[] names2 = {"m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x"};

    public void showGonggao() {
        Random ra = new Random();
        int temp = ra.nextInt(11);//0-11
        GlideUtil.intoImageView(getActivity(), photos[temp], binding.rivPhoto);

        binding.tvGgname.setText(names1[temp] + names2[temp] + temp + (3 * temp) + "**");

        ggShowAnimation = AnimUtils.getAnimation(getActivity(), R.anim.anim_slice_in_bottom);
        ggGoneAnimation = AnimUtils.getAnimation(getActivity(), R.anim.anim_slice_out_top);
        binding.rlGonggao.clearAnimation();
        binding.rlGonggao.startAnimation(ggShowAnimation);
        ggShowAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.rlGonggao.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                freedGGTimerTask();
                ggTimer = new Timer();
                ggAnimTask = new TimerTask() {

                    @Override
                    public void run() {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.rlGonggao.clearAnimation();
                                binding.rlGonggao.startAnimation(ggGoneAnimation);
                                ggGoneAnimation.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        binding.rlGonggao.setVisibility(View.GONE);
                                        freedGGTimerTask();
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                            }
                        });

                    }
                };
                ggTimer.schedule(ggAnimTask, 3000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void showSkeleton1() {
        skeletonScreen1 = Skeleton.bind(binding.banner)
                .shimmer(true)//是否开启动画
                .color(R.color.flashColor)//shimmer的颜色
                .angle(Constant.flashAngle)//shimmer的倾斜角度
                .duration(Constant.flashDuration)//动画时间，以毫秒为单位
                .load(R.layout.def_banner)//骨架屏UI
                .show();
    }

    public void showSkeleton3() {
        skeletonScreen3 = Skeleton.bind(binding.vpMenu1)
                .shimmer(true)//是否开启动画
                .color(R.color.flashColor)//shimmer的颜色
                .angle(Constant.flashAngle)//shimmer的倾斜角度
                .duration(Constant.flashDuration)//动画时间，以毫秒为单位
                .load(R.layout.def_mall_vp_menu)//骨架屏UI
                .show();
    }

    public void showSkeleton2() {
        skeletonScreen2 = Skeleton.bind(binding.rvMenu2)
                .adapter(mallHotMenuAdapter)//设置实际adapter
                .shimmer(true)//是否开启动画
                .color(R.color.flashColor)//shimmer的颜色
                .angle(Constant.flashAngle)//shimmer的倾斜角度
                .frozen(true)//true则表示显示骨架屏时，RecyclerView不可滑动，否则可以滑动
                .duration(Constant.flashDuration)//动画时间，以毫秒为单位
                .count(2)//显示骨架屏时item的个数
                .load(R.layout.item_mall_hot_def)//骨架屏UI
                .show();
    }

    public void showSkeleton4() {
        skeletonScreen4 = Skeleton.bind(binding.rvMenu3)
                .adapter(mallMenu3Adapter)//设置实际adapter
                .shimmer(true)//是否开启动画
                .color(R.color.flashColor)//shimmer的颜色
                .angle(Constant.flashAngle)//shimmer的倾斜角度
                .frozen(true)//true则表示显示骨架屏时，RecyclerView不可滑动，否则可以滑动
                .duration(Constant.flashDuration)//动画时间，以毫秒为单位
                .count(2)//显示骨架屏时item的个数
                .load(R.layout.item_mall_menu3_def)//骨架屏UI
                .show();
    }


    /**
     * 控制appbar的滑动
     *
     * @param isScroll true 允许滑动 false 禁止滑动
     */
    private void banAppBarScroll(boolean isScroll) {
        View mAppBarChildAt = binding.appBar.getChildAt(0);
        AppBarLayout.LayoutParams mAppBarParams = (AppBarLayout.LayoutParams) mAppBarChildAt.getLayoutParams();
        if (isScroll) {
            mAppBarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
            mAppBarChildAt.setLayoutParams(mAppBarParams);
        } else {
            mAppBarParams.setScrollFlags(0);
        }
    }

    private boolean isFirst = true;

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.dismissDialog.observe(this, isDismiss -> {
            dismissDialog();
        });

        viewModel.mallMenuRes.observe(this, mallMenuRes -> {
            if (mallMenuRes != null) {
                binding.rvMenu2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        skeletonScreen1.hide();
                        skeletonScreen2.hide();
                        skeletonScreen3.hide();
                        skeletonScreen4.hide();
                    }
                }, 1000);
                if (mallMenuRes.category != null) {
                    setMenu1Data(mallMenuRes.category.menuInfoList);
                }
                if (mallMenuRes.advertising != null) {
                    setMenu2Data(mallMenuRes.advertising.menuInfoList);
                }
                if (mallMenuRes.banners != null) {
                    setBannerData(mallMenuRes.banners.menuInfoList);
                }
            }
        });

        viewModel.menu3.observe(this, datas -> {
            if (datas != null && datas.size() > 0) {

                fragments = new ArrayList<>();
                for (int i = 0; i < datas.size(); i++) {
                    GoodsListFragment goodsListFragment = new GoodsListFragment(i, datas.get(i).menuName, isFirst);
                    fragments.add(goodsListFragment);
                }
                isFirst = false;
                binding.vpContent.setOffscreenPageLimit(datas.size());
                goodsPagerAdapter = new FragmentPagerAdapter(this, fragments);
                binding.vpContent.setAdapter(goodsPagerAdapter);


                datas.get(0).isSelect = true;
                menu3OldPos = 0;
                mallMenu3Adapter.setNewInstance(datas);
            }
        });

        binding.srlTop.setOnRefreshListener(() -> {
            binding.srlTop.setRefreshing(false);
            viewModel.getMenuInfo();
            viewModel.getMenuType();
            //通知对应页面刷新
            RxBus.getDefault().post(new EventBean(CodeTable.RESH_MALL_LIST, menu3OldPos));

        });
        binding.appBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {

            //为菜单栏固定位置
            if (verticalOffset > -binding.rvMenu3.getTop()) {
                binding.rvMenu3.setBackgroundColor(getResources().getColor(R.color.transparent));
            } else {
                binding.rvMenu3.setBackgroundColor(getResources().getColor(R.color.white));
            }

            //判单只有滑倒最顶部才能下拉刷新
            if (verticalOffset == 0) {
                isTop = true;
                binding.srlTop.setEnabled(true);
            } else {
                isTop = false;
                binding.srlTop.setEnabled(false);
            }
        });

        subscribe = RxBus.getDefault().toObservable(UniEventBean.class).subscribe(uniEventBean -> {
            if (uniEventBean.msgId == CodeTable.UNI_RELEASE) {
                if (uniEventBean.taskId == 0x10001)
                    UniUtil.openUniApp(getContext(), uniEventBean.appId, uniEventBean.jumpUrl, null, uniEventBean.isSelfUni);
            }
        });
        RxSubscriptions.add(subscribe);
    }

    private void setBannerData(List<MenuBean> bannerImages) {
        binding.banner.setDatas(bannerImages);
        binding.banner.setAdapter(new BannerImageAdapter<MenuBean>(bannerImages) {
            @Override
            public void onBindView(BannerImageHolder holder, MenuBean data, int position, int size) {
                holder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                Glide.with(holder.itemView)
                        .load(data.imageUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .thumbnail(0.3f)
                        .into(holder.imageView);
            }
        });
        binding.banner.setOnBannerListener(new OnBannerListener<MenuBean>() {
            @Override
            public void OnBannerClick(MenuBean data, int position) {
                if (UserInfoMgr.getInstance().getAccessToken() == null) {
                    startActivity(SelectLoginActivity.class);
                    return;
                }
                //                UniService.startService(getContext(), data.event, 0x10001, data.jumpUrl);

                MenuBean menuBean = data;
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.jumpMenu(mainActivity.convertToSlideBarMenu(menuBean));
            }
        });
    }

    private void setMenu2Data(List<MenuBean> datas) {
        if (datas != null && datas.size() > 0) {

            datas.get(0).type = 1;
            mallHotMenuAdapter.setNewInstance(datas);
        }
    }

    private void setMenu1Data(List<MenuBean> datas) {
        if (datas != null && datas.size() > 0) {

            //重组ViewPager2的数据
            //计算页数
            if (datas.size() % pageSize == 0) {
                pageCount = (datas.size() / pageSize);
            } else {
                pageCount = (datas.size() / pageSize) + 1;
            }

            List<MenuBean> pageList = new ArrayList<>();
            for (int i = 0; i < pageCount; i++) {
                MenuBean menuBean = new MenuBean();
                if (i == pageCount - 1) {
                    menuBean.menuBeans.addAll(datas.subList(i * pageSize, datas.size()));
                } else {
                    menuBean.menuBeans.addAll(datas.subList(i * pageSize, (i + 1) * pageSize));
                }
                pageList.add(menuBean);
            }

            binding.vpMenu1.setOffscreenPageLimit(pageCount);

            pageAdapter.setNewInstance(pageList);

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxSubscriptions.remove(subscribe);
    }
}
