package com.example.ankie.drysister.sister;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.ankie.drysister.service.MCenterDownloaderService;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**********************************************************************
 * 网络请求处理相关类
 *
 * @author ankie
 * @类名 SisterApi
 * @包名 com.example.ankie.drysister
 * @创建日期 2018/5/14
/**********************************************************************/

public class SisterApi {
    /**
     * TAG
     */
    private static final String TAG = "Network";
    /**
     * BASE_URL
     */
    private static final String BASE_URL = "http://gank.io/api/data/福利/";
    /**
     * handler
     */
    private static final int CONNECT_FETCH_IMAGE = 1;
    /**
     * SisterList
     */
    private SisterList mSisterList = null;
    /**
     * 整合之后的Url
     */
    private String mFetchUrl = null;
    /**
     * Handler
     */
    private int mMsgWhat = 0;
    /**
     * 构造方法
     */
    public SisterApi() {
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Object[] objects;
            switch (msg.arg1) {
                case CONNECT_FETCH_IMAGE:
                    objects = (Object[]) msg.obj;
                    mSisterList = (SisterList) objects[0];
                    MCenterDownloaderService.getInstance().handleMsg(mSisterList.getSisterList());
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 从服务器获取SisterList对象
     *
     * @param count     数量
     * @param page      页数
     */
    public void fetchSister(int count, int page) {

        mFetchUrl = BASE_URL + count + "/" + page;
        // todo 此处应该更换成线程池
        new Thread(new Runnable() {
            @Override
            public void run() {
                SisterList sisterList = null;
                try {
                    URL url = new URL(mFetchUrl);
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    connection.setConnectTimeout(5000);
                    connection.setRequestMethod("GET");
                    Log.e(TAG, "fetchSister: "+ "fetchUrl: " + mFetchUrl );
                    int code = connection.getResponseCode();
                    Log.e(TAG, "fetchSister: " + code );
                    if (code == 200) {
                        InputStream in = connection.getInputStream();
                        byte[] data = readFromStream(in);
                        String result = new String(data, "UTF-8");
                        sisterList = parseSister(result);
                    } else {
                        Log.e(TAG, "fetchSister: " + "请求失败" + code );
                    }
                } catch (Exception e) {
                    Log.e(TAG, "fetchSister: " + "catch (Exception e)" );
                    e.printStackTrace();
                }
                Message msg = mHandler.obtainMessage(mMsgWhat);
                msg.arg1 = CONNECT_FETCH_IMAGE;
                msg.obj = new Object[]{sisterList};
                mHandler.sendMessage(msg);
            }
        }).start();
    }

    /**
     * 从输入流读取字节码
     *
     * @param inputStream   输入流
     * @return              字节数组
     * @throws Exception    异常
     */
    private byte[] readFromStream(InputStream inputStream) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        inputStream.close();
        return outputStream.toByteArray();
    }

    /**
     * 解析字符串
     *
     * @param content       字符串的内容
     * @return              SisterList对象
     * @throws Exception    不知道
     */
    private SisterList parseSister(String content) throws Exception {
        Gson gson = new Gson();
        return gson.fromJson(content, SisterList.class);
    }


    public ArrayList<Sister> getSisterList() {
        if (mSisterList != null) {
            return mSisterList.getSisterList();
        }
        return null;
    }
}
