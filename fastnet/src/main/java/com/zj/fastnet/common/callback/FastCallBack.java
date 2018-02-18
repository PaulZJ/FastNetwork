package com.zj.fastnet.common.callback;

import com.zj.fastnet.error.FastNetError;

/**
 * Created by zhangjun on 2018/1/21.
 *
 * Common Callback for Fast Network
 * <p></p>
 * Note: this is a common Callback interface ,
 * T can be JsonArray, JsonObject, Object parsed with Gson,
 * Bitmap, Okhttp Response;
 * FastNetError enclosure the Errors within the Request;
 * @see FastNetError#errorBody
 * @see FastNetError#errorCode
 * @see FastNetError#errorDetail
 * @see FastNetError#response
 *
 */
public interface FastCallBack<T> {
    void onResponse(T response);
    void onError(FastNetError error);
}
