package com.xaqinren.healthyelders.moduleLiteav.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.google.android.material.animation.AnimationUtils;
import com.luck.picture.lib.tools.ScreenUtils;
import com.tencent.qcloud.ugckit.module.record.MusicInfo;
import com.tencent.qcloud.ugckit.module.record.RecordMusicManager;
import com.tencent.qcloud.ugckit.module.record.VideoRecordSDK;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.ActivityChooseMusicBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.moduleLiteav.Constant;
import com.xaqinren.healthyelders.moduleLiteav.adapter.MusicAdapter;
import com.xaqinren.healthyelders.moduleLiteav.adapter.MusicClassAdapter;
import com.xaqinren.healthyelders.moduleLiteav.bean.MMusicBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.MMusicItemBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.MusicClassBean;
import com.xaqinren.healthyelders.moduleLiteav.liteAv.MusicRecode;
import com.xaqinren.healthyelders.moduleLiteav.viewModel.ChooseMusicViewModel;
import com.xaqinren.healthyelders.moduleLiteav.widget.InterceptLinearLayout;
import com.xaqinren.healthyelders.utils.LogUtils;


import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.utils.StringUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;

public class ChooseMusicActivity extends BaseActivity<ActivityChooseMusicBinding, ChooseMusicViewModel>  {

    private final String TAG = "ChooseMusicActivity";

    private int cSheetPage = 1, cSheetPageSize = 10;

    private MusicAdapter musicAdapter;
    private List<MMusicBean> mMusicBeans;
    List<MusicClassBean> classBeans;
    private MusicClassAdapter musicClassAdapter;
    private int currentScrollY;
    private int statusBarHeight;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_choose_music;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void finish() {
        setResult(Activity.RESULT_OK);
        super.finish();
        overridePendingTransition(R.anim.activity_push_none,R.anim.activity_bottom_2exit);
    }

    @Override
    public void initData() {
        super.initData();
        setTitle("????????????");
        setStatusBarTransparent();
        rlTitle.setVisibility(View.GONE);
        musicAdapter = new MusicAdapter(R.layout.item_music_block);
        mMusicBeans = new ArrayList<>();
        classBeans = new ArrayList<>();
        addAdapterHeader();
        musicAdapter.setList(mMusicBeans);
        binding.content.setAdapter(musicAdapter);
        binding.content.setLayoutManager(new LinearLayoutManager(this));
        viewModel.getMusicClass();
        viewModel.getMusicChannelSheet();
        musicAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                toMusicList(mMusicBeans.get(position).id, mMusicBeans.get(position).name, null);
            }
        });
        binding.search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    //??????
                    String search = binding.search.getText().toString();
                    if (StringUtils.isEmpty(search)) {
                        ToastUtils.showShort("?????????????????????");
                        return false;
                    }
                    toMusicList(null, null, search);
                    return true;
                }
                return false;
            }
        });
        statusBarHeight = ScreenUtils.getStatusBarHeight(getContext());

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) binding.contentLayout.getLayoutParams();
        layoutParams.topMargin = statusBarHeight;
        binding.contentLayout.setLayoutParams(layoutParams);
        binding.content.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                currentScrollY += dy;
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        binding.rlContainer.setOnScrollListener(new InterceptLinearLayout.OnScrollListener() {
            @Override
            public void onMove(float y) {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) binding.contentLayout.getLayoutParams();
                layoutParams.topMargin = (int) y + statusBarHeight;
                binding.contentLayout.setLayoutParams(layoutParams);
            }

            @Override
            public void onUp(float y) {
                if (y > 700) {
                    //??????finish??????
                    finish();
                }else{
                    //????????????
                    ValueAnimator valueAnimator = ObjectAnimator.ofInt((int)y, statusBarHeight)
                            .setDuration(300);
                    valueAnimator.addUpdateListener(animation -> {
                        int value = (int) animation.getAnimatedValue();
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) binding.contentLayout.getLayoutParams();
                        layoutParams.topMargin = value;
                        binding.contentLayout.setLayoutParams(layoutParams);
                    });
                    valueAnimator.start();
                }
            }

            @Override
            public void onLimitUp() {
                finish();
            }

            @Override
            public boolean isChildIntercept(float offsetY) {
                if (currentScrollY == 0) {
                    if (offsetY <= 0) {
                        //????????????,???????????????
                        return false;
                    }else{
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void toMusicList(String id, String name , String search) {
        MMusicItemBean bean = MusicRecode.getInstance().getUseMusicItem();
        if (bean == null) {
            //?????????????????????,????????????
            RecordMusicManager.getInstance().stopPreviewMusic();
            RxBus.getDefault().post(new EventBean(CodeTable.EVENT_MUSIC_OP,"-1"));
        }

        Intent intent = new Intent(ChooseMusicActivity.this, MusicListActivity.class);
        intent.putExtra(Constant.MUSIC_CLASS_ID, id);
        intent.putExtra(Constant.MUSIC_CLASS_NAME, name);
        intent.putExtra(Constant.MUSIC_SEARCH_NAME, search);
        startActivity(intent);
    }

    /**
     * ????????????
     */
    private void addAdapterHeader() {
        View header = View.inflate(this, R.layout.header_music_class_title, null);
        View view = View.inflate(this, R.layout.header_music_class, null);
        musicAdapter.addHeaderView(header);
        musicAdapter.addHeaderView(view);
        musicClassAdapter = new MusicClassAdapter(R.layout.item_music_class);
        RecyclerView classListView = view.findViewById(R.id.class_list);
        classListView.setLayoutManager(new GridLayoutManager(this, 5));
        classListView.setAdapter(musicClassAdapter);
        classListView.setNestedScrollingEnabled(false);

        musicClassAdapter.setOnItemClickListener((adapter, view1, position) -> {
            Intent intent = new Intent(ChooseMusicActivity.this, MusicListActivity.class);
            intent.putExtra(Constant.MUSIC_CLASS_ID, classBeans.get(position).id);
            intent.putExtra(Constant.MUSIC_CLASS_NAME, classBeans.get(position).name);
            startActivity(intent);
        });
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.musicClassLiveData.observe(this, musicClassBeans -> {
            this.classBeans = musicClassBeans;
            musicClassAdapter.setList(classBeans);
        });
        viewModel.musicChannelSheetData.observe(this, mMusicBeans -> {
            this.mMusicBeans = mMusicBeans;
            musicAdapter.setList(this.mMusicBeans);
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        RecordMusicManager.getInstance().stopPreviewMusic();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}
