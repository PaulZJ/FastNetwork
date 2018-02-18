package com.zj.fastnet.common.builder;

import com.zj.fastnet.common.consts.RequestPriority;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by zhangjun on 2018/1/15.
 *
 * the interface for common RequestBuilder
 */

public interface FastRequestBuilder {

    FastRequestBuilder setPriority(RequestPriority priority);

    FastRequestBuilder setTag(Object tag);

    FastRequestBuilder addHeaders(String key, String value);

    FastRequestBuilder addHeaders(Map<String, String> headerMap);

    FastRequestBuilder addHeaders(Object object);

    FastRequestBuilder addQueryParameter(String key, String value);

    FastRequestBuilder addQueryParameter(Map<String, String> queryParameterMap);

    FastRequestBuilder addQueryParameter(Object object);

    FastRequestBuilder addPathParameter(String key, String value);

    FastRequestBuilder addPathParameter(Map<String, String> pathParameterMap);

    FastRequestBuilder addPathParameter(Object object);

    FastRequestBuilder doNotCacheResponse();

    FastRequestBuilder getResponseOnlyIfCached();

    FastRequestBuilder getResponseOnlyFromNetwork();

    FastRequestBuilder setMaxAgeCacheControl(int maxAge, TimeUnit timeUnit);

    FastRequestBuilder setMaxStaleCacheControl(int maxStale, TimeUnit timeUnit);

    FastRequestBuilder setExecutor(Executor executor);

    FastRequestBuilder setOkHttpClient(OkHttpClient okHttpClient);

    FastRequestBuilder setUserAgent(String userAgent);
}
