package com.xaqinren.healthyelders.moduleZhiBo.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.IDCardParams;
import com.baidu.ocr.sdk.model.IDCardResult;
import com.bumptech.glide.Glide;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.ActivityStartRenzhengBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleZhiBo.viewModel.StartRenZheng2ViewModel;
import com.xaqinren.healthyelders.moduleZhiBo.viewModel.StartRenZhengViewModel;
import com.xaqinren.healthyelders.uniApp.UniService;
import com.xaqinren.healthyelders.uniApp.UniUtil;
import com.xaqinren.healthyelders.uniApp.bean.UniEventBean;
import com.xaqinren.healthyelders.utils.GlideEngine;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;

/**
 * Created by Lee. on 2021/4/24.
 * ?????????????????????1
 */
public class StartRenZhengActivity extends BaseActivity<ActivityStartRenzhengBinding, StartRenZhengViewModel> {
    private List<LocalMedia> selectList;
    private String photoPath;
    private String zmPath;
    private String fmPath;
    private int selectType;//1?????? 2??????
    private boolean isAgree = true;
    private Bundle bundle = new Bundle();
    private Handler handler;
    private Disposable uniSubscribe;
    private Disposable subscribe;


    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_start_renzheng;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        handler = new Handler();
        tvTitle.setText("????????????");
        setMoreTextData("?????????????????????", "???????????????????????????????????????????????????", "???", "?????????????????????????????????????????????");
        initEvent();
        initAccessToken();
    }

    private void initEvent() {
        //??????????????????
        binding.rlSelect.setOnClickListener(lis -> {
            isAgree = !isAgree;
            if (isAgree) {
                binding.ivSelect.setBackgroundResource(R.mipmap.radbox_sel);
            } else {
                binding.ivSelect.setBackgroundResource(R.mipmap.radbox_nor);
            }
        });
        binding.ivZm.setOnClickListener(lis -> {
            selectType = 1;
            selectImage();
        });
        binding.ivFm.setOnClickListener(lis -> {
            selectType = 2;
            selectImage();
        });
        binding.btnNext.setOnClickListener(lis -> {
            if (!isAgree) {
                ToastUtil.toastShortMessage("????????????????????????");
            }

            if (isSuccess1 && isSuccess2) {
                showDialog();
                viewModel.updatePhoto(zmPath, 1);
            } else if (!isSuccess1) {
                ToastUtil.toastShortMessage("????????????????????????");
            } else if (!isSuccess2) {
                ToastUtil.toastShortMessage("????????????????????????");
            } else {
                ToastUtil.toastShortMessage("??????????????????");
            }


        });

        uniSubscribe = RxBus.getDefault().toObservable(UniEventBean.class).subscribe(event -> {
            if (event != null) {
                if (event.msgId == CodeTable.UNI_RELEASE) {
                    if (event.taskId == 99) {
                        UniUtil.openUniApp(this, event.appId, event.jumpUrl, null, event.isSelfUni);
                    }
                } else if (event.msgId == CodeTable.UNI_RELEASE_FAIL) {
                    //ToastUtils.showShort("?????????????????????");
                }
            }
        });
        RxSubscriptions.add(uniSubscribe);

        subscribe = RxBus.getDefault().toObservable(EventBean.class).subscribe(eventBean -> {
            if (eventBean != null) {
                if (eventBean.msgId == CodeTable.FINISH_ACT && eventBean.content.equals("rz-success")) {
                    finish();
                }
            }
        });
    }


    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.fileUrl1.observe(this, url -> {
            if (!TextUtils.isEmpty(url)) {
                bundle.putString("idCardFrontImage", url);
                showDialog();
                viewModel.updatePhoto(fmPath, 2);
            }
        });
        viewModel.fileUrl2.observe(this, url -> {
            if (!TextUtils.isEmpty(url)) {
                bundle.putString("idCardBackImage", url);
                //?????????
                int key = getIntent().getIntExtra(Constant.REN_ZHENG_TYPE, 0);
                bundle.putInt(Constant.REN_ZHENG_TYPE, key);
                startActivity(StartRenZheng2Activity.class, bundle);
            }
        });
        viewModel.dismissDialog.observe(this, dis -> {
            if (dis != null) {
                if (dis) {
                    dismissDialog();
                }
            }
        });
    }

    /**
     * ???license?????????????????????
     */
    private void initAccessToken() {
        OCR.getInstance(this).initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken accessToken) {
                String token = accessToken.getAccessToken();
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                LogUtils.v(Constant.TAG_LIVE, "licence????????????token??????" + error.getMessage());
            }
        }, getApplicationContext());
    }

    //????????????????????????????????????
    public void setMoreTextData(String text1, String text2, String text3, String text4) {
        String all = text1 + text2 + text3 + text4;
        SpannableString spannableString = new SpannableString(all);
        spannableString.setSpan(new NoLineColorSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                //????????????????????????
                //????????????
                UniService.startService(StartRenZhengActivity.this, Constant.JKZL_MINI_APP_ID, 99, Constant.ZB_SYTK);
            }
        }, text1.length(), (text1 + text2).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.gray_999)), 0, text1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.gray_999)), (text1 + text2).length(), (text1 + text2 + text3).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_252525)), text1.length(), (text1 + text2).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(new NoLineColorSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                //????????????????????????
                //????????????
                UniService.startService(StartRenZhengActivity.this, Constant.JKZL_MINI_APP_ID, 99, Constant.ZB_XWGF);
            }
        }, (text1 + text2 + text3).length(), all.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_252525)), (text1 + text2 + text3).length(), all.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


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
                            photoPath = getFilePathByUri(uri, this);
                        }
                        if (selectType == 1) {
                            zmPath = photoPath;
                            Glide.with(this).load(photoPath).into(binding.ivZm);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    recIDCard(IDCardParams.ID_CARD_SIDE_FRONT, photoPath);
                                }
                            }, 500);
                        } else if (selectType == 2) {
                            fmPath = photoPath;
                            Glide.with(this).load(photoPath).into(binding.ivFm);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    recIDCard(IDCardParams.ID_CARD_SIDE_BACK, photoPath);
                                }
                            }, 500);
                        }
                    }
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        if (subscribe != null) {
            subscribe.dispose();
        }
    }

    private boolean isSuccess1;
    private boolean isSuccess2;

    private void recIDCard(String idCardSide, String filePath) {
        showDialog("?????????");

        IDCardParams param = new IDCardParams();
        param.setImageFile(new File(filePath));
        // ????????????????????????
        param.setIdCardSide(idCardSide);
        // ??????????????????
        param.setDetectDirection(true);
        // ??????????????????????????????0-100, ??????????????????????????????????????????????????? ????????????????????????20
        param.setImageQuality(20);

        OCR.getInstance(this).recognizeIDCard(param, new OnResultListener<IDCardResult>() {
            @Override
            public void onResult(IDCardResult result) {
                dismissDialog();
                if (result != null) {
                    LogUtils.v(Constant.TAG_LIVE, result.toString());
                    if (result.getImageStatus().equals("normal")) {
                        if (selectType == 1) {
                            if (result.getAddress() != null) {
                                bundle.putString("address", result.getAddress().toString());
                            }
                            if (result.getIdNumber() != null) {
                                bundle.putString("idNumber", result.getIdNumber().toString());
                            }
                            if (result.getBirthday() != null) {
                                bundle.putString("birthday", result.getBirthday().toString());
                            }
                            if (result.getName() != null) {
                                bundle.putString("name", result.getName().toString());
                            }
                            if (result.getGender() != null) {
                                bundle.putString("gender", result.getGender().toString());
                            }
                            if (result.getEthnic() != null) {
                                bundle.putString("ethnic", result.getEthnic().toString());
                            }
                            isSuccess1 = true;
                        } else {
                            if (result.getIssueAuthority() != null) {
                                bundle.putString("issueAuthority", result.getIssueAuthority().toString());
                            }
                            if (result.getSignDate() != null) {
                                bundle.putString("signDate", result.getSignDate().toString());
                            }
                            if (result.getExpiryDate() != null) {
                                bundle.putString("expiryDate", result.getExpiryDate().toString());
                            }
                            isSuccess2 = true;
                        }
                    } else {
                        ToastUtil.toastShortMessage("?????????????????????");
                    }
                }
            }

            @Override
            public void onError(OCRError error) {
                dismissDialog();
                LogUtils.v(Constant.TAG_LIVE, error.getMessage());
                ToastUtil.toastShortMessage("?????????????????????");
            }
        });
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


}
