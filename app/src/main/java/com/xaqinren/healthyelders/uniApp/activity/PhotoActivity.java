package com.xaqinren.healthyelders.uniApp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityPhotoBinding;
import com.xaqinren.healthyelders.uniApp.adapter.PhotoAdapter;
import com.xaqinren.healthyelders.uniApp.bean.FileBean;
import com.xaqinren.healthyelders.utils.GetFilesUtils;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;

public class PhotoActivity extends BaseActivity<ActivityPhotoBinding, BaseViewModel> {
    private PhotoAdapter photoAdapter;
    private List<FileBean> fileBeans;
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_photo;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        setTitle("选择图片");
        photoAdapter = new PhotoAdapter(R.layout.item_photo);
        fileBeans = GetFilesUtils.getInstance().showAllPicture(getContext());
        photoAdapter.setList(fileBeans);
        binding.content.setLayoutManager(new GridLayoutManager(this, 3));
        binding.content.setAdapter(photoAdapter);

        photoAdapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent = new Intent();
            intent.putExtra("path", fileBeans.get(position).path);
            setResult(Activity.RESULT_OK,intent);
            finish();
        });
    }
}
