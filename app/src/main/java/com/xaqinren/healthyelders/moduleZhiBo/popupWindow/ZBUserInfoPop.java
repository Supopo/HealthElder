package com.xaqinren.healthyelders.moduleZhiBo.popupWindow;

import android.content.Context;
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
import com.xaqinren.healthyelders.http.RetrofitClient;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ListPopMenuBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBUserListBean;
import com.xaqinren.healthyelders.utils.AnimUtils;
import com.xaqinren.healthyelders.widget.ListBottomPopup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
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

    private ZBUserListBean zbUserListBean;
    private ZBListMenuPop listBottomPopup;
    private UserInfoBean userInfoBean;
    private LiveInitInfo mLiveInitInfo;
    private LoadingDialog loadingDialog;
    private TextView tvGz;
    private ImageView ivGz;

    public ZBUserInfoPop(Context context, LiveInitInfo mLiveInitInfo, ZBUserListBean zbUserListBean) {
        super(context);
        this.zbUserListBean = zbUserListBean;
        this.mLiveInitInfo = mLiveInitInfo;
        setBackground(R.color.transparent);
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
        tvGz = findViewById(R.id.tv_gz);
        TextView tvAt = findViewById(R.id.tv_at);
        ivGz = findViewById(R.id.iv_gz);
        ImageView ivSex = findViewById(R.id.iv_sex);
        ImageView ivGly = findViewById(R.id.iv_gly);
        LinearLayout llGl = findViewById(R.id.ll_gl);
        LinearLayout llGz = findViewById(R.id.ll_gz);
        tvAt.setText("@TA");
        QMUIRadiusImageView rivPhoto = findViewById(R.id.riv_photo);
        tvName.setText(zbUserListBean.nickname);
        ivSex.setBackground(zbUserListBean.sex.equals("MALE") ? getContext().getResources().getDrawable(R.mipmap.male) : getContext().getResources().getDrawable(R.mipmap.female));
        Glide.with(getContext()).load(zbUserListBean.avatarUrl).into(rivPhoto);


        UserRepository.getInstance().getLiveUserInfo(otherUserInfo, zbUserListBean.userId, mLiveInitInfo.liveRoomRecordId);
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
                    ivGz.setVisibility(View.GONE);
                } else {
                    tvGz.setText("关注");
                    ivGz.setVisibility(View.VISIBLE);
                }
                zbUserListBean.hasSpeech = userInfoBean.getHasSpeech();
            }
        });

        llGl.setOnClickListener(lis -> {
            showListPop();
        });

        llGz.setOnClickListener(lis -> {
            setUserFollow();
        });
    }

    private void showListPop() {
        listBottomPopup = new ZBListMenuPop(getContext(), mLiveInitInfo, zbUserListBean);
        listBottomPopup.showPopupWindow();
    }

    //关注用户操作
    private void setUserFollow() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userId", zbUserListBean.userId);
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
                        } else {
                            tvGz.setText("关注");
                            ivGz.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private MutableLiveData<UserInfoBean> otherUserInfo = new MutableLiveData<>();

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.pop_zbj_user_info);
    }
}
