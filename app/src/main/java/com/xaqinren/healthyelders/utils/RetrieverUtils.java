package com.xaqinren.healthyelders.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Environment;

import com.luck.picture.lib.tools.BitmapUtils;
import com.tencent.qcloud.tim.uikit.utils.FileUtil;
import com.tencent.qcloud.ugckit.utils.CoverUtil;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.goldze.mvvmhabit.utils.ImageUtils;

public class RetrieverUtils {
    public interface OnBitmapCall{
        void onBack(String bitmap);
    }

    /**
     * 根据视频路径，生成封面
     * @param path 视频路径
     * @param call 回调
     * @return
     */
    public static void createVideoCover(String path,OnBitmapCall call) {
        Observable.just(path)
                .subscribeOn(Schedulers.io())
                .map(s -> {
                    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                    mmr.setDataSource(path);
                    Bitmap bitmap = mmr.getFrameAtTime((1000 * 1000 + 1L), MediaMetadataRetriever.OPTION_PREVIOUS_SYNC);
                    return bitmap;
                })
                .map(bitmap -> {
                    File directory_pictures = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    return FileUtil.saveBitmap(directory_pictures.getAbsolutePath(), bitmap);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> call.onBack(s));
    }

    /**
     * 裁剪
     *
     * @param path 原图
     * @return 裁剪后的图像
     */
    public static void cropBitmap(String path,OnBitmapCall call) {
        Observable.just(path)
                .subscribeOn(Schedulers.io())
                .map(s -> {
                    Bitmap bitmap = BitmapFactory.decodeFile(s);
                    int w = bitmap.getWidth(); // 得到图片的宽，高
                    int h = bitmap.getHeight();
                    int cropWidth = w - 10;// 裁切后所取的正方形区域边长
                    int cropHeight = h - 10;
                    return Bitmap.createBitmap(bitmap, 5, 5, cropWidth, cropHeight, null, false);
                })
                .map(s -> {
                    File directory_pictures = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    return FileUtil.saveBitmap(directory_pictures.getAbsolutePath(), s);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> call.onBack(s));
    }

}
