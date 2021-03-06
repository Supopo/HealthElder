
package com.xaqinren.healthyelders.moduleZhiBo.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.tencent.liteav.demo.beauty.model.BeautyInfo;
import com.tencent.liteav.demo.beauty.model.ItemInfo;
import com.tencent.liteav.demo.beauty.model.TabInfo;
import com.tencent.liteav.demo.beauty.view.BeautyPanel;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.FragmentStartLiveBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.bean.MenuBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.LocationBean;
import com.xaqinren.healthyelders.moduleLiteav.service.LocationService;
import com.xaqinren.healthyelders.moduleZhiBo.activity.LiveZhuboActivity;
import com.xaqinren.healthyelders.moduleZhiBo.activity.SettingRoomPwdActivity;
import com.xaqinren.healthyelders.moduleZhiBo.activity.StartLiveActivity;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.LiveMenuAdapter;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ListPopMenuBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.MLVBLiveRoom;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.roomutil.im.IMMessageMgr;
import com.xaqinren.healthyelders.moduleZhiBo.popupWindow.ZBGoodsListPop;
import com.xaqinren.healthyelders.moduleZhiBo.popupWindow.ZBStartSettingPop;
import com.xaqinren.healthyelders.moduleZhiBo.viewModel.LiveZhuboViewModel;
import com.xaqinren.healthyelders.moduleZhiBo.viewModel.StartLiveUiViewModel;
import com.xaqinren.healthyelders.moduleZhiBo.viewModel.StartLiveZbViewModel;
import com.xaqinren.healthyelders.uniApp.UniService;
import com.xaqinren.healthyelders.uniApp.UniUtil;
import com.xaqinren.healthyelders.uniApp.bean.UniEventBean;
import com.xaqinren.healthyelders.utils.GlideEngine;
import com.xaqinren.healthyelders.utils.GlideUtil;
import com.xaqinren.healthyelders.widget.BottomDialog;
import com.xaqinren.healthyelders.widget.ListBottomPopup;
import com.xaqinren.healthyelders.widget.YesOrNoDialog;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.utils.PermissionUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;

import static com.tencent.liteav.demo.beauty.utils.BeautyUtils.getPackageName;

/**
 * Created by Lee. on 2021/4/23.
 * ???????????????
 */
public class StartLiveFragment extends BaseFragment<FragmentStartLiveBinding, StartLiveZbViewModel> {

    private MLVBLiveRoom mLiveRoom;
    private List<LocalMedia> selectList;
    private String photoPath;
    private boolean isAgree = true;
    public boolean isBackCamera;//?????????????????????
    private boolean isOpenRoom = true;//????????????
    private ZBStartSettingPop startSettingPop;
    private BottomDialog mLvJingPop;
    private BottomDialog mMeiYanPop;
    private BeautyPanel mMeiYanControl;
    private BeautyPanel mLvJingControl;
    private QMUIDialog showSelectDialog;
    private LiveInitInfo mLiveInitInfo = new LiveInitInfo();
    private StartLiveUiViewModel liveUiViewModel;
    private boolean hasCheckInfo;
    private double lat;
    private double lon;
    private Disposable subscribe;
    private String cityName;
    private String poiName;
    private String cityCode;
    private String province;
    private String district;
    private LiveMenuAdapter menuAdapter;
    private LiveZhuboViewModel liveZbViewModel;
    private List<MenuBean> menus;
    private Disposable disposable;
    private Disposable preDisposable;
    private ZBGoodsListPop zbGoodsListPop;
    private Disposable uniSubscribe;
    private boolean isCloseXN;
    private int goodsTempPos;//????????????
    private int openTempPos = 4;//????????????
    private StartLiveActivity startLiveActivity;
    private ListBottomPopup listBottomPopup;
    private boolean isFirst = true;
    private YesOrNoDialog yesOrNoDialog;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_start_live;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        liveUiViewModel = ViewModelProviders.of(getActivity()).get(StartLiveUiViewModel.class);
        setMoreTextData("?????????????????????", "???????????????????????????????????????????????????", "???", "?????????????????????????????????????????????");
        //??????????????????
        mLiveRoom = MLVBLiveRoom.sharedInstance(getActivity());
        //???????????????????????????
        //        mLiveRoom.startLocalPreview(true, binding.videoView);
        initLiveMenu();

        initEvent();
        mLiveInitInfo.setHasLocation(true);
    }

    private void initLiveMenu() {
        try {
            startLiveActivity = (StartLiveActivity) getActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }

        menuAdapter = new LiveMenuAdapter(R.layout.item_start_live_menu);
        binding.rvMenu.setLayoutManager(new GridLayoutManager(getActivity(), 5));

        menus = new ArrayList<>();
        for (int i = 0; i < menuNames.length; i++) {
            menus.add(new MenuBean(menuNames[i], menuRes[i]));
        }

        menuAdapter.setOnItemClickListener(((adapter, view, position) -> {
            switch (menuAdapter.getData().get(position).menuName) {
                case "??????":
                    isBackCamera = !isBackCamera;
                    //                    mLiveRoom.switchCamera();
                    startLiveActivity.startLiteAVFragment.liteAvRecode.switchCamera();
                    mLiveInitInfo.isBackCamera = isBackCamera;
                    break;
                case "??????":
                    //??????????????????????????????????????????????????????
                    mLiveInitInfo.isMirror = !mLiveInitInfo.isMirror;
                    if (mLiveInitInfo.isMirror) {
                        menuAdapter.getData().get(position).menuRes = R.mipmap.icon_jingxiang_fz;
                    } else {
                        menuAdapter.getData().get(position).menuRes = R.mipmap.icon_jingxiang;
                    }
                    menuAdapter.notifyItemChanged(position, 99);
                    break;
                case "??????":
                    //                    showMYPop();
                    //??????StartLiveAVFragment????????????
                    startLiveActivity.startLiteAVFragment.hidePanel();
                    startLiveActivity.startLiteAVFragment.showMYPop();
                    //??????????????????LiveInitInfo???????????????
                    break;
                case "??????":
                    //                    showLJPop();
                    startLiveActivity.startLiteAVFragment.hidePanel();
                    startLiveActivity.startLiteAVFragment.showLJPop();
                    break;
                case "??????":
                    //                    UniService.startService(getActivity(), mLiveInitInfo.appId, 0x10112, mLiveInitInfo.jumpUrl);
                    zbGoodsListPop = new ZBGoodsListPop(getActivity(), mLiveInitInfo.id, 0);
                    zbGoodsListPop.showPopupWindow();
                    break;
                case "??????":
                    //??????????????????
                    showOpenRoomPop();
                    break;
                case "??????":
                    //?????????????????????
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), SettingRoomPwdActivity.class);
                    startActivityForResult(intent, 1001);
                    break;
                case "??????":
                    startSettingPop = new ZBStartSettingPop(getActivity(), mLiveInitInfo);
                    startSettingPop.showPopupWindow();
                    break;

            }
        }));

    }

    private String[] menuNames = {"??????", "??????", "??????", "??????", "??????", "??????", "??????"
    };
    private int[] menuRes = {
            R.mipmap.icon_fanzhuan, R.mipmap.icon_jingxiang, R.mipmap.icon_meibai,
            R.mipmap.icon_lvjing, R.mipmap.icon_shangpin, R.mipmap.icon_gongkai, R.mipmap.icon_shezhi
    };

    private String[] permission = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

    private void initEvent() {
        binding.ivBack.setOnClickListener(lis -> {
            try {
                getActivity().finish();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                getActivity().finish();
            }
        });
        //????????????
        binding.rlAddCover.setOnClickListener(lis -> {
            selectImage();
        });


        binding.llLoc.setOnClickListener(lis -> {
            showToStartLocPre();
        });

        //??????????????????
        binding.rlSelect.setOnClickListener(lis -> {
            isAgree = !isAgree;
            if (isAgree) {
                binding.ivSelect.setBackgroundResource(R.mipmap.radbox_sel);
            } else {
                binding.ivSelect.setBackgroundResource(R.mipmap.radbox_nor);
            }
        });

        binding.btnStart.setOnClickListener(lis -> {
            if (!isAgree) {
                ToastUtil.toastShortMessage("????????????????????????????????????????????????");
                return;
            }

            if (hasCheckInfo) {
                //?????????????????????????????????
                if (mLiveInitInfo != null && mLiveInitInfo.liveRoomType != null && mLiveInitInfo.liveRoomType.equals(Constant.REQ_ZB_TYPE_XN) && !mLiveInitInfo.liveRoomStatus.equals("LIVE_OVER")) {
                    isCloseXN = true;
                    //??????????????????
                    viewModel.closeLastLive(mLiveInitInfo.liveRoomRecordId);
                } else {
                    toStartLive();
                }
            } else {
                viewModel.checkLiveInfo();
            }


        });
    }

    private void showToStartLocPre() {
        boolean hasPre = PermissionUtils.hasPermission(getActivity(), permission);
        //???????????????????????????????????????????????????
        if (hasPre) {
            if (locSuccess == 2) {
                openGPSSettings();
            } else {
                showListPop();
            }
        } else {
            showToStartDialog();
        }
    }

    /**
     * ??????GPS????????????
     */
    private boolean checkGPSIsOpen() {
        boolean isOpen;
        LocationManager locationManager = (LocationManager) getActivity()
                .getSystemService(Context.LOCATION_SERVICE);
        isOpen = locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        return isOpen;
    }

    /**
     * ??????GPS??????
     */
    private void openGPSSettings() {
        if (!checkGPSIsOpen()) {
            //??????????????????????????????
            YesOrNoDialog yesOrNoDialog = new YesOrNoDialog(getActivity());
            yesOrNoDialog.setMessageText("?????????????????????????????????GPS");
            yesOrNoDialog.showDialog();
            yesOrNoDialog.setRightBtnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    locSuccess = 3;
                    //?????????GPS
                    //??????GPS????????????
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, 1010);

                    yesOrNoDialog.dismissDialog();
                }
            });

        }
    }


    private void toStartLive() {
        preDisposable = permissions.request(Manifest.permission.RECORD_AUDIO)
                .subscribe(granted -> {
                    if (granted) {
                        if (TextUtils.isEmpty(binding.etTitle.getText().toString().trim())) {
                            ToastUtil.toastShortMessage("????????????????????????");
                            return;
                        }
                        mLiveInitInfo.liveRoomName = binding.etTitle.getText().toString().trim();

                        if (!TextUtils.isEmpty(photoPath)) {
                            showDialog("??????????????????...");
                            viewModel.updatePhoto(photoPath);
                            return;
                        }


                        if (TextUtils.isEmpty(mLiveInitInfo.liveRoomCover)) {
                            if (TextUtils.isEmpty(photoPath)) {
                                ToastUtil.toastShortMessage("??????????????????");
                                return;
                            }
                        } else {
                            startLiveZhuboActivity();
                        }

                    } else {
                        ToastUtils.showShort("???????????????????????????????????????");
                    }

                });
    }

    private int locSuccess;//2- ???????????? 3-????????????????????????GPS

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        subscribe = RxBus.getDefault().toObservable(EventBean.class).subscribe(event -> {
            if (event != null) {
                if (event.msgId == CodeTable.LOCATION_SUCCESS) {
                    locSuccess = 1;
                    //????????????
                    LocationBean locationBean = (LocationBean) event.data;
                    lat = locationBean.lat;
                    lon = locationBean.lon;
                    AppApplication.get().setmLat(lat);
                    AppApplication.get().setmLon(lon);
                    cityCode = locationBean.cityCode;
                    province = locationBean.province;
                    cityName = locationBean.cityName;
                    district = locationBean.district;
                    poiName = locationBean.desName;
                    binding.tvLoc.setText(cityName + poiName);
                } else if (event.msgId == CodeTable.LOCATION_ERROR) {
                    locSuccess = 2;
                    openGPSSettings();
                }
            }
        });

        disposable = RxBus.getDefault().toObservable(UniEventBean.class).subscribe(event -> {
            if (event != null) {
                if (event.msgId == CodeTable.UNI_RELEASE) {
                    if (event.taskId == 0x10112) {
                        UniUtil.openUniApp(getContext(), event.appId, event.jumpUrl, null, event.isSelfUni);
                    }
                } else if (event.msgId == CodeTable.UNI_RELEASE_FAIL) {
                    //ToastUtils.showShort("?????????????????????");
                }
            }
        });

        viewModel.liveInfo.observe(this, liveInfo -> {
            if (liveInfo != null) {
                isFirst = false;
                binding.rvMenu.setVisibility(View.VISIBLE);
                binding.rlTopInfo.setVisibility(View.VISIBLE);

                hasCheckInfo = true;
                //?????????????????????
                mLiveInitInfo = liveInfo;

                for (int i = 0; i < menus.size(); i++) {
                    if (menus.get(i).menuName.equals("??????")) {
                        goodsTempPos = i;
                    }
                }


                if ((mLiveInitInfo.liveRoomLevel != null && !mLiveInitInfo.liveRoomLevel.getCanSale()) || !mLiveInitInfo.getCanSale()) {
                    //????????????????????????????????????
                    menus.remove(goodsTempPos);
                }


                for (int i = 0; i < menus.size(); i++) {
                    if (menus.get(i).menuName.equals("??????") || menus.get(i).menuName.equals("??????")) {
                        openTempPos = i;
                    }
                }


                menuAdapter.setNewInstance(menus);
                binding.rvMenu.setAdapter(menuAdapter);
                initRoomInfo();
                //?????????????????????????????????????????????????????????
                if (!TextUtils.isEmpty(liveInfo.liveRoomRecordId) && !liveInfo.liveRoomType.equals(Constant.REQ_ZB_TYPE_XN)) {
                    showSelectDialog(liveInfo.liveRoomRecordId);
                }
            }
        });
        viewModel.exitSuccess.observe(this, exitSuccess -> {
            if (exitSuccess != null) {
                if (exitSuccess) {
                    //????????????????????????????????????????????????
                    mLiveInitInfo.liveRoomRecordId = "";
                    //?????????
                    mLiveRoom.destroyGroup(Constant.getRoomId(mLiveInitInfo.liveRoomCode), new IMMessageMgr.Callback() {
                        @Override
                        public void onError(int code, String errInfo) {
                        }

                        @Override
                        public void onSuccess(Object... args) {
                            if (isCloseXN) {
                                isCloseXN = false;
                                toStartLive();
                            }
                        }
                    });


                }
            }
        });

        viewModel.dismissDialog.observe(this, dismiss -> {
            if (dismiss != null) {
                if (dismiss) {
                    dismissDialog();
                }
            }
        });

        viewModel.fileUrl.observe(this, url -> {
            if (!TextUtils.isEmpty(url)) {
                mLiveInitInfo.liveRoomCover = url;
                startLiveZhuboActivity();
            }
        });


        liveUiViewModel.getCurrentPage().observe(getActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer.intValue() == 0) {
                    //??????????????????
                    boolean hasPre = PermissionUtils.hasPermission(getActivity(), permission);
                    if (isFirst) {
                        if (hasPre) {
                            LocationService.startService(getActivity());
                        }
                        viewModel.checkLiveInfo();
                    }
                }

            }
        });

    }

    private void startLiveZhuboActivity() {
        if (!hasCheckInfo) {
            ToastUtil.toastShortMessage("??????????????????????????????????????????");
            return;
        }

        mLiveInitInfo.latitude = lat;
        mLiveInitInfo.longitude = lon;
        mLiveInitInfo.address = poiName;
        mLiveInitInfo.province = province;
        mLiveInitInfo.city = cityName;
        mLiveInitInfo.district = district;

        //??????????????????????????????
        mLiveInitInfo.beautyStyle = mBeautyStyle;
        mLiveInitInfo.beautyLevel = mBeautyLevel;
        mLiveInitInfo.whitenessLevel = mWhitenessLevel;
        mLiveInitInfo.ruddinessLevel = mRuddinessLevel;
        mLiveInitInfo.beautyPos = mBeautypos;
        mLiveInitInfo.allBeautyLevel = mAllBeautyLevel;

        mLiveInitInfo.filterStyle = mFilterStyle;

        mLiveInitInfo.isBackCamera = isBackCamera;

        if (startLiveActivity.startLiteAVFragment.mMeiYanControl != null) {
            mLiveInitInfo.beautyInfo1 = startLiveActivity.startLiteAVFragment.mMeiYanControl.getBeautyInfo();
        }
        if (startLiveActivity.startLiteAVFragment.mLvJingControl != null) {
            mLiveInitInfo.beautyInfo2 = startLiveActivity.startLiteAVFragment.mLvJingControl.getBeautyInfo();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.LiveInitInfo, mLiveInitInfo);

        startActivity(LiveZhuboActivity.class, bundle);
    }


    private void initRoomInfo() {
        binding.etTitle.setText(mLiveInitInfo.liveRoomName);
        GlideUtil.intoImageView(getActivity(), mLiveInitInfo.liveRoomCover, binding.ivCover, R.mipmap.icon_add_cover);


        //?????????????????????
        if (!TextUtils.isEmpty(mLiveInitInfo.roomPassword)) {
            menuAdapter.getData().get(openTempPos).menuRes = R.mipmap.icon_jiami;
            menuAdapter.getData().get(openTempPos).menuName = "??????";
        } else {
            menuAdapter.getData().get(openTempPos).menuRes = R.mipmap.icon_gongkai;
            menuAdapter.getData().get(openTempPos).menuName = "??????";
        }
        menuAdapter.notifyItemChanged(openTempPos, 99);

    }

    private void showSelectDialog(String liveRoomRecordId) {
        final String[] items = new String[]{"????????????", "????????????"};
        if (showSelectDialog == null) {
            showSelectDialog = new QMUIDialog.MenuDialogBuilder(getActivity())
                    .addItems(items, (dialog, which) -> {
                        Toast.makeText(getActivity(), "???????????? " + items[which], Toast.LENGTH_SHORT).show();
                        if (which == 0) {
                            //?????????????????????
                            startLiveZhuboActivity();
                        } else {
                            //?????????????????????
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

    private void showListPop() {
        List<ListPopMenuBean> menus = new ArrayList<>();
        menus.add(new ListPopMenuBean("????????????????????????????????????", getResources().getColor(R.color.gray_999), 14));
        menus.add(new ListPopMenuBean("????????????", 0, 16));
        menus.add(new ListPopMenuBean("????????????", 0, 16));
        if (listBottomPopup == null) {
            listBottomPopup = new ListBottomPopup(getActivity(), menus);
        }
        listBottomPopup.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (position == 1) {
                    //????????????
                    mLiveInitInfo.setHasLocation(true);
                    if (lat != 0 && lon != 0) {
                        binding.tvLoc.setText(cityName + poiName);
                    }
                } else if (position == 2) {


                    YesOrNoDialog yesOrNoDialog = new YesOrNoDialog(getActivity());
                    yesOrNoDialog.setMessageText("???????????????????????????????????????????????????????????????");
                    yesOrNoDialog.setMessageTextSize(17);
                    yesOrNoDialog.setRightBtnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mLiveInitInfo.setHasLocation(false);
                            binding.tvLoc.setText("????????????");
                            yesOrNoDialog.dismissDialog();
                        }
                    });
                    yesOrNoDialog.showDialog();

                }
                listBottomPopup.dismiss();
            }
        });
        listBottomPopup.showPopupWindow();
    }

    private void showOpenRoomPop() {
        List<ListPopMenuBean> menus = new ArrayList<>();
        menus.add(new ListPopMenuBean("??????????????????", 0, 16));
        ListBottomPopup listBottomPopup = new ListBottomPopup(getActivity(), menus);
        listBottomPopup.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (position == 0) {
                    //??????????????????
                    mLiveInitInfo.roomPassword = "";
                    menuAdapter.getData().get(openTempPos).menuRes = R.mipmap.icon_gongkai;
                    menuAdapter.getData().get(openTempPos).menuName = "??????";
                    menuAdapter.notifyItemChanged(openTempPos, 99);
                }
                listBottomPopup.dismiss();
            }
        });
        listBottomPopup.showPopupWindow();
    }

    public int mBeautyStyle = 2;//????????????????????????????????? ???????????????
    public int mBeautyLevel = 4;//??????????????????????????? 0 - 9
    public int mWhitenessLevel = 1;//???????????????????????????
    public int mRuddinessLevel;//???????????????????????????
    public int mBeautypos;
    public int mAllBeautyLevel;

    public ItemInfo mFilterStyle;//????????????


    //????????????????????????????????????
    public void setMoreTextData(String text1, String text2, String text3, String text4) {
        String all = text1 + text2 + text3 + text4;
        SpannableString spannableString = new SpannableString(all);
        spannableString.setSpan(new NoLineColorSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                //????????????
                UniService.startService(getActivity(), Constant.JKZL_MINI_APP_ID, 0x10112, Constant.ZB_SYTK);
            }
        }, text1.length(), (text1 + text2).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_DADA)), 0, text1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_DADA)), (text1 + text2).length(), (text1 + text2 + text3).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.white)), text1.length(), (text1 + text2).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(new NoLineColorSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                //????????????
                UniService.startService(getActivity(), Constant.JKZL_MINI_APP_ID, 0x10112, Constant.ZB_XWGF);
            }
        }, (text1 + text2 + text3).length(), all.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.white)), (text1 + text2 + text3).length(), all.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        binding.tvShow.setHighlightColor(Color.TRANSPARENT);
        binding.tvShow.setMovementMethod(LinkMovementMethod.getInstance());
        binding.tvShow.setText(spannableString);
    }

    public abstract class NoLineColorSpan extends ClickableSpan {

        @Override
        public void onClick(@NonNull View widget) {

        }

        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            ds.setColor(ds.linkColor);
            ds.setUnderlineText(false);
        }
    }

    //????????????
    private void selectImage() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .loadImageEngine(GlideEngine.createGlideEngine()) // ?????????Demo GlideEngine.java
                .maxSelectNum(1)// ????????????????????????
                .isCamera(true)// ????????????????????????
                .previewEggs(true)//??????????????? ????????????????????????????????????(???????????????????????????????????????????????????)
                .previewImage(true)// ?????????????????????
                .enableCrop(false)// ???????????? true or false
                .compress(true)// ?????????????????? ????????????Luban??????
                .isAndroidQTransform(false)//???????????? ??????????????????????????????????????????
                .showCropFrame(false)// ?????????????????????????????? ???????????????????????????false   true or false
                .showCropGrid(false)// ?????????????????????????????? ???????????????????????????false    true or false
                .forResult(PictureConfig.CHOOSE_REQUEST);//????????????onActivityResult code
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // ????????????????????????
                    selectList = PictureSelector.obtainMultipleResult(data);
                    if (selectList != null && selectList.size() > 0) {
                        LocalMedia localMedia = selectList.get(0);
                        // ?????? LocalMedia ??????????????????path
                        // 1.media.getPath(); ?????????path
                        // 2.media.getCutPath();????????????path????????????media.isCut();?????????true
                        // 3.media.getCompressPath();????????????path????????????media.isCompressed();?????????true
                        // 4.media.getOriginalPath()); media.isOriginal());???true?????????????????????
                        // 5.media.getAndroidQToPath();???Android Q?????????????????????????????????????????????????????????????????????
                        // ???????????????????????????????????????????????????????????????????????????????????????

                        // ????????????????????????
                        if (localMedia.isCompressed()) {
                            photoPath = localMedia.getCompressPath();
                        } else if (localMedia.isOriginal()) {
                            photoPath = localMedia.getOriginalPath();
                        } else if (localMedia.isCut()) {
                            photoPath = localMedia.getCutPath();
                        } else {
                            photoPath = localMedia.getRealPath();
                        }
                        // ???????????????
                        if (photoPath == null) {
                            photoPath = localMedia.getAndroidQToPath();
                        }
                        if (photoPath == null) {
                            photoPath = localMedia.getPath();
                        }
                        if (photoPath.contains("content://")) {
                            Uri uri = Uri.parse(photoPath);
                            photoPath = getFilePathByUri(uri, getActivity());
                        }
                        Glide.with(this).load(photoPath).into(binding.ivCover);
                    }
                    break;
            }
        } else if (requestCode == 1001) {
            if (data != null) {
                mLiveInitInfo.roomPassword = data.getStringExtra("pwd");
                int temp = 0;
                for (int i = 0; i < menuAdapter.getData().size(); i++) {
                    if (menuAdapter.getData().get(i).menuName.equals("??????") || menuAdapter.getData().get(i).menuName.equals("??????")) {
                        temp = i;
                    }
                }

                //?????????????????????
                if (!TextUtils.isEmpty(mLiveInitInfo.roomPassword)) {
                    menuAdapter.getData().get(temp).menuRes = R.mipmap.icon_jiami;
                    menuAdapter.getData().get(temp).menuName = "??????";
                    mLiveInitInfo.hasPassword = true;
                } else {
                    menuAdapter.getData().get(temp).menuRes = R.mipmap.icon_gongkai;
                    menuAdapter.getData().get(temp).menuName = "??????";
                    mLiveInitInfo.hasPassword = false;
                }
                menuAdapter.notifyItemChanged(temp, 99);
            }
        } else if (requestCode == 1010) {
            //????????????
            LocationService.startService(getActivity());
        } else if (requestCode == 1011) {
            //????????????-?????????????????????????????????
            boolean hasPre = PermissionUtils.hasPermission(getActivity(), permission);
            //???????????????????????????????????????????????????
            if (hasPre) {
                //????????????
                LocationService.startService(getActivity());
                showListPop();
            } else {
                showToStartDialog();
            }
        }
    }

    public void showToStartDialog() {
        if (yesOrNoDialog == null) {
            yesOrNoDialog = new YesOrNoDialog(getActivity());
            yesOrNoDialog.setMessageText("?????????????????????-????????????");
            yesOrNoDialog.setRightBtnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //???????????????????????????????????????
                    Intent intent = new Intent();
                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    intent.setData(Uri.fromParts("package", getPackageName(), null));
                    startActivityForResult(intent, 1011);
                    yesOrNoDialog.dismissDialog();
                }
            });
        }
        yesOrNoDialog.showDialog();
    }

    private String getFilePathByUri(Uri uri, Context context) {
        // ??? content:// ??????????????????  content://media/external/file/960
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            String path = null;
            String[] projection = new String[]{MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, projection, null,
                    null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    if (columnIndex > -1) {
                        path = cursor.getString(columnIndex);
                    }
                }
                cursor.close();
            }
            return path;
        }
        return null;
    }

    public void onActivityStop() {
        try {
            mLiveRoom.stopBGM();
            mLiveRoom.stopLocalPreview();
            mLiveRoom.stopScreenCapture();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onActivityRestart() {
        mLiveRoom.startLocalPreview(true, binding.videoView);

        if (zbGoodsListPop != null && zbGoodsListPop.isShowing()) {
            if (zbGoodsListPop.toUniApp) {
                zbGoodsListPop.refreshData();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (disposable != null) {
            disposable.dispose();
        }
        if (preDisposable != null) {
            preDisposable.dispose();
        }
        if (subscribe != null) {
            subscribe.dispose();
        }
    }

}
