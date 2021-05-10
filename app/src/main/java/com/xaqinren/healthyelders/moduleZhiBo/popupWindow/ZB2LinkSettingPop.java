package com.xaqinren.healthyelders.moduleZhiBo.popupWindow;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.apiserver.ApiServer;
import com.xaqinren.healthyelders.apiserver.CustomObserver;
import com.xaqinren.healthyelders.apiserver.MBaseResponse;
import com.xaqinren.healthyelders.bean.BaseListRes;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.http.RetrofitClient;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ChatStatusManageDto;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBSettingBean;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.LiveConstants;
import com.xaqinren.healthyelders.widget.SwitchButton;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.utils.RxUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import razerdp.basepopup.BasePopupWindow;

/**
 * Created by Lee. on 2021/4/20.
 * 直播间聊设置弹窗
 */
public class ZB2LinkSettingPop extends BasePopupWindow {

    private LiveInitInfo liveInitInfo;
    private ZBSettingBean zbSettingBean;
    private ChatStatusManageDto chatStatusManageDto;
    private SwitchButton sbMenu1;
    private SwitchButton sbMenu3;
    private SwitchButton sbMenu4;
    private boolean needSetStuas;
    private QMUITipDialog dialog;

    public ZB2LinkSettingPop(Context context, LiveInitInfo liveInitInfo) {
        super(context);
        this.liveInitInfo = liveInitInfo;
        initView();
    }

    private void initView() {
        TextView tvSetting = findViewById(R.id.tv_setting);
        LinearLayout llMenu4 = findViewById(R.id.ll_menu4);
        //仅接受粉丝的的连线申请
        sbMenu1 = findViewById(R.id.sb_menu1);
        //观众连线需要通过申请
        sbMenu3 = findViewById(R.id.sb_menu3);
        //仅允许通过邀请上麦
        sbMenu4 = findViewById(R.id.sb_menu4);
        zbSettingBean = new ZBSettingBean();
        zbSettingBean.liveRoomId = liveInitInfo.liveRoomId;

        chatStatusManageDto = new ChatStatusManageDto();




        if (liveInitInfo.linkType == 0) {
            tvSetting.setText("双人聊设置");
            chatStatusManageDto.onlyFansMic = liveInitInfo.getOnlyFansMic();
            chatStatusManageDto.userApplyMic = liveInitInfo.getUserApplyMic();
            chatStatusManageDto.onlyInviteMic = liveInitInfo.getOnlyInviteMic();
        } else if (liveInitInfo.linkType == 1) {
            tvSetting.setText("聊天室设置");
            chatStatusManageDto.onlyFansMic = liveInitInfo.getChatRoomOnlyFansMic();
            chatStatusManageDto.userApplyMic = liveInitInfo.getChatRoomUserApplyMic();
            llMenu4.setVisibility(View.GONE);
        }

        sbMenu1.setChecked(chatStatusManageDto.onlyFansMic);
        sbMenu3.setChecked(chatStatusManageDto.userApplyMic);
        sbMenu4.setChecked(chatStatusManageDto.onlyInviteMic);
    }

    @Override
    public void dismiss() {
        if (canDismiss) {
            super.dismiss();
        }else {
            setLiveStatus();
        }
    }

    private boolean canDismiss;

    private void setLiveStatus() {
        dialog = new QMUITipDialog.Builder(getContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("请稍后")
                .create();
        dialog.show();

        chatStatusManageDto.onlyFansMic = sbMenu1.isChecked();
        chatStatusManageDto.userApplyMic = sbMenu3.isChecked();
        chatStatusManageDto.onlyInviteMic = sbMenu4.isChecked();
        zbSettingBean.chatStatusManageDto = chatStatusManageDto;
        String json = JSON.toJSONString(zbSettingBean);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        RetrofitClient.getInstance().create(ApiServer.class).setZBStatus(
                UserInfoMgr.getInstance().getHttpToken(),
                body)
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {

                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<BaseListRes<Object>>>() {
                    @Override
                    protected void dismissDialog() {
                        dialog.dismiss();
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<BaseListRes<Object>> data) {
                        RxBus.getDefault().post(new EventBean(LiveConstants.ZBJ_SET_SUCCESS, zbSettingBean));
                        canDismiss = true;
                        dismiss();
                    }
                });
    }


    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.pop_zbj_2link_setting);
    }
}
