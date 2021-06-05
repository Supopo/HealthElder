package com.xaqinren.healthyelders.moduleZhiBo.popupWindow;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.tencent.liteav.demo.beauty.model.BeautyInfo;
import com.tencent.liteav.demo.beauty.model.ItemInfo;
import com.tencent.liteav.demo.beauty.model.TabInfo;
import com.tencent.liteav.demo.beauty.view.BeautyPanel;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.apiserver.ApiServer;
import com.xaqinren.healthyelders.apiserver.CustomObserver;
import com.xaqinren.healthyelders.apiserver.LiveRepository;
import com.xaqinren.healthyelders.apiserver.MBaseResponse;
import com.xaqinren.healthyelders.bean.BaseListRes;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.http.RetrofitClient;
import com.xaqinren.healthyelders.moduleHome.bean.MenuBean;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.LiveMenuAdapter;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.ZBBlackListAdapter;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBSettingBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBUserListBean;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.LiveConstants;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.MLVBLiveRoom;
import com.xaqinren.healthyelders.utils.AnimUtils;
import com.xaqinren.healthyelders.widget.BottomDialog;
import com.xaqinren.healthyelders.widget.share.ShareDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.widget.LoadingDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import razerdp.basepopup.BasePopupWindow;

/**
 * Created by Lee. on 2021/4/20.
 * 直播间更多设置-拉黑禁言名单
 */
public class ZBMoreSettingUserListPop extends BasePopupWindow {


    private RecyclerView rvContent;
    private RecyclerView rvContent2;
    private LiveInitInfo mLiveInitInfo;
    private LoadingDialog loadingDialog;
    private ZBBlackListAdapter zbBlackListAdapter;
    private ZBBlackListAdapter zbJinyanListAdapter;
    private TextView tvTips;
    private TextView tvLahei;
    private TextView tvJinyan;
    private ImageView ivBack;
    private BaseLoadMoreModule mLoadMore;
    private BaseLoadMoreModule mLoadMore2;
    private Context context;

    public ZBMoreSettingUserListPop(Context context, LiveInitInfo mLiveInitInfo) {
        super(context);
        this.context = context;
        this.mLiveInitInfo = mLiveInitInfo;

        //去掉背景
        setBackground(R.color.transparent);
        setShowAnimation(AnimUtils.PopAnimRight2Enter(context));
        setDismissAnimation(AnimUtils.PopAnimRight2Exit(context));
        initView();
        initEvent();
    }

    private int pageNum = 1;
    private int pageNum2 = 1;

    private void initView() {
        loadingDialog = new LoadingDialog(getContext());
        tvTips = findViewById(R.id.tv_tips);
        rvContent = findViewById(R.id.rv_list);
        rvContent2 = findViewById(R.id.rv_list2);
        ivBack = findViewById(R.id.iv_back);
        tvLahei = findViewById(R.id.tv_lh);
        tvJinyan = findViewById(R.id.tv_jy);

        initLaHei();
        initJinYan();
        ivBack.setOnClickListener(lis ->{
            dismiss();
        });

    }

    private void initLaHei() {
        zbBlackListAdapter = new ZBBlackListAdapter(R.layout.item_user_setting);
        rvContent.setLayoutManager(new LinearLayoutManager(getContext()));
        rvContent.setAdapter(zbBlackListAdapter);
        mLoadMore = zbBlackListAdapter.getLoadMoreModule();//创建适配器.上拉加载
        mLoadMore.setEnableLoadMore(true);//打开上拉加载
        mLoadMore.setAutoLoadMore(true);//自动加载
        mLoadMore.setPreLoadNumber(1);//设置滑动到倒数第几个条目时自动加载，默认为1
        mLoadMore.setEnableLoadMoreIfNotFullPage(true);//当数据不满一页时继续自动加载
        mLoadMore.setOnLoadMoreListener(new OnLoadMoreListener() {//设置加载更多监听事件
            @Override
            public void onLoadMore() {
                pageNum++;
                LiveRepository.getInstance().getBlackList(dismissDialog, userList, pageNum, mLiveInitInfo.liveRoomId);
            }
        });
        loadingDialog.show();
        LiveRepository.getInstance().getBlackList(dismissDialog, userList, pageNum, mLiveInitInfo.liveRoomId);


        tvLahei.setOnClickListener(lis -> {
            selectType = 0;
            tvLahei.setTextColor(getContext().getResources().getColor(R.color.color_252525));
            tvLahei.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            tvJinyan.setTextColor(getContext().getResources().getColor(R.color.gray_999));
            tvJinyan.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            rvContent.setVisibility(View.VISIBLE);
            rvContent2.setVisibility(View.GONE);
        });

        zbBlackListAdapter.setOnItemChildClickListener((adapter, view, pos) -> {
            blackPos = pos;
            loadingDialog.show();
            //取消拉黑 请求服务器
            setUserBlackStatus(zbBlackListAdapter.getData().get(pos).userId);
        });
    }

    private void initJinYan() {
        zbJinyanListAdapter = new ZBBlackListAdapter(R.layout.item_user_setting);
        rvContent2.setLayoutManager(new LinearLayoutManager(getContext()));
        rvContent2.setAdapter(zbJinyanListAdapter);

        mLoadMore2 = zbJinyanListAdapter.getLoadMoreModule();//创建适配器.上拉加载
        mLoadMore2.setEnableLoadMore(true);//打开上拉加载
        mLoadMore2.setAutoLoadMore(true);//自动加载
        mLoadMore2.setPreLoadNumber(1);//设置滑动到倒数第几个条目时自动加载，默认为1
        mLoadMore2.setEnableLoadMoreIfNotFullPage(true);//当数据不满一页时继续自动加载
        mLoadMore2.setOnLoadMoreListener(new OnLoadMoreListener() {//设置加载更多监听事件
            @Override
            public void onLoadMore() {
                pageNum2++;
                LiveRepository.getInstance().getJinYanList(dismissDialog, userList, pageNum2, mLiveInitInfo.liveRoomRecordId);
            }
        });

        tvJinyan.setOnClickListener(lis -> {
            if (zbJinyanListAdapter.getData().size() == 0) {
                loadingDialog.show();
                pageNum2 = 1;
                LiveRepository.getInstance().getJinYanList(dismissDialog, userList, pageNum2, mLiveInitInfo.liveRoomRecordId);
            }

            selectType = 1;
            tvJinyan.setTextColor(getContext().getResources().getColor(R.color.color_252525));
            tvJinyan.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            tvLahei.setTextColor(getContext().getResources().getColor(R.color.gray_999));
            tvLahei.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            rvContent.setVisibility(View.GONE);
            rvContent2.setVisibility(View.VISIBLE);
        });

        zbJinyanListAdapter.setOnItemChildClickListener((adapter, view, pos) -> {
            jinyanPos = pos;
            loadingDialog.show();
            //禁言取消禁言 请求服务器
            setUserSpeechStatus(zbJinyanListAdapter.getData().get(pos).userId, !zbJinyanListAdapter.getData().get(pos).hasSpeech);
        });
    }

    public int blackPos;
    public int jinyanPos;
    public int selectType;

    public int blackNum;

    public void initEvent() {
        dismissDialog.observe((LifecycleOwner) getContext(), dismiss -> {
            if (dismiss != null) {
                loadingDialog.dismiss();
            }
        });

        userList.observe((LifecycleOwner) context, userList -> {
            if (userList != null) {

                for (ZBUserListBean bean : userList.content) {
                    bean.showType = selectType;
                }
                blackNum = userList.totalElements;

                if (selectType == 0) {
                    tvTips.setText("已被拉黑的用户 (" + userList.totalElements + "/5000)");
                    if (userList.content.size() > 0) {
                        //加载更多加载完成
                        mLoadMore.loadMoreComplete();
                    }
                    if (pageNum == 1) {
                        //为了防止刷新时候图片闪烁统一用notifyItemRangeInserted刷新
                        zbBlackListAdapter.setList(userList.content);
                    } else {
                        if (userList.content.size() == 0) {
                            //加载更多加载结束
                            mLoadMore.loadMoreEnd(true);
                        }
                        zbBlackListAdapter.addData(userList.content);
                    }
                } else {
                    tvTips.setText("已被禁言的用户 (" + userList.totalElements + "/5000)");
                    if (userList.content.size() > 0) {
                        //加载更多加载完成
                        mLoadMore2.loadMoreComplete();
                    }
                    if (pageNum2 == 1) {
                        //为了防止刷新时候图片闪烁统一用notifyItemRangeInserted刷新
                        zbJinyanListAdapter.setList(userList.content);
                    } else {
                        if (userList.content.size() == 0) {
                            //加载更多加载结束
                            mLoadMore2.loadMoreEnd(true);
                        }
                        zbJinyanListAdapter.addData(userList.content);
                    }
                }
            }
        });
    }

    public MutableLiveData<Boolean> dismissDialog = new MutableLiveData<>();
    public MutableLiveData<BaseListRes<List<ZBUserListBean>>> userList = new MutableLiveData<>();


    //通知后台拉黑操作
    private void setUserBlackStatus(String userId) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("targetId", userId);
        hashMap.put("liveRoomId", mLiveInitInfo.liveRoomId);
        hashMap.put("status", false);
        String json = JSON.toJSONString(hashMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        RetrofitClient.getInstance().create(ApiServer.class).setUserBlack(
                UserInfoMgr.getInstance().getHttpToken(),
                body)
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        loadingDialog.show();
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<BaseListRes<Object>>>() {
                    @Override
                    protected void dismissDialog() {
                        loadingDialog.dismiss();
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<BaseListRes<Object>> data) {
                        zbBlackListAdapter.remove(blackPos);
                        tvTips.setText("已被拉黑的用户 (" + (blackNum - 1) + "/5000)");
                    }
                });
    }

    private void setUserSpeechStatus(String userId, boolean status) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("targetId", userId);
        hashMap.put("liveRoomRecordId", mLiveInitInfo.liveRoomRecordId);
        hashMap.put("status", status);
        String json = JSON.toJSONString(hashMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        RetrofitClient.getInstance().create(ApiServer.class).setUserSpeech(
                UserInfoMgr.getInstance().getHttpToken(),
                body)
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        loadingDialog.show();
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<BaseListRes<Object>>>() {
                    @Override
                    protected void dismissDialog() {
                        loadingDialog.dismiss();
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<BaseListRes<Object>> data) {
                        zbJinyanListAdapter.getData().get(jinyanPos).hasSpeech = status;
                        zbJinyanListAdapter.notifyItemChanged(jinyanPos, 99);
                        //通知主播页面禁言操作了 1禁言 0没有禁言
                        RxBus.getDefault().post(new EventBean(LiveConstants.ZB_USER_SET, LiveConstants.SETTING_JINYAN,
                                userId, status ? 1 : 0));
                    }
                });
    }

    @Override
    public void dismiss() {
        super.dismiss();

    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.pop_zbj_more_setting_user_list);
    }
}
