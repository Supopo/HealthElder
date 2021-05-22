package com.xaqinren.healthyelders.moduleLiteav.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.tencent.qcloud.ugckit.module.record.MusicInfo;
import com.tencent.qcloud.ugckit.module.record.RecordMusicManager;
import com.tencent.qcloud.ugckit.module.record.VideoRecordSDK;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityChooseMusicBinding;
import com.xaqinren.healthyelders.moduleLiteav.Constant;
import com.xaqinren.healthyelders.moduleLiteav.adapter.MusicAdapter;
import com.xaqinren.healthyelders.moduleLiteav.adapter.MusicClassAdapter;
import com.xaqinren.healthyelders.moduleLiteav.bean.MMusicBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.MMusicItemBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.MusicClassBean;
import com.xaqinren.healthyelders.moduleLiteav.liteAv.MusicRecode;
import com.xaqinren.healthyelders.moduleLiteav.viewModel.ChooseMusicViewModel;


import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;

public class ChooseMusicActivity extends BaseActivity<ActivityChooseMusicBinding, ChooseMusicViewModel> {

    private final String TAG = "ChooseMusicActivity";

    private int cSheetPage = 1, cSheetPageSize = 10;

    private MusicAdapter musicAdapter;
    private List<MMusicBean> mMusicBeans;
    List<MusicClassBean> classBeans;
    private MusicClassAdapter musicClassAdapter;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_choose_music;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        setTitle("选择音乐");
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
                Intent intent = new Intent(ChooseMusicActivity.this, MusicListActivity.class);
                intent.putExtra(Constant.MUSIC_CLASS_ID, mMusicBeans.get(position).id);
                intent.putExtra(Constant.MUSIC_CLASS_NAME, classBeans.get(position).name);
                startActivity(intent);
            }
        });
    }

    /**
     * 头部分类
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
//        RecordMusicManager.getInstance().stopPreviewMusic();
    }
}
