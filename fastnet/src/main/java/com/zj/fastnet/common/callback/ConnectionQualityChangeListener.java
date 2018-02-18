package com.zj.fastnet.common.callback;

import com.zj.fastnet.common.consts.ConnectionQuality;

/**
 * Created by zhangjun on 2018/1/20.
 *
 * the callback for network state change
 * <p></p>
 * Note: we can check the speed of Network when @see #onChange called
 */

public interface ConnectionQualityChangeListener {
    void  onChange(ConnectionQuality currentConnectionQuality, int currentBandwidth);
}
