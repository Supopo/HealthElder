package com.xaqinren.healthyelders.utils;

import android.content.Context;
import android.os.Environment;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Boyce on 2017/11/9.
 */

public class DownloadUtil {
    private static DownloadUtil downloadUtil;
    private final OkHttpClient okHttpClient;
    private static final File savePath = Environment.getExternalStorageDirectory().getAbsoluteFile();
    private Call downloadCall = null;

    public static DownloadUtil get() {
        if (downloadUtil == null) {
            downloadUtil = new DownloadUtil();
        }
        return downloadUtil;
    }

    private DownloadUtil() {
        okHttpClient = new OkHttpClient();
    }

    /**
     * @param url      下载连接
     * @param saveFile 储存下载文件的SDCard目录
     * @param listener 下载监听
     */
    public void download(final String url, final File saveFile, final OnDownloadListener listener) {
        if (url.isEmpty() || !url.contains("http")) {
            listener.onDownloadFailed();
            return;
        }
        Request request = new Request.Builder().url(url).build();
        realDownLoad(request, saveFile, listener);
    }

    private void realDownLoad(final Request request, final File saveFile, final OnDownloadListener listener) {
        downloadCall = okHttpClient.newCall(request);
        downloadCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败
                listener.onDownloadFailed();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    fos = new FileOutputStream(saveFile);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        // 下载中
                        listener.onDownloading(progress);
                    }
                    fos.flush();
                    // 下载完成
                    listener.onDownloadSuccess();
                } catch (Exception e) {
                    listener.onDownloadFailed();
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }

    /**
     * @param url
     * @param requestBody FormBody.Builder params=new FormBody.Builder();
     *                    params.add("data", "0");
     *                    requestBody = params.build();
     * @param saveFile
     * @param listener
     */
    public void postDownLoad(final String url, final RequestBody requestBody, final File saveFile, final OnDownloadListener listener, Map<String, String> map) {
        if (url.isEmpty() || !url.contains("http")) {
            listener.onDownloadFailed();
            return;
        }
        Request request = createBuilder(url, requestBody, map).post(requestBody).build();
        realDownLoad(request, saveFile, listener);
    }

    private Request.Builder createBuilder(String url, RequestBody requestBody, Map<String, String> map) {
        Request.Builder builder = new Request.Builder().url(url).post(requestBody);
        if (map != null) {
            for (Map.Entry<String, String> header : map.entrySet()) {
                String key = header.getKey();
                String value = header.getValue();
                builder.addHeader(key, value);
            }
        }
        return builder;
    }

    /**
     * @param saveDir
     * @return
     * @throws IOException 判断下载目录是否存在
     */
    private String isExistDir(String saveDir) throws IOException {
        // 下载位置
        File downloadFile = new File(saveDir);
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile();
        }
        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }

    /**
     * @param url
     * @return 从下载连接中解析出文件名
     */
    @NonNull
    public String getNameFromUrl(String url) {
        return System.currentTimeMillis() + url.substring(url.lastIndexOf("/") + 1);
    }

    public File getSaveFileFromUrl(String url, Context context, String folder) {
        File appDir;
        appDir = new File(Environment.getExternalStorageDirectory().getPath() + "/JKZL/");

        if (!appDir.exists()) {
            appDir.mkdirs();
        }

        File downFolder = new File(appDir, folder);
        if (!downFolder.exists()) {
            downFolder.mkdirs();
        }
        File saveFile = new File(downFolder, getNameFromUrl(url));
        if (saveFile.exists())
            saveFile.delete();
        try {
            saveFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return saveFile;
    }

    public void cancelDownload() {
        if (downloadCall != null)
            downloadCall.cancel();
    }

    public interface OnDownloadListener {
        /**
         * 下载成功
         */
        void onDownloadSuccess();

        /**
         * @param progress 下载进度
         */
        void onDownloading(int progress);

        /**
         * 下载失败
         */
        void onDownloadFailed();
    }
}
