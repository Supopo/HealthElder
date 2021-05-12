package com.xaqinren.healthyelders.moduleLiteav.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.tv.TvView;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.nostra13.dcloudimageloader.utils.L;
import com.tencent.qcloud.tim.uikit.utils.ImageUtil;
import com.tencent.qcloud.ugckit.UGCKit;
import com.tencent.qcloud.ugckit.UGCKitConstants;
import com.tencent.qcloud.ugckit.module.effect.VideoEditerSDK;
import com.tencent.qcloud.ugckit.utils.BitmapUtils;
import com.tencent.qcloud.xiaoshipin.play.TCVideoPreviewActivity;
import com.tencent.ugc.TXVideoEditConstants;
import com.tencent.ugc.TXVideoEditer;
import com.tencent.ugc.TXVideoInfoReader;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityChooseVideoCoverBinding;
import com.xaqinren.healthyelders.moduleLiteav.viewModel.ChooseVideoCoverViewModel;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.widget.VideoPublishCoverLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.utils.ImageUtils;
/**
 * 选择封面
 */
public class ChooseVideoCoverActivity extends BaseActivity<ActivityChooseVideoCoverBinding, ChooseVideoCoverViewModel> implements VideoPublishCoverLayout.OnCoverChangeListener {
    private String mVideoPath = null;
    private String mCoverPath = null;
    int thumbnailCount = 10;  //可以根据视频时长生成缩略图个数
    private List<Bitmap> bitmaps = new ArrayList<>();
    private List<Long> timeMsList = new ArrayList<>();


    private TXVideoEditer.TXThumbnailListener mThumbnailListener = new TXVideoEditer.TXThumbnailListener() {
        @Override
        public void onThumbnail(int index, long timeMs, final Bitmap bitmap) {
            //将缩略图放入图片控件上
            LogUtils.e("ChooseVideoCoverActivity", "生成一张预览图\t" + index + "\tbitmapSize\t"+bitmap.getByteCount());
            bitmaps.add(bitmap);
            timeMsList.add(timeMs);
            if (index == thumbnailCount - 1) {
                addCoverToLayout();
            }
        }
    };

    private TXVideoEditer.TXThumbnailListener mCoverListener = new TXVideoEditer.TXThumbnailListener() {
        @Override
        public void onThumbnail(int index, long timeMs, final Bitmap bitmap) {
            LogUtils.e("ChooseVideoCoverActivity", "生成一张预览图\t" + index + "\tbitmapSize\t"+bitmap.getByteCount());
            runOnUiThread(()->{
                dismissDialog();
                binding.coverListView.setEnabled(true);
                binding.coverView.setImageBitmap(bitmap);
            });
        }
    };

    private TXVideoEditer txVideoEditer;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_choose_video_cover;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        setStatusBarTransparent();
        setStatusBar(getResources().getColor(R.color.black));
        mVideoPath = getIntent().getStringExtra(UGCKitConstants.VIDEO_PATH);
        mCoverPath = getIntent().getStringExtra(UGCKitConstants.VIDEO_COVERPATH);

        binding.cancel.setOnClickListener(view -> finish());
        binding.save.setOnClickListener(view -> {
            //选择成功
            saveBitmapCover();
        });
        showDialog();
        showEditer();
    }

    @Override
    protected void onDestroy() {
        txVideoEditer.release();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        txVideoEditer.cancel();
        txVideoEditer.stopPlay();
        txVideoEditer.deleteLastEffect();
        txVideoEditer.deleteAllEffect();
        txVideoEditer.setThumbnailListener(null);
        VideoEditerSDK.getInstance().clear();
        super.onBackPressed();
    }

    private void showEditer() {
        TXVideoEditConstants.TXVideoInfo info = TXVideoInfoReader.getInstance(UGCKit.getAppContext()).getVideoFileInfo(mVideoPath);
        VideoEditerSDK wrapper = VideoEditerSDK.getInstance();
        wrapper.initSDK();
        txVideoEditer = wrapper.getEditer();
        if (txVideoEditer == null) {
            VideoEditerSDK.getInstance().initSDK();
            txVideoEditer = VideoEditerSDK.getInstance().getEditer();
        }
        VideoEditerSDK.getInstance().setTXVideoInfo(info);
        VideoEditerSDK.getInstance().setCutterStartTime(0,info.duration);
        VideoEditerSDK.getInstance().setVideoPath(mVideoPath);
        txVideoEditer.setVideoPath(mVideoPath);
        txVideoEditer.setThumbnailListener(mThumbnailListener);
        TXVideoEditConstants.TXThumbnail thumbnail = new TXVideoEditConstants.TXThumbnail();
        thumbnail.count = thumbnailCount;
        thumbnail.width = 100;   // 输出缩略图宽
        thumbnail.height = 100;  // 输出缩略图高
        txVideoEditer.setThumbnail(thumbnail);
        txVideoEditer.processVideo();

    }

    private void saveBitmapCover(){
        new Thread(() -> {
            binding.coverView.setDrawingCacheEnabled(true);
            binding.coverView.buildDrawingCache();
            Bitmap bitmap = binding.coverView.getDrawingCache();
            try {
                String name = "cover" + System.currentTimeMillis() + ".png";
                ImageUtils.saveImage(this, name, bitmap);
                String path = getFilesDir() + File.separator + name;
                LogUtils.i("CoverPath", path);
                runOnUiThread(()->{
                    Intent intent = new Intent();
                    intent.putExtra("path", path);
                    setResult(Activity.RESULT_OK,intent);
                    finish();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void addCoverToLayout() {
        runOnUiThread(()->{
            TXVideoEditConstants.TXVideoInfo info = TXVideoInfoReader.getInstance(UGCKit.getAppContext()).getVideoFileInfo(mVideoPath);
            binding.coverListView.setOnCoverChangeListener(this);
            binding.coverListView.setMaxSize(thumbnailCount);
            binding.coverListView.setData(bitmaps);
            binding.coverListView.setLiftRightMarginDp((int) getResources().getDimension(R.dimen.dp_19));
            binding.coverListView.layout();
            onChange(0);
        });
    }


    @Override
    public void onChange(int index) {
        List<Long> list = new ArrayList<>();
        list.add(timeMsList.get(index));
        showDialog();
        txVideoEditer.getThumbnail(list, 360, 640, false , mCoverListener);
    }
}
