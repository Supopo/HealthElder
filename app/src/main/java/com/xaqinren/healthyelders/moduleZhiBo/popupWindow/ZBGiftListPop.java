package com.xaqinren.healthyelders.moduleZhiBo.popupWindow;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.viewpager2.widget.ViewPager2;

import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.apiserver.ApiServer;
import com.xaqinren.healthyelders.apiserver.CustomObserver;
import com.xaqinren.healthyelders.apiserver.MBaseResponse;
import com.xaqinren.healthyelders.apiserver.UserRepository;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.http.RetrofitClient;
import com.xaqinren.healthyelders.moduleMine.bean.WalletBean;
import com.xaqinren.healthyelders.moduleZhiBo.activity.CZSelectPopupActivity;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.GiftListPageAdapter;
import com.xaqinren.healthyelders.moduleZhiBo.bean.GiftBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.GiftSelectBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.zhpan.indicator.IndicatorView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.utils.RxUtils;
import razerdp.basepopup.BasePopupWindow;

/**
 * 直播间礼物列表弹窗
 */
public class ZBGiftListPop extends BasePopupWindow {

    private Context context;
    private LiveInitInfo mLiveInitInfo;
    private TextView tvGoldNum;
    private TextView tvCZ;
    private GiftListPageAdapter pageAdapter;
    private ViewPager2 vpContent;
    private IndicatorView indView;
    private Disposable subscribe;
    private int nowPage;

    public ZBGiftListPop(Context context, LiveInitInfo liveInitInfo) {
        super(context);
        this.context = context;
        this.mLiveInitInfo = liveInitInfo;
        setBackPressEnable(true);
        setAlignBackground(true);
        initView();
    }

    public void rushGold() {
        UserRepository.getInstance().getWalletInfo(null, userBanlance);
    }

    private int lastPage;
    private int selectPage;

    private void initView() {
        indView = findViewById(R.id.indicator_view);
        vpContent = findViewById(R.id.vp_content);
        tvGoldNum = findViewById(R.id.tv_num);
        tvCZ = findViewById(R.id.tv_cz);

        pageAdapter = new GiftListPageAdapter(R.layout.item_gift_rv);
        vpContent.setAdapter(pageAdapter);

        vpContent.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                nowPage = position;
            }
        });

        //充值
        tvCZ.setOnClickListener(lis -> {
            context.startActivity(new Intent(context, CZSelectPopupActivity.class));
        });


        subscribe = RxBus.getDefault().toObservable(EventBean.class).subscribe(eventBean -> {
            if (eventBean != null) {
                if (eventBean.msgId == CodeTable.ZHJ_SELECT_GIFT) {
                    GiftSelectBean selectBean = (GiftSelectBean) eventBean.data;
                    selectPage = selectBean.selectPage;

                    if (selectPage != lastPage) {
                        for (int i = 0; i < pageAdapter.getData().size(); i++) {

                            if (i != selectBean.selectPage) {
                                for (GiftBean giftBean : pageAdapter.getData().get(i).giftBeans) {
                                    giftBean.isSelect = false;
                                }
                                pageAdapter.notifyItemChanged(i);
                            }

                        }
                    }
                    lastPage = selectPage;

                } else if (eventBean.msgId == CodeTable.WX_PAY_CODE && eventBean.msgType == 1) {
                    //重新获取用户余额
                    UserRepository.getInstance().getWalletInfo(null, userBanlance);
                }
            }
        });

        userBanlance.observe((LifecycleOwner) context, userInfoBean -> {
            if (userInfoBean != null) {
                tvGoldNum.setText("" + userInfoBean.getPointBalance());
            }
        });

        getGiftList();
        UserRepository.getInstance().getWalletInfo(null, userBanlance);
    }

    private MutableLiveData<WalletBean> userBanlance = new MutableLiveData<>();

    int pageCount;//menu1菜单页数 ViewPager+RecvclerView
    int pageSize = 8;//menu1菜单页数数量

    private void getGiftList() {
        RetrofitClient.getInstance().create(ApiServer.class).getGiftList(
                UserInfoMgr.getInstance().getHttpToken())
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .subscribe(new CustomObserver<MBaseResponse<List<GiftBean>>>() {
                    @Override
                    protected void dismissDialog() {

                    }

                    @Override
                    protected void onSuccess(MBaseResponse<List<GiftBean>> data) {
                        List<GiftBean> datas = data.getData();
                        if (datas.size() > 0) {

                            datas.get(0).isSelect = true;
                            //重组ViewPager2的数据
                            //计算页数
                            if (datas.size() % pageSize == 0) {
                                pageCount = (datas.size() / pageSize);
                            } else {
                                pageCount = (datas.size() / pageSize) + 1;
                            }

                            List<GiftBean> pageList = new ArrayList<>();
                            for (int i = 0; i < pageCount; i++) {
                                GiftBean giftBean = new GiftBean();
                                if (i == pageCount - 1) {
                                    giftBean.giftBeans.addAll(datas.subList(i * pageSize, datas.size()));
                                } else {
                                    giftBean.giftBeans.addAll(datas.subList(i * pageSize, (i + 1) * pageSize));
                                }
                                pageList.add(giftBean);
                            }

                            vpContent.setOffscreenPageLimit(pageCount);
                            pageAdapter.setNewInstance(pageList);
                            indView.setupWithViewPager(vpContent);

                        }
                    }
                });
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.layout_zb_gift_list_pop);
    }

}
