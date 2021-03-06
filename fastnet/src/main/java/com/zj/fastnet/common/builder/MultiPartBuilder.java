package com.zj.fastnet.common.builder;

import com.zj.fastnet.common.consts.RequestPriority;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.OkHttpClient;

/**
 * Created by zhangjun on 2018/1/15.
 *
 * the builder for multi upload HTTP Request
 */

public class MultiPartBuilder<T extends MultiPartBuilder> implements FastRequestBuilder {
    private RequestPriority priority = RequestPriority.MEDIUM;
    private String url;
    private Object tag;
    private HashMap<String, List<String>> headersMap = new HashMap<>();
    private HashMap<String, String> multiPartParameterMap = new HashMap<>();
    private HashMap<String, List<String>> queryParameterMap = new HashMap<>();
    private HashMap<String, String> pathParameterMap = new HashMap<>();
    private HashMap<String, File> multiPartFileMap = new HashMap<>();
    private CacheControl cacheControl;
    private int percentageThresholdForCancelling = 0;
    private Executor executor;
    private OkHttpClient okHttpClient;
    private String userAgent;
    private String customContentType;

    public MultiPartBuilder(String url) {
        this.url = url;
    }

    @Override
    public T setPriority(RequestPriority priority) {
        this.priority = priority;
        return (T) this;
    }

    @Override
    public T setTag(Object tag) {
        this.tag = tag;
        return (T) this;
    }

    @Override
    public T addHeaders(String key, String value) {
        List<String> list = headersMap.get(key);
        if(null == list) {
            list = new ArrayList<>();
            headersMap.put(key, list);
        }
        if(!list.contains(value)) {
            list.add(value);
        }
        return (T) this;
    }

    @Override
    public T addHeaders(Map<String, String> headerMap) {
        if (null != headerMap) {
            for (HashMap.Entry<String, String> entry: headerMap.entrySet()) {
                addHeaders(entry.getKey(), entry.getValue());
            }
        }
        return (T) this;
    }

    @Override
    public T addHeaders(Object object) {
        return (T) this;
    }

    @Override
    public T addQueryParameter(String key, String value) {
        List<String> list = queryParameterMap.get(key);
        if (null == list) {
            list = new ArrayList<>();
            queryParameterMap.put(key, list);
        }
        if (!list.contains(value)) {
            list.add(value);
        }
        return (T) this;
    }

    @Override
    public T addQueryParameter(Map<String, String> queryParameterMap) {
        if (null != queryParameterMap) {
            for (HashMap.Entry<String, String> entry: queryParameterMap.entrySet()) {
                addQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        return (T) this;
    }

    @Override
    public T addQueryParameter(Object object) {
        return (T) this;
    }

    @Override
    public T addPathParameter(String key, String value) {
        pathParameterMap.put(key, value);
        return (T) this;
    }

    @Override
    public T addPathParameter(Map<String, String> pathParameterMap) {
        if (null != pathParameterMap) {
            this.pathParameterMap.putAll(pathParameterMap);
        }
        return (T) this;
    }

    @Override
    public T addPathParameter(Object object) {
        return (T) this;
    }

    @Override
    public T doNotCacheResponse() {
        cacheControl = new CacheControl.Builder().noStore().build();
        return (T) this;
    }

    @Override
    public T getResponseOnlyIfCached() {
        cacheControl = CacheControl.FORCE_CACHE;
        return (T) this;
    }

    @Override
    public T getResponseOnlyFromNetwork() {
        cacheControl = CacheControl.FORCE_NETWORK;
        return (T) this;
    }

    @Override
    public T setMaxAgeCacheControl(int maxAge, TimeUnit timeUnit) {
        cacheControl = new CacheControl.Builder().maxAge(maxAge, timeUnit).build();
        return (T) this;
    }

    @Override
    public T setMaxStaleCacheControl(int maxStale, TimeUnit timeUnit) {
        cacheControl = new CacheControl.Builder().maxStale(maxStale, timeUnit).build();
        return (T) this;
    }

    @Override
    public T setExecutor(Executor executor) {
        this.executor = executor;
        return (T) this;
    }

    @Override
    public T setOkHttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
        return (T) this;
    }

    @Override
    public T setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return (T) this;
    }

    public T addMultipartParameter(String key, String value) {
        multiPartParameterMap.put(key, value);
        return (T) this;
    }

    public T addMultipartParameter(Map<String, String> multiPartParameterMap) {
        if (null != multiPartParameterMap) {
            this.multiPartParameterMap.putAll(multiPartParameterMap);
        }
        return (T) this;
    }

    public T addMultipartParameter(Object object) {
        return (T) this;
    }

    public T addMultipartFile(String key, File file) {
        multiPartFileMap.put(key, file);
        return (T) this;
    }

    public T addMultipartFile(Map<String, File> multiPartFileMap) {
        if (null != multiPartFileMap) {
            this.multiPartFileMap.putAll(multiPartFileMap);
        }
        return (T) this;
    }

    public T setPercentageThresholdForCancelling(int percentageThresholdForCancelling) {
        this.percentageThresholdForCancelling = percentageThresholdForCancelling;
        return (T) this;
    }

    public T setContentType(String contentType) {
        this.customContentType = contentType;
        return (T) this;
    }
}
