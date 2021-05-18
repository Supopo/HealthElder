package com.xaqinren.healthyelders.moduleLiteav.activity;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;

import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityMusicDetailsBinding;
import com.xaqinren.healthyelders.moduleLiteav.adapter.MusicDetailsAdapter;
import com.xaqinren.healthyelders.moduleLiteav.bean.MusicBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.MusicDetailBean;
import com.xaqinren.healthyelders.moduleLiteav.viewModel.MusicDetailsViewModel;
import com.xaqinren.healthyelders.widget.GridDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.utils.ConvertUtils;

/**
 * 音乐详情
 */
public class MusicDetailsActivity extends BaseActivity<ActivityMusicDetailsBinding , MusicDetailsViewModel> {
    private MusicDetailsAdapter adapter;
    private MusicDetailBean detailBean;
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_music_details;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        rlTitle.setVisibility(View.GONE);
        detailBean = new MusicDetailBean();
        detailBean.setColl(true);
        detailBean.setCoverPath("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fyouimg1.c-ctrip.com%2Ftarget%2Ftg%2F035%2F063%2F726%2F3ea4031f045945e1843ae5156749d64c.jpg&refer=http%3A%2F%2Fyouimg1.c-ctrip.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1623893011&t=c9c202dbd95db0a3f41cbef5dba69896");
        detailBean.setCreate(false);
        detailBean.setOriginal(true);
        detailBean.setUserId(123456);
        detailBean.setMusicId(123456);
        detailBean.setUseCount("666");
        detailBean.setNickName("你好呀");
        List<MusicBean> musicBeans = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            MusicBean bean = new MusicBean();
            bean.setMusicId(456789);
            bean.setMusicName("测试音乐");
            bean.setMusicType("摇滚");
            bean.setCoverPath("https://gimg2.baidu.com/image_search/src=http%3A%2F%2F1812.img.pp.sohu.com.cn%2Fimages%2Fblog%2F2009%2F11%2F18%2F18%2F8%2F125b6560a6ag214.jpg&refer=http%3A%2F%2F1812.img.pp.sohu.com.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1623893011&t=a210e32b88b974220a2f20d5a9b7302a");
            bean.setShoufa(true);
            musicBeans.add(bean);
        }
        detailBean.setMusicBeans(musicBeans);
        adapter = new MusicDetailsAdapter(R.layout.item_music_detail);
        adapter.setList(detailBean.getMusicBeans());
        binding.contentList.setLayoutManager(new GridLayoutManager(this, 3));
//        binding.contentList.addItemDecoration(new GridDividerItemDecoration(ConvertUtils.dp2px(3), getResources().getColor(R.color.transparent)));
        binding.contentList.setAdapter(adapter);

        View header = View.inflate(this, R.layout.haader_musci_deatil, null);
        adapter.setHeaderView(header);
    }
}
