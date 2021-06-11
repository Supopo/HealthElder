package com.xaqinren.healthyelders.moduleMall.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.material.appbar.AppBarLayout;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.FragmentMallBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.InfoCache;
import com.xaqinren.healthyelders.moduleHome.adapter.FragmentPagerAdapter;
import com.xaqinren.healthyelders.moduleHome.bean.MenuBean;
import com.xaqinren.healthyelders.moduleLogin.activity.SelectLoginActivity;
import com.xaqinren.healthyelders.moduleMall.adapter.MallHotMenuAdapter;
import com.xaqinren.healthyelders.moduleMall.adapter.MallMenu1PageAdapter;
import com.xaqinren.healthyelders.moduleMall.adapter.MallMenu3Adapter;
import com.xaqinren.healthyelders.moduleMall.viewModel.MallViewModel;
import com.xaqinren.healthyelders.moduleMine.activity.OrderListActivity;
import com.xaqinren.healthyelders.uniApp.UniService;
import com.xaqinren.healthyelders.uniApp.UniUtil;
import com.xaqinren.healthyelders.uniApp.bean.UniEventBean;
import com.xaqinren.healthyelders.widget.SpeacesItemDecoration;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;

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
        pageAdapter = new MallMenu1PageAdapter(R.layout.item_mall_rv);
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
            UniService.startService(getContext(), menuBean.appId, 0x10001, menuBean.jumpUrl);
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
            if (InfoCache.getInstance().checkLogin()) {
                startActivity(OrderListActivity.class);
            }else{
                startActivity(SelectLoginActivity.class);
            }
        });

        viewModel.getMenuInfo();
        viewModel.getMenuType();
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

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.dismissDialog.observe(this, isDismiss -> {
            dismissDialog();
        });

        viewModel.mallMenuRes.observe(this, mallMenuRes -> {
            if (mallMenuRes != null) {
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
                    GoodsListFragment goodsListFragment = new GoodsListFragment(i, datas.get(i).menuName);
                    fragments.add(goodsListFragment);
                }
                binding.vpContent.setOffscreenPageLimit(datas.size());
                goodsPagerAdapter = new FragmentPagerAdapter(getActivity(), fragments);
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
                UniService.startService(getContext(), data.appId, 0x10001, data.jumpUrl);
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
