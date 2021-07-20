package com.xaqinren.healthyelders.moduleZhiBo.popupWindow;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tencent.liteav.demo.beauty.model.BeautyInfo;
import com.tencent.liteav.demo.beauty.model.ItemInfo;
import com.tencent.liteav.demo.beauty.model.TabInfo;
import com.tencent.liteav.demo.beauty.view.BeautyPanel;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.apiserver.LiveRepository;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.bean.MenuBean;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.LiveMenuAdapter;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBSettingBean;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.LiveConstants;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.MLVBLiveRoom;
import com.xaqinren.healthyelders.uniApp.UniService;
import com.xaqinren.healthyelders.uniApp.UniUtil;
import com.xaqinren.healthyelders.uniApp.bean.UniEventBean;
import com.xaqinren.healthyelders.utils.AnimUtils;
import com.xaqinren.healthyelders.widget.BottomDialog;
import com.xaqinren.healthyelders.widget.share.ShareDialog;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.widget.LoadingDialog;
import razerdp.basepopup.BasePopupWindow;

/**
 * Created by Lee. on 2021/4/20.
 * 直播间更多设置弹窗-观众端
 */
public class ZBMoreGZPop extends BasePopupWindow {


    private RelativeLayout rlItem;
    private RecyclerView rvContent;
    private LiveMenuAdapter menuAdapter;
    private MLVBLiveRoom mLiveRoom;
    private LiveInitInfo mLiveInitInfo;
    private ShareDialog shareDialog;
    private Context context;
    private Disposable uniSubscribe;

    public ZBMoreGZPop(Context context, MLVBLiveRoom mLiveRoom, LiveInitInfo mLiveInitInfo) {
        super(context);
        this.mLiveRoom = mLiveRoom;
        this.mLiveInitInfo = mLiveInitInfo;
        this.context = context;

        //去掉背景
        setBackground(R.color.transparent);
        setShowAnimation(AnimUtils.PopAnimBottom2Enter(context));
        setDismissAnimation(AnimUtils.PopAnimBottom2Exit(context));
        initView();
        initEvent();
    }

    private String[] menuNames = {"分享", "举报"};
    private int[] menuRes = {
            R.mipmap.icon_zb_share,
            R.mipmap.icon_jb,
    };

    private void initView() {
        rlItem = findViewById(R.id.rl_pop);
        rvContent = findViewById(R.id.rv_menu);
        menuAdapter = new LiveMenuAdapter(R.layout.item_start_live_menu);
        rvContent.setLayoutManager(new GridLayoutManager(getContext(), 4));
        rvContent.setAdapter(menuAdapter);

        List<MenuBean> menus = new ArrayList<>();
        for (int i = 0; i < menuNames.length; i++) {
            menus.add(new MenuBean("" + i, menuNames[i], menuRes[i], 1));
        }

        menuAdapter.setNewInstance(menus);
        menuAdapter.setOnItemClickListener(((adapter, view, position) -> {
            selectPos = position;

            switch (position) {
                case 0:
                    if (shareDialog == null) {
                        shareDialog = new ShareDialog(getContext(), mLiveInitInfo.share, 2);
                    }
                    shareDialog.show(rlItem);
                    break;
                case 1:
                    //小程序举报
                    UniService.startService(context, Constant.JKZL_MINI_APP_ID, 99, Constant.LIVE_REPORT + mLiveInitInfo.share.id);
                    break;
                default:
                    break;
            }
        }));
    }

    public int selectPos;

    public void initEvent() {
        uniSubscribe = RxBus.getDefault().toObservable(UniEventBean.class).subscribe(event -> {
            if (event != null) {
                if (event.msgId == CodeTable.UNI_RELEASE) {
                    if (event.taskId == 99) {
                        UniUtil.openUniApp(context, Constant.JKZL_MINI_APP_ID, event.jumpUrl, null, event.isSelfUni);
                    }
                } else if (event.msgId == CodeTable.UNI_RELEASE_FAIL) {
                    //ToastUtils.showShort("打开小程序失败");
                }
            }
        });
    }


    @Override
    public void dismiss() {
        super.dismiss();
        if (uniSubscribe != null) {
            uniSubscribe.dispose();
        }
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.pop_zbj_more_gz);
    }
}
