package com.xaqinren.healthyelders;

import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.ActivityMainBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.global.InfoCache;
import com.xaqinren.healthyelders.moduleHome.fragment.GirlsFragment;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleMine.fragment.MineFragment;
import com.xaqinren.healthyelders.moduleHome.fragment.XxxFragment;
import com.xaqinren.healthyelders.moduleZhiBo.activity.StartLiveActivity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.utils.SPUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;


public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel> {
    private List<Fragment> mFragments;
    private double firstTime;
    private TextView oldView;
    private TextView selectView;
    private Disposable disposable;
    private Disposable eventDisposable;
    private String accessToken;
    private UserInfoBean userInfoBean;
    private QMUIDialog showSelectDialog;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    public int initVariableId() {
        return com.xaqinren.healthyelders.BR.viewModel;
    }

    @Override
    public void initData() {
        setStatusBarTransparent();
        //初始化Fragment
        initFragment();
        getCacheUserInfo();
    }

    private void getCacheUserInfo() {
        //获取token
        accessToken = InfoCache.getInstance().getAccessToken();
        userInfoBean = InfoCache.getInstance().getLoginUser();
        //已登陆，判断下用户信息存不存在请求用户信息接口
        if (!TextUtils.isEmpty(accessToken)) {
            if (userInfoBean == null) {
                //获取用户信息
                viewModel.getUserInfo(accessToken);
            } else {
                UserInfoMgr.getInstance().setUserInfo(userInfoBean);
                UserInfoMgr.getInstance().setAccessToken(accessToken);
                UserInfoMgr.getInstance().setHttpToken(Constant.API_HEADER + accessToken);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getCacheUserInfo();
    }

    private void initFragment() {
        mFragments = new ArrayList<>();
        mFragments.add(new XxxFragment());
        mFragments.add(new GirlsFragment());
        mFragments.add(new XxxFragment());
        mFragments.add(new MineFragment());
        //默认选中第一个
        commitAllowingStateLoss(0);
        oldView = binding.tvMenu1;
        initEvent();
    }

    private void initEvent() {
        binding.tvMenu1.setOnClickListener(lis -> {
            selectView = binding.tvMenu1;
            initBottomTab();
            oldView = binding.tvMenu1;
        });
        binding.tvMenu2.setOnClickListener(lis -> {
            selectView = binding.tvMenu2;
            initBottomTab();
            oldView = binding.tvMenu2;
        });
        binding.tvMenu3.setOnClickListener(lis -> {
            selectView = binding.tvMenu3;
            initBottomTab();
            oldView = binding.tvMenu3;
        });
        binding.tvMenu4.setOnClickListener(lis -> {
            selectView = binding.tvMenu4;
            initBottomTab();
            oldView = binding.tvMenu4;
        });
        binding.ivLive.setOnClickListener(lis -> {
            disposable = permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                    .subscribe(granted -> {
                        if (granted) {
                            //去检查直播权限
                            showDialog();
                            viewModel.checkLiveInfo();
                        } else {
                            ToastUtils.showShort("访问权限已拒绝");
                        }

                    });
        });

        eventDisposable = RxBus.getDefault().toObservable(EventBean.class).subscribe(o -> {
            if (o.msgId == CodeTable.TOKEN_ERR) {
                //TODO token失效 重新登录
                SPUtils.getInstance().put(Constant.SP_KEY_TOKEN_INFO, "");
                SPUtils.getInstance().put(Constant.SP_KEY_WX_INFO, "");
            }
        });
        RxSubscriptions.add(eventDisposable);
    }


    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.liveInfo.observe(this, liveInfo -> {
            if (liveInfo != null) {
                dismissDialog();
                //有上次记录，说明没有结束直播，弹选择框
                if (!TextUtils.isEmpty(liveInfo.liveRoomRecordId)) {
                    showSelectDialog(liveInfo.liveRoomRecordId);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constant.LiveInitInfo, liveInfo);
                    startActivity(StartLiveActivity.class, bundle);
                }
            }
        });
        viewModel.overSuccess.observe(this, overSuccess -> {
            if (overSuccess != null) {
                if (overSuccess) {
                    dismissDialog();
                }
            }
        });
    }

    private void showSelectDialog(String liveRoomRecordId) {
        final String[] items = new String[]{"继续直播", "结束直播"};
        if (showSelectDialog == null) {
            showSelectDialog = new QMUIDialog.MenuDialogBuilder(this)
                    .addItems(items, (dialog, which) -> {
                        Toast.makeText(this, "你选择了 " + items[which], Toast.LENGTH_SHORT).show();
                        if (which == 0) {
                            //直接进入直播间 省去创建
                        } else {
                            //调接口结束直播
                            showDialog();
                            viewModel.closeLastLive(liveRoomRecordId);
                        }
                        dialog.dismiss();
                    })
                    .show();
        } else {
            showSelectDialog.show();
        }

    }


    private void initBottomTab() {

        Drawable dawable = getResources().getDrawable(R.mipmap.line_bq);
        dawable.setBounds(0, 0, dawable.getMinimumWidth(), dawable.getMinimumHeight());

        oldView.setCompoundDrawables(null, null, null, null);
        oldView.setTextColor(getResources().getColor(R.color.gray_666));
        oldView.setTextSize(16);

        selectView.setCompoundDrawables(null, null, null, dawable);
        selectView.setTextColor(getResources().getColor(R.color.color_DC3530));
        selectView.setTextSize(18);

        switch (selectView.getId()) {
            case R.id.tv_menu1:
                commitAllowingStateLoss(0);
                break;
            case R.id.tv_menu2:
                commitAllowingStateLoss(1);
                break;
            case R.id.tv_menu3:
                commitAllowingStateLoss(2);
                break;
            case R.id.tv_menu4:
                commitAllowingStateLoss(3);
                break;

        }

    }

    private void commitAllowingStateLoss(int position) {
        hideAllFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(position + "");
        if (currentFragment != null) {
            transaction.show(currentFragment);
        } else {
            currentFragment = mFragments.get(position);
            transaction.add(R.id.frameLayout, currentFragment, position + "");
        }
        transaction.commitAllowingStateLoss();
    }

    //隐藏所有Fragment
    private void hideAllFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < mFragments.size(); i++) {
            Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(i + "");
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }
        }
        transaction.commitAllowingStateLoss();
    }

    /**
     * 二次点击（返回键）退出
     */
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                long secondTime = System.currentTimeMillis();
                if (secondTime - firstTime > 2000) {
                    //如果两次按键时间间隔大于2秒，则不退出
                    Toast.makeText(this, "再按一次退出程序~", Toast.LENGTH_SHORT).show();
                    firstTime = secondTime;//更新firstTime
                    return true;
                } else {
                    //两次按键小于2秒时，退出应用
                    System.exit(0);
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
        eventDisposable.dispose();
    }
}
