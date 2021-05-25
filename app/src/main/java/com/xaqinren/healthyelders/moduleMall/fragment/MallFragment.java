package com.xaqinren.healthyelders.moduleMall.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.FragmentMallBinding;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.bean.MenuBean;
import com.xaqinren.healthyelders.moduleMall.adapter.MallGoodsAdapter;
import com.xaqinren.healthyelders.moduleMall.adapter.MallHotMenuAdapter;
import com.xaqinren.healthyelders.moduleMall.adapter.MallMenu1PageAdapter;
import com.xaqinren.healthyelders.moduleMall.adapter.MallMenu3Adapter;
import com.xaqinren.healthyelders.moduleMall.viewModel.MallViewModel;
import com.xaqinren.healthyelders.modulePicture.bean.DiaryInfoBean;
import com.xaqinren.healthyelders.widget.SpeacesItemDecoration;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseFragment;

/**
 * Created by Lee. on 2021/5/25.
 */
public class MallFragment extends BaseFragment<FragmentMallBinding, MallViewModel> {

    private MallMenu1PageAdapter pageAdapter;
    private MallMenu3Adapter mallMenu3Adapter;
    private MallHotMenuAdapter mallHotMenuAdapter;
    private MallGoodsAdapter mallGoodsAdapter;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_mall;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();

        binding.srlTop.setEnabled(false);
        pageAdapter = new MallMenu1PageAdapter(R.layout.item_mall_rv);
        binding.vpMenu1.setAdapter(pageAdapter);

        binding.banner.addBannerLifecycleObserver(this);
        binding.banner.setIndicator(new CircleIndicator(getActivity()));

        mallHotMenuAdapter = new MallHotMenuAdapter(R.layout.item_mall_hot);
        binding.rvMenu2.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        binding.rvMenu2.addItemDecoration(new SpeacesItemDecoration(getActivity(), 1, false));

        binding.rvMenu2.setAdapter(mallHotMenuAdapter);

        mallMenu3Adapter = new MallMenu3Adapter(R.layout.item_mall_menu3);
        binding.rvMenu3.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        binding.rvMenu3.setAdapter(mallMenu3Adapter);

        mallGoodsAdapter = new MallGoodsAdapter(R.layout.item_mall_good);
        //瀑布流
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        //防止Item切换
        manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        binding.rvContent.setLayoutManager(manager);
        binding.rvContent.setAdapter(mallGoodsAdapter);
        //防止刷新跳动
        binding.rvContent.setItemAnimator(null);

        viewModel.getMenu3List();
        viewModel.getMallGoods();
        viewModel.getMenuInfo();
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

    int pageCount;
    int pageSize = 8;

    @Override
    public void initViewObservable() {
        super.initViewObservable();


        viewModel.mallMenuRes.observe(this, mallMenuRes -> {
            if (mallMenuRes != null) {
                if (mallMenuRes.category != null) {
                    setMenu1Data(mallMenuRes.category.menuInfoList);
                }
                if (mallMenuRes.advertising != null) {
                    setMenu2Data(mallMenuRes.advertising.menuInfoList);
                }
                if(mallMenuRes.banners != null){
                    setBannerData(mallMenuRes.banners.menuInfoList);
                }
            }
        });

        viewModel.menu3.observe(this, datas -> {
            if (datas != null && datas.size() > 0) {
                mallMenu3Adapter.setNewInstance(datas);
            }
        });

        viewModel.datas.observe(this, datas -> {
            if (datas != null && datas.size() > 0) {
                mallGoodsAdapter.setNewInstance(datas);
            }
        });


        binding.srlTop.setOnRefreshListener(() -> {
            binding.srlTop.setRefreshing(false);
        });
        binding.appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                //为菜单栏固定位置
                if (verticalOffset > -binding.rvMenu3.getTop()) {
                    binding.rvMenu3.setBackgroundColor(getResources().getColor(R.color.transparent));
                } else {
                    binding.rvMenu3.setBackgroundColor(getResources().getColor(R.color.white));
                }

                //判单只有滑倒最顶部才能下拉刷新
                if (verticalOffset == 0) {
                    binding.srlTop.setEnabled(true);
                } else {
                    binding.srlTop.setEnabled(false);
                }
            }
        });
    }

    private void setBannerData(List<MenuBean> bannerImages) {
        binding.banner.setDatas(bannerImages);
        binding.banner.setAdapter(new BannerImageAdapter<MenuBean>(bannerImages) {
            @Override
            public void onBindView(BannerImageHolder holder, MenuBean data, int position, int size) {
                holder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                Glide.with(holder.itemView)
                        .load(data.imageUrl)
                        .thumbnail(0.3f)
                        .into(holder.imageView);
            }
        });
    }

    private void setMenu2Data(List<MenuBean> datas) {
        if (datas != null && datas.size() > 0) {
            mallHotMenuAdapter.setNewInstance(datas);
        }
    }

    private void setMenu1Data(List<MenuBean> datas) {
        if (datas != null && datas.size() > 0) {

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
}
