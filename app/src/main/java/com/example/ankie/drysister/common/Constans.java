package com.example.ankie.drysister.common;

/**********************************************************************
 *
 *
 * @author ankie
 * @类名 Constans
 * @包名 com.example.ankie.drysister.common
 * @创建日期 2018/5/29
/**********************************************************************/

public class Constans {
    /**
     * Url
     */
    public static class Url {
        /**
         * URL
         */
        public static String BASE_URL = "http://gank.io/api/data/福利/";
    }

    /**
     * 下载相关
     */
    public static class DownState {
        /**
         * 网络
         */
        public final static int NETWORK = 1;
        /**
         * 本地
         */
        public final static int LOCAL = 2;
    }

    /**
     * 其他的参数
     */
    public static class Param {
        /**
         * 数量
         */
        public final static int NUMBER = 10;
        /**
         * 下载图片的key
         */
        public final static String DEFAULT_OUT_OF_UITHREA = "downloadImage";
        /**
         * 获取Sister的key
         */
        public final static String CONNECT_FETCH_SISTER = "connectFetchSister";
    }

}
