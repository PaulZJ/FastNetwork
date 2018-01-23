package com.zj.fastnet.common.callback;

/**
 * Created by zhangjun on 2018/1/20.
 *
 * the callback for network data change
 */

public interface DataAnalyticsListener {
    void onReceived(long timeTakenInMills, long bytesSent, long byteReceived, boolean isFromCache);
}
