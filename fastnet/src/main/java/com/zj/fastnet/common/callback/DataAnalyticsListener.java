package com.zj.fastnet.common.callback;

/**
 * Created by zhangjun on 2018/1/20.
 *
 * the callback for network data change
 * <p></p>
 * Note: we can check the data stream size when #onReceived called
 */

public interface DataAnalyticsListener {
    void onReceived(long timeTakenInMills, long bytesSent, long byteReceived, boolean isFromCache);
}
