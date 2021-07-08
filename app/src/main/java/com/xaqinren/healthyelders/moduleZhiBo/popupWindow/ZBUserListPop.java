package com.xaqinren.healthyelders.moduleZhiBo.popupWindow;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.apiserver.ApiServer;
import com.xaqinren.healthyelders.apiserver.CustomObserver;
import com.xaqinren.healthyelders.apiserver.MBaseResponse;
import com.xaqinren.healthyelders.bean.BaseListRes;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.http.RetrofitClient;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.ZBUserListAdapter;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBUserListBean;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.LiveConstants;
import com.xaqinren.healthyelders.uniApp.UniUtil;
import com.xaqinren.healthyelders.uniApp.bean.UniEventBean;

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
 * Created by Lee. on 2021/3/30.
 * 直播间用户列表
 */
public class ZBUserListPop extends BasePopupWindow {

    private RecyclerView rvUsers;
    private ZBUserListAdapter usersAdapter;
    private int page = 1;
    private String liveRoomRecordId;
    private String liveRoomId;
    private int nowPosition;
    private BaseLoadMoreModule loadMore;
    private QMUIBottomSheet menuDialog;
    private LoadingDialog dialog;
    private ZBUserInfoPop userInfoPop;

    public ZBUserListPop(Context context) {
        super(context);
    }

    public ZBUserListPop(Context context, LiveInitInfo mLiveInitInfo, String fansTeamName) {
        super(context);
        this.liveRoomId = mLiveInitInfo.liveRoomId;
        this.liveRoomRecordId = mLiveInitInfo.liveRoomRecordId;
        rvUsers = findViewById(R.id.rv_list);
        setBackPressEnable(false);
        setAlignBackground(true);

        rvUsers.setLayoutManager(new LinearLayoutManager(context));
        usersAdapter = new ZBUserListAdapter(R.layout.item_zb_list_user, fansTeamName);
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

        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.height = 20;
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setBackgroundColor(context.getResources().getColor(R.color.color_EEE));

        usersAdapter.setOnItemClickListener(((adapter, view, position) -> {
            //说明是主播点开
            if (mLiveInitInfo.userId.equals(UserInfoMgr.getInstance().getUserInfo().getId())) {
                nowPosition = position;
                String userId = String.valueOf(usersAdapter.getData().get(position).userId);
                showMenuDialog(position, userId, context);
            } else {
                if (!UserInfoMgr.getInstance().getUserInfo().getId().equals(usersAdapter.getData().get(position).userId)) {
                    userInfoPop = new ZBUserInfoPop(getContext(), 1, mLiveInitInfo, usersAdapter.getData().get(position).userId);
                    userInfoPop.showPopupWindow();
                }

            }

        }));
        usersAdapter.setOnItemChildClickListener(((adapter, view, position) -> {
            nowPosition = position;
            if (view.getId() == R.id.iv_follow) {
                //关注
                setUserFollow(String.valueOf(usersAdapter.getData().get(position).userId));
            } else if (view.getId() == R.id.iv_avatar) {
                //头像
                if (!UserInfoMgr.getInstance().getUserInfo().getId().equals(usersAdapter.getData().get(position).userId)) {
                    userInfoPop = new ZBUserInfoPop(getContext(), 1, mLiveInitInfo, usersAdapter.getData().get(position).userId);
                    userInfoPop.showPopupWindow();
                }
            }
        }));

    }

    @Override
    public void showPopupWindow() {
        super.showPopupWindow();
        page = 1;
        getUserList();
    }

    public void showWaitDialog() {
        if (dialog == null) {
            dialog = new LoadingDialog(getContext());
        }
        dialog.show();
    }

    public void dismissWaitDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void showMenuDialog(int position, String userId, Context context) {
        menuDialog = new QMUIBottomSheet.BottomListSheetBuilder(context)
                .setGravityCenter(true)
                .addItem(usersAdapter.getData().get(position).hasSpeech ? "解除禁言" : "禁言")
                .addItem("拉黑")
                .addItem("踢出直播间")
                .setAddCancelBtn(true)
                .setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                    @Override
                    public void onClick(QMUIBottomSheet dialog, View itemView, int pos, String tag) {
                        setUserEvent(dialog, pos, userId);
                    }
                })
                .build();
        menuDialog.show();
    }


    private void setUserEvent(QMUIBottomSheet dialog, int pos, String userId) {
        dialog.dismiss();

        if (pos == 0) {
            setUserSpeechStatus(userId, !usersAdapter.getData().get(nowPosition).hasSpeech);
        } else if (pos == 1) {
            //拉黑
            setUserBlackStatus(userId);
        } else {
            //踢出
            RxBus.getDefault().post(new EventBean(LiveConstants.ZB_USER_SET, LiveConstants.SETTING_TICHU,
                    userId, usersAdapter.getData().get(nowPosition).nickname));
            usersAdapter.remove(nowPosition);
        }
    }

    private void getUserList() {
        RetrofitClient.getInstance().create(ApiServer.class).getZBUserList(
                UserInfoMgr.getInstance().getHttpToken(),
                liveRoomRecordId, page, 30)
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {

                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<BaseListRes<List<ZBUserListBean>>>>() {
                    @Override
                    protected void dismissDialog() {
                        dismissWaitDialog();
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
                    }
                });
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.layout_top_list_pop);
    }

    //通知后台禁言操作
    private void setUserSpeechStatus(String userId, boolean status) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("targetId", userId);
        hashMap.put("liveRoomRecordId", liveRoomRecordId);
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
                        showWaitDialog();
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<BaseListRes<Object>>>() {
                    @Override
                    protected void dismissDialog() {
                        dismissWaitDialog();
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<BaseListRes<Object>> data) {
                        usersAdapter.getData().get(nowPosition).hasSpeech = status;
                        //刷新item
                        usersAdapter.notifyItemChanged(nowPosition, 99);
                        //通知主播页面禁言操作了 1禁言 0没有禁言
                        RxBus.getDefault().post(new EventBean(LiveConstants.ZB_USER_SET, LiveConstants.SETTING_JINYAN,
                                userId, status ? 1 : 0));
                    }
                });
    }

    //通知后台拉黑操作
    private void setUserBlackStatus(String userId) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("targetId", userId);
        hashMap.put("liveRoomId", liveRoomId);
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
                        showWaitDialog();
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<BaseListRes<Object>>>() {
                    @Override
                    protected void dismissDialog() {
                        dismissWaitDialog();
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<BaseListRes<Object>> data) {
                        //更新列表
                        rvUsers.post(new Runnable() {
                            @Override
                            public void run() {
                                usersAdapter.remove(nowPosition);
                            }
                        });
                        //通知主播页面拉黑操作了
                        RxBus.getDefault().post(new EventBean(LiveConstants.ZB_USER_SET, LiveConstants.SETTING_LAHEI,
                                userId, usersAdapter.getData().get(nowPosition).nickname));
                    }
                });
    }

    //关注用户操作
    private void setUserFollow(String userId) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userId", userId);
        hashMap.put("attentionSource", "LIVE_ROOM");
        String json = JSON.toJSONString(hashMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        RetrofitClient.getInstance().create(ApiServer.class).setUserFollow(
                UserInfoMgr.getInstance().getHttpToken(),
                body)
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        showWaitDialog();
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<BaseListRes<Object>>>() {
                    @Override
                    protected void dismissDialog() {
                        dismissWaitDialog();
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<BaseListRes<Object>> data) {
                        usersAdapter.getData().get(nowPosition).identity = "FRIEND";
                        //刷新item
                        usersAdapter.notifyItemChanged(nowPosition, 99);
                    }
                });
    }
}
