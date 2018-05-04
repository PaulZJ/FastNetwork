package com.zj.fastnet.manager;

import android.graphics.Bitmap;

import com.zj.fastnet.common.builder.GetRequestBuilder;
import com.zj.fastnet.common.builder.PostRequestBuilder;
import com.zj.fastnet.common.callback.DownloadProgressListener;
import com.zj.fastnet.common.callback.FastCallBack;
import com.zj.fastnet.common.consts.Method;
import com.zj.fastnet.common.consts.RequestType;
import com.zj.fastnet.common.consts.ResponseType;
import com.zj.fastnet.process.FastRequest;
import com.zj.fastnet.process.FastRequestQueue;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by zhangjun on 2018/1/27.
 */

public class DefaultHttpManager {
    private static DefaultHttpManager mInstance = null;
    private static boolean isImmediateTaskForSingleRequest = false;
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
     * set FastRequst running in immediateNetTask
     * @return
     */
    public DefaultHttpManager immediate() {
        isImmediateTaskForSingleRequest = true;
        return this;
    }

    /**
     * call for String Data with FastNetwork, without parsing data
     *
     * @param method       Http Method defined in {@link Method}
     * @param url          the string url for Request
     * @param params       the request params for request
     * @param fastCallBack the common callback for handling Response
     */
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
                getRequest.setImmediateNetTask(isImmediateTaskForSingleRequest);
                FastRequestQueue.getInstance().addRequest(getRequest);
                break;
            case Method.POST:
                PostRequestBuilder postBuilder = new PostRequestBuilder(url);
                if (params != null) {
                    postBuilder.addBodyParameter(params);
                }
                FastRequest postRequest = postBuilder.build(fastCallBack);
                postRequest.setResponseType(ResponseType.STRING);
                postRequest.setImmediateNetTask(isImmediateTaskForSingleRequest);
                FastRequestQueue.getInstance().addRequest(postRequest);
                break;
        }
        isImmediateTaskForSingleRequest = false;
    }

    /**
     * call for Json Data with FastNetwork, parsing Json data with Gson(Default)
     *
     * @param method       Http Method defined in {@link Method}
     * @param url          the string url for Request
     * @param params       the request params for request
     * @param fastCallBack the common callback for handling Response
     */
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
                getRequest.setImmediateNetTask(isImmediateTaskForSingleRequest);
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
                postRequest.setImmediateNetTask(isImmediateTaskForSingleRequest);
                FastRequestQueue.getInstance().addRequest(postRequest);
                break;
        }
        isImmediateTaskForSingleRequest = false;
    }

    /**
     * calling Bitmap with FastNetwork
     *
     * @param method       Http Method defined in {@link Method}
     * @param url          the string url for Request
     * @param params       the request params for request
     * @param fastCallBack the common callback for handling Response
     */
    public void callForBitmap(@Method int method, String url, Map<String, String> params, FastCallBack<Bitmap>
            fastCallBack) {
        callForBitmap(method, url, params, 0, 0, fastCallBack);
    }

    /**
     * calling Bitmap with specific height and width with FastNetwork
     *
     * @param method       HTTP Method defined in {@link Method}
     * @param url          the string url for Request
     * @param params       the request params for request
     * @param maxHeight    the specific height
     * @param maxWidth     the specific width
     * @param fastCallBack the common callback for handing Response
     */
    public void callForBitmap(@Method int method, String url, Map<String, String> params, int maxHeight, int
            maxWidth, FastCallBack<Bitmap> fastCallBack) {
        switch (method) {
            case Method.GET:
                GetRequestBuilder getBuilder = new GetRequestBuilder(url);
                if (params != null) {
                    getBuilder.addQueryParameter(params);
                }
                FastRequest<Bitmap> getRequest = getBuilder.build(fastCallBack);
                getRequest.setResponseType(ResponseType.BITMAP);
                getRequest.setImmediateNetTask(isImmediateTaskForSingleRequest);
                getRequest.setBitmapMaxHeight(maxHeight);
                getRequest.setBitmapMaxWidth(maxWidth);
                FastRequestQueue.getInstance().addRequest(getRequest);
                break;
            case Method.POST:
                PostRequestBuilder postBuilder = new PostRequestBuilder(url);
                if (null != params) {
                    postBuilder.addBodyParameter(params);
                }
                FastRequest<Bitmap> postRequest = postBuilder.build(fastCallBack);
                postRequest.setResponseType(ResponseType.BITMAP);
                postRequest.setImmediateNetTask(isImmediateTaskForSingleRequest);
                postRequest.setBitmapMaxHeight(maxHeight);
                postRequest.setBitmapMaxWidth(maxWidth);
                FastRequestQueue.getInstance().addRequest(postRequest);
                break;
        }
        isImmediateTaskForSingleRequest = false;
    }

    /**
     * calling File downloading
     *
     * @param method       HTTP Method defined in {@link Method}
     * @param url          the string url for Request
     * @param params       the request params for Request
     * @param filePath     file path for the download file
     * @param fileName     file name for the download file
     * @param listener     download progress listener
     * @param fastCallBack common callback for request
     */
    public void callForFileDownload(@Method int method, String url, Map<String, String> params, String filePath,
                                    String fileName,
                                    DownloadProgressListener listener, FastCallBack<Void>
                                            fastCallBack) {
        switch (method) {
            case Method.GET:
                GetRequestBuilder getBuilder = new GetRequestBuilder(url);
                if (params != null) {
                    getBuilder.addQueryParameter(params);
                }
                FastRequest<Void> getRequest = getBuilder.build(fastCallBack);
                getRequest.setRequestType(RequestType.DOWNLOAD);
                getRequest.setImmediateNetTask(isImmediateTaskForSingleRequest);
                getRequest.setDownloadProgressListener(listener);
                getRequest.setDownloadFilePath(filePath);
                getRequest.setDownloadFileName(fileName);
                FastRequestQueue.getInstance().addRequest(getRequest);
                break;
            case Method.POST:
                PostRequestBuilder postBuilder = new PostRequestBuilder(url);
                if (null != params) {
                    postBuilder.addBodyParameter(params);
                }
                FastRequest<Void> postRequest = postBuilder.build(fastCallBack);
                postRequest.setRequestType(RequestType.DOWNLOAD);
                postRequest.setImmediateNetTask(isImmediateTaskForSingleRequest);
                postRequest.setDownloadProgressListener(listener);
                postRequest.setDownloadFilePath(filePath);
                postRequest.setDownloadFileName(fileName);
                FastRequestQueue.getInstance().addRequest(postRequest);
                break;
        }
        isImmediateTaskForSingleRequest = false;
    }

}
