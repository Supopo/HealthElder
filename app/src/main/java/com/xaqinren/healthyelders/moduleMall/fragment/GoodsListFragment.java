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
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.FragmentGoodsListBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.moduleMall.adapter.MallGoodsAdapter;
import com.xaqinren.healthyelders.moduleMall.viewModel.GoodsListViewModel;
import com.xaqinren.healthyelders.moduleZhiBo.bean.GoodsBean;
import com.xaqinren.healthyelders.uniApp.UniService;
import com.xaqinren.healthyelders.uniApp.UniUtil;
import com.xaqinren.healthyelders.uniApp.bean.UniEventBean;
import com.xaqinren.healthyelders.widget.SpeacesItemDecoration;

import org.json.JSONObject;

import io.dcloud.feature.sdk.DCSDKInitConfig;
import io.dcloud.feature.sdk.DCUniMPSDK;
import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.utils.ToastUtils;

/**
 * Created by Lee. on 2021/5/25.
 */
public class GoodsListFragment extends BaseFragment<FragmentGoodsListBinding, GoodsListViewModel> {
    private MallGoodsAdapter mallGoodsAdapter;
    private int page = 1;
    private String category;
    private BaseLoadMoreModule mLoadMore;
    private int fPosition;
    private Disposable subscribe;
    private Disposable uniSubscribe;
    private int clickIndex;

    public GoodsListFragment(int position, String category) {
        fPosition = position;
        this.category = category;
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
                clickIndex = position;
                //UniService.startService(getContext(), "__UNI__DFE7692", 0x123456);
            }
        });
        isFirst = false;
    }
    private boolean isFirst = true;

    @Override
    public void initViewObservable() {
        super.initViewObservable();

        subscribe = RxBus.getDefault().toObservable(EventBean.class).subscribe(event -> {
            if (event != null) {
                if (fPosition == event.msgType) {
                    if (event.msgId == CodeTable.RESH_MALL_LIST ) {
                        page = 1;
                        viewModel.getGoodsList(page, category);
                    }else if(event.msgId == CodeTable.ADD_MALL_LIST ){
                        if (mallGoodsAdapter.getData().size() == 0) {
                            page = 1;
                            viewModel.getGoodsList(page, category);
                        }
                    }
                }
            }
        });

        uniSubscribe = RxBus.getDefault().toObservable(UniEventBean.class).subscribe(event -> {
            if (event != null) {
                if (event.msgId == CodeTable.UNI_RELEASE) {
                    //GoodsBean goodsBean = (GoodsBean) mallGoodsAdapter.getData().get(clickIndex);
                    //UniUtil.openUniApp(getContext(), event.appId, "/page/index/index", null, true);
                } else if (event.msgId == CodeTable.UNI_RELEASE_FAIL) {
                    //ToastUtils.showShort("打开小程序失败");
                    //DCUniMPSDK.getInstance().sendUniMPEvent(event, data) 发送上层消息
                }
            }
        });
        RxSubscriptions.add(uniSubscribe);

        viewModel.dismissDialog.observe(this, isDismiss -> {
            dismissDialog();
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (subscribe != null) {
            subscribe.dispose();
        }
        RxSubscriptions.remove(uniSubscribe);
        if (uniSubscribe != null) {
            uniSubscribe.dispose();
        }
    }
}
