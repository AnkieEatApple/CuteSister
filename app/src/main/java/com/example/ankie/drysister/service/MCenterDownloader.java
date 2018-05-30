package com.example.ankie.drysister.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.ankie.drysister.common.Constans;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageExtraInfo;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**********************************************************************
 * 下载的服务中心
 *
 * @author ankie
 * @类名 MCenterDownloader
 * @包名 com.example.ankie.drysister.service
 * @创建日期 2018/5/28
/**********************************************************************/

public class MCenterDownloader implements ImageLoadingListener{
    /**
     * TAG
     */
    private static final String TAG = "MCenterDownloader";
    /**
     * 锁对象
     */
    private final Object mWaitImageDownload = new Object();
    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 类对象
     */
    private MCenterDownloader mCenterDownloader;
    /**
     * Handler
     */
    private Handler mHandler;
    /**
     * 假的ImageView用作下载的
     */
    private ImageView mFakeView;
    /**
     * 需要配置的options
     */
    private DisplayImageOptions options;
    /**
     * Image是否下载成功
     */
    private boolean mImageDownRes = false;

    /**
     * 构造方法
     *
     * @param context   上下文
     */
    public MCenterDownloader(Context context) {
        this.mContext = context;
        mFakeView = new ImageView(context);
        mHandler = new Handler(OutOfUiThreadUtil.getOrCreateThread(Constans.Param.DEFAULT_OUT_OF_UITHREA).getLooper());
    }

    /**
     * 下载图片
     *
     * @param url   URL
     * @return      是否下载成功
     */
    public boolean downloadImage(final String url) {
        if (url == null) {
            return false;
        }
        synchronized (mWaitImageDownload) {
            // TODO 此处必须使用Handler的线程？还得是具有looper的？
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "run: " + "mHandler.post(new Runnable() {" );
                    ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(mContext);
                    ImageLoader.getInstance().init(configuration);
                    options = DisplayImageOptions.createSimple();
//                    options = new DisplayImageOptions.Builder()
//                            .showImageOnLoading(R.drawable.notify_image_none) // 设置图片在下载期间显示的图片
//                            .showImageForEmptyUri(R.drawable.notify_image_none)// 设置图片Uri为空或是错误的时候显示的图片
//                            .showImageOnFail(R.drawable.notify_image_none) // 设置图片加载/解码过程中错误时候显示的图片
//                            .cacheInMemory(true)// 设置下载的图片是否缓存在内存中
//                            .cacheOnDisc(true)// 设置下载的图片是否缓存在SD卡中
//                            .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// 设置图片以如何的编码方式显示
//                            .bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型//
//                            .resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
//                            .build();// 构建完成
                    ImageLoader.getInstance().displayImage(url, null, mFakeView, options, MCenterDownloader.this);
                }
            });
            try {
                mWaitImageDownload.wait(3 * 60 * 1000);
            } catch (InterruptedException e) {
                return false;
            }
        }
        return mImageDownRes;
    }

    @Override
    public void onLoadingStarted(String s, View view) {

    }

    @Override
    public void onLoading(String s, View view, int i) {

    }

    @Override
    public void onLoadingFailed(String s, View view, FailReason failReason, ImageExtraInfo imageExtraInfo) {
        mImageDownRes = false;
        synchronized (mWaitImageDownload) {
            mWaitImageDownload.notify();
        }
    }

    @Override
    public void onLoadingComplete(String s, View view, Bitmap bitmap, ImageExtraInfo imageExtraInfo) {
        Log.e(TAG, "onLoadingComplete: " + "onLoadingComplete");
        mImageDownRes = true;
        synchronized (mWaitImageDownload) {
            mWaitImageDownload.notify();
        }
    }

    @Override
    public void onLoadingCancelled(String s, View view, ImageExtraInfo imageExtraInfo) {
        mImageDownRes = false;
        synchronized (mWaitImageDownload) {
            mWaitImageDownload.notify();
        }
    }

    public static ExecutorService newCachedThreadPool() {
        return new ThreadPoolExecutor(0, 20, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
    }
}
