package com.zj.fastnet.process;

import android.net.TrafficStats;
import android.text.TextUtils;

import com.zj.fastnet.common.consts.Const;
import com.zj.fastnet.common.consts.Method;
import com.zj.fastnet.error.FastNetError;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zhangjun on 2018/1/14.
 */

public final class FastNetWorking {
    private static FastNetWorking mInstance;
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

    public Response doSimpleRequest(FastRequest request) throws FastNetError{
        Request okHttpRequest;
        Response okHttpResponse;
        try {
            Request.Builder builder = new Request.Builder().url(request.getUrl());
            addHeadersToRequestBuilder(builder, request);
            switch (request.getMethod()) {
                case Method.GET:
                    builder = builder.get();
                    break;
                case Method.POST:

                    break;
                case Method.PUT:

                    break;
                case Method.DELETE:

                    break;
                case Method.HEAD:

                    break;
                case Method.OPTIONS:

                    break;
                case Method.PATCH:
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
            }
        }catch (IOException e) {
            throw new FastNetError(e);
        }
        return okHttpResponse;
    }

    /**
     * add headers for Request
     * */
    public void addHeadersToRequestBuilder(Request.Builder builder, FastRequest request) {
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
