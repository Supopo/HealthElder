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
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
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
import com.xaqinren.healthyelders.databinding.ActivityStartRenzhengBinding;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.utils.GlideEngine;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;

/**
 * Created by Lee. on 2021/4/24.
 * 身份证认证页面1
 */
public class StartRenZhengActivity extends BaseActivity<ActivityStartRenzhengBinding, BaseViewModel> {
    private List<LocalMedia> selectList;
    private String photoPath;
    private String zmPath;
    private String fmPath;
    private int selectType;//1正面 2反面
    private boolean isAgree = true;
    private Bundle bundle = new Bundle();
    private Handler handler;


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
        tvTitle.setText("实名认证");
        setMoreTextData("我已阅读并同意", "《健康长老视频号直播功能使用条款》", "及", "《健康长老视频号直播行为规范》");
        initEvent();
        initAccessToken();
    }

    private void initEvent() {
        //选择同意协议
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
            startActivity(StartRenZheng2Activity.class, bundle);
            finish();
        });
    }

    /**
     * 以license文件方式初始化
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
                LogUtils.v(Constant.TAG_LIVE, "licence方式获取token失败" + error.getMessage());
            }
        }, getApplicationContext());
    }

    //设置一段文字多种点击事件
    public void setMoreTextData(String text1, String text2, String text3, String text4) {
        String all = text1 + text2 + text3 + text4;
        SpannableString spannableString = new SpannableString(all);
        spannableString.setSpan(new NoLineColorSpan() {
            @Override
            public void onClick(@NonNull View widget) {
            }
        }, text1.length(), (text1 + text2).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.gray_999)), 0, text1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.gray_999)), (text1 + text2).length(), (text1 + text2 + text3).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_252525)), text1.length(), (text1 + text2).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(new NoLineColorSpan() {
            @Override
            public void onClick(@NonNull View widget) {
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
                .loadImageEngine(GlideEngine.createGlideEngine()) // 请参考Demo GlideEngine.java
                .maxSelectNum(1)// 最大图片选择数量
                .isCamera(true)// 是否显示拍照按钮
                .previewEggs(true)//预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                .previewImage(true)// 是否可预览图片
                .enableCrop(false)// 是否裁剪 true or false
                .compress(true)// 是否压缩图片 使用的是Luban压缩
                .isAndroidQTransform(false)//开启沙盒 高版本必须选择不然拿不到小图
                .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    if (selectList != null && selectList.size() > 0) {
                        LocalMedia localMedia = selectList.get(0);

                        // 例如 LocalMedia 里面返回五种path
                        // 1.media.getPath(); 为原图path
                        // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                        // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                        // 4.media.getOriginalPath()); media.isOriginal());为true时此字段才有值
                        // 5.media.getAndroidQToPath();为Android Q版本特有返回的字段，此字段有值就用来做上传使用
                        // 如果同时开启裁剪和压缩，则取压缩路径为准因为是先裁剪后压缩

                        // 裁剪会出一些问题
                        if (localMedia.isCompressed()) {
                            photoPath = localMedia.getCompressPath();
                        } else if (localMedia.isOriginal()) {
                            photoPath = localMedia.getOriginalPath();
                        } else if (localMedia.isCut()) {
                            photoPath = localMedia.getCutPath();
                        } else {
                            photoPath = localMedia.getRealPath();
                        }
                        // 顺序挺重要
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
                            },500);
                        } else if (selectType == 2) {
                            fmPath = photoPath;
                            Glide.with(this).load(photoPath).into(binding.ivFm);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    recIDCard(IDCardParams.ID_CARD_SIDE_BACK, photoPath);
                                }
                            },500);
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
    }

    private void recIDCard(String idCardSide, String filePath) {
        showDialog("识别中");

        IDCardParams param = new IDCardParams();
        param.setImageFile(new File(filePath));
        // 设置身份证正反面
        param.setIdCardSide(idCardSide);
        // 设置方向检测
        param.setDetectDirection(true);
        // 设置图像参数压缩质量0-100, 越大图像质量越好但是请求时间越长。 不设置则默认值为20
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
                        }
                    } else {
                        ToastUtil.toastShortMessage("身份证识别失败");
                    }
                }
            }

            @Override
            public void onError(OCRError error) {
                dismissDialog();
                LogUtils.v(Constant.TAG_LIVE, error.getMessage());
                ToastUtil.toastShortMessage("身份证识别失败");
            }
        });
    }

    private String getFilePathByUri(Uri uri, Context context) {
        // 以 content:// 开头的，比如  content://media/external/file/960
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
