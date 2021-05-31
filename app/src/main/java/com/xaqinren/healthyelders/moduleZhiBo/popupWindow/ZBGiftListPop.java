package com.xaqinren.healthyelders.moduleZhiBo.popupWindow;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;

import me.goldze.mvvmhabit.bus.RxBus;
import razerdp.basepopup.BasePopupWindow;

/**
 * Created by Suuu on 2021/3/30.
 * 直播间礼物列表弹窗
 */
public class ZBGiftListPop extends BasePopupWindow {

    private Context context;
    private String mPusherId;
    private String userGold;
    private TextView tvGoldNum;
    private TextView tvCZ;

    public ZBGiftListPop(Context context, String mPusherId) {
        super(context);
        this.context = context;
        this.mPusherId = mPusherId;
        setBackPressEnable(true);
        setAlignBackground(true);
        getGiftList();

    }

    private int oldPos;

    private void initAdapter(Context context) {
        tvGoldNum = (TextView) findViewById(R.id.tv_num);
        tvCZ = (TextView) findViewById(R.id.tv_cz);
        tvGoldNum.setText(userGold);


        //充值
        tvCZ.setOnClickListener(lis -> {

        });

        getUserInfo();

        RxBus.getDefault().toObservable(EventBean.class).subscribe(eventBean -> {
            if (eventBean != null) {
                if (eventBean.msgId == 10010 && eventBean.msgType == 100) {
                    getUserInfo();
                }
            }
        });

    }

    private void getGiftList() {
//        RetrofitClient.getInstance().create(ApiServer.class).getGiftList(
//                Constant.getToken()
//        )
//                .compose(RxUtils.schedulersTransformer()) //线程调度
//                .doOnSubscribe(new Consumer<Disposable>() {
//                    @Override
//                    public void accept(Disposable disposable) throws Exception {
//
//                    }
//                })
//                .subscribe(new Consumer<GiftResponse>() {
//                    @Override
//                    public void accept(GiftResponse bean) throws Exception {
//                        if (bean.isOk()) {
//                            if (bean.getResult() != null && bean.getResult().size() > 0) {
//                                dataList.addAll(bean.getResult());
//                                initAdapter(context);
//                            }
//                        }
//                    }
//                });
    }

    private void sendGift(String giftId, int pos) {
//        RetrofitClient.getInstance().create(ApiServer.class).sendGift(
//                Constant.getToken(), giftId, mPusherId
//        )
//                .compose(RxUtils.schedulersTransformer()) //线程调度
//                .doOnSubscribe(new Consumer<Disposable>() {
//                    @Override
//                    public void accept(Disposable disposable) throws Exception {
//
//                    }
//                })
//                .subscribe(new Consumer<BaseResponse<Integer>>() {
//                    @Override
//                    public void accept(BaseResponse<Integer> bean) throws Exception {
//                        if (bean.isOk()) {
//                            //发送成功
//                            //通知页面发送自定义消息发送礼物
//                            RxBus.getDefault().post(new EventBean(Constant.ZB_SEND_GIFT, dataList.get(pos)));
//                            if (bean.getResult() != null) {
//                                SPUtils.getInstance().put(Constant.USER_GOLD, bean.getResult().toString());
//                            }
//                        } else {
//                            ToastUtils.showShort(bean.getMessage());
//                        }
//                    }
//                });
    }


    private void getUserInfo() {
//        RetrofitClient.getInstance().create(ApiServer.class).getUserInfo(
//                Constant.getToken()
//        )
//                .compose(RxUtils.schedulersTransformer()) //线程调度
//                .doOnSubscribe(new Consumer<Disposable>() {
//                    @Override
//                    public void accept(Disposable disposable) throws Exception {
//
//                    }
//                })
//                .subscribe(new Consumer<UserInfoResBean>() {
//                    @Override
//                    public void accept(UserInfoResBean bean) throws Exception {
//                        if (bean.isSuccess()) {
//                            userGold = bean.user.goldCoin == null ? "0" : bean.user.goldCoin.toString();
//                            SPUtils.getInstance().put(Constant.USER_GOLD, userGold);
//                            tvGoldNum.setText(userGold);
//                        }
//                    }
//                });
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.layout_zb_gift_list_pop);
    }

}
