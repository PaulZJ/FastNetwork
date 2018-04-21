package com.zj.fastnet.process;

import android.net.TrafficStats;
import android.text.TextUtils;

import com.zj.fastnet.common.body.RequestProgressBody;
import com.zj.fastnet.common.body.ResponseProgressBody;
import com.zj.fastnet.common.consts.Const;
import com.zj.fastnet.common.consts.Method;
import com.zj.fastnet.common.util.CommonUtils;
import com.zj.fastnet.common.util.ConnectionStateManager;
import com.zj.fastnet.error.FastNetError;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by zhangjun on 2018/1/14.
 */

public final class FastNetWorking {
    private static FastNetWorking mInstance;
    @Getter
    private OkHttpClient okHttpClient;
    private FastNetWorking() {
        okHttpClient = getDefaultClient();
    }

    public static FastNetWorking getInstance() {
        if (null == mInstance) {
            synchronized (FastNetWorking.class) {
                if (null == mInstance) {
                    mInstance = new FastNetWorking();
                }
            }
        }
        return mInstance;
    }

    /**
     * create a default config OkHttpClient
     * */
    public OkHttpClient getDefaultClient() {
        return new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    /**
     * do common Request
     *
     * @param request the FastRequest
     * @return the okhttp Response
     * @throws FastNetError
     */
    public Response doSimpleRequest(FastRequest request) throws FastNetError{
        Request okHttpRequest;
        Response okHttpResponse;
        try {
            Request.Builder builder = new Request.Builder().url(request.getUrl());
            addHeadersToRequestBuilder(builder, request);
            RequestBody requestBody = null;
            switch (request.getMethod()) {
                case Method.GET:
                    builder = builder.get();
                    break;
                case Method.POST:
                    requestBody = request.getRequestBody();
                    builder = builder.post(requestBody);
                    break;
                case Method.PUT:
                    requestBody = request.getRequestBody();
                    builder = builder.put(requestBody);
                    break;
                case Method.DELETE:
                    requestBody = request.getRequestBody();
                    builder = builder.delete(requestBody);
                    break;
                case Method.HEAD:
                    builder = builder.head();
                    break;
                case Method.OPTIONS:
                    builder = builder.method(Const.OPTIONS, null);
                    break;
                case Method.PATCH:
                    requestBody = request.getRequestBody();
                    builder = builder.patch(requestBody);
                    break;
            }
            if (request.getCacheControl() != null) {
                builder = builder.cacheControl(request.getCacheControl());
            }
            okHttpRequest = builder.build();
            if (request.getOkHttpClient() != null) {
                request.setCall(request.getOkHttpClient().newBuilder().cache(okHttpClient.cache()).build().newCall(okHttpRequest));
            } else {
                request.setCall(okHttpClient.newCall(okHttpRequest));
            }
            final long startTime = System.currentTimeMillis();
            final long startBytes = TrafficStats.getTotalRxBytes();
            okHttpResponse = request.getCall().execute();
            final long timeTaken = System.currentTimeMillis() - startTime;
            if (okHttpResponse.cacheResponse() == null) {
                final long finalBytes = TrafficStats.getTotalRxBytes();
                final long diffBytes;
                if (startBytes == TrafficStats.UNSUPPORTED || finalBytes == TrafficStats.UNSUPPORTED) {
                    diffBytes = okHttpResponse.body().contentLength();
                }else {
                    diffBytes = finalBytes - startBytes;
                }
                ConnectionStateManager.getInstance().updateBandWidth(diffBytes, timeTaken);
                CommonUtils.sendAnalytics(request.getDataAnalyticsListener(), timeTaken,
                        (requestBody != null && requestBody.contentLength() !=0)? requestBody.contentLength(): -1,
                        okHttpResponse.body().contentLength(), false);
            }else if (request.getDataAnalyticsListener() != null) {
                if (okHttpResponse.networkResponse() == null)
                    CommonUtils.sendAnalytics(request.getDataAnalyticsListener(), timeTaken, 0, 0, true);
                else
                    CommonUtils.sendAnalytics(request.getDataAnalyticsListener(), timeTaken,
                            (requestBody != null && requestBody.contentLength() !=0)? requestBody.contentLength(): -1,
                            0, true);
            }
        }catch (IOException e) {
            throw new FastNetError(e);
        }
        return okHttpResponse;
    }

    /**
     * do download Request
     * @param request the FastRequest
     * @return the okhttp Response
     * @throws FastNetError
     */
    public Response doDownloadRequest(final FastRequest request) throws FastNetError{
        Request okHttpRequest;
        final Response okHttpResponse;
        try {
            Request.Builder builder = new Request.Builder().url(request.getUrl());
            addHeadersToRequestBuilder(builder, request);
            builder = builder.get();
            if (request.getCacheControl() != null) {
                builder.cacheControl(request.getCacheControl());
            }
            okHttpRequest = builder.build();
            OkHttpClient okHttpClient;
            if (request.getOkHttpClient() != null) {
                okHttpClient = request.getOkHttpClient().newBuilder().cache(this.okHttpClient.cache())
                        .addNetworkInterceptor(new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Response originalResponse = chain.proceed(chain.request());
                                return originalResponse.newBuilder()
                                        .body(new ResponseProgressBody(originalResponse.body(), request
                                                .getDownloadProgressListener()))
                                        .build();
                            }
                        }).build();
            }else {
                okHttpClient = this.okHttpClient.newBuilder()
                        .addNetworkInterceptor(new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Response originalResponse = chain.proceed(chain.request());
                                return originalResponse.newBuilder()
                                        .body(new ResponseProgressBody(originalResponse.body(), request
                                                .getDownloadProgressListener()))
                                        .build();
                            }
                        }).build();
            }
            request.setCall(okHttpClient.newCall(okHttpRequest));
            final long startTime = System.currentTimeMillis();
            final long startBytes = TrafficStats.getTotalRxBytes();
            okHttpResponse = request.getCall().execute();
            CommonUtils.saveFile(okHttpResponse, request.getDownloadFilePath(), request.getDownloadFileName());
            final long timeTaken = System.currentTimeMillis() - startTime;
            if (okHttpResponse.cacheResponse() == null) {
                final long finalBytes = TrafficStats.getTotalRxBytes();
                final long diffBytes;
                if (startBytes == TrafficStats.UNSUPPORTED || finalBytes == TrafficStats.UNSUPPORTED) {
                    diffBytes = okHttpResponse.body().contentLength();
                }else {
                    diffBytes = finalBytes - startBytes;
                }
                ConnectionStateManager.getInstance().updateBandWidth(diffBytes, timeTaken);
                CommonUtils.sendAnalytics(request.getDataAnalyticsListener(), timeTaken, -1,
                        okHttpResponse.body().contentLength(), false);
            }else if (request.getDataAnalyticsListener() != null) {
                CommonUtils.sendAnalytics(request.getDataAnalyticsListener(), timeTaken, -1, 0, true);
            }
        }catch (IOException e){
            try {
                File file = new File(request.getDownloadFilePath() + File.separator + request.getDownloadFileName());
                if (file.exists()) {
                    file.delete();
                }
            }catch (Exception e1) {
                e1.printStackTrace();
            }
            throw new FastNetError(e);
        }

        return okHttpResponse;
    }

    /**
     * do upload Request
     * @param request the FastRequest
     * @return the okhttp Response
     * @throws FastNetError
     */
    public Response doUploadRequest(FastRequest request) throws FastNetError {
        Request okHttpRequest;
        Response okHttpResponse;
        try {
            Request.Builder builder = new Request.Builder().url(request.getUrl());
            addHeadersToRequestBuilder(builder, request);
            final RequestBody requestBody = request.getMultiPartRequestBody();
            final long requestBodyLength = requestBody.contentLength();
            builder = builder.post(new RequestProgressBody(requestBody, request.getUploadProgressListener()));
            if (request.getCacheControl() != null) {
                builder.cacheControl(request.getCacheControl());
            }
            okHttpRequest = builder.build();
            if (request.getOkHttpClient() != null) {
                request.setCall(request.getOkHttpClient()
                    .newBuilder()
                    .cache(this.okHttpClient.cache())
                    .build()
                    .newCall(okHttpRequest));
            }else {
                request.setCall(this.okHttpClient.newCall(okHttpRequest));
            }
            final long startTime = System.currentTimeMillis();
            okHttpResponse = request.getCall().execute();
            final long timeTaken = System.currentTimeMillis() - startTime;
            if (request.getDataAnalyticsListener() != null) {
                if (okHttpResponse.cacheResponse() == null) {
                    CommonUtils.sendAnalytics(request.getDataAnalyticsListener(), timeTaken, requestBodyLength,
                            okHttpResponse.body().contentLength(), false);
                }else {
                    if (okHttpResponse.networkResponse() == null) {
                        CommonUtils.sendAnalytics(request.getDataAnalyticsListener(), timeTaken, 0, 0, true);
                    }else {
                        CommonUtils.sendAnalytics(request.getDataAnalyticsListener(), timeTaken,
                                requestBodyLength != 0 ? requestBodyLength : -1, 0,true);
                    }
                }
            }
        }catch (IOException e) {
            throw new FastNetError(e);
        }
        return okHttpResponse;
    }


    /**
     * add headers for Request
     * */
    public static void addHeadersToRequestBuilder(Request.Builder builder, FastRequest request) {
        if (!TextUtils.isEmpty(request.getUserAgent())) {
            builder.addHeader(Const.HEADER_KEY_FOR_UA, request.getUserAgent());
        }

        Headers requestHeaders = request.getHeaders();
        if (null != requestHeaders) {
            builder.headers(requestHeaders);
            if (!TextUtils.isEmpty(request.getUserAgent()) && !requestHeaders.names().contains(Const
                    .HEADER_KEY_FOR_UA)) {
                builder.addHeader(Const.HEADER_KEY_FOR_UA, request.getUserAgent());
            }
        }
    }
}
