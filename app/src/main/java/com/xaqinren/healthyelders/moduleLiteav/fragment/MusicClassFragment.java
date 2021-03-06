package com.xaqinren.healthyelders.moduleLiteav.fragment;

import android.app.Activity;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.tencent.qcloud.ugckit.module.effect.bgm.TCMusicActivity;
import com.tencent.qcloud.ugckit.module.record.MusicInfo;
import com.tencent.qcloud.ugckit.module.record.RecordMusicManager;
import com.tencent.qcloud.ugckit.module.record.VideoRecordSDK;
import com.tencent.ugc.TXRecordCommon;
import com.tencent.ugc.TXUGCRecord;
import com.thoughtworks.xstream.converters.javabean.BeanProperty;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.FragmentMusicClassBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.moduleLiteav.Constant;
import com.xaqinren.healthyelders.moduleLiteav.activity.VideoEditerActivity;
import com.xaqinren.healthyelders.moduleLiteav.adapter.MusicItemAdapter;
import com.xaqinren.healthyelders.moduleLiteav.bean.MMusicItemBean;
import com.xaqinren.healthyelders.moduleLiteav.liteAv.MusicRecode;
import com.xaqinren.healthyelders.moduleLiteav.service.DownMusicBean;
import com.xaqinren.healthyelders.moduleLiteav.service.DownMusicProBean;
import com.xaqinren.healthyelders.moduleLiteav.service.DownloadMusic;
import com.xaqinren.healthyelders.moduleLiteav.viewModel.MusicClassViewModel;
import com.xaqinren.healthyelders.moduleZhiBo.activity.StartLiveActivity;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.http.download.DownLoadStateBean;
import me.goldze.mvvmhabit.utils.RxUtils;

public class MusicClassFragment extends BaseFragment<FragmentMusicClassBinding, MusicClassViewModel> {
    //??????ID
    private String classId;
    //????????????????????????
    private int page;
    //??????=3
    private int pageSize = 3;
    private List<MMusicItemBean> mMusicItemBeans;
    private MusicItemAdapter adapter;
    private Disposable mSubscription;
    private String TAG = "MusicClassFragment";
    private String rootPath;
    private DownloadMusic downloadMusic;
    private Disposable mDownSubscription;
    private TXUGCRecord record;


    private int operationIndex = -1;
    private String currentPlayId;


    private Handler handler = new Handler(Looper.myLooper());


    public MusicClassFragment(String classId, int page) {
        this.classId = classId;
        this.page = page;
    }

    public MusicClassFragment(String classId, int page, List<MMusicItemBean> mMusicItemBeans) {
        this.classId = classId;
        this.page = page;
        this.mMusicItemBeans = mMusicItemBeans;
    }

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_music_class;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        File file = new File(getContext().getFilesDir(), "music");
        rootPath = file.getAbsolutePath();
        if (!file.exists()) {
            file.mkdir();
        }
        downloadMusic = DownloadMusic.getInstance();
        downloadMusic.init(file.getAbsolutePath());
    }

    @Override
    public void initData() {
        super.initData();
        if (this.mMusicItemBeans == null) {
            mMusicItemBeans = new ArrayList<>();
        }
        //????????????3???
        adapter = new MusicItemAdapter(R.layout.item_music_item);
        binding.content.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.content.setAdapter(adapter);
        adapter.setList(mMusicItemBeans);
        setOperationIndex(operationIndex);
        if (mMusicItemBeans.isEmpty()) {
            //??????
            viewModel.getMusicList(classId, null, page, pageSize);
        }
        record = VideoRecordSDK.getInstance().getRecorder();
        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            MMusicItemBean item = mMusicItemBeans.get(position);
            switch (view.getId()) {
                case R.id.shoucang:
                    viewModel.collMusic(item.getId(), !item.hasFavorite);
                    item.hasFavorite = !item.hasFavorite;
                    break;
                case R.id.use_btn:
                    if (item.isUse) {
                        item.isUse = false;
                        MusicRecode.getInstance().setUseMusicItem(null);
                    }else{
                        item.isUse = true;
                        MusicRecode.getInstance().setUseMusicItem(item);
                        /*if (MusicRecode.CURRENT_BOURN == Constant.BOURN_RECODE) {
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("music", true);
                            startActivity(StartLiveActivity.class,bundle);
                        }else {
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("music", true);
                            startActivity(VideoEditerActivity.class,bundle);
                        }*/
                        getActivity().finish();
                    }
                    break;
            }
            adapter.notifyItemChanged(position);
        });
        adapter.setOnItemClickListener((adapter, view, position) -> {
            MMusicItemBean bean = mMusicItemBeans.get(position);
            operation(bean,position);
            adapter.notifyItemChanged(position);
        });
    }

    private void operation(MMusicItemBean bean,int position) {
        if (bean.myMusicStatus == 0) {
            if (operationIndex != -1) {
                mMusicItemBeans.get(operationIndex).myMusicStatus = 0;
                mMusicItemBeans.get(operationIndex).setOperation(false);
                adapter.notifyItemChanged(operationIndex);
            }
            bean.myMusicStatus = 1;
            operationIndex = position;
            record.setBGMNofify(bgmNotify);
            bean.setOperation(true);
            loadTrialMusic(bean);
            RxBus.getDefault().post(new EventBean(CodeTable.EVENT_MUSIC_OP,bean.getId()));
//                MusicRecode.getInstance().setUseMusicItem(bean);
        }else {
            bean.myMusicStatus = 0;
            bean.setOperation(false);
            operationIndex = -1;
            record.setBGMNofify(null);
            stopPlayMusic();
            MusicRecode.getInstance().setUseMusicItem(null);
        }
    }

    private TXRecordCommon.ITXBGMNotify bgmNotify = new TXRecordCommon.ITXBGMNotify() {

        @Override
        public void onBGMStart() {
            handler.post(() -> {
                if (operationIndex==-1)return;
                mMusicItemBeans.get(operationIndex).myMusicStatus = 2;
                mMusicItemBeans.get(operationIndex).setOperation(true);
                adapter.notifyItemChanged(operationIndex);
            });
        }

        @Override
        public void onBGMProgress(long l, long l1) {

        }

        @Override
        public void onBGMComplete(int i) {
            //????????????
            RecordMusicManager.getInstance().startMusic();
        }
    };

    public void clear(String id) {
        currentPlayId = id;
        if (operationIndex != -1) {
            if (mMusicItemBeans.get(operationIndex).getId().equals(id)) {
                return;
            }
            mMusicItemBeans.get(operationIndex).myMusicStatus = 0;
            mMusicItemBeans.get(operationIndex).setOperation(false);
            adapter.notifyItemChanged(operationIndex);
            operationIndex = -1;
        }
    }

    public int getOperationIndex() {
        return operationIndex;
    }

    public void setOperationIndex(int operationIndex) {
        this.operationIndex = operationIndex;
        if (operationIndex != -1) {
            if (!mMusicItemBeans.get(operationIndex).getId().equals(currentPlayId)) {
                this.operationIndex = -1;
                mMusicItemBeans.get(operationIndex).setOperation(false);
            }else
                mMusicItemBeans.get(operationIndex).setOperation(true);
            adapter.notifyItemChanged(operationIndex);
        }
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        mSubscription = RxBus.getDefault().toObservable(EventBean.class)
                .observeOn(AndroidSchedulers.mainThread()) //????????????????????????UI
                .subscribe(new Consumer<EventBean>() {
                    @Override
                    public void accept(EventBean eventBean) throws Exception {
                        if (eventBean.msgId == CodeTable.EVENT_MUSIC_OP) {
                            clear(eventBean.content);
                        }
                    }
                });
        //???????????????????????????
        RxSubscriptions.add(mSubscription);

        viewModel.requestSuccess.observe(this, aBoolean -> dismissDialog());
        viewModel.musicListData.observe(this, data ->{
            mMusicItemBeans.addAll(data);
            MMusicItemBean bean = MusicRecode.getInstance().getUseMusicItem();
            if (bean != null) {
                for (int i = 0; i < mMusicItemBeans.size(); i++) {
                    if (mMusicItemBeans.get(i).getId().equals(bean.getId())) {
                        mMusicItemBeans.get(i).setOperation(true);
                        operation(mMusicItemBeans.get(i),i);
                        break;
                    }
                }
            }
            adapter.setList(mMusicItemBeans);
        });

        initMusicItemScription();
    }

    private void initMusicItemScription() {
        mDownSubscription = RxBus.getDefault().toObservable(DownMusicProBean.class)
                .observeOn(AndroidSchedulers.mainThread()) //????????????????????????UI
                .subscribe(eventBean -> {
                    switch (eventBean.status) {
                        case DownMusicProBean.WAIT:
                            break;
                        case DownMusicProBean.DOWN:
                            break;
                        case DownMusicProBean.ERROR:
                            LogUtils.e(TAG, eventBean.err);
                            break;
                        case DownMusicProBean.FULL:{
                            //layMusic(itemBean); ????????????
                            LogUtils.e(TAG, "this -> " + this.toString());
                            if (operationIndex == -1)return;
                            MMusicItemBean itemBean = mMusicItemBeans.get(operationIndex);
                            itemBean.localPath = new File(rootPath, itemBean.getId()).getAbsolutePath();
                            playMusic(itemBean);
                        }
                        break;
                        case DownMusicProBean.IDLE:
                            break;
                    }
                });
        //???????????????????????????
        RxSubscriptions.add(mDownSubscription);
    }

    @Override
    public void onStop() {
        super.onStop();
        operationIndex = -1;
        record.setBGMNofify(null);
        LogUtils.e(TAG, "MusicClassFragment onStop ->" + operationIndex);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxSubscriptions.remove(mSubscription);
        RxSubscriptions.remove(mDownSubscription);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtils.e(TAG, "MusicClassFragment ->" + operationIndex);
    }

    //??????,??????????????????
    private void loadTrialMusic(MMusicItemBean bean) {
        DownMusicBean downMusicBean = new DownMusicBean();
        downMusicBean.size = bean.size;
        downMusicBean.path = bean.musicUrl;
        downMusicBean.id = bean.getId();
        downloadMusic.addWork(downMusicBean);
    }

    //????????????
    private void stopPlayMusic() {
        RecordMusicManager.getInstance().stopPreviewMusic();
        RecordMusicManager.getInstance().deleteMusic();
        setVolum(100, 0);
    }

    //????????????
    private void playMusic(MMusicItemBean bean) {
        stopPlayMusic();
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                MusicInfo musicInfo = new MusicInfo();
                musicInfo.path = bean.musicUrl;
                musicInfo.name = bean.name;
                if (bean.localPath != null) {
                    musicInfo.path = bean.localPath;
                }
                LogUtils.d(getClass().getSimpleName(), "music path:" + musicInfo.path);
                if (record != null) {
                    long duration = record.setBGM(musicInfo.path);
                    musicInfo.duration = duration;
                    LogUtils.d(getClass().getSimpleName(), "music duration:" + musicInfo.duration);
                }
                // ??????????????????
                RecordMusicManager.getInstance().setRecordMusicInfo(musicInfo);
                // ????????????
                RecordMusicManager.getInstance().startMusic();
                setVolum(0, 1f);
                return "";
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
            }
        }.execute(new Object());
    }
    /**
     *
     * @param volume ??????
     * @param bgmVolume bgm
     */
    public void setVolum(float volume,float bgmVolume) {
        TXUGCRecord record = VideoRecordSDK.getInstance().getRecorder();
        if (record != null) {
            record.setMicVolume(volume);
            record.setBGMVolume(bgmVolume);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
