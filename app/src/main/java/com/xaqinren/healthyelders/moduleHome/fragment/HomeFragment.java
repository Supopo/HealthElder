package com.xaqinren.healthyelders.moduleHome.fragment;

import android.Manifest;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen;
import com.ethanhua.skeleton.Skeleton;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.tencent.bugly.proguard.A;
import com.tencent.qcloud.tim.uikit.utils.ScreenUtil;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.MainActivity;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.FragmentHomeBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.LockableNestedScrollView;
import com.xaqinren.healthyelders.moduleHome.activity.SearchActivity;
import com.xaqinren.healthyelders.moduleHome.activity.VideoListActivity;
import com.xaqinren.healthyelders.moduleHome.adapter.HomeVP2Adapter;
import com.xaqinren.healthyelders.moduleHome.adapter.MenuAdapter;
import com.xaqinren.healthyelders.moduleHome.bean.MenuBean;
import com.xaqinren.healthyelders.moduleHome.bean.VideoEvent;
import com.xaqinren.healthyelders.moduleHome.bean.VideoListBean;
import com.xaqinren.healthyelders.moduleHome.viewModel.HomeViewModel;
import com.xaqinren.healthyelders.moduleLiteav.service.LocationService;
import com.xaqinren.healthyelders.moduleLogin.activity.SelectLoginActivity;
import com.xaqinren.healthyelders.moduleZhiBo.activity.StartLiveActivity;
import com.xaqinren.healthyelders.uniApp.UniUtil;
import com.xaqinren.healthyelders.uniApp.bean.UniEventBean;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.utils.MScreenUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.goldze.mvvmhabit.widget.LoadingDialog;

/**
 * Created by Lee. on 2021/5/11.
 * ??????Fragment
 */
public class HomeFragment extends BaseFragment<FragmentHomeBinding, HomeViewModel> {
    private static final String TAG = "HomeFragment";
    private Disposable subscribe;
    private String[] titles = {"??????", "??????", "??????"};
    public ViewPager2 vp2;
    private HomeGZFragment gzFragment;
    private HomeTJFragment tjFragment;
    private HomeFJFragment fjFragment;
    private int screenWidth;
    private BaseQuickAdapter<MenuBean, BaseViewHolder> menu1Adapter;
    private MenuAdapter menu2Adapter;
    private Disposable uniSubscribe;
    private int oldWidth;
    public LockableNestedScrollView nsv;
    private RecyclerViewSkeletonScreen skeletonScreen1;
    private Disposable disposable;
    private LoadingDialog loadingDialog;


    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_home;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }


    public boolean isShowTop = true;

    private boolean isNeedRefresh;//?????????????????????

    private Handler mHandler = new Handler();

    public void showMDialog() {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(getActivity(), false);
        }
        loadingDialog.show();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (loadingDialog.isShowing()) {
                    isNeedRefresh = false;
                    loadingDialog.dismiss();
                }
            }
        }, 1000 * 10);
    }

    public void dismissMDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        subscribe = RxBus.getDefault().toObservable(EventBean.class).subscribe(event -> {
            if (event != null) {
                if (event.msgId == CodeTable.EVENT_HOME) {
                    if (event.msgType == CodeTable.SHOW_TAB_LAYOUT) {
                        isShowTop = false;
                        //??????TabLayout
                        binding.rlTabMenu.setVisibility(View.VISIBLE);
                    } else if (event.msgType == CodeTable.SHOW_HOME1_TOP) {
                        if (isNeedRefresh) {
                            return;
                        }
                        isNeedRefresh = true;

                        showMDialog();

                        AppApplication.get().setShowTopMenu(true);
                        isShowTop = true;

                        //???????????? ??????
                        AppApplication.get().setLayoutPos(0);
                        AppApplication.get().setTjPlayPosition(-1);
                        //????????????????????????
                        viewModel.getHomeInfo();
                        //??????????????????
                        binding.tabLayout.setCurrentTab(0, false);
                        //????????????
                        tjFragment.homeRefreshData();

                        //???????????????????????????
                        RxBus.getDefault().post(new VideoEvent(10010, 0));

                        //??????TabLayout
                        binding.rlTabMenu.setVisibility(View.GONE);

                        //??????viewPager2 ??????
                        int dimension = (int) getActivity().getResources().getDimension(R.dimen.dp_20);
                        setCardWidth(screenWidth - dimension);
                        binding.cardView.setRadius(getResources().getDimension(R.dimen.dp_14));

                        if (AppApplication.get().getLayoutPos() == 0) {
                            tjFragment.tjViewPager2.setUserInputEnabled(false);
                        } else if (AppApplication.get().getLayoutPos() == 1) {
                            gzFragment.setVP2Enabled(false);
                        } else {
                            fjFragment.recyclerView.setNestedScrollingEnabled(false);
                            fjFragment.viewTop.setVisibility(View.GONE);
                        }

                        //???????????????
                        binding.rlTop.setVisibility(View.VISIBLE);
                        binding.nsv.setScrollingEnabled(true);
                        binding.viewPager2.setUserInputEnabled(false);

                        binding.nsv.fling(0);
                        binding.nsv.smoothScrollTo(0, 0);

                    } else if (event.msgType == CodeTable.SHOW_HOME1_TOP_HT) {
                        //???????????????
                        binding.nsv.setScrollingEnabled(true);
                        binding.viewPager2.setUserInputEnabled(false);
                        binding.nsv.fling(0);
                        binding.nsv.smoothScrollTo(0, 0);
                    } else if (event.msgType == CodeTable.SHOW_HOME1_TOP_ZK) {
                        //????????????
                        binding.nsv.fling((int) getResources().getDimension(R.dimen.dp_234));
                        binding.nsv.smoothScrollTo(0, (int) getResources().getDimension(R.dimen.dp_234));
                    } else if (event.msgType == CodeTable.HOME_STOP_LOADING) {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isNeedRefresh = false;
                                dismissMDialog();
                            }
                        }, 500);

                    }
                }
            }
        });
        RxSubscriptions.add(subscribe);

        uniSubscribe = RxBus.getDefault().toObservable(UniEventBean.class).subscribe(event -> {
            if (event != null) {
                if (event.msgId == CodeTable.UNI_RELEASE) {
                    if (event.taskId == 0x10111) {
                        UniUtil.openUniApp(getContext(), event.appId, event.jumpUrl, null, event.isSelfUni);
                    }
                } else if (event.msgId == CodeTable.UNI_RELEASE_FAIL) {
                    //ToastUtils.showShort("?????????????????????");
                }
            }
        });
        RxSubscriptions.add(uniSubscribe);

        binding.ivZhibo.setOnClickListener(lis -> {
            VideoListBean listBean = new VideoListBean();
            listBean.openType = 1;
            listBean.isFollow = AppApplication.get().getLayoutPos() == 1 ? 1 : 0;
            Bundle bundle = new Bundle();
            bundle.putSerializable("key", listBean);
            startActivity(VideoListActivity.class, bundle);
        });


        viewModel.homeInfo.observe(this, homeRes -> {
            if (homeRes != null) {
                binding.rvMenu2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        skeletonScreen1.hide();
                    }
                }, 1000);

                if (homeRes.commodityType != null) {
                    menu1Adapter.setNewInstance(homeRes.contentMenu);
                }
                if (homeRes.contentMenu != null) {
                    menu2Adapter.setNewInstance(homeRes.commodityType);
                }
            }
        });


    }

    public void setCardWidth(int width) {
        ViewGroup.LayoutParams params = binding.cardView.getLayoutParams();
        params.width = width;
        binding.cardView.setLayoutParams(params);
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

    public void showSkeleton1() {
        skeletonScreen1 = Skeleton.bind(binding.rvMenu2)
                .adapter(menu2Adapter)//????????????adapter
                .shimmer(true)//??????????????????
                .color(R.color.flashColor)//shimmer?????????
                .angle(Constant.flashAngle)//shimmer???????????????
                .frozen(true)//true??????????????????????????????RecyclerView?????????????????????????????????
                .duration(Constant.flashDuration)//?????????????????????????????????
                .count(4)//??????????????????item?????????
                .load(R.layout.item_home_menu2_def)//?????????UI
                .show();
    }

    private void initFragment() {
        List<Fragment> fragments = new ArrayList<>();
        tjFragment = new HomeTJFragment(getActivity());
        gzFragment = new HomeGZFragment(getActivity());
        fjFragment = new HomeFJFragment();
        fragments.add(tjFragment);
        fragments.add(gzFragment);
        fragments.add(fjFragment);
        HomeVP2Adapter vp2Adapter = new HomeVP2Adapter(this, fragments);
        vp2 = binding.viewPager2;
        nsv = binding.nsv;
        binding.viewPager2.setUserInputEnabled(false);


        //??????viewPager2
        ViewGroup.LayoutParams params = binding.cardView.getLayoutParams();
        int dimension = (int) getActivity().getResources().getDimension(R.dimen.dp_20);
        oldWidth = screenWidth - dimension;
        params.height = MScreenUtil.getScreenHeight(getActivity());
        params.width = screenWidth - dimension;
        binding.cardView.setLayoutParams(params);

        binding.viewPager2.setAdapter(vp2Adapter);
        binding.viewPager2.setOffscreenPageLimit(2);
        binding.tabLayout.setViewPager2(binding.viewPager2, titles);

        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                AppApplication.get().setLayoutPos(position);

                //????????????????????????Fragment?????????
                if (!isFirst) {

                    if (binding.rlTabMenu.getVisibility() == View.GONE) {
                        binding.rlTabMenu.setVisibility(View.VISIBLE);
                    }
                    if (position == 0 && binding.rlTop.getVisibility() == View.VISIBLE) {
                        binding.rlTabMenu.setVisibility(View.GONE);
                    }

                    //??????HomeVideoFragment??????????????????????????????
                    RxBus.getDefault().post(new VideoEvent(101, position));
                    //??????????????????????????????????????????
                    RxBus.getDefault().post(new EventBean(101, position));

                    if (position == 0) {//??????
                        RxBus.getDefault().post(new EventBean(CodeTable.EVENT_HOME, CodeTable.SET_MENU_TOUMING));
                    } else if (position == 1) {//??????
                        RxBus.getDefault().post(new EventBean(CodeTable.EVENT_HOME, CodeTable.SET_MENU_TOUMING));
                    } else {//??????
                        RxBus.getDefault().post(new EventBean(CodeTable.EVENT_HOME, CodeTable.SET_MENU_BLACK));
                    }
                }
                isFirst = false;
            }
        });

        binding.nsv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (scrollY >= (int) getResources().getDimension(R.dimen.dp_234)) {
                    LogUtils.v("???????????????", "?????????234");
                    AppApplication.get().setShowTopMenu(false);

                    isShowTop = false;


                    //ViewPager2??????
                    setCardWidth(screenWidth);
                    binding.cardView.setRadius(0);
                    if (AppApplication.get().getLayoutPos() == 0) {
                        tjFragment.tjViewPager2.setUserInputEnabled(true);
                        //???????????????
                        //???????????????????????????
                        if (AppApplication.get().getTjPlayPosition() < 0) {
                            AppApplication.get().setTjPlayPosition(0);
                            //????????????????????????????????????View
                            RxBus.getDefault().post(new VideoEvent(10011, 0));
                        }
                    } else if (AppApplication.get().getLayoutPos() == 1) {
                        gzFragment.setVP2Enabled(true);
                    } else {
                        fjFragment.recyclerView.setNestedScrollingEnabled(true);
                        fjFragment.viewTop.setVisibility(View.VISIBLE);
                    }

                    //??????????????????
                    binding.nsv.setScrollingEnabled(false);
                    binding.viewPager2.setUserInputEnabled(true);
                    binding.rlTop.setVisibility(View.GONE);

                    String tag = "";
                    if (AppApplication.get().getLayoutPos() == 0) {
                        tag = "home-tj";
                    } else if (AppApplication.get().getLayoutPos() == 1) {
                        tag = "home-gz";
                    }
                    //????????????????????????
                    RxBus.getDefault().post(new VideoEvent(1, tag));

                    //???????????????????????????
                    RxBus.getDefault().post(new EventBean(CodeTable.EVENT_HOME, CodeTable.SET_MENU_TOUMING));
                    //??????HomeFragment??????TabLayout
                    RxBus.getDefault().post(new EventBean(CodeTable.EVENT_HOME, CodeTable.SHOW_TAB_LAYOUT));
                } else {

                    if (scrollY == 0) {
                        return;
                    }

                    //h??????237 w??????20
                    //ViewPager2????????????

                    float bb = getResources().getDimension(R.dimen.dp_20) / getResources().getDimension(R.dimen.dp_234);
                    ViewGroup.LayoutParams params = binding.cardView.getLayoutParams();
                    params.width = oldWidth + (int) ((float) (scrollY) * bb);
                    binding.cardView.setLayoutParams(params);


                    //?????????????????????????????????????????????
                    float colorBb = 10 / getResources().getDimension(R.dimen.dp_234);
                    //??????-?????? 100-0
                    int colorA = 10 - (int) (scrollY * colorBb);

                    RxBus.getDefault().post(new EventBean(CodeTable.EVENT_HOME, CodeTable.SET_MENU_COLOR, "", colorA));


                }
            }
        });

        binding.ivSearch.setOnClickListener(lis -> {
            startActivity(SearchActivity.class);
        });
        binding.ivSearch2.setOnClickListener(lis -> {
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
                MenuBean menuBean = menu1Adapter.getData().get(position);
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.jumpMenu(mainActivity.convertToSlideBarMenu(menuBean));
                /*Bundle bundle = new Bundle();
                bundle.putString("title", menu1Adapter.getData().get(position).menuName);
                //??????tags
                String tags = UrlUtils.getUrlQueryByTag(menu1Adapter.getData().get(position).jumpUrl, "tags");
                bundle.putString("tags", tags);
                startActivity(VideoGridActivity.class, bundle);*/
            }
        });


        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getActivity());
        linearLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.rvMenu2.setLayoutManager(linearLayoutManager2);
        menu2Adapter = new MenuAdapter(R.layout.item_home_menu2);
        binding.rvMenu2.setAdapter(menu2Adapter);
        showSkeleton1();

        viewModel.getHomeInfo();
        menu2Adapter.setOnItemClickListener((adapter, view, position) -> {
            MenuBean menuBean = menu2Adapter.getData().get(position);
            if (UserInfoMgr.getInstance().getAccessToken() == null) {
                startActivity(SelectLoginActivity.class);
                return;
            }
            //            UniService.startService(getContext(), menuBean.event, 0x10111, menuBean.jumpUrl);
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.jumpMenu(mainActivity.convertToSlideBarMenu(menuBean));
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxSubscriptions.clear();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
