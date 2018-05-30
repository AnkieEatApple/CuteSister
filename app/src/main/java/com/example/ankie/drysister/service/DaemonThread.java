package com.example.ankie.drysister.service;

/**********************************************************************
 * 后台运行的线程基类
 *
 * @author ankie
 * @类名 DaemonThread
 * @包名 com.example.ankie.drysister.service
 * @创建日期 2018/5/29
/**********************************************************************/

abstract class DaemonThread extends Thread {
    /**
     * 网络相关锁
     */
    private final Object mNetworkLock = new Object();
    /**
     * 网络是否畅通
     */
    private volatile boolean mIsNetworkAvail = false;
    /**
     * 线程工作是否需要网络.
     */
    private volatile boolean mQuit = false;

    public DaemonThread(boolean mIsNetworkAvail) {
        this.mIsNetworkAvail = mIsNetworkAvail;
    }

    @Override
    public void run() {
        while (!mQuit) {
            doWork();
        }
    }

    /**
     * 设置网络是否可用,可用时唤醒等待线程.
     *
     * @param networkState 网络状态
     */
    void setNetwork(boolean networkState) {
        synchronized (mNetworkLock) {
            this.mIsNetworkAvail = networkState;
            if (mIsNetworkAvail) {
                mNetworkLock.notify();
            }
        }
    }

    /**
     * 等待网络
     */
    void waitForNetWork() {
        if (!mIsNetworkAvail) {
            synchronized (mNetworkLock) {
                if (!mIsNetworkAvail) {
                    try {
                        mNetworkLock.wait(5 * 60 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    void quitNetwork() {
        this.mQuit = true;
        this.interrupt();
    }


    /**
     * 线程实际执行的任务，与网络有关.
     */
    abstract void doWork();
}
