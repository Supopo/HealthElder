package com.xaqinren.healthyelders.moduleLiteav.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.tencent.qcloud.ugckit.module.record.MusicInfo;
import com.tencent.qcloud.ugckit.module.record.RecordMusicManager;
import com.tencent.qcloud.ugckit.module.record.VideoRecordSDK;
import com.tencent.qcloud.xiaoshipin.mainui.list.DividerGridItemDecoration;
import com.tencent.ugc.TXRecordCommon;
import com.tencent.ugc.TXUGCRecord;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.ActivityMusicListBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.moduleLiteav.Constant;
import com.xaqinren.healthyelders.moduleLiteav.adapter.MusicItemAdapter;
import com.xaqinren.healthyelders.moduleLiteav.bean.MMusicItemBean;
import com.xaqinren.healthyelders.moduleLiteav.liteAv.MusicRecode;
import com.xaqinren.healthyelders.moduleLiteav.service.DownMusicBean;
import com.xaqinren.healthyelders.moduleLiteav.service.DownMusicProBean;
import com.xaqinren.healthyelders.moduleLiteav.service.DownloadMusic;
import com.xaqinren.healthyelders.moduleLiteav.viewModel.MusicClassViewModel;
import com.xaqinren.healthyelders.moduleLiteav.viewModel.MusicListViewModel;
import com.xaqinren.healthyelders.moduleZhiBo.activity.StartLiveActivity;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.widget.SpeacesItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.utils.StringUtils;

public class MusicListActivity extends BaseActivity<ActivityMusicListBinding, MusicClassViewModel> {
    private List<MMusicItemBean> mMusicItemBeans;
    private MusicItemAdapter adapter;
    private int page = 1;
    private int pageSize = 20;
    private String classId;
    private int operationIndex = -1;
    private String currentPlayId;
    private String rootPath;
    private DownloadMusic downloadMusic;
    private Disposable mDownSubscription;
    private TXUGCRecord record;

    private Handler handler = new Handler(Looper.myLooper());
    private String TAG = "MusicListActivity";
    private String className;
    private String searchName;


    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_music_list;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        File file = new File(getFilesDir(), "music");
        rootPath = file.getAbsolutePath();
        if (!file.exists()) {
            file.mkdir();
        }
        ivLeft.setOnClickListener(v -> {
            MusicRecode.getInstance().setUseMusicItem(null);
            finish();
        });
        downloadMusic = DownloadMusic.getInstance();
        downloadMusic.init(file.getAbsolutePath());
        mMusicItemBeans = new ArrayList<>();
        adapter = new MusicItemAdapter(R.layout.item_music_item);
        binding.content.setLayoutManager(new LinearLayoutManager(this));
        binding.content.setAdapter(adapter);
        adapter.setList(mMusicItemBeans);
        classId = getIntent().getExtras().getString(Constant.MUSIC_CLASS_ID);
        className = getIntent().getExtras().getString(Constant.MUSIC_CLASS_NAME);
        searchName = getIntent().getExtras().getString(Constant.MUSIC_SEARCH_NAME);
        if (StringUtils.isEmpty(className)){
            setTitle(searchName);
        }else
            setTitle(className);

        viewModel.getMusicList(classId, searchName, page, pageSize);

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
                        if (MusicRecode.CURRENT_BOURN == Constant.BOURN_RECODE)
                            startActivity(StartLiveActivity.class);
                        else
                            startActivity(VideoEditerActivity.class);
                    }
                    break;
            }
            adapter.notifyItemChanged(position);
        });
        adapter.setOnItemClickListener((adapter, view, position) -> {
            MMusicItemBean bean = mMusicItemBeans.get(position);
            operation(bean, position);
            adapter.notifyItemChanged(position);
        });
        adapter.getLoadMoreModule().setEnableLoadMore(true);
        adapter.getLoadMoreModule().setAutoLoadMore(true);
        adapter.getLoadMoreModule().setOnLoadMoreListener(() -> viewModel.getMusicList(classId, searchName, page, pageSize));
    }

    private void operation(MMusicItemBean bean ,int position) {
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
            //重复播放
            RecordMusicManager.getInstance().startMusic();
        }
    };

    @Override
    public void initViewObservable() {
        super.initViewObservable();
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
            if (!data.isEmpty()){
                page++;
                adapter.getLoadMoreModule().loadMoreComplete();
            }else{
                adapter.getLoadMoreModule().loadMoreEnd(false);
            }
        });

        mDownSubscription = RxBus.getDefault().toObservable(DownMusicProBean.class)
                .observeOn(AndroidSchedulers.mainThread()) //回调到主线程更新UI
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
                            //layMusic(itemBean); 播放音乐
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
        //将订阅者加入管理站
        RxSubscriptions.add(mDownSubscription);
    }

    //下载,执行播放顺序
    private void loadTrialMusic(MMusicItemBean bean) {
        DownMusicBean downMusicBean = new DownMusicBean();
        downMusicBean.size = bean.size;
        downMusicBean.path = bean.musicUrl;
        downMusicBean.id = bean.getId();
        downloadMusic.addWork(downMusicBean);
    }

    //取消使用
    private void stopPlayMusic() {
        RecordMusicManager.getInstance().stopPreviewMusic();
//        RecordMusicManager.getInstance().deleteMusic();
//        setVolum(100, 0);
    }

    //播放音乐
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
                // 设置音乐信息
                RecordMusicManager.getInstance().setRecordMusicInfo(musicInfo);
                // 音乐试听
                RecordMusicManager.getInstance().startMusic();
                setVolum(0, 100);
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
     * @param volume 原声
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
    protected void onDestroy() {
        super.onDestroy();
        RxSubscriptions.remove(mDownSubscription);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlayMusic();
    }
}
