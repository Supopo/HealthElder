package com.xaqinren.healthyelders.moduleMall.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.google.android.material.appbar.AppBarLayout;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.FragmentMallBinding;
import com.xaqinren.healthyelders.moduleHome.bean.MenuBean;
import com.xaqinren.healthyelders.moduleMall.adapter.MallGoodsAdapter;
import com.xaqinren.healthyelders.moduleMall.adapter.MallHotMenuAdapter;
import com.xaqinren.healthyelders.moduleMall.adapter.MallMenu1PageAdapter;
import com.xaqinren.healthyelders.moduleMall.adapter.MallMenu3Adapter;
import com.xaqinren.healthyelders.moduleMall.viewModel.MallViewModel;
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
    private int page = 1;
    private String category = "";
    private BaseLoadMoreModule mLoadMore;

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

        mallMenu3Adapter.setOnItemClickListener(((adapter, view, position) -> {
            mallMenu3Adapter.getData().get(menu3OldPos).isSelect = false;
            mallMenu3Adapter.notifyItemChanged(menu3OldPos);
            mallMenu3Adapter.getData().get(position).isSelect = true;
            mallMenu3Adapter.notifyItemChanged(position);
            menu3OldPos = position;
            //请求对应商品数据
            category = mallMenu3Adapter.getData().get(position).subMenuName;

            showDialog();
            page = 1;
            viewModel.getGoodsList(page, category);

        }));

        mallGoodsAdapter = new MallGoodsAdapter(R.layout.item_mall_good);
        binding.rvContent.addItemDecoration(new SpeacesItemDecoration(getActivity(), 3, false));

        binding.rvContent.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        binding.rvContent.setAdapter(mallGoodsAdapter);
        //防止刷新跳动
        binding.rvContent.setItemAnimator(null);

        mLoadMore = mallGoodsAdapter.getLoadMoreModule();//创建适配器.上拉加载
        mLoadMore.setEnableLoadMore(true);//打开上拉加载
        mLoadMore.setAutoLoadMore(true);//自动加载
        mLoadMore.setPreLoadNumber(1);//设置滑动到倒数第几个条目时自动加载，默认为1
        mLoadMore.setEnableLoadMoreIfNotFullPage(true);//当数据不满一页时继续自动加载
        //mLoadMore.setLoadMoreView(new BaseLoadMoreView)//设置自定义加载布局
        mLoadMore.setOnLoadMoreListener(new OnLoadMoreListener() {//设置加载更多监听事件
            @Override
            public void onLoadMore() {
                page++;
                viewModel.getGoodsList(page, category);
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

    int pageCount;
    int pageSize = 10;

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.dismissDialog.observe(this,isDismiss ->{
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
                datas.get(0).isSelect = true;
                mallMenu3Adapter.setNewInstance(datas);

                category = datas.get(0).subMenuName;
                showDialog();
                viewModel.getGoodsList(page, category);
            }
        });

        viewModel.goodsList.observe(this, datas -> {
            if (datas != null) {
                if (datas.size() > 0) {
                    //加载更多加载完成
                    mLoadMore.loadMoreComplete();
                }

                if (page == 1) {
                    //为了防止刷新时候图片闪烁统一用notifyItemRangeInserted刷新
                    mallGoodsAdapter.setList(datas);
                    if (datas.size() == 0) {
                        //创建适配器.空布局，没有数据时候默认展示的
                        mallGoodsAdapter.setEmptyView(R.layout.list_empty);
                    }
                } else {
                    if (datas.size() == 0) {
                        //加载更多加载结束
                        mLoadMore.loadMoreEnd(true);
                        page--;
                    }
                    mallGoodsAdapter.addData(datas);
                }

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
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .thumbnail(0.3f)
                        .into(holder.imageView);
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
