package com.zj.fastnet.common.util;

import com.zj.fastnet.common.callback.DataAnalyticsListener;
import com.zj.fastnet.kernel.Core;

import java.net.FileNameMap;
import java.net.URLConnection;

/**
 * Created by zhangjun on 2018/1/20.
 */

public class CommonUtils {
    public static String getMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    public static void sendAnalytics(final DataAnalyticsListener analyticsListener, final long timeTakenInMills,
                                     final long bytesSent, final long bytesReceived, final boolean isFromCache) {
        Core.getInstance().getExecutorSupplier().executorForMainThreadTask().execute(new Runnable() {
            @Override
            public void run() {
                if (null != analyticsListener) {
                    analyticsListener.onReceived(timeTakenInMills, bytesSent, bytesReceived, isFromCache);
                }
            }
        });
    }
}
