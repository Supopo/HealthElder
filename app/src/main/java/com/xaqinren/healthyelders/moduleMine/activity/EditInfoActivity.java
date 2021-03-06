package com.xaqinren.healthyelders.moduleMine.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.configure.PickerOptions;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnTimeSelectChangeListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.contrarywind.view.WheelView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.PictureSelectorActivity;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.style.PictureCropParameterStyle;
import com.luck.picture.lib.style.PictureParameterStyle;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;
import com.tencent.qcloud.ugckit.utils.ScreenUtils;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.ActivityEditInfoBinding;
import com.xaqinren.healthyelders.global.InfoCache;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleMine.adapter.EditInfoAdapter;
import com.xaqinren.healthyelders.moduleMine.bean.EditMenuBean;
import com.xaqinren.healthyelders.moduleMine.viewModel.EditInfoViewModel;
import com.xaqinren.healthyelders.moduleMsg.activity.AddFriendActivity;
import com.xaqinren.healthyelders.modulePicture.activity.PublishTextPhotoActivity;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ListPopMenuBean;
import com.xaqinren.healthyelders.utils.DateUtils;
import com.xaqinren.healthyelders.utils.GlideEngine;
import com.xaqinren.healthyelders.utils.GlideUtil;
import com.xaqinren.healthyelders.utils.IntentUtils;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.utils.MScreenUtil;
import com.xaqinren.healthyelders.utils.PictureSelectorUtils;
import com.xaqinren.healthyelders.utils.UrlUtils;
import com.xaqinren.healthyelders.widget.ListBottomPopup;
import com.xaqinren.healthyelders.widget.SwitchButton;
import com.xaqinren.healthyelders.widget.YesOrNoDialog;
import com.xaqinren.healthyelders.widget.pickerView.cityPicker.CityPickerActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.utils.StringUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.goldze.mvvmhabit.utils.Utils;
import razerdp.basepopup.BasePopupWindow;

public class EditInfoActivity extends BaseActivity<ActivityEditInfoBinding, EditInfoViewModel> {
    private List<EditMenuBean> editMenuBeans = new ArrayList<>();
    private UserInfoBean userInfoBean;
    private EditInfoAdapter editInfoAdapter;
    private TimePickerView pvTime;
    private int REQUEST_GALLERY = 666;
    private int updateType = 0;//0??????1??????2??????3??????
    private String updateValue = "";
    private Disposable disposable;
    private YesOrNoDialog yesOrNoDialog;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_edit_info;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        setTitle("????????????");
        userInfoBean = UserInfoMgr.getInstance().getUserInfo();
        createEditList();
        editInfoAdapter = new EditInfoAdapter();
        editInfoAdapter.setList(editMenuBeans);
        binding.menuList.setLayoutManager(new LinearLayoutManager(this));
        binding.menuList.setNestedScrollingEnabled(false);
        binding.menuList.setAdapter(editInfoAdapter);
        binding.tv1.getPaint().setFakeBoldText(true);
        binding.selAvatar.setOnClickListener(view -> {

            //??????????????????
            disposable = permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                    .subscribe(granted -> {
                        if (granted) {
                            //????????????
                            updateType = 0;
                            selAvatar();
                        } else {
                            ToastUtils.showShort("?????????????????????");
                        }

                    });

        });
        editInfoAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (position == 0) {
                changeName();
            } else if (position == 1) {
                //?????????
                startActivity(MyRecommendCodeActivity.class);
            } else if (position == 2) {
                changeInfo();
            } else if (position == 3) {
                if (userInfoBean.getHasRealName()) {
                    return;
                }
                updateType = 1;
                changeSex();
            } else if (position == 4) {
                if (userInfoBean.getHasRealName()) {
                    return;
                }
                updateType = 2;
                changeBirth();
            } else if (position == 5) {
                updateType = 3;
                changeCity();
            }
        });
        int dp94 = (int) getResources().getDimension(R.dimen.dp_94);
        GlideUtil.intoImageView(this, UrlUtils.resetImgUrl(userInfoBean.getAvatarUrl(), dp94, dp94), binding.avatar);
    }

    private void createEditList() {
        editMenuBeans.add(new EditMenuBean("??????", userInfoBean.getNickname(), true, ""));
        editMenuBeans.add(new EditMenuBean("?????????", userInfoBean.getRecommendedCode(), false, ""));
        editMenuBeans.add(new EditMenuBean("??????", getValue(userInfoBean.getIntroduce(), "????????????"), true, ""));
        editMenuBeans.add(new EditMenuBean("??????", getValue(getSex(userInfoBean.getSex()), "?????????"), userInfoBean.getHasRealName() ? false : true, ""));
        editMenuBeans.add(new EditMenuBean("??????", getValue(userInfoBean.getBirthday(), "?????????"), userInfoBean.getHasRealName() ? false : true, ""));
        editMenuBeans.add(new EditMenuBean("?????????", getValue(userInfoBean.getCityAddress(), "????????????"), true, ""));
    }

    private String getSex(String sex) {
        if (sex == null) {
            return null;
        }
        return sex.equals("MALE") ? "???" : "???";
    }

    private String getValue(String value, String defaultValue) {
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }
        return value;
    }

    private void changeName() {
        startActivityForResult(new Intent(this, EditNameActivity.class), 0x0010);
    }

    private void changeInfo() {
        startActivityForResult(new Intent(this, EditBriefActivity.class), 0x0020);
    }

    private void changeSex() {
        List<ListPopMenuBean> menus = new ArrayList<>();
        menus.add(new ListPopMenuBean("???"));
        menus.add(new ListPopMenuBean("???"));
        ListBottomPopup listBottomPopup = new ListBottomPopup(this, menus);
        listBottomPopup.setOnItemClickListener((adapter, view, position) -> {
            //?????????
            if (position == 0) {
                viewModel.updateSex("MALE");
                updateValue = "MALE";
            } else if (position == 1) {
                viewModel.updateSex("FEMALE");
                updateValue = "FEMALE";
            }
            listBottomPopup.dismiss();
        });
        listBottomPopup.showPopupWindow();
        ScreenUtils.setWindowAlpha(getContext(), 1.0f, 0.6f, 400);
        listBottomPopup.setOnDismissListener(new BasePopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ScreenUtils.setWindowAlpha(getContext(), 0.6f, 1.0f, 200);
            }
        });
    }

    private String birthday;
    boolean hasBirth = false;

    private void changeBirth() {
        Calendar selData = Calendar.getInstance();
        selData.set(2000, 0, 1);
        Calendar startDate = Calendar.getInstance();
        startDate.set(1900, 0, 1);
        Calendar endDate = Calendar.getInstance();
        birthday = "2000-01-01";
        String birth = UserInfoMgr.getInstance().getUserInfo().getBirthday();
        if (birth != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date = simpleDateFormat.parse(birth);
                Calendar instance = Calendar.getInstance();
                instance.setTime(date);
                int year = instance.get(Calendar.YEAR);
                int month = instance.get(Calendar.MONTH);
                int day = instance.get(Calendar.DAY_OF_MONTH);
                selData.set(year, month, day);
                hasBirth = true;
                birthday = birth;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        pvTime = new TimePickerBuilder(this, (date, v) -> {
        })
                .setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
                    @Override
                    public void onTimeSelectChanged(Date date) {
                        birthday = DateUtils.getYMR(date);
                        LogUtils.e(TAG, "data ->" + birthday);
                    }
                })
                .setLayoutRes(R.layout.picker_custom_data_1, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        SwitchButton switchButton = v.findViewById(R.id.sb_menu1);
                        switchButton.setOnCheckedChangeListener((view, isChecked) -> {

                        });
                        if (hasBirth) {
                            switchButton.setChecked(false);
                        }
                        TextView confirm = v.findViewById(R.id.confirm);
                        confirm.setOnClickListener(view -> {
                            updateValue = switchButton.isChecked() ? "" : birthday;
                            viewModel.updateBirthday(
                                    switchButton.isChecked() ? "" : birthday
                            );
                            pvTime.dismiss();
                        });
                    }
                })

                .setType(new boolean[]{true, true, true, false, false, false})// ??????????????????
                .setItemVisibleCount(3)
                .setContentTextSize(20)//??????????????????
                .setTitleText("Title")//????????????
                .setDecorView(binding.rlContainer)
                .setTextColorCenter(getResources().getColor(R.color.color_252525))
                .setTextColorOut(getResources().getColor(R.color.color_DEDEDE))
                .isAlphaGradient(false)
                .setDividerType(WheelView.DividerType.WRAP)
                .setOutSideCancelable(true)//???????????????????????????????????????????????????????????????
                .setLineSpacingMultiplier(3)
                .isCyclic(false)//??????????????????
                .setDate(selData)
                .setRangDate(startDate, endDate)//???????????????????????????
                .setLabel("???", "???", "???", "???", "???", "???")//?????????????????????????????????
                .isCenterLabel(true) //?????????????????????????????????label?????????false?????????item???????????????label???
                .isDialog(false)//??????????????????????????????
                .build();
        pvTime.show();


    }

    private void changeCity() {
        disposable = permissions.request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(granted -> {
                    if (granted) {
                        Intent intent = new Intent();
                        intent.putExtra("show_area", 0);
                        intent.setClass(this, CityPickerActivity.class);
                        startActivityForResult(intent, 0x0030);
                    }else {
                        Intent intent = new Intent();
                        intent.putExtra("show_area", 0);
                        intent.setClass(this, CityPickerActivity.class);
                        startActivityForResult(intent, 0x0030);
                    }
                });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0x0030) {
                if (data != null) {
                    String cityName = data.getStringExtra(CityPickerActivity.KEY_PICKED_CITY);
                    updateValue = cityName;
                    viewModel.updateCity(cityName);
                }
            } else if (requestCode == REQUEST_GALLERY) {
                List<LocalMedia> result = PictureSelector.obtainMultipleResult(data);
                if (result.size() > 0) {
                    String path = PictureSelectorUtils.getFilePath(result.get(0));
                    GlideUtil.intoImageView(this, path, binding.avatar);
                    updateAvatar(path);
                }


            }
        }
    }

    private void updateAvatar(String path) {
        viewModel.uploadFile(path);
    }

    private void selAvatar() {
        List<ListPopMenuBean> menus = new ArrayList<>();
        menus.add(new ListPopMenuBean("??????"));
        menus.add(new ListPopMenuBean("??????????????????"));
        ListBottomPopup listBottomPopup = new ListBottomPopup(this, menus);
        listBottomPopup.setOnItemClickListener((adapter, view, position) -> {
            //?????????
            if (position == 0) {
                toCamera();
            } else if (position == 1) {
                toPhoto();
            }
            listBottomPopup.dismiss();
        });
        listBottomPopup.showPopupWindow();
        ScreenUtils.setWindowAlpha(getContext(), 1.0f, 0.6f, 400);
        listBottomPopup.setOnDismissListener(new BasePopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ScreenUtils.setWindowAlpha(getContext(), 0.6f, 1.0f, 200);
            }
        });

    }

    private void toCamera() {
        int width = (int) MScreenUtil.dp2px(this, 150);
        PictureSelector.create(this)
                .openCamera(PictureMimeType.ofImage())
                .imageEngine(GlideEngine.createGlideEngine()) // ?????????Demo GlideEngine.java
                .maxSelectNum(1)// ????????????????????????
                .isEnableCrop(true)
                .isCompress(true)
                .freeStyleCropEnabled(true)
                .cropImageWideHigh(width, width)
                .isAndroidQTransform(false)//???????????? ??????????????????????????????????????????
                .showCropFrame(false)// ?????????????????????????????? ???????????????????????????false   true or false
                .showCropGrid(false)// ?????????????????????????????? ???????????????????????????false    true or false
                .forResult(REQUEST_GALLERY);//????????????onActivityResult code
    }

    private void toPhoto() {
        int width = (int) MScreenUtil.dp2px(this, 150);
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .imageEngine(GlideEngine.createGlideEngine()) // ?????????Demo GlideEngine.java
                .maxSelectNum(1)// ????????????????????????
                .isCamera(true)// ????????????????????????
                .isPreviewEggs(true)//??????????????? ????????????????????????????????????(???????????????????????????????????????????????????)
                .isPreviewImage(true)// ?????????????????????
                .isEnableCrop(true)// ???????????? true or false
                .freeStyleCropEnabled(true)
                .isCompress(true)// ?????????????????? ????????????Luban??????
                .cropImageWideHigh(width, width)
                .isAndroidQTransform(false)//???????????? ??????????????????????????????????????????
                .showCropFrame(false)// ?????????????????????????????? ???????????????????????????false   true or false
                .showCropGrid(false)// ?????????????????????????????? ???????????????????????????false    true or false
                .forResult(REQUEST_GALLERY);//????????????onActivityResult code
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        String nick = UserInfoMgr.getInstance().getUserInfo().getNickname();
        String introduce = UserInfoMgr.getInstance().getUserInfo().getIntroduce();
        editInfoAdapter.getData().get(0).setValue(nick);
        editInfoAdapter.getData().get(2).setValue(introduce);
        editInfoAdapter.notifyItemRangeChanged(0, 3);
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.requestSuccess.observe(this, aBoolean -> {
            dismissDialog();
        });
        viewModel.fileLiveData.observe(this, s -> {
            updateValue = s;
            viewModel.updateAvatar(s);
        });
        viewModel.status.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    ToastUtil.toastShortMessage("????????????");
                    if (updateType == 0) {
                        UserInfoMgr.getInstance().getUserInfo().setAvatarUrl(updateValue);
                    } else if (updateType == 1) {
                        UserInfoMgr.getInstance().getUserInfo().setSex(updateValue);
                        String sex = getSex(updateValue);
                        editInfoAdapter.getData().get(3).setValue(sex == null ? "?????????" : sex);
                        editInfoAdapter.notifyItemChanged(3);
                    } else if (updateType == 2) {
                        UserInfoMgr.getInstance().getUserInfo().setBirthday(updateValue);
                        editInfoAdapter.getData().get(4).setValue(updateValue);
                        editInfoAdapter.notifyItemChanged(4);
                    } else if (updateType == 3) {
                        UserInfoMgr.getInstance().getUserInfo().setCityAddress(updateValue);
                        editInfoAdapter.getData().get(5).setValue(updateValue);
                        editInfoAdapter.notifyItemChanged(5);
                    }
                    InfoCache.getInstance().setLoginUser(UserInfoMgr.getInstance().getUserInfo());
                } else {
                    ToastUtil.toastShortMessage("????????????");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
        }
    }
}
