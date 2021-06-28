package com.xaqinren.healthyelders.moduleZhiBo.popupWindow;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tencent.qcloud.tim.uikit.utils.ToastUtil;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.moduleZhiBo.activity.ZBEditTextDialogActivity;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.SensitiveWordsAdapter;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.LiveConstants;
import com.xaqinren.healthyelders.utils.AnimUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.widget.LoadingDialog;
import razerdp.basepopup.BasePopupWindow;

/**
 * Created by Lee. on 2021/4/20.
 * 直播间更多设置-设置
 */
public class ZBMoreSettingPop extends BasePopupWindow {
    private LiveInitInfo mLiveInitInfo;
    private LoadingDialog loadingDialog;
    private LinearLayout llSet;
    private LinearLayout llDes;
    private Context context;
    private ZBBlackUserListPop settingUserListPop;
    private ZBDesSettingPop zbDesSettingPop;
    private RecyclerView rvTags;
    private SensitiveWordsAdapter wordsAdapter;
    private Disposable subscribe;

    public ZBMoreSettingPop(Context context, LiveInitInfo mLiveInitInfo) {
        super(context);
        this.mLiveInitInfo = mLiveInitInfo;
        this.context = context;

        //去掉背景
        setBackground(R.color.transparent);
        setShowAnimation(AnimUtils.PopAnimBottom2Enter(context));
        setDismissAnimation(AnimUtils.PopAnimBottom2Exit(context));
        initView();
    }


    private List<String> words = new ArrayList<>();
    private Set<String> wordsSet = new HashSet<>();

    private void initView() {
        loadingDialog = new LoadingDialog(getContext());
        rvTags = findViewById(R.id.rv_tags);
        llSet = findViewById(R.id.ll_set);
        llDes = findViewById(R.id.ll_des);

        wordsAdapter = new SensitiveWordsAdapter(R.layout.item_sensitive_words);
        rvTags.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvTags.setAdapter(wordsAdapter);
        words.add("");
        wordsAdapter.setNewInstance(words);

        wordsAdapter.setOnItemClickListener(((adapter, view, position) -> {
            if (position == 0) {
                //添加标签弹窗
                Intent intent = new Intent();
                intent.putExtra("type", 1);
                intent.setClass(getContext(), ZBEditTextDialogActivity.class);
                getContext().startActivity(intent);
            }

        }));

        llSet.setOnClickListener(lis -> {
            settingUserListPop = new ZBBlackUserListPop(context, mLiveInitInfo);
            settingUserListPop.showPopupWindow();
        });
        llDes.setOnClickListener(lis -> {
            zbDesSettingPop = new ZBDesSettingPop(context, mLiveInitInfo, 1);
            zbDesSettingPop.showPopupWindow();
        });

        subscribe = RxBus.getDefault().toObservable(EventBean.class).subscribe(event -> {
            if (event != null) {
                if (event.msgId == LiveConstants.SEND_WORD) {
                    if (!wordsSet.equals(event.content)) {
                        wordsSet.add(event.content);
                        wordsAdapter.addData(event.content);
                    }else {
                        ToastUtil.toastShortMessage("该词汇已存在");
                    }

                }
            }
        });

    }


    public MutableLiveData<Boolean> dismissDialog = new MutableLiveData<>();
    public MutableLiveData<Boolean> setSuccess = new MutableLiveData<>();

    @Override
    public void dismiss() {
        super.dismiss();
        subscribe.dispose();
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.pop_zbj_more_setting);
    }
}
