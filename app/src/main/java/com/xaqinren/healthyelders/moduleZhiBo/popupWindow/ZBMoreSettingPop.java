package com.xaqinren.healthyelders.moduleZhiBo.popupWindow;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;
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
import com.xaqinren.healthyelders.moduleZhiBo.activity.ZBEditTextDialogActivity;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.SensitiveWordsAdapter;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.LiveConstants;
import com.xaqinren.healthyelders.utils.AnimUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private TextView tvNum;

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


    private List<String> words;
    private Set<String> wordsSet = new HashSet<>();

    private int nowPos;

    private void initView() {
        loadingDialog = new LoadingDialog(getContext());
        tvNum = findViewById(R.id.tv_num);
        rvTags = findViewById(R.id.rv_tags);
        llSet = findViewById(R.id.ll_set);
        llDes = findViewById(R.id.ll_des);

        words = mLiveInitInfo.shieldList;
        if (words == null) {
            words = new ArrayList<>();
            words.add("");
        }

        wordsAdapter = new SensitiveWordsAdapter(R.layout.item_sensitive_words);
        rvTags.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvTags.setAdapter(wordsAdapter);
        if (words.size() == 0) {
            words.add("");
        }else {
            if (!words.get(0).equals("")) {
                words.add(0, "");
            }
        }

        wordsAdapter.setNewInstance(words);
        updateNum();

        wordsAdapter.setOnItemClickListener(((adapter, view, position) -> {
            if (position == 0) {
                //添加标签弹窗
                Intent intent = new Intent();
                intent.putExtra("type", 1);
                intent.setClass(getContext(), ZBEditTextDialogActivity.class);
                getContext().startActivity(intent);
            }

        }));

        wordsAdapter.setOnItemChildClickListener(((adapter, view, position) -> {
            if (view.getId() == R.id.rl_del) {
                nowPos = position;
                setBlockWord(wordsAdapter.getData().get(position), false);
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
                        //调用添加屏蔽词接口
                        setBlockWord(event.content, true);
                    } else {
                        ToastUtil.toastShortMessage("该词汇已存在");
                    }

                }
            }
        });

    }

    private void delWord() {
        wordsSet.remove(wordsAdapter.getData().get(nowPos));
        wordsAdapter.remove(nowPos);
        updateNum();
    }

    private void updateNum() {
        tvNum.setText("(" + (wordsAdapter.getData().size() - 1) + "/10)");
    }

    private void addWords(String blockWord) {
        wordsSet.add(blockWord);//添加到set方便判断
        wordsAdapter.addData(1, blockWord);
        updateNum();
    }


    //设置屏蔽词
    private void setBlockWord(String blockWord, boolean isAdd) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("liveRoomId", mLiveInitInfo.liveRoomId);
        hashMap.put("blockWord", blockWord);
        hashMap.put("addOrDel", isAdd);
        String json = JSON.toJSONString(hashMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        RetrofitClient.getInstance().create(ApiServer.class).setBlockWord(
                UserInfoMgr.getInstance().getHttpToken(),
                body)
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        loadingDialog.show();
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<Object>>() {
                    @Override
                    protected void dismissDialog() {
                        loadingDialog.dismiss();
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<Object> data) {
                        if (isAdd) {
                            addWords(blockWord);
                            RxBus.getDefault().post(new EventBean(LiveConstants.IMCMD_BLOCK_WORD_ADD, blockWord));
                        } else {
                            delWord();
                            RxBus.getDefault().post(new EventBean(LiveConstants.IMCMD_BLOCK_WORD_DEL, blockWord));
                        }
                    }
                });
    }


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
