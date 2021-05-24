package com.xaqinren.healthyelders.moduleHome.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.apiserver.LiteAvRepository;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.ActivityDraftBinding;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.adapter.DraftAdapter;
import com.xaqinren.healthyelders.moduleLiteav.activity.VideoPublishActivity;
import com.xaqinren.healthyelders.moduleLiteav.bean.SaveDraftBean;
import com.xaqinren.healthyelders.modulePicture.activity.PublishTextPhotoActivity;
import com.xaqinren.healthyelders.widget.GridDividerItemDecoration;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;

/**
 * 草稿箱
 */
public class DraftActivity extends BaseActivity <ActivityDraftBinding, BaseViewModel>{
    private DraftAdapter draftAdapter;
    private boolean isEdit = false;
    private int selCount;
    private int dataCount;
    private boolean isAllSel;
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_draft;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
    }

    @Override
    public void initData() {
        super.initData();
        setTitle("草稿箱");
        setTvRight("编辑");
        String fileName = UserInfoMgr.getInstance().getUserInfo().getId();
        draftAdapter = new DraftAdapter(R.layout.item_draft);
        reset();
        binding.content.setLayoutManager(new GridLayoutManager(this, 3));
        binding.content.addItemDecoration(new GridDividerItemDecoration(2,getResources().getColor(R.color.transparent)));
        binding.content.setAdapter(draftAdapter);
        tvRight.setOnClickListener(view -> {
            setEdit();
            showBottom();
            setDataEdit();
            checkAllSel();
        });
        draftAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                SaveDraftBean bean = (SaveDraftBean) adapter.getData().get(position);
                if (isEdit) {
                    bean.setSel(!bean.isSel());
                    adapter.notifyItemChanged(position);
                    if (bean.isSel()) {
                        selCount++;
                    }else{
                        selCount--;
                    }
                    checkAllSel();
                }else{
                    //进入详情
                    Bundle bundle = new Bundle();
                    bundle.putLong(Constant.DraftId, bean.getId());
                    int type = bean.getType();
                    startActivity(type == 0 ? VideoPublishActivity.class : PublishTextPhotoActivity.class, bundle);
                }
            }
        });
        binding.selAllIv.setOnClickListener(view -> {
            selAll();
            checkAllSel();
        });
        binding.selAllTv.setOnClickListener(view -> {
            selAll();
            checkAllSel();
        });
        binding.delBtn.setOnClickListener(view -> {
            for (SaveDraftBean datum : draftAdapter.getData()) {
                if (datum.isSel()) {
                    LiteAvRepository.getInstance().delDraftsById(this,fileName , datum.getId());
                }
            }
            //重新加载数据
            reset();
            setEdit();
            showBottom();
        });
    }

    private void reset() {
        String fileName = UserInfoMgr.getInstance().getUserInfo().getId();
        List<SaveDraftBean>  list = LiteAvRepository.getInstance().getDraftsList(this, fileName);
        dataCount = list.size();
        draftAdapter.setList(list);
        selCount = 0;
        isAllSel = false;
    }

    private void checkAllSel() {
        if (selCount < dataCount) {
            isAllSel = false;
            binding.selAllIv.setImageResource(R.mipmap.rad_py_nor);
        }else{
            isAllSel = true;
            binding.selAllIv.setImageResource(R.mipmap.rad_py_sel);
        }
    }

    private void setEdit() {
        isEdit = !isEdit;
        setTvRight(isEdit ? "保存" : "编辑");
    }
    private void setDataEdit() {
        for (SaveDraftBean datum : draftAdapter.getData()) {
            datum.setEdit(isEdit);
            if (!isEdit) datum.setSel(false);
        }
        if (!isEdit) {
            isAllSel = false;
            selCount = 0;
        }
        draftAdapter.notifyDataSetChanged();
    }
    private void showBottom() {
        binding.bottomLayout.setVisibility(isEdit ? View.VISIBLE : View.GONE);
    }

    private void selAll() {
        boolean isSel = selCount == dataCount;
        for (SaveDraftBean datum : draftAdapter.getData()) {
            datum.setSel(!isSel);
        }
        if (isSel) {
            selCount = 0;
        }else{
            selCount = dataCount;
        }
        draftAdapter.notifyDataSetChanged();
    }
}
