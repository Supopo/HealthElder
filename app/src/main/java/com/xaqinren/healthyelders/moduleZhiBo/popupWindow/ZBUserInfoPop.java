package com.xaqinren.healthyelders.moduleZhiBo.popupWindow;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.apiserver.ApiServer;
import com.xaqinren.healthyelders.apiserver.CustomObserver;
import com.xaqinren.healthyelders.apiserver.MBaseResponse;
import com.xaqinren.healthyelders.apiserver.UserRepository;
import com.xaqinren.healthyelders.bean.BaseListRes;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.http.RetrofitClient;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleMine.activity.UserInfoActivity;
import com.xaqinren.healthyelders.moduleZhiBo.activity.ZBEditTextDialogActivity;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ListPopMenuBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBUserListBean;
import com.xaqinren.healthyelders.uniApp.UniService;
import com.xaqinren.healthyelders.uniApp.UniUtil;
import com.xaqinren.healthyelders.uniApp.bean.UniEventBean;
import com.xaqinren.healthyelders.utils.AnimUtils;
import com.xaqinren.healthyelders.widget.ListBottomPopup;
import com.xaqinren.healthyelders.widget.YesOrNoDialog;

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
 * 直播间用户信息弹窗
 */
public class ZBUserInfoPop extends BasePopupWindow {

    private String userId;
    private ZBListMenuPop listBottomPopup;
    private UserInfoBean userInfoBean;
    private LiveInitInfo mLiveInitInfo;
    private LoadingDialog loadingDialog;
    private TextView tvGz;
    private ImageView ivGz;
    private int type;
    private Disposable uniSubscribe;

    public ZBUserInfoPop(Context context, LiveInitInfo mLiveInitInfo, String userId) {
        super(context);
        this.userId = userId;
        this.mLiveInitInfo = mLiveInitInfo;
        setBackground(R.color.transparent);
        setShowAnimation(AnimUtils.PopAnimBottom2Enter(context));
        setDismissAnimation(AnimUtils.PopAnimBottom2Exit(context));
        initView();
    }

    public ZBUserInfoPop(Context context, int type, LiveInitInfo mLiveInitInfo, String userId) {
        super(context);
        this.userId = userId;
        this.mLiveInitInfo = mLiveInitInfo;
        this.type = type;

        setShowAnimation(AnimUtils.PopAnimBottom2Enter(context));
        setDismissAnimation(AnimUtils.PopAnimBottom2Exit(context));
        initView();
    }

    private void initView() {
        loadingDialog = new LoadingDialog(getContext());

        TextView tvName = findViewById(R.id.tv_name);
        TextView tvFansNum = findViewById(R.id.tv_fansNum);
        TextView tvFollowNum = findViewById(R.id.tv_followNum);
        TextView tvDes = findViewById(R.id.tv_des);
        TextView tvHome = findViewById(R.id.tv_home);
        tvGz = findViewById(R.id.tv_gz);
        TextView tvAt = findViewById(R.id.tv_at);
        ivGz = findViewById(R.id.iv_gz);
        ImageView ivSex = findViewById(R.id.iv_sex);
        ImageView ivGly = findViewById(R.id.iv_gly);
        LinearLayout llGl = findViewById(R.id.ll_gl);
        LinearLayout llJb = findViewById(R.id.ll_jb);

        if (!mLiveInitInfo.userId.equals(UserInfoMgr.getInstance().getUserInfo().getId())) {
            llGl.setVisibility(View.INVISIBLE);
        }

        LinearLayout llGz = findViewById(R.id.ll_gz);
        tvAt.setText("@TA");
        QMUIRadiusImageView rivPhoto = findViewById(R.id.riv_photo);

        UserRepository.getInstance().getLiveUserInfo(otherUserInfo, userId, mLiveInitInfo.liveRoomRecordId);
        otherUserInfo.observe((LifecycleOwner) getContext(), userInfoBean -> {
            if (userInfoBean != null) {
                this.userInfoBean = userInfoBean;
                tvDes.setText(userInfoBean.getIntroduce());
                tvFansNum.setText("" + userInfoBean.getFansCount());
                tvFollowNum.setText("" + userInfoBean.getAttentionCount());
                if (userInfoBean.getHasAdmin()) {
                    ivGly.setVisibility(View.VISIBLE);
                }
                if (userInfoBean.getHasAttention()) {
                    tvGz.setText("已关注");
                    tvGz.setTextColor(getContext().getResources().getColor(R.color.gray_999));
                    ivGz.setVisibility(View.GONE);
                } else {
                    tvGz.setText("关注");
                    ivGz.setVisibility(View.VISIBLE);
                    tvGz.setTextColor(getContext().getResources().getColor(R.color.color_DC3530));
                }

                tvName.setText(userInfoBean.getNickname());
                ivSex.setBackground(userInfoBean.getSex().equals("MALE") ? getContext().getResources().getDrawable(R.mipmap.male) : getContext().getResources().getDrawable(R.mipmap.female));
                Glide.with(getContext()).load(userInfoBean.getAvatarUrl()).into(rivPhoto);
            }
        });

        llGl.setOnClickListener(lis -> {
            showListPop();
        });

        llGz.setOnClickListener(lis -> {

            if (userInfoBean.getHasAttention()) {
                //取消关注
                YesOrNoDialog yesOrNoDialog = new YesOrNoDialog(getContext());
                yesOrNoDialog.setMessageText("确定不再关注此人？");
                yesOrNoDialog.setRightBtnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setUserFollow();
                        yesOrNoDialog.dismissDialog();
                    }
                });
                yesOrNoDialog.showDialog();
            } else {
                setUserFollow();
            }

        });

        llJb.setOnClickListener(lis -> {
            //举报用户
            UniService.startService(getContext(), Constant.JKZL_MINI_APP_ID, 99, Constant.USER_REPORT + userId);
        });

        tvAt.setOnClickListener(lis -> {
            //弹出输入dialog
            Bundle bundle = new Bundle();
            bundle.putString("content", "@" + userInfoBean.getNickname() + " ");
            Intent intent = new Intent(getContext(), ZBEditTextDialogActivity.class);
            intent.putExtras(bundle);
            getContext().startActivity(intent);
            dismiss();
        });

        tvHome.setOnClickListener(lis -> {
            Bundle bundle = new Bundle();
            bundle.putString("userId", userId);
            Intent intent = new Intent(getContext(), UserInfoActivity.class);
            intent.putExtras(bundle);
            getContext().startActivity(intent);
            dismiss();
        });


        uniSubscribe = RxBus.getDefault().toObservable(UniEventBean.class).subscribe(event -> {
            if (event != null) {
                if (event.msgId == CodeTable.UNI_RELEASE) {
                    if (event.taskId == 99) {
                        //举报用户
                        UniUtil.openUniApp(getContext(), Constant.JKZL_MINI_APP_ID, event.jumpUrl, null, event.isSelfUni);
                    }
                } else if (event.msgId == CodeTable.UNI_RELEASE_FAIL) {
                    //ToastUtils.showShort("打开小程序失败");
                }
            }
        });
    }

    private void showListPop() {
        listBottomPopup = new ZBListMenuPop(getContext(), mLiveInitInfo, userInfoBean);
        listBottomPopup.showPopupWindow();
    }

    //关注用户操作
    private void setUserFollow() {
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
                        userInfoBean.setHasAttention(!userInfoBean.getHasAttention());

                        if (userInfoBean.getHasAttention()) {
                            tvGz.setText("已关注");
                            ivGz.setVisibility(View.GONE);
                            tvGz.setTextColor(getContext().getResources().getColor(R.color.gray_999));
                        } else {
                            tvGz.setText("关注");
                            ivGz.setVisibility(View.VISIBLE);
                            tvGz.setTextColor(getContext().getResources().getColor(R.color.color_DC3530));
                        }
                    }
                });
    }

    private MutableLiveData<UserInfoBean> otherUserInfo = new MutableLiveData<>();

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.pop_zbj_user_info);
    }

    @Override
    public void onDismiss() {
        super.onDismiss();
        if (uniSubscribe != null) {
            uniSubscribe.dispose();
        }
    }
}
