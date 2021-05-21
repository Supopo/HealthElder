package com.xaqinren.healthyelders.moduleLiteav.service;


import com.xaqinren.healthyelders.utils.LogUtils;

import java.io.File;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.http.DownLoadManager;
import me.goldze.mvvmhabit.http.download.ProgressCallBack;

public class DownloadMusic  {

    private static DownloadMusic instance = new DownloadMusic();
    private DownloadMusic(){}
    public static DownloadMusic getInstance(){
        return instance;
    }

    public void init(String rootPath) {
        this.rootPath = rootPath;
        works = new LinkedList<>();
        executor = Executors.newFixedThreadPool(3);
    }

    private String rootPath;
    private String TAG = "DownloadMusic";
    //任务
    private LinkedList<MyDownWork> works;
    //3单线程模式
    private ExecutorService executor;

    class MyDownWork implements Runnable{
        private DownMusicBean url;

        public MyDownWork(DownMusicBean url) {
            this.url = url;
            //未开始状态
            sendMessage(url.id,DownMusicProBean.IDLE, null);
        }

        @Override
        public void run() {
            //等待中
            sendMessage(url.id,DownMusicProBean.WAIT, null);
            File file = new File(rootPath, url.id);
            LogUtils.e(TAG, "url -> " + url.path + "\t开始下载 本地路径 ->" + rootPath + "\\" + url.id + "\t文件尺寸 -> \t" + file.length());
            if (file.exists()) {
                //存在
                boolean same =  file.length() >= url.size;
                if (!same) {
                    //重新下载
                    file.delete();
                    down();
                }else{
                    //下载完成
                    sendMessage(url.id,DownMusicProBean.FULL, null);
                }
            }else{
                //下载
                down();
            }
        }
        //执行下载
        private void down() {
            DownLoadManager.getInstance().load(url.path, new ProgressCallBack(rootPath , url.id) {
                @Override
                public void onSuccess(Object o) {
                    //下载完成
                    sendMessage(url.id,DownMusicProBean.FULL, null);
                }

                @Override
                public void progress(long progress, long total) {
                    //开始下载,正在下载
                    sendMessage(url.id,DownMusicProBean.DOWN, null, progress, total);
                }

                @Override
                public void onError(Throwable e) {
                    //下载错误
                    sendMessage(url.id,DownMusicProBean.ERROR, e.getMessage());
                }
            });
        }

        public void sendMessage(String id , int status , String msg) {
            RxBus.getDefault().post(new DownMusicProBean(id , status, msg));
        }
        public void sendMessage(String id ,int status , String msg , long pro , long to) {
            RxBus.getDefault().post(new DownMusicProBean(id ,status, msg).setProgress(pro, to));
        }
    }
    //添加下载任务
    public void addWork(DownMusicBean url) {
        MyDownWork myDownWork = new MyDownWork(url);
        works.add(myDownWork);
        startWork();
    }
    //执行任务
    public void startWork() {
        if (works.peek()!=null)
            executor.execute(works.poll());
    }
    //剩余任务数量
    public int getWorkLength() {
        return works.size();
    }

    public void stopExecutor() {
        if (!executor.isShutdown()) {
            executor.shutdown();
        }
        executor = null;
        if (works != null) {
            works.clear();
            works = null;
        }
    }
}

