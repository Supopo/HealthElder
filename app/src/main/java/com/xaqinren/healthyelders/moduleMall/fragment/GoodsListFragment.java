package com.xaqinren.healthyelders.moduleMall.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.FragmentGoodsListBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleLogin.activity.SelectLoginActivity;
import com.xaqinren.healthyelders.moduleMall.adapter.MallGoodsAdapter;
import com.xaqinren.healthyelders.moduleMall.viewModel.GoodsListViewModel;
import com.xaqinren.healthyelders.moduleZhiBo.bean.GoodsBean;
import com.xaqinren.healthyelders.uniApp.UniService;
import com.xaqinren.healthyelders.utils.MScreenUtil;
import com.xaqinren.healthyelders.widget.SpeacesItemDecoration;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.bus.RxBus;

/**
 * Created by Lee. on 2021/5/25.
 */
public class GoodsListFragment extends BaseFragment<FragmentGoodsListBinding, GoodsListViewModel> {
    private MallGoodsAdapter mallGoodsAdapter;
    private int page = 1;
    private String category;
    private BaseLoadMoreModule mLoadMore;
    private int fPosition;
    private boolean isFirstNet;
    private Disposable subscribe;
    private SkeletonScreen skeletonScreen;

    public GoodsListFragment() {
    }

    public GoodsListFragment(int position, String category, boolean isFirstNet) {
        this.fPosition = position;
        this.category = category;
        this.isFirstNet = isFirstNet;
    }


    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_goods_list;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }


    @Override
    public void initData() {
        super.initData();
        mallGoodsAdapter = new MallGoodsAdapter(R.layout.item_mall_good);
        binding.rvContent.addItemDecoration(new SpeacesItemDecoration(getActivity(), 3, true));
        //瀑布流
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        //防止Item切换
        manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        binding.rvContent.setLayoutManager(manager);
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

        mallGoodsAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (UserInfoMgr.getInstance().getAccessToken() == null) {
                    startActivity(SelectLoginActivity.class);
                    return;
                }
                GoodsBean goodsBean = mallGoodsAdapter.getData().get(position);
                UniService.startService(getContext(), goodsBean.appId, 0x10001, goodsBean.jumpUrl);
            }
        });
        isFirst = false;

        //default count is 10
        if (AppApplication.get().isFirstLoadGoodsList) {
            showSkeleton();
            AppApplication.get().isFirstLoadGoodsList = false;
        }
    }

    public void showSkeleton() {
        skeletonScreen = Skeleton.bind(binding.rvContent)
                .adapter(mallGoodsAdapter)//设置实际adapter
                .shimmer(true)//是否开启动画
                .color(R.color.flashColor)//shimmer的颜色
                .angle(Constant.flashAngle)//shimmer的倾斜角度
                .frozen(true)//true则表示显示骨架屏时，RecyclerView不可滑动，否则可以滑动
                .duration(Constant.flashDuration)//动画时间，以毫秒为单位
                .count(10)//显示骨架屏时item的个数
                .load(R.layout.item_mall_good_def)//骨架屏UI
                .show();
    }

    private boolean isFirst = true;

    @Override
    public void initViewObservable() {
        super.initViewObservable();

        subscribe = RxBus.getDefault().toObservable(EventBean.class).subscribe(event -> {
            if (event != null) {
                if (fPosition == event.msgType) {
                    if (event.msgId == CodeTable.RESH_MALL_LIST) {
                        page = 1;
                        viewModel.getGoodsList(page, category);
                    } else if (event.msgId == CodeTable.ADD_MALL_LIST) {
                        if (mallGoodsAdapter.getData().size() == 0) {
                            page = 1;
                            viewModel.getGoodsList(page, category);
                        }
                    }
                }
            }
        });


        viewModel.dismissDialog.observe(this, isDismiss -> {
            dismissDialog();
        });


        viewModel.goodsList.observe(this, datas -> {
            if (datas != null) {
                if (skeletonScreen != null) {
                    if (page == 1) {
                        binding.rvContent.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                skeletonScreen.hide();
                            }
                        }, 1000);
                    }
                }


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
                        mLoadMore.loadMoreEnd(false);
                        page--;
                    }
                    mallGoodsAdapter.addData(datas);
                }

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (subscribe != null) {
            subscribe.dispose();
        }
    }
}
