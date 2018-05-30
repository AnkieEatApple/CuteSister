package com.example.ankie.drysister.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.ankie.drysister.common.Constans;
import com.example.ankie.drysister.sister.Sister;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**********************************************************************
 * 下载中心服务
 *
 * @author ankie
 * @类名 MCenterDownloaderService
 * @包名 com.example.ankie.drysister.service
 * @创建日期 2018/5/29
/**********************************************************************/

public class MCenterDownloaderService extends BroadcastReceiver{
    /**
     * TAG
     */
    private static final String TAG = "CenterDownloaderService";
    /**
     * sInstance
     */
    private static volatile MCenterDownloaderService sInstance;
    /**
     * 是否已经初始化
     */
    private boolean mInit = false;
    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 下载器对象
     */
    private MCenterDownloader mMCenterDownloader = null;
    /**
     * 连接时间管理？？？？？？？？？？
     */
    private ConnectivityManager mConnManager;
    /**
     * 线程
     */
    private HandleMsgThread mThread;
    /**
     * 下载队列
     */
    private WaitQueue mWaitQueue = new WaitQueue();

    /**
     * 单例模式
     *
     * @param context 上下文
     */
    public static synchronized void initDownloaderService(Context context) {
        if (sInstance == null) {
            sInstance = new MCenterDownloaderService(context);
        }
    }

    /**
     * 单例模式
     *
     * @return sInstance
     */
    public static MCenterDownloaderService getInstance() {
        if (sInstance == null) {
            Log.e(TAG, "getInstance: " + "(sInstance == null)" );
        }
        return sInstance;
    }

    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public MCenterDownloaderService(Context context) {
        this.mContext = context;
        mMCenterDownloader = new MCenterDownloader(context);
        mConnManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     * 保证只初始化一次
     */
    public void init () {
        synchronized (this) {
            if (mInit) {
                return;
            }
            mInit = true;
        }

        // 这个还没弄懂
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.getApplicationContext().registerReceiver(this, intentFilter);

        // 为了保持当前的网络畅通？？？？？
        NetworkInfo.State state = mConnManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();

        // 初始化处理消息线程
        mThread = new HandleMsgThread(mWaitQueue, NetworkInfo.State.CONNECTED == state);
        mThread.setPriority(Thread.MIN_PRIORITY);
        mThread.start();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkInfo.State state = mConnManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        mThread.setNetwork(NetworkInfo.State.CONNECTED == state);
    }

    /**
     * 处理消息，将消息添加到队列中
     * @param sisterList    sisterList
     */
    public void handleMsg(ArrayList<Sister> sisterList) {
        mWaitQueue.addData(sisterList);
    }

    private class HandleMsgThread extends DaemonThread {

        private WaitQueue mWaitQueue;

        boolean result = true;

        private HandleMsgThread(WaitQueue waitQueue, boolean mIsNetworkAvail) {
            super(mIsNetworkAvail);
            this.mWaitQueue = waitQueue;
        }

        @Override
        void doWork() {
            Log.e(TAG, "doWork: " + "void doWork() {" );
            Sister sister = mWaitQueue.getNextData();

            if (sister == null) {
                return;
            }
            Log.e(TAG, "doWork: " + sister.get_id() );
            // 下载该条信息
            if (!mMCenterDownloader.downloadImage(sister.getUrl())) {
                Log.e(TAG, "doWork: " + "" );
                result = false;
            }
            if (result) {
                // 添加下载完成的通知？
                sister.setState(Constans.DownState.LOCAL);      // 网络的图片
            } else {
                sister.setState(Constans.DownState.NETWORK);      // 本地的图片
                mWaitQueue.addData(sister);
            }
        }
    }

    private static class WaitQueue {

        private Queue<Sister> mAddDataQueue = new LinkedList<>();

        Sister getNextData () {
            synchronized (this) {
                Log.e(TAG, "getNextData: " + "getNextData ()" );
                if (mAddDataQueue.size() == 0) {
                    try {
                        this.wait(10 * 60 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    // 如果全是失败记录，等待20s后再继续执行
                    boolean allFailed = true;
                    for (Sister sister : mAddDataQueue) {
                        if (sister.getDownloadCut() > 9) {
                            allFailed = false;
                        }
                    }
                    if (allFailed) {
                        try {
                            this.wait(20 * 60 * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return mAddDataQueue.poll();
        }

        void addData(Sister sister) {
            synchronized (this) {
                mAddDataQueue.add(sister);
                this.notify();
            }
        }

        void addData(List<Sister> sisterList) {
            synchronized (this) {
                mAddDataQueue.addAll(sisterList);
                this.notify();
            }
        }
    }
}
