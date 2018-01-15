package com.zj.fastnet.common.builder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.zj.fastnet.common.consts.Method;
import com.zj.fastnet.common.consts.RequestPriority;

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
 */

public class GetRequestBuilder<T extends GetRequestBuilder> implements FastRequestBuilder {
    private RequestPriority priority = RequestPriority.MEDIUM;
    private @Method int method = Method.GET;
    private String url;
    private Object tag;
    private Bitmap.Config bitmapConfig;
    private BitmapFactory.Options bitmapOptions;
    private int maxWidth;
    private int maxHeight;
    private ImageView.ScaleType scaleType;
    private HashMap<String, List<String>> headersMap = new HashMap<>();
    private HashMap<String, List<String>> queryParameterMap = new HashMap<>();
    private HashMap<String, String> pathParameterMap = new HashMap<>();
    private CacheControl cacheControl;
    private Executor executor;
    private OkHttpClient okHttpClient;
    private String userAgent;

    public GetRequestBuilder(String url) {
        this.url = url;
        this.method = Method.GET;
    }

    public GetRequestBuilder(String url, @Method int method) {
        this.url = url;
        this.method = method;
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
        if (null == list) {
            list = new ArrayList<>();
            headersMap.put(key, list);
        }
        if (!list.contains(value)) {
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
        if (list == null) {
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
        if (queryParameterMap != null) {
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
        if (this.pathParameterMap != null) {
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
}
