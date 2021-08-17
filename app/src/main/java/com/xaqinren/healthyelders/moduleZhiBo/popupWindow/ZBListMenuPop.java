package com.xaqinren.healthyelders.moduleZhiBo.popupWindow;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tencent.qcloud.tim.uikit.utils.ScreenUtil;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.apiserver.ApiServer;
import com.xaqinren.healthyelders.apiserver.CustomObserver;
import com.xaqinren.healthyelders.apiserver.MBaseResponse;
import com.xaqinren.healthyelders.bean.BaseListRes;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.http.RetrofitClient;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ListPopMenuBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBUserListBean;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.LiveConstants;
import com.xaqinren.healthyelders.utils.AnimUtils;
import com.xaqinren.healthyelders.utils.MScreenUtil;

import org.jetbrains.annotations.NotNull;

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
 * Created by Lee. on 2021/4/24.
 * 禁言 拉黑 踢出
 */
public class ZBListMenuPop extends BasePopupWindow {
    public List<ListPopMenuBean> menus;
    private BaseQuickAdapter<ListPopMenuBean, BaseViewHolder> adapter;
    private LiveInitInfo mLiveInitInfo;
    private UserInfoBean userListBean;
    private LoadingDialog loadingDialog;

    public ZBListMenuPop(Context context, LiveInitInfo mLiveInitInfo, UserInfoBean userListBean) {
        super(context);
        setShowAnimation(AnimUtils.PopAnimBottom2Enter(context));
        setDismissAnimation(AnimUtils.PopAnimBottom2Exit(context));
        this.mLiveInitInfo = mLiveInitInfo;
        this.userListBean = userListBean;
        initView();
    }


    private void initView() {
        menus = new ArrayList<>();
        menus.add(new ListPopMenuBean(userListBean.getHasSpeech() ? "取消禁言" : "禁言", 0, 0));
        menus.add(new ListPopMenuBean("拉黑", 0, 0));
        menus.add(new ListPopMenuBean("踢出直播间", 0, 0));

        loadingDialog = new LoadingDialog(getContext());

        RecyclerView rvPop = findViewById(R.id.rv_pop);
        TextView tvCancel = findViewById(R.id.tv_cancel);
        RelativeLayout rlPop = findViewById(R.id.rl_pop);

        //根据item计Pop的高度，不然是整页
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rlPop.getLayoutParams();
        lp.height = (int) MScreenUtil.dp2px(getContext(), (64 + menus.size() * 57));
        rlPop.setLayoutParams(lp);

        rvPop.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BaseQuickAdapter<ListPopMenuBean, BaseViewHolder>(R.layout.item_pop_list) {
            @Override
            protected void convert(@NotNull BaseViewHolder holder, ListPopMenuBean menuBean) {
                TextView tvMenu = holder.getView(R.id.tv_menu);
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tvMenu.getLayoutParams();
                lp.height = (int) MScreenUtil.dp2px(getContext(), 56);
                tvMenu.setLayoutParams(lp);

                holder.setText(R.id.tv_menu, menuBean.menuName);
                if (menuBean.textColor != 0) {
                    tvMenu.setTextColor(menuBean.textColor);
                }
                if (menuBean.textSize != 0) {
                    tvMenu.setTextSize(menuBean.textSize);
                }
                if (menuBean.textStyle == 1) {
                    tvMenu.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }
            }
        };
        rvPop.setAdapter(adapter);
        if (menus != null) {
            adapter.setNewInstance(menus);
        }

        adapter.setOnItemClickListener(((adapter, view, position) -> {
            switch (position) {
                case 0:
                    //禁言
                    setUserSpeechStatus(!userListBean.getHasSpeech());
                    break;
                case 1:
                    //拉黑
                    setUserBlackStatus();
                    break;
                case 2:
                    //踢出
                    RxBus.getDefault().post(new EventBean(LiveConstants.ZB_USER_SET, LiveConstants.SETTING_TICHU,
                            userListBean.getId(), userListBean.getNickname()));
                    break;
            }
            dismiss();
        }));
        tvCancel.setOnClickListener(lis -> {
            dismiss();
        });
    }

    //通知后台禁言操作
    private void setUserSpeechStatus(boolean status) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("targetId", userListBean.getId());
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
                        //通知主播页面禁言操作了 1禁言 0没有禁言
                        userListBean.setHasSpeech(status);
                        RxBus.getDefault().post(new EventBean(LiveConstants.ZB_USER_SET, LiveConstants.SETTING_JINYAN,
                                userListBean.getId(), status ? 1 : 0));
                    }
                });
    }

    //通知后台拉黑操作
    private void setUserBlackStatus() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("targetId", userListBean.getId());
        hashMap.put("liveRoomId", mLiveInitInfo.liveRoomId);
        hashMap.put("status", true);
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

                        //通知主播页面拉黑操作了
                        RxBus.getDefault().post(new EventBean(LiveConstants.ZB_USER_SET, LiveConstants.SETTING_LAHEI,
                                userListBean.getId(), userListBean.getNickname()));
                    }
                });
    }


    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.pop_bottom_list);
    }

}
