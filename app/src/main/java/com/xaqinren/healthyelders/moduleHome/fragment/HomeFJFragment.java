package com.xaqinren.healthyelders.moduleHome.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.FragmentHomeFjBinding;
import com.xaqinren.healthyelders.moduleHome.viewModel.HomeFJViewModel;

import me.goldze.mvvmhabit.base.BaseFragment;

/**
 * Created by Lee. on 2021/5/13.
 * 首页-附近
 */
public class HomeFJFragment extends BaseFragment<FragmentHomeFjBinding, HomeFJViewModel> {
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_home_fj;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }
}
