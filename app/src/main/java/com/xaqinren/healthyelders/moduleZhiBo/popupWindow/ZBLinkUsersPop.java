package com.xaqinren.healthyelders.moduleZhiBo.popupWindow;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.apiserver.ApiServer;
import com.xaqinren.healthyelders.apiserver.CustomObserver;
import com.xaqinren.healthyelders.apiserver.MBaseResponse;
import com.xaqinren.healthyelders.bean.BaseListRes;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.http.RetrofitClient;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.ZBLinkShowAdapter;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.ZBLinkUsersAdapter;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBUserListBean;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.LiveConstants;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.roomutil.commondef.AnchorInfo;


import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.utils.RxUtils;
import razerdp.basepopup.BasePopupWindow;

/**
 * Created by Lee. on 2021/3/30.
 */
public class ZBLinkUsersPop extends BasePopupWindow {

    private RecyclerView rvLinks;//申请连麦的列表
    private RecyclerView rvUsers;//用户列表
    private LinearLayout llSearch;
    private EditText etSearch;
    private TextView tvYQLM;
    private TextView tvSQXX;
    private TextView tvCloseLink;
    private View line1;
    private View line2;
    private int type;//0表示点开始申请消息 1表示是邀请连麦
    private int page = 1;
    private int openType;//1从底部打开 2从多人连麦的座位打开
    private String liveSceneId;
    private List<AnchorInfo> pusherList;
    private boolean isLinking;
    private ZBLinkShowAdapter linksShowAdapter;
    private ZBLinkUsersAdapter usersAdapter;
    private Context context;
    private BaseLoadMoreModule loadMore;
    private TextView tvSetting;

    public ZBLinkUsersPop(int openType, Context context, String liveSceneId, int type, ZBLinkShowAdapter linksShowAdapter, List<AnchorInfo> pusherList, boolean isLinking) {
        super(context);
        this.openType = openType;
        this.pusherList = pusherList;
        this.isLinking = isLinking;
        this.linksShowAdapter = linksShowAdapter;
        this.context = context;
        this.type = type;
        this.liveSceneId = liveSceneId;
        initView();
        if (linksShowAdapter != null && linksShowAdapter.getData().size() == 0) {
            linksShowAdapter.setEmptyView(R.layout.item_empty_sqlm);
        }
    }


    private void initView() {
        rvLinks = findViewById(R.id.rv_link_list);
        rvUsers = findViewById(R.id.rv_list);
        llSearch = findViewById(R.id.ll_search);
        etSearch = findViewById(R.id.et_search);
        tvSQXX = findViewById(R.id.tv_sqxx);
        line1 = findViewById(R.id.line1);
        line2 = findViewById(R.id.line2);
        tvYQLM = findViewById(R.id.tv_yqlm);
        tvCloseLink = findViewById(R.id.tv_close_link);
        tvSetting = findViewById(R.id.tv_setting);

        if (isLinking) {
            tvCloseLink.setTextColor(context.getResources().getColor(R.color.color_252525));
            tvCloseLink.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        } else {
            tvCloseLink.setTextColor(context.getResources().getColor(R.color.gray_999));
            tvCloseLink.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }

        setBackPressEnable(false);
        setAlignBackground(true);
        //展示用户列表
        rvUsers.setLayoutManager(new LinearLayoutManager(context));
        usersAdapter = new ZBLinkUsersAdapter(R.layout.item_zb_user);
        rvUsers.setAdapter(usersAdapter);


        loadMore = usersAdapter.getLoadMoreModule();
        loadMore.setEnableLoadMore(true);//打开上拉加载
        loadMore.setAutoLoadMore(true);//自动加载
        loadMore.setPreLoadNumber(1);//设置滑动到倒数第几个条目时自动加载，默认为1
        loadMore.setEnableLoadMoreIfNotFullPage(true);//
        loadMore.setOnLoadMoreListener(() -> {
            page++;
            getUserList();
        });


        //展示申请连麦列表
        rvLinks.setLayoutManager(new LinearLayoutManager(context));
        rvLinks.setAdapter(linksShowAdapter);

        if (type == 1) {
            showYQLM(context);
        } else {
            showSQXX(context);
        }


        initEvent();
    }

    private void initEvent() {
        tvSQXX.setOnClickListener(lis -> {
            showSQXX(context);
        });
        tvYQLM.setOnClickListener(lis -> {
            showYQLM(context);
        });

        tvCloseLink.setOnClickListener(lis -> {
            RxBus.getDefault().post(new EventBean(LiveConstants.ZB_LINK_GB, 1));
        });

        tvSetting.setOnClickListener(lis -> {
            ZB2LinkSettingPop zb2LinkSettingPop = new ZB2LinkSettingPop(context);
            zb2LinkSettingPop.showPopupWindow();
        });

        usersAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (view.getId() == R.id.tv_yq) {
                    //发起邀请连麦
                    if (usersAdapter.getData().get(position).isSelect) {
                        return;
                    }
                    RxBus.getDefault().post(new EventBean(LiveConstants.ZB_LINK_YQ, String.valueOf(usersAdapter.getData().get(position).userId), openType));
                }
            }
        });
    }

    private void showYQLM(Context context) {
        llSearch.setVisibility(View.VISIBLE);
        line1.setVisibility(View.INVISIBLE);
        line2.setVisibility(View.VISIBLE);
        tvYQLM.setTextColor(context.getResources().getColor(R.color.color_1C1E1D));
        tvSQXX.setTextColor(context.getResources().getColor(R.color.gray_999));
        rvUsers.setVisibility(View.VISIBLE);
        rvLinks.setVisibility(View.GONE);
        if (usersAdapter.getData().size() == 0) {
            getUserList();
        }
    }

    private void showSQXX(Context context) {
        llSearch.setVisibility(View.GONE);
        line1.setVisibility(View.VISIBLE);
        line2.setVisibility(View.INVISIBLE);
        tvSQXX.setTextColor(context.getResources().getColor(R.color.color_1C1E1D));
        tvYQLM.setTextColor(context.getResources().getColor(R.color.gray_999));
        rvUsers.setVisibility(View.GONE);
        rvLinks.setVisibility(View.VISIBLE);
    }


    private List<Integer> linkPosList = new ArrayList<>();

    private void getUserList() {
        RetrofitClient.getInstance().create(ApiServer.class).getZBUserList(
                UserInfoMgr.getInstance().getHttpToken(),
                liveSceneId, page, 30)
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {

                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<BaseListRes<List<ZBUserListBean>>>>() {
                    @Override
                    protected void dismissDialog() {

                    }

                    @Override
                    protected void onSuccess(MBaseResponse<BaseListRes<List<ZBUserListBean>>> data) {
                        if (page == 1) {
                            usersAdapter.setNewInstance(data.getData().content);
                        } else {
                            usersAdapter.addData(data.getData().content);
                        }
                        if (data.getData().content.size() == 0) {
                            loadMore.loadMoreEnd(true);
                        } else {
                            loadMore.loadMoreComplete();
                        }

                        if (isLinking) {
                            for (int i = 0; i < usersAdapter.getData().size(); i++) {
                                for (AnchorInfo anchorInfo : pusherList) {
                                    if (usersAdapter.getData().get(i).userId.toString().equals(anchorInfo.userID)) {
                                        linkPosList.add(i);
                                    }
                                }

                            }
                            for (Integer pos : linkPosList) {
                                usersAdapter.getData().get(pos).isSelect = true;
                                usersAdapter.notifyItemChanged(pos, 99);
                            }
                        }
                    }
                });
    }


    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.layout_link_list_pop);
    }

    @Override
    public void onDismiss() {
        super.onDismiss();
    }
}
