package com.xaqinren.healthyelders.moduleLiteav.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.tencent.qcloud.ugckit.module.effect.utils.DraftEditer;
import com.tencent.qcloud.ugckit.module.record.MusicInfo;
import com.tencent.qcloud.ugckit.module.record.RecordMusicManager;
import com.tencent.qcloud.ugckit.module.record.VideoRecordSDK;
import com.tencent.ugc.TXRecordCommon;
import com.tencent.ugc.TXUGCRecord;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.apiserver.LiteAvRepository;
import com.xaqinren.healthyelders.moduleLiteav.adapter.MusicSelAdapter;
import com.xaqinren.healthyelders.moduleLiteav.adapter.MusicSelCollAdapter;
import com.xaqinren.healthyelders.moduleLiteav.bean.MMusicItemBean;
import com.xaqinren.healthyelders.moduleLiteav.liteAv.MusicRecode;
import com.xaqinren.healthyelders.moduleLiteav.service.DownMusicBean;
import com.xaqinren.healthyelders.moduleLiteav.service.DownMusicProBean;
import com.xaqinren.healthyelders.moduleLiteav.service.DownloadMusic;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.widget.BottomDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;

public class MusicSelDialog extends BottomDialog implements BottomDialog.OnBottomItemClickListener, DialogInterface.OnDismissListener {

    private DraftEditer mEditerDraft;
    private MusicInfo mMusicInfo;
    private int currentPage = 0;//0??????  1??????
    private MusicSelAdapter musicSelAdapter;
    private MusicSelCollAdapter selCollAdapter;
    private List<MMusicItemBean> musicItemBeans;
    private List<MMusicItemBean> musicCollItemBeans;//??????
    private int currentPlayReIndex = -1;//?????????????????????
    private int currentPlayCoIndex = -1;//?????????????????????
    //    private FragmentMusicSettingBinding binding;
    private boolean isFirstShow;

    private LinearLayout musicLayout;

    private LinearLayout voiceLayout;
    private TextView originalTvValue;
    private TextView bgmTvValue;
    private SeekBar originalSeekBar;
    private SeekBar bmgSeekBar;


    private TextView comment;
    private TextView coll;
    private ImageView jianjiIv;
    private ImageView collIv;
    private RecyclerView musicList;
    private TextView selMusic;
    private TextView selVoice;
    private OnClickListener clickListener;

    private float currentVolume = 1f;
    private float currentBGMVolume = 1f;
    private float tempBGMVolume = 1f;
    private Context context;
    private TXUGCRecord record;

    private int showPage = 0;//0?????????  1??????
    private int playPage = -1;//0?????????  1?????? -1?????????

    private boolean justChooseNoPlay = false;//???????????????????????????

    private Handler handler = new Handler(Looper.myLooper());

    private DownloadMusic downloadMusic;
    private Disposable mSubscription;
    private String TAG = "MusicSelDialog";
    private String rootPath;

    private boolean canEditVolume = true;
    private boolean canEditOriginalVolume = true;
    private RelativeLayout originalLayout;//??????????????????
    private int type;
    private BaseLoadMoreModule selCollMoreModule;

    /**
     * ???show????????????
     *
     * @param editVolume
     * @param editOriginal
     */
    public void setEnableVolumeEdit(boolean editVolume, boolean editOriginal) {
        canEditVolume = editVolume;
        canEditOriginalVolume = editOriginal;
    }

    public void setJustChooseNoPlay(boolean justChooseNoPlay) {
        this.justChooseNoPlay = justChooseNoPlay;
    }

    public MusicSelDialog(Context context) {
        super(context, R.layout.fragment_music_setting, new int[]{
                R.id.comment,
                R.id.coll,
                R.id.jianji_iv,
                R.id.coll_iv,
                R.id.sel_music,
                R.id.sel_voice,
        });
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        comment = findViewById(R.id.comment);
        coll = findViewById(R.id.coll);
        jianjiIv = findViewById(R.id.jianji_iv);
        collIv = findViewById(R.id.coll_iv);
        musicList = findViewById(R.id.music_list);
        selMusic = findViewById(R.id.sel_music);
        selVoice = findViewById(R.id.sel_voice);

        musicLayout = findViewById(R.id.music_layout);
        voiceLayout = findViewById(R.id.voice_layout);
        originalLayout = findViewById(R.id.original_layout);
        originalTvValue = findViewById(R.id.original_tv_seek_bar_value);
        bgmTvValue = findViewById(R.id.bgm_tv_seek_bar_value);
        originalSeekBar = findViewById(R.id.original_seek_bar_third);
        bmgSeekBar = findViewById(R.id.bgm_seek_bar_third);


        musicItemBeans = new ArrayList<>();
        musicCollItemBeans = new ArrayList<>();
        musicItemBeans.add(new MMusicItemBean(1));
        musicSelAdapter = new MusicSelAdapter();
        musicSelAdapter.setList(musicItemBeans);
        musicList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        musicList.setAdapter(musicSelAdapter);

        musicSelAdapter.setOnItemClickListener((adapter, view, position) -> {
            MMusicItemBean bean = musicItemBeans.get(position);
            if (playPage == 1) {
                stopPlayMusic();
                clearPlayStatus();
            }
            if (position == 0) {
                //????????????
                clickListener.onMoreClick();
                stopPlayMusicByMore();

            } else {
                if (bean.myMusicStatus == 0) {
                    if (currentPlayReIndex != -1) {
                        musicItemBeans.get(currentPlayReIndex).myMusicStatus = 0;
                        adapter.notifyItemChanged(currentPlayReIndex);
                    }
                    bean.myMusicStatus = 1;
                    //                    currentPlayCoIndex = -1;
                    currentPlayReIndex = position;
                    record.setBGMNofify(itxbgmNotify);
                    loadTrialMusic(bean);
                    showTitleRight(true);
                    if (clickListener != null) {
                        clickListener.onItemPlay(bean);
                    }
                    playPage = 0;
                    //????????????
                    collIv.setVisibility(View.VISIBLE);
                    collIv.setImageResource(bean.hasFavorite ? R.mipmap.icon_music_coll : R.mipmap.icon_music_coll_nor);
                } else {
                    collIv.setVisibility(View.GONE);
                    bean.myMusicStatus = 0;
                    currentPlayReIndex = -1;
                    //                    currentPlayCoIndex = -1;
                    showTitleRight(false);
                    record.setBGMNofify(null);
                    stopPlayMusic();
                    playPage = -1;
                }
                adapter.notifyItemChanged(position);
            }
        });
        selCollAdapter = new MusicSelCollAdapter(R.layout.item_music_sel);
        selCollAdapter.setList(musicCollItemBeans);

        selCollMoreModule = selCollAdapter.getLoadMoreModule();
        selCollMoreModule.setEnableLoadMore(true);
        selCollMoreModule.setPreLoadNumber(1);//???????????????????????????????????????????????????????????????1
        selCollMoreModule.setEnableLoadMoreIfNotFullPage(true);//??????????????????????????????????????????

        selCollMoreModule.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getCollList();
            }
        });

        selCollAdapter.setOnItemClickListener((adapter, view, position) -> {
            MMusicItemBean bean = musicCollItemBeans.get(position);
            if (playPage == 0) {
                stopPlayMusic();
                clearPlayStatus();
            }
            if (bean.myMusicStatus == 0) {
                if (currentPlayCoIndex != -1) {
                    musicCollItemBeans.get(currentPlayCoIndex).myMusicStatus = 0;
                    adapter.notifyItemChanged(currentPlayCoIndex);
                }
                bean.myMusicStatus = 1;
                currentPlayCoIndex = position;
                //                    currentPlayReIndex = -1;
                record.setBGMNofify(itxbgmNotify);
                loadTrialMusic(bean);
                showTitleRight(true);
                if (clickListener != null) {
                    clickListener.onItemPlay(bean);
                }
                playPage = 1;
                //????????????
                collIv.setVisibility(View.VISIBLE);
                collIv.setImageResource(bean.hasFavorite ? R.mipmap.icon_music_coll : R.mipmap.icon_music_coll_nor);
            } else {
                collIv.setVisibility(View.GONE);
                bean.myMusicStatus = 0;
                currentPlayCoIndex = -1;
                //                    currentPlayReIndex = -1;
                showTitleRight(false);
                record.setBGMNofify(null);
                stopPlayMusic();
                playPage = -1;
            }
            adapter.notifyItemChanged(position);
        });
        clearSeekBar();
        originalSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                originalTvValue.setText(i + "%");
                currentVolume = (float) i / 100;
                setVolum(currentVolume, currentBGMVolume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        bmgSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                bgmTvValue.setText(i + "%");
                currentBGMVolume = (float) i / 100;
                setVolum(currentVolume, currentBGMVolume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        setOnBottomItemClickListener(this);
        setOnDismissListener(this);
        initObservable();
        getCommentList();
        getCollList();
        File file = new File(context.getFilesDir(), "music");
        rootPath = file.getAbsolutePath();
        if (!file.exists()) {
            file.mkdir();
        }
        downloadMusic = DownloadMusic.getInstance();
        downloadMusic.init(file.getAbsolutePath());
        record = VideoRecordSDK.getInstance().getRecorder();
    }

    private TXRecordCommon.ITXBGMNotify itxbgmNotify = new TXRecordCommon.ITXBGMNotify() {

        @Override
        public void onBGMStart() {
            //??????????????????????????????
            handler.post(() -> {
                if (playPage == 0) {
                    musicItemBeans.get(currentPlayReIndex).myMusicStatus = 2;
                    musicSelAdapter.notifyItemChanged(currentPlayReIndex);
                } else {
                    musicCollItemBeans.get(currentPlayCoIndex).myMusicStatus = 2;
                    selCollAdapter.notifyItemChanged(currentPlayCoIndex);
                }
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

    //????????????????????????????????????????????????
    public void setType(int type) {
        this.type = type;
    }

    @Override
    public void show() {
        super.show();
        if (!isFirstShow) {
            comment.setSelected(true);
            coll.setSelected(false);
            selMusic.setSelected(true);
            selVoice.setSelected(false);
            showTitleRight(false);
        }
        if (!canEditVolume) {
            selVoice.setVisibility(View.GONE);
        }
        isFirstShow = true;
        //???????????????????????????
        if (mSubscription.isDisposed()) {
            //???new
            initMusicItemScription();
        }
        RxSubscriptions.add(mSubscription);
        page = 1;

        initComment = false;
        initColl = false;
        getCommentList();
        getCollList();
    }

    MutableLiveData<List<MMusicItemBean>> commentList = new MutableLiveData<>();
    MutableLiveData<List<MMusicItemBean>> collList = new MutableLiveData<>();
    MutableLiveData<Boolean> collStatus = new MutableLiveData<>();

    private int page = 1, pageSize = 10;
    private boolean initComment, initColl;

    private void initObservable() {
        commentList.observe((LifecycleOwner) context, mMusicItemBeans -> {
            initComment = true;
            musicItemBeans.clear();
            musicItemBeans.add(new MMusicItemBean(1));
            musicItemBeans.addAll(mMusicItemBeans);

            musicSelAdapter.setList(musicItemBeans);

            if (initColl)
                addCurrentPlayIntoList();
            if (initColl && initComment)
                if (currentPlayReIndex != -1 || currentPlayCoIndex != -1) {
                    //????????????
                    if (currentPlayReIndex != -1) {
                        if (!musicItemBeans.isEmpty()) {
                            loadTrialMusic(musicItemBeans.get(currentPlayReIndex));
                            musicList.scrollToPosition(currentPlayReIndex);
                        }
                    } else if (currentPlayCoIndex != -1) {
                        if (!musicCollItemBeans.isEmpty()) {
                            loadTrialMusic(musicCollItemBeans.get(currentPlayCoIndex));
                            musicList.scrollToPosition(currentPlayCoIndex);
                        }
                    }
                    //?????????????????????
                    if (clickListener != null) {
                        clickListener.onMusicPlay();
                    }
                }
        });
        collList.observe((LifecycleOwner) context, mMusicItemBeans -> {
            if (mMusicItemBeans == null) {
                return;
            }
            initColl = true;

            if (page == 1) {
                musicCollItemBeans.clear();
            }

            for (MMusicItemBean bean : mMusicItemBeans) {
                bean.hasFavorite = true;
            }
            musicCollItemBeans.addAll(mMusicItemBeans);

            if (page == 1) {
                selCollAdapter.setList(mMusicItemBeans);
            } else {
                selCollAdapter.addData(mMusicItemBeans);
            }
            if (mMusicItemBeans.size() >= 10) {
                selCollMoreModule.loadMoreComplete();
            } else {
                selCollMoreModule.loadMoreEnd(true);
            }

            page++;
            selCollMoreModule.setAutoLoadMore(true);//????????????


            if (initComment)
                addCurrentPlayIntoList();
            if (initColl && initComment)
                if (currentPlayReIndex != -1 || currentPlayCoIndex != -1) {
                    //????????????
                    if (currentPlayReIndex != -1) {
                        if (!musicItemBeans.isEmpty()) {
                            loadTrialMusic(musicItemBeans.get(currentPlayReIndex));
                            musicList.scrollToPosition(currentPlayReIndex);
                        }
                    } else if (currentPlayCoIndex != -1) {
                        if (!musicCollItemBeans.isEmpty()) {
                            loadTrialMusic(musicCollItemBeans.get(currentPlayCoIndex));
                            musicList.scrollToPosition(currentPlayCoIndex);
                        }
                    }
                    //?????????????????????
                    if (clickListener != null) {
                        clickListener.onMusicPlay();
                    }
                }
        });
        initMusicItemScription();
    }

    /**
     * ???????????????????????????????????????list???
     */
    private void addCurrentPlayIntoList() {
        if (MusicRecode.getInstance().getUseMusicItem() != null) {
            playPage = 0;
            collIv.setVisibility(View.VISIBLE);
            List<MMusicItemBean> temp;
            if (showPage == 0) {
                temp = musicItemBeans;
            } else {
                temp = musicCollItemBeans;
            }
            for (MMusicItemBean bean : temp) {
                if (bean.getId() == null)
                    continue;
                if (bean.getId().equals(MusicRecode.getInstance().getUseMusicItem().getId())) {
                    //????????????
                    bean.setOperation(true);
                    bean.myMusicStatus = 2;
                    playPage = showPage;
                    musicSelAdapter.notifyItemChanged(temp.indexOf(bean));
                    musicList.scrollToPosition(temp.indexOf(bean));
                    if (showPage == 0)
                        currentPlayReIndex = temp.indexOf(bean);
                    else
                        currentPlayCoIndex = temp.indexOf(bean);
                    collIv.setImageResource(bean.hasFavorite ? R.mipmap.icon_music_coll : R.mipmap.icon_music_coll_nor);
                    if (showPage == 0) {
                        return;
                    }
                }
            }
            MMusicItemBean useMusicItem = MusicRecode.getInstance().getUseMusicItem();
            boolean flag = false;
            for (MMusicItemBean itemBean : musicItemBeans) {
                if (itemBean.getId() == null)
                    continue;
                if (itemBean.getId().equals(useMusicItem.getId())) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                musicItemBeans.add(1, useMusicItem.cloneThis());
                currentPlayReIndex = 1;
                collIv.setImageResource(MusicRecode.getInstance().getUseMusicItem().hasFavorite ? R.mipmap.icon_music_coll : R.mipmap.icon_music_coll_nor);
                musicSelAdapter.setList(musicItemBeans);
                musicSelAdapter.notifyDataSetChanged();
            }
        }
    }

    private void initMusicItemScription() {
        mSubscription = RxBus.getDefault().toObservable(DownMusicProBean.class)
                .observeOn(AndroidSchedulers.mainThread()) //????????????????????????UI
                .subscribe(eventBean -> {
                    if (!isShowing()) {
                        return;
                    }
                    MMusicItemBean itemBean = getCurrentPlayItem();
                    switch (eventBean.status) {
                        case DownMusicProBean.WAIT:
                            break;
                        case DownMusicProBean.DOWN:
                            break;
                        case DownMusicProBean.ERROR:
                            LogUtils.e(TAG, eventBean.err);
                            break;
                        case DownMusicProBean.FULL: {
                            if (itemBean != null) {
                                if (eventBean.id.equals(itemBean.getId())) {
                                    //?????????????????????
                                    itemBean.localPath = new File(rootPath, itemBean.getId()).getAbsolutePath();
                                    if (!justChooseNoPlay) {
                                        playMusic(itemBean);
                                    }
                                    MusicRecode.getInstance().setUseMusicItem(itemBean);
                                }
                            }
                        }
                        break;
                        case DownMusicProBean.IDLE:
                            break;
                    }
                });
    }

    public void getCommentList() {
        LiteAvRepository.getInstance().getMusicReComment(commentList);
    }

    public void getCollList() {
        LiteAvRepository.getInstance().getMusicColl(collList, page, pageSize);
    }

    public void collMusic(MMusicItemBean bean) {
        if (bean == null) {
            return;
        }
        bean.hasFavorite = !bean.hasFavorite;
        LiteAvRepository.getInstance().musicColl(collStatus, bean.getId(), bean.hasFavorite);
        if (bean.hasFavorite) {
            addCollLocal(bean);
        } else {
            //????????????????????????
            removeCollLocal(bean);
        }
        if (showPage == 0) {
            collIv.setImageResource(bean.hasFavorite ? R.mipmap.icon_music_coll : R.mipmap.icon_music_coll_nor);
        } else {
            collIv.setVisibility(View.GONE);
        }
    }

    /**
     * ????????????????????????
     */
    private void addCollLocal(MMusicItemBean bean) {
        List<MMusicItemBean> list = new ArrayList<>();
        list.addAll(musicCollItemBeans);
        try {
            MMusicItemBean bean1 = bean.cloneThis();
            bean1.myMusicStatus = 0;
            list.add(bean1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        collList.postValue(list);
    }

    /**
     * ????????????????????????
     *
     * @param bean
     */
    private void removeCollLocal(MMusicItemBean bean) {
        MMusicItemBean bean1 = getCollPlayItemById(bean.getId());
        if (bean1 != null) {
            musicCollItemBeans.remove(bean1);
            selCollAdapter.setList(musicCollItemBeans);
        }
        bean1 = getRecommendPlayItemById(bean.getId());
        if (bean1 != null) {
            bean1.hasFavorite = false;
        }
    }

    //??????
    private void loadTrialMusic(MMusicItemBean bean) {
        //????????????
        DownMusicBean downMusicBean = new DownMusicBean();
        downMusicBean.size = bean.size;
        downMusicBean.path = bean.musicUrl;
        downMusicBean.id = bean.getId();

        downloadMusic.addWork(downMusicBean);

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
                if (!justChooseNoPlay) {
                    // ??????????????????
                    RecordMusicManager.getInstance().setRecordMusicInfo(musicInfo);
                    // ????????????
                    RecordMusicManager.getInstance().startMusic();
                }
                setVolum(currentVolume, currentBGMVolume);
                return "";
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                prepareSeekBar();
                if (clickListener != null) {
                    clickListener.onItemPlay(bean);
                }
            }
        }.execute(new Object());
    }

    //????????????
    private void stopPlayMusic() {
        MusicRecode.getInstance().setUseMusicItem(null);
        stopPreviewMusic();
        RecordMusicManager.getInstance().deleteMusic();
        clearSeekBar();
        setVolum(currentVolume, currentBGMVolume);
        if (clickListener != null) {
            clickListener.onStopPlay();
        }
    }

    //????????????
    private void stopPlayMusicByMore() {
        stopPreviewMusic();
        RecordMusicManager.getInstance().deleteMusic();
        clearSeekBar();
        setVolum(currentVolume, currentBGMVolume);
        if (clickListener != null) {
            clickListener.onStopPlay();
        }
    }

    private void prepareSeekBar() {
        //        currentVolume = 0;
        if (tempBGMVolume == 0) {
            //?????????????????????,??????????????????0???????????????????????????
            tempBGMVolume = 1f;
        }
        currentBGMVolume = tempBGMVolume;
        int temp = (int) (currentVolume * 100);
        int temp2 = (int) (currentBGMVolume * 100);
        if (canEditVolume) {
            if (canEditOriginalVolume)
                originalSeekBar.setEnabled(true);
            else {
                temp = 0;
                originalSeekBar.setEnabled(false);
            }
            bmgSeekBar.setEnabled(true);
        } else {
            originalSeekBar.setEnabled(false);
            bmgSeekBar.setEnabled(false);
        }
        originalSeekBar.setProgress(temp);
        bmgSeekBar.setProgress(temp2);
        originalTvValue.setText(temp + "%");
        bgmTvValue.setText(temp2 + "%");
    }

    private void clearSeekBar() {
        //        currentVolume = 0;
        tempBGMVolume = currentBGMVolume;
        currentBGMVolume = 0;
        int temp = (int) (currentVolume * 100);
        int temp2 = (int) (currentBGMVolume * 100);
        originalSeekBar.setProgress(temp);
        bmgSeekBar.setProgress(temp2);

        if (canEditVolume) {
            if (canEditOriginalVolume)
                originalSeekBar.setEnabled(true);
            else {
                temp = 0;
                originalSeekBar.setEnabled(false);
            }
            bmgSeekBar.setEnabled(true);
        } else {
            originalSeekBar.setEnabled(false);
            bmgSeekBar.setEnabled(false);
        }


        originalTvValue.setText(temp + "%");
        bgmTvValue.setText(temp2 + "%");
    }

    private void clearPlayStatus() {
        //        currentPlayReIndex = -1;
        //        currentPlayCoIndex = -1;
        showTitleRight(false);
    }

    //????????????
    private void stopPreviewMusic() {
        RecordMusicManager.getInstance().stopPreviewMusic();
    }

    /**
     * @param volume    ??????
     * @param bgmVolume bgm
     */
    public void setVolum(float volume, float bgmVolume) {
        TXUGCRecord record = VideoRecordSDK.getInstance().getRecorder();
        if (record != null) {
            record.setMicVolume(volume);
            record.setBGMVolume(bgmVolume);
            if (clickListener != null) {
                clickListener.onVolumeChange(volume, bgmVolume);
            }
        }
    }

    @Override
    public void onBottomItemClick(BottomDialog dialog, View view) {
        switch (view.getId()) {
            case R.id.comment: {
                showComment(true);
                if (musicList.getAdapter() instanceof MusicSelCollAdapter) {
                    musicList.setAdapter(musicSelAdapter);
                }
                changeHolder(true);
            }
            break;
            case R.id.coll: {
                showComment(false);
                if (musicList.getAdapter() instanceof MusicSelAdapter) {
                    musicList.setAdapter(selCollAdapter);
                }
                changeHolder(false);
            }
            break;
            case R.id.jianji_iv:
                if (currentPlayReIndex != -1) {
                    clickListener.onJianJiClick(musicItemBeans.get(currentPlayReIndex));
                    return;
                }
                if (currentPlayCoIndex != -1) {
                    clickListener.onJianJiClick(musicCollItemBeans.get(currentPlayCoIndex));
                }

                break;
            case R.id.coll_iv: {
                if (currentPlayReIndex != -1) {
                    clickListener.onCollClick(musicItemBeans.get(currentPlayReIndex));
                } else if (currentPlayCoIndex != -1) {
                    clickListener.onCollClick(musicCollItemBeans.get(currentPlayCoIndex));
                }

                //                ??????,??????
                MMusicItemBean item = getCurrentPlayItem();
                collMusic(item);
                if (showPage == 1) {
                    stopPlayMusic();
                    clearPlayStatus();
                    currentPlayCoIndex = -1;
                    holdRecommendPlayItem(item.getId());
                }
            }
            break;
            case R.id.sel_music:
                showMusic(true);
                break;
            case R.id.sel_voice:
                showMusic(false);
                break;
        }
    }

    private MMusicItemBean getCurrentPlayItem() {
        MMusicItemBean itemBean = null;
        if (playPage == 0) {
            itemBean = getCurrentRePlayItem();
        } else if (playPage == 1) {
            itemBean = getCurrentCollPlayItem();
        }
        return itemBean;
    }

    private MMusicItemBean getCurrentRePlayItem() {
        MMusicItemBean itemBean = null;
        if (currentPlayReIndex != -1) {
            itemBean = musicItemBeans.get(currentPlayReIndex);
        }
        return itemBean;
    }

    private MMusicItemBean getCurrentCollPlayItem() {
        MMusicItemBean itemBean = null;
        if (currentPlayCoIndex != -1) {
            itemBean = musicCollItemBeans.get(currentPlayCoIndex);
        }
        return itemBean;
    }

    private MMusicItemBean getRecommendPlayItemById(String id) {
        if (id == null)
            return null;
        for (MMusicItemBean musicItemBean : musicItemBeans) {
            if (musicItemBean.getId() == null)
                continue;
            if (musicItemBean.getId().equals(id)) {
                return musicItemBean;
            }
        }
        return null;
    }

    private MMusicItemBean getCollPlayItemById(String id) {
        if (id == null)
            return null;
        for (MMusicItemBean musicItemBean : musicCollItemBeans) {
            if (musicItemBean.getId().equals(id)) {
                return musicItemBean;
            }
        }
        return null;
    }

    /**
     * ?????????????????????
     *
     * @param id
     */
    private void holdRecommendPlayItem(String id) {
        MMusicItemBean itemBean = getRecommendPlayItemById(id);
        if (itemBean == null) {
            //?????????????????????
            changeCollIv(false, false);
            //???????????????
            currentPlayReIndex = -1;
            for (MMusicItemBean musicItemBean : musicItemBeans) {
                musicItemBean.myMusicStatus = 0;
            }
            musicSelAdapter.notifyDataSetChanged();
            return;
        }

        MMusicItemBean currentPlay = getCurrentRePlayItem();
        if (currentPlay != null) {
            if (!currentPlay.getId().equals(id)) {
                currentPlay.myMusicStatus = 0;
                musicSelAdapter.notifyItemChanged(musicItemBeans.indexOf(currentPlay));
            }
        }
        itemBean.myMusicStatus = 2;
        musicSelAdapter.notifyItemChanged(musicItemBeans.indexOf(itemBean));
        //??????????????????????????????
        changeCollIv(true, itemBean.hasFavorite);

        //??????????????????????????????
        MMusicItemBean collBean = getCollPlayItemById(id);
        if (collBean != null) {
            collBean.myMusicStatus = 0;
            selCollAdapter.notifyItemChanged(musicCollItemBeans.indexOf(itemBean));
        }

        currentPlayReIndex = musicItemBeans.indexOf(itemBean);
    }

    /**
     * ?????????????????????
     *
     * @param id
     */
    private void holdCollPlayItem(String id) {
        MMusicItemBean itemBean = getCollPlayItemById(id);
        if (itemBean == null) {
            //?????????????????????
            changeCollIv(false, false);
            //???????????????
            currentPlayCoIndex = -1;
            for (MMusicItemBean musicItemBean : musicCollItemBeans) {
                musicItemBean.myMusicStatus = 0;
            }
            selCollAdapter.notifyDataSetChanged();
            return;
        }

        //???????????????????????????
        MMusicItemBean currentPlay = getCurrentCollPlayItem();
        if (currentPlay != null) {
            if (!currentPlay.getId().equals(id)) {
                currentPlay.myMusicStatus = 0;
                selCollAdapter.notifyItemChanged(musicCollItemBeans.indexOf(currentPlay));
            }
        }

        itemBean.myMusicStatus = 2;
        selCollAdapter.notifyItemChanged(musicCollItemBeans.indexOf(itemBean));
        //??????????????????????????????
        changeCollIv(true, true);

        //??????????????????????????????
        MMusicItemBean collBean = getRecommendPlayItemById(id);
        if (collBean != null) {
            collBean.myMusicStatus = 0;
            musicSelAdapter.notifyItemChanged(musicItemBeans.indexOf(itemBean));
        }
        currentPlayCoIndex = musicCollItemBeans.indexOf(itemBean);
    }

    private void changeCollIv(boolean showColl, boolean isColl) {
        collIv.setVisibility(showColl ? View.VISIBLE : View.GONE);
        collIv.setImageResource(isColl ? R.mipmap.icon_music_coll : R.mipmap.icon_music_coll_nor);
    }

    private void showComment(boolean comment) {
        this.comment.setSelected(comment);
        this.coll.setSelected(!comment);

    }

    private void changeHolder(boolean comment) {
        MMusicItemBean item = getCurrentPlayItem();
        int tempPage = showPage;
        showPage = comment ? 0 : 1;
        if (tempPage != showPage) {
            if (showPage == 0) {
                //?????????
                holdRecommendPlayItem(item == null ? null : item.getId());
            } else {
                //?????????
                holdCollPlayItem(item == null ? null : item.getId());
            }
        }
    }

    private void showMusic(boolean comment) {
        selMusic.setSelected(comment);
        selVoice.setSelected(!comment);
        musicLayout.setVisibility(comment ? View.VISIBLE : View.GONE);
        voiceLayout.setVisibility(!comment ? View.VISIBLE : View.GONE);
        if (type == 1) {
            originalLayout.setVisibility(View.INVISIBLE);
        } else {
            originalLayout.setVisibility(View.VISIBLE);
        }
    }

    private void showTitleRight(boolean show) {
        jianjiIv.setVisibility(show ? View.GONE : View.GONE);
        collIv.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void setOnClickListener(OnClickListener listener) {
        this.clickListener = listener;
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        stopPreviewMusic();
        if (clickListener != null) {
            clickListener.onDismiss();
        }
        RxSubscriptions.remove(mSubscription);
    }

    public interface OnClickListener {
        void onMusicPlay();

        void onMoreClick();

        void onItemPlay(MMusicItemBean bean);

        void onStopPlay();

        void onJianJiClick(MMusicItemBean bean);

        void onCollClick(MMusicItemBean bean);

        void onDismiss();

        void onVolumeChange(float oVolume, float bgmVolume);
    }

}
