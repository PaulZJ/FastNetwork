package com.zj.fastnet.common.callback;

import com.zj.fastnet.error.FastNetError;

/**
 * Created by zhangjun on 2018/1/21.
 *
 * Common Callback for Fast Network
 */
public interface FastCallBack<T> {
    void onResponse(T response);
    void onError(FastNetError error);
}
