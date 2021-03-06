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
        //?????????
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        //??????Item??????
        manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        binding.rvContent.setLayoutManager(manager);
        binding.rvContent.setAdapter(mallGoodsAdapter);

        //??????????????????
        binding.rvContent.setItemAnimator(null);


        mLoadMore = mallGoodsAdapter.getLoadMoreModule();//???????????????.????????????
        mLoadMore.setEnableLoadMore(true);//??????????????????
        mLoadMore.setAutoLoadMore(true);//????????????
        mLoadMore.setPreLoadNumber(1);//???????????????????????????????????????????????????????????????1
        mLoadMore.setEnableLoadMoreIfNotFullPage(true);//??????????????????????????????????????????
        //mLoadMore.setLoadMoreView(new BaseLoadMoreView)//???????????????????????????
        mLoadMore.setOnLoadMoreListener(new OnLoadMoreListener() {//??????????????????????????????
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
                .adapter(mallGoodsAdapter)//????????????adapter
                .shimmer(true)//??????????????????
                .color(R.color.flashColor)//shimmer?????????
                .angle(Constant.flashAngle)//shimmer???????????????
                .frozen(true)//true??????????????????????????????RecyclerView?????????????????????????????????
                .duration(Constant.flashDuration)//?????????????????????????????????
                .count(10)//??????????????????item?????????
                .load(R.layout.item_mall_good_def)//?????????UI
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
                    //????????????????????????
                    mLoadMore.loadMoreComplete();
                }

                if (page == 1) {
                    //?????????????????????????????????????????????notifyItemRangeInserted??????
                    mallGoodsAdapter.setList(datas);
                    if (datas.size() == 0) {
                        //???????????????.?????????????????????????????????????????????
                        mallGoodsAdapter.setEmptyView(R.layout.list_empty);
                    }
                } else {
                    if (datas.size() == 0) {
                        //????????????????????????
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
