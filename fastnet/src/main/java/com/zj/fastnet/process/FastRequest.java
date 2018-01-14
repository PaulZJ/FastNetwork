package com.zj.fastnet.process;

import android.graphics.Bitmap;

import com.zj.fastnet.common.Method;
import com.zj.fastnet.common.RequestType;
import com.zj.fastnet.common.ResponseType;
import com.zj.fastnet.error.FastNetError;
import com.zj.fastnet.kernel.Core;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import lombok.Getter;
import lombok.Setter;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okio.Okio;

/**
 * Created by zhangjun on 2018/1/10.
 */

public class FastRequest {
    @Setter @Getter
    private boolean isRunning;
    private boolean isCancelled;
    private boolean isDelivered;
    @Setter @Getter
    private String userAgent;
    private @RequestType int requestType;
    private @Method int method;
    private String url;
    @Setter @Getter
    private CacheControl cacheControl = null;
    @Setter @Getter
    private OkHttpClient okHttpClient = null;
    @Setter @Getter
    private Call call;

    private @ResponseType String responseType;

    private Executor mExecutor = null;



    private HashMap<String, List<String>> mHeadersMap = new HashMap<>();
    private HashMap<String, String> mPathParameterMap = new HashMap<>();
    private HashMap<String, List<String>> mQueryParameterMap = new HashMap<>();


    public void setRequestType(@RequestType int requestType) {
        this.requestType = requestType;
    }

    public @RequestType int getRequestType() {
        return requestType;
    }

    public void setResponseType(@ResponseType String responseType) {
        this.responseType = responseType;
    }

    public @ResponseType String getResponseType() {
        return this.responseType;
    }

    public void setMethod(@Method int method) {
        this.method = method;
    }

    public @Method int getMethod() {
        return  method;
    }

    public Headers getHeaders() {
        Headers.Builder builder = new Headers.Builder();
        try {
            if (mHeadersMap != null) {
                Set<Map.Entry<String, List<String>>> entries = mHeadersMap.entrySet();
                for (Map.Entry<String, List<String>> entry : entries) {
                    String name = entry.getKey();
                    List<String> list = entry.getValue();
                    if (list != null) {
                        for (String value : list) {
                            builder.add(name, value);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.build();
    }

    public String getUrl() {
        String tempUrl = url;
        for (HashMap.Entry<String, String> entry : mPathParameterMap.entrySet()) {
            tempUrl = tempUrl.replace("{" + entry.getKey() + "}", String.valueOf(entry.getValue()));
        }
        HttpUrl.Builder urlBuilder = HttpUrl.parse(tempUrl).newBuilder();
        if (mQueryParameterMap != null) {
            Set<Map.Entry<String, List<String>>> entries = mQueryParameterMap.entrySet();
            for (Map.Entry<String, List<String>> entry : entries) {
                String name = entry.getKey();
                List<String> list = entry.getValue();
                if (list != null) {
                    for (String value : list) {
                        urlBuilder.addQueryParameter(name, value);
                    }
                }
            }
        }
        return urlBuilder.build().toString();
    }

    public FastResponse parseResponse(Response response) {
        switch (responseType) {
            case ResponseType.JSON_OBJECT:
                break;
            case ResponseType.JSON_ARRAY:
                break;
            case ResponseType.STRING:
                break;
            case ResponseType.BITMAP:
                break;
            case ResponseType.PARSED:
                break;
            case ResponseType.PREFETCH:
                break;
        }
        return null;
    }

    public void deliverResponse(final FastResponse response) {
        try {
            isDelivered = true;
            if (!isCancelled) {
                if (mExecutor != null) {
                    mExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            deliverSuccessResponse(response);
                        }
                    });
                } else {
                    Core.getInstance().getExecutorSupplier().executorForMainThreadTask().execute(new Runnable() {
                        public void run() {
                            deliverSuccessResponse(response);
                        }
                    });
                }
            } else {
                FastNetError fastNetError = new FastNetError();
                fastNetError.setCancellationMessageInError();
                fastNetError.setErrorCode(0);
                deliverErrorResponse(fastNetError);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deliverSuccessResponse(FastResponse response) {

    }

    private void deliverErrorResponse(FastNetError fastNetError) {

    }

    public void finish() {
//        destroy();
//        ANRequestQueue.getInstance().finish(this);
    }

    public FastNetError parseNetworkError(FastNetError error) {
        try {
            if (error.getResponse() != null && error.getResponse().body() != null && error.getResponse().body()
                    .source() != null) {
                error.setErrorBody(Okio.buffer(error.getResponse().body().source()).readUtf8());
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        return error;
    }

    public void handleOkHttpResponse(final Response response) {

    }
}
