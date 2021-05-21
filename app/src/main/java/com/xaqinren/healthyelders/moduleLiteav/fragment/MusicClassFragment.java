package com.xaqinren.healthyelders.moduleLiteav.fragment;

import android.nfc.Tag;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.FragmentMusicClassBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.moduleLiteav.adapter.MusicItemAdapter;
import com.xaqinren.healthyelders.moduleLiteav.bean.MMusicItemBean;
import com.xaqinren.healthyelders.moduleLiteav.viewModel.MusicClassViewModel;
import com.xaqinren.healthyelders.utils.LogUtils;

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
    //分类ID
    private String classId;
    //当前分类下第几页
    private int page;
    //固定=3
    private int pageSize = 3;
    private List<MMusicItemBean> mMusicItemBeans;
    private MusicItemAdapter adapter;
    private Disposable mSubscription;
    private String TAG = "MusicClassFragment";
    private int operationIndex = -1;

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
    }

    @Override
    public void initData() {
        super.initData();
        if (this.mMusicItemBeans == null) {
            mMusicItemBeans = new ArrayList<>();
        }

        //抖音一页3个
        adapter = new MusicItemAdapter(R.layout.item_music_item);
        binding.content.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.content.setAdapter(adapter);
        adapter.setList(mMusicItemBeans);
        adapter.setOperationIndex(operationIndex );
        if (mMusicItemBeans.isEmpty()) {
            //请求
            viewModel.getMusicList(classId, null, page, pageSize);
        }
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        mSubscription = RxBus.getDefault().toObservable(EventBean.class)
                .observeOn(AndroidSchedulers.mainThread()) //回调到主线程更新UI
                .subscribe(new Consumer<EventBean>() {
                    @Override
                    public void accept(EventBean eventBean) throws Exception {
                        if (eventBean.msgId == CodeTable.EVENT_MUSIC_OP) {
                            adapter.clear(eventBean.content);
                        }
                    }
                });
        //将订阅者加入管理站
        RxSubscriptions.add(mSubscription);

        viewModel.requestSuccess.observe(this, aBoolean -> dismissDialog());
        viewModel.musicListData.observe(this, data ->{
            mMusicItemBeans.addAll(data);
            adapter.setList(mMusicItemBeans);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxSubscriptions.remove(mSubscription);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        operationIndex = adapter.getOperationIndex();
        LogUtils.e(TAG, "MusicClassFragment ->" + adapter.getOperationIndex());
    }
}
