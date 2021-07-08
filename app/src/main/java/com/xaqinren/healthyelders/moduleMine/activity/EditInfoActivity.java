package com.xaqinren.healthyelders.moduleMine.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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
import com.xaqinren.healthyelders.widget.pickerView.cityPicker.CityPickerActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.utils.StringUtils;
import me.goldze.mvvmhabit.utils.Utils;
import razerdp.basepopup.BasePopupWindow;

public class EditInfoActivity extends BaseActivity<ActivityEditInfoBinding, EditInfoViewModel> {
    private List<EditMenuBean> editMenuBeans = new ArrayList<>();
    private UserInfoBean userInfoBean;
    private EditInfoAdapter editInfoAdapter;
    private TimePickerView pvTime;
    private int REQUEST_GALLERY = 666;
    private int updateType = 0;//0头像1性别2生日3地址
    private String updateValue = "";

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
        setTitle("编辑资料");
        userInfoBean = UserInfoMgr.getInstance().getUserInfo();
        createEditList();
        editInfoAdapter = new EditInfoAdapter();
        editInfoAdapter.setList(editMenuBeans);
        binding.menuList.setLayoutManager(new LinearLayoutManager(this));
        binding.menuList.setNestedScrollingEnabled(false);
        binding.menuList.setAdapter(editInfoAdapter);
        binding.tv1.getPaint().setFakeBoldText(true);
        binding.selAvatar.setOnClickListener(view -> {
            //选择头像
            updateType = 0;
            selAvatar();
        });
        editInfoAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (position == 0) {
                changeName();
            } else if (position == 1) {
                //健康号
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
        editMenuBeans.add(new EditMenuBean("名字", userInfoBean.getNickname(), true, ""));
        editMenuBeans.add(new EditMenuBean("健康号", userInfoBean.getRecommendedCode(), false, ""));
        editMenuBeans.add(new EditMenuBean("简介", getValue(userInfoBean.getIntroduce(), "点击设置"), true, ""));
        editMenuBeans.add(new EditMenuBean("性别", getValue(getSex(userInfoBean.getSex()), "不展示"), userInfoBean.getHasRealName() ? false : true, ""));
        editMenuBeans.add(new EditMenuBean("生日", getValue(userInfoBean.getBirthday(), "不展示"), userInfoBean.getHasRealName() ? false : true, ""));
        editMenuBeans.add(new EditMenuBean("所在地", getValue(userInfoBean.getCityAddress(), "暂不设置"), true, ""));
    }

    private String getSex(String sex) {
        if (sex == null) {
            return null;
        }
        return sex.equals("MALE") ? "男" : "女";
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
        menus.add(new ListPopMenuBean("男"));
        menus.add(new ListPopMenuBean("女"));
        ListBottomPopup listBottomPopup = new ListBottomPopup(this, menus);
        listBottomPopup.setOnItemClickListener((adapter, view, position) -> {
            //掉接口
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

                .setType(new boolean[]{true, true, true, false, false, false})// 默认全部显示
                .setItemVisibleCount(3)
                .setContentTextSize(20)//滚轮文字大小
                .setTitleText("Title")//标题文字
                .setDecorView(binding.rlContainer)
                .setTextColorCenter(getResources().getColor(R.color.color_252525))
                .setTextColorOut(getResources().getColor(R.color.color_DEDEDE))
                .isAlphaGradient(false)
                .setDividerType(WheelView.DividerType.WRAP)
                .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                .setLineSpacingMultiplier(3)
                .isCyclic(false)//是否循环滚动
                .setDate(selData)
                .setRangDate(startDate, endDate)//起始终止年月日设定
                .setLabel("年", "月", "日", "时", "分", "秒")//默认设置为年月日时分秒
                .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(false)//是否显示为对话框样式
                .build();
        pvTime.show();


    }

    private void changeCity() {
        Intent intent = new Intent();
        intent.putExtra("show_area", 0);
        intent.setClass(this, CityPickerActivity.class);
        startActivityForResult(intent, 0x0030);
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
        menus.add(new ListPopMenuBean("拍摄"));
        menus.add(new ListPopMenuBean("从相册中选取"));
        ListBottomPopup listBottomPopup = new ListBottomPopup(this, menus);
        listBottomPopup.setOnItemClickListener((adapter, view, position) -> {
            //掉接口
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
                .imageEngine(GlideEngine.createGlideEngine()) // 请参考Demo GlideEngine.java
                .maxSelectNum(1)// 最大图片选择数量
                .isEnableCrop(true)
                .isCompress(true)
                .freeStyleCropEnabled(true)
                .cropImageWideHigh(width, width)
                .isAndroidQTransform(false)//开启沙盒 高版本必须选择不然拿不到小图
                .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                .forResult(REQUEST_GALLERY);//结果回调onActivityResult code
    }

    private void toPhoto() {
        int width = (int) MScreenUtil.dp2px(this, 150);
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .imageEngine(GlideEngine.createGlideEngine()) // 请参考Demo GlideEngine.java
                .maxSelectNum(1)// 最大图片选择数量
                .isCamera(true)// 是否显示拍照按钮
                .isPreviewEggs(true)//预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                .isPreviewImage(true)// 是否可预览图片
                .isEnableCrop(true)// 是否裁剪 true or false
                .freeStyleCropEnabled(true)
                .isCompress(true)// 是否压缩图片 使用的是Luban压缩
                .cropImageWideHigh(width, width)
                .isAndroidQTransform(false)//开启沙盒 高版本必须选择不然拿不到小图
                .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                .forResult(REQUEST_GALLERY);//结果回调onActivityResult code
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
                    ToastUtil.toastShortMessage("修改成功");
                    if (updateType == 0) {
                        UserInfoMgr.getInstance().getUserInfo().setAvatarUrl(updateValue);
                    } else if (updateType == 1) {
                        UserInfoMgr.getInstance().getUserInfo().setSex(updateValue);
                        String sex = getSex(updateValue);
                        editInfoAdapter.getData().get(3).setValue(sex == null ? "不展示" : sex);
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
                    ToastUtil.toastShortMessage("修改失败");
                }
            }
        });
    }
}
