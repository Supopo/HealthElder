package com.xaqinren.healthyelders.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.luck.picture.lib.entity.LocalMedia;
import com.xaqinren.healthyelders.global.AppApplication;

/**
 * Created by Lee. on 2021/7/7.
 */
public class PictureSelectorUtils {

    public static String getFilePath(LocalMedia localMedia) {
        String photoPath;
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
            photoPath = getFilePathByUri(uri);
        }
        return photoPath;
    }

    private static String getFilePathByUri(Uri uri) {
        // 以 content:// 开头的，比如  content://media/external/file/960
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            String path = null;
            String[] projection = new String[]{MediaStore.Images.Media.DATA};
            Cursor cursor = AppApplication.getContext().getContentResolver().query(uri, projection, null,
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
