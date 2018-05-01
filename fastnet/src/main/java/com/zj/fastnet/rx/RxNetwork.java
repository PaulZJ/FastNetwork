package com.zj.fastnet.rx;

import com.google.gson.reflect.TypeToken;
import com.zj.fastnet.common.builder.GetRequestBuilder;
import com.zj.fastnet.common.builder.PostRequestBuilder;
import com.zj.fastnet.common.consts.Method;
import com.zj.fastnet.common.consts.RequestType;

import java.util.Map;

/**
 * Created by zhangjun on 2018/1/5.
 */

public class RxNetwork {
    private volatile static RxNetwork mInstance = null;
    private RxNetwork() {
    }

    public static RxNetwork getInstance() {
        if (null == mInstance) {
            synchronized (RxNetwork.class) {
                if (null == mInstance) {
                    mInstance = new RxNetwork();
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
     * */
    public RxFastRequest callForRxData(@Method int method, String url, Map<String, String> params) {
        RxFastRequest request = null;
        switch (method) {
            case Method.GET:
                GetRequestBuilder getBuilder = new GetRequestBuilder(url);
                if (params != null) {
                    getBuilder.addQueryParameter(params);
                }
                request = getBuilder.buildWithRx();
                request.setRequestType(RequestType.SIMPLE);
                break;
            case Method.POST:
                PostRequestBuilder postBuilder = new PostRequestBuilder(url);
                if (params != null) {
                    postBuilder.addBodyParameter(params);
                }
                request.setRequestType(RequestType.SIMPLE);
                request = postBuilder.buildWithRx();
                break;
        }

        return request;
    }

/*    *//**
     * call for Json Data with FastNetwork, parsing Json data with Gson(Default)
     *
     * @param method Http Method defined in {@link Method}
     * @param url the string url for Request
     * @param params the request params for request
     * *//*
    public <T> RxFastRequest callForJsonData(@Method int method, String url, Map<String, String> params, TypeToken<T>
            typeToken) {
        RxFastRequest request = null;
        switch (method) {
            case Method.GET:
                GetRequestBuilder getBuilder = new GetRequestBuilder(url);
                if (params != null) {
                    getBuilder.addQueryParameter(params);
                }
                request = getBuilder.buildWithRx();
                request.setRequestType(RequestType.SIMPLE);
                break;
            case Method.POST:
                PostRequestBuilder postBuilder = new PostRequestBuilder(url);
                if (params != null) {
                    postBuilder.addBodyParameter(params);
                }
                request = postBuilder.buildWithRx();
                request.setRequestType(RequestType.SIMPLE);
                break;
        }

        return request;
    }*/

}
