package com.zj.fastnet.manager;

import android.graphics.Bitmap;

import com.zj.fastnet.common.builder.GetRequestBuilder;
import com.zj.fastnet.common.builder.PostRequestBuilder;
import com.zj.fastnet.common.callback.FastCallBack;
import com.zj.fastnet.common.consts.Method;
import com.zj.fastnet.common.consts.RequestPriority;
import com.zj.fastnet.common.consts.ResponseType;
import com.zj.fastnet.process.FastNetWorking;
import com.zj.fastnet.process.FastRequest;
import com.zj.fastnet.process.FastRequestQueue;
import com.zj.fastnet.process.NetWorkRunnable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.BitSet;
import java.util.Map;

/**
 * Created by zhangjun on 2018/1/27.
 */

public class DefaultHttpManager {
    private static DefaultHttpManager mInstance = null;
    private DefaultHttpManager() {
    }

    public static DefaultHttpManager getInstance() {
        if (null == mInstance) {
            synchronized (DefaultHttpManager.class) {
                if (null == mInstance) {
                    mInstance = new DefaultHttpManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * call for String Data with FastNetwork, without parsing data
     *
     * @param method Http Method defined in {@link Method}
     * @param url the string url for Request
     * @param params the request params for request
     * @param fastCallBack the common callback for handling Response
     * */
    public void callForStringData(@Method int method, String url, Map<String, String> params, FastCallBack<String>
            fastCallBack) {
        switch (method) {
            case Method.GET:
                GetRequestBuilder getBuilder = new GetRequestBuilder(url);
                if (params != null) {
                    getBuilder.addQueryParameter(params);
                }
                FastRequest getRequest = getBuilder.build(fastCallBack);
                getRequest.setResponseType(ResponseType.STRING);
                FastRequestQueue.getInstance().addRequest(getRequest);
                break;
            case Method.POST:
                PostRequestBuilder postBuilder = new PostRequestBuilder(url);
                if (params != null) {
                    postBuilder.addBodyParameter(params);
                }
                FastRequest postRequest = postBuilder.build(fastCallBack);
                postRequest.setResponseType(ResponseType.STRING);
                FastRequestQueue.getInstance().addRequest(postRequest);
                break;
        }
    }

    /**
     * call for Json Data with FastNetwork, parsing Json data with Gson(Default)
     *
     * @param method Http Method defined in {@link Method}
     * @param url the string url for Request
     * @param params the request params for request
     * @param fastCallBack the common callback for handling Response
     * */
    public void callForJsonData(@Method int method, String url, Map<String, String> params, FastCallBack fastCallBack) {
        //get T.class
        Type[] types = fastCallBack.getClass().getGenericInterfaces();
        Type[] typeParams = ((ParameterizedType) types[0]).getActualTypeArguments();
        switch (method) {
            case Method.GET:
                GetRequestBuilder getBuilder = new GetRequestBuilder(url);
                if (params != null) {
                    getBuilder.addQueryParameter(params);
                }
                FastRequest getRequest = getBuilder.build(fastCallBack);
                getRequest.setMType(typeParams[0]);
                getRequest.setResponseType(ResponseType.PARSED);
                FastRequestQueue.getInstance().addRequest(getRequest);
                break;
            case Method.POST:
                PostRequestBuilder postBuilder = new PostRequestBuilder(url);
                if (params != null) {
                    postBuilder.addBodyParameter(params);
                }
                FastRequest postRequest = postBuilder.build(fastCallBack);
                postRequest.setMType(typeParams[0]);
                postRequest.setResponseType(ResponseType.PARSED);
                FastRequestQueue.getInstance().addRequest(postRequest);
                break;
        }
    }

    /**
     * calling Bitmap with FastNetwork
     *
     * @param method Http Method defined in {@link Method}
     * @param url the string url for Request
     * @param params the request params for request
     * @param fastCallBack the common callback for handling Response
     */
    public void callForBitmap(@Method int method, String url, Map<String, String> params, FastCallBack<Bitmap> fastCallBack) {
        switch (method) {
            case Method.GET:
                GetRequestBuilder getBuilder = new GetRequestBuilder(url);
                if (params != null) {
                    getBuilder.addQueryParameter(params);
                }
                FastRequest<Bitmap> getRequest = getBuilder.build(fastCallBack);
                getRequest.setResponseType(ResponseType.BITMAP);
                FastRequestQueue.getInstance().addRequest(getRequest);
                break;
        }
    }

    /**
     * calling Bitmap with specific height and width with FastNetwork
     *
     * @param method HTTP Method defined in {@link Method}
     * @param url the string url for Request
     * @param params the request params for request
     * @param maxHeight the specific height
     * @param maxWidth the specific width
     * @param fastCallBack the common callback for handing Response
     */
    public void callForBitmap(@Method int method, String url, Map<String, String> params,int maxHeight, int maxWidth, FastCallBack<Bitmap> fastCallBack) {
        switch (method) {
            case Method.GET:
                GetRequestBuilder getBuilder = new GetRequestBuilder(url);
                if (params != null) {
                    getBuilder.addQueryParameter(params);
                }
                FastRequest<Bitmap> getRequest = getBuilder.build(fastCallBack);
                getRequest.setResponseType(ResponseType.BITMAP);
                getRequest.setBitmapMaxHeight(maxHeight);
                getRequest.setBitmapMaxWidth(maxWidth);
                FastRequestQueue.getInstance().addRequest(getRequest);
                break;
        }
    }

}
