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
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.ZBUserListAdapter;
import com.xaqinren.healthyelders.moduleZhiBo.bean.JsonMsgBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBUserListBean;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.IMLVBLiveRoomListener;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.LiveConstants;
import com.xaqinren.healthyelders.uniApp.UniUtil;
import com.xaqinren.healthyelders.uniApp.bean.UniEventBean;
import com.xaqinren.healthyelders.utils.LogUtils;

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
    private Disposable subscribe;
    private LiveInitInfo mLiveInitInfo;
    private ZBListMenuPop listBottomPopup;

    public ZBUserListPop(Context context) {
        super(context);
    }

    public ZBUserListPop(Context context, LiveInitInfo mLiveInitInfo, String fansTeamName) {
        super(context);
        this.mLiveInitInfo = mLiveInitInfo;
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
                showMenuDialog(position);
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

        subscribe = RxBus.getDefault().toObservable(EventBean.class).subscribe(eventBean -> {
            if (eventBean.msgId == LiveConstants.ZB_USER_SET) {
                if (eventBean.msgType == LiveConstants.SETTING_JINYAN) {      //禁言-取消禁言
                    usersAdapter.getData().get(nowPosition).hasSpeech = !usersAdapter.getData().get(nowPosition).hasSpeech;
                    usersAdapter.notifyItemChanged(nowPosition, 99);
                } else if (eventBean.msgType == LiveConstants.SETTING_LAHEI) {      //拉黑
                    usersAdapter.remove(nowPosition);
                } else if (eventBean.msgType == LiveConstants.SETTING_TICHU) {      //踢出
                    usersAdapter.remove(nowPosition);
                }
            }
        });

    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (subscribe != null) {
            subscribe.dispose();
        }
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

    private void showMenuDialog(int position) {
        ZBUserListBean userListBean = usersAdapter.getData().get(position);
        UserInfoBean userInfoBean = new UserInfoBean();
        userInfoBean.setId(userListBean.userId);
        userInfoBean.setNickname(userListBean.nickname);
        userInfoBean.setHasSpeech(userListBean.hasSpeech);
        listBottomPopup = new ZBListMenuPop(getContext(), mLiveInitInfo, userInfoBean);
        listBottomPopup.showPopupWindow();
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
