package com.zj.fastnet.process;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.zj.fastnet.common.callback.DataAnalyticsListener;
import com.zj.fastnet.common.callback.DownloadProgressListener;
import com.zj.fastnet.common.callback.FastCallBack;
import com.zj.fastnet.common.callback.UploadProgressListener;
import com.zj.fastnet.common.consts.Const;
import com.zj.fastnet.common.consts.Method;
import com.zj.fastnet.common.consts.RequestType;
import com.zj.fastnet.common.consts.ResponseType;
import com.zj.fastnet.common.convert.ParseManager;
import com.zj.fastnet.common.util.CommonUtils;
import com.zj.fastnet.common.util.ErrorUtils;
import com.zj.fastnet.error.FastNetError;
import com.zj.fastnet.kernel.Core;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import lombok.Getter;
import lombok.Setter;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Okio;

/**
 * Created by zhangjun on 2018/1/10.
 */

public class FastRequest<T> {
    private static final MediaType JSON_MEDIA_TYPE =
            MediaType.parse("application/json; charset=utf-8");
    private static final MediaType MEDIA_TYPE_MARKDOWN =
            MediaType.parse("text/x-markdown; charset=utf-8");

    @Setter @Getter
    private boolean isRunning;
    @Getter
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
    private String applicationJsonString = null;
    private MediaType customMediaType = null;
    private String stringBody = null;
    private File file = null;
    private byte[] bytes = null;
    private Type mType = null;
    @Setter @Getter
    private Object tag;

    private @ResponseType String responseType;

    private Executor mExecutor = null;
    private Future future;


    private FastCallBack<Response> okhttpResponseCallback;
    private FastCallBack<Void> downlaodCompletionCallback;
    @Setter
    private FastCallBack<T> commonCallback;
    @Setter @Getter
    private DownloadProgressListener downloadProgressListener;
    @Setter @Getter
    private UploadProgressListener uploadProgressListener;
    @Setter @Getter
    private DataAnalyticsListener dataAnalyticsListener;
    @Setter @Getter
    private String downloadFilePath;
    @Setter @Getter
    private String downloadFileName;

    private static final Object sDecodeLock = new Object();
    private int bitmapMaxWidth;
    private int bitmapMaxHeight;
    private Bitmap.Config decodeConfig;
    private ImageView.ScaleType imgScaleType;

    private int mPercentageThresholdForCancelling = 0;
    private int mProgress;

    private HashMap<String, List<String>> mHeadersMap = new HashMap<>();
    private HashMap<String, String> mPathParameterMap = new HashMap<>();
    private HashMap<String, List<String>> mQueryParameterMap = new HashMap<>();
    private HashMap<String, String> mBodyParameterMap = new HashMap<>();
    private HashMap<String, String> mUrlEncodedFormBodyParameterMap = new HashMap<>();
    private HashMap<String, String> mMultiPartParameterMap = new HashMap<>();
    private HashMap<String, File> mMultiPartFileMap = new HashMap<>();


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

    /**
     * build Headers for Okhttp with a Map named mHeadersMap
     * */
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

    public RequestBody getRequestBody() {
        if (applicationJsonString != null) {
            if (customMediaType != null) {
                return RequestBody.create(customMediaType, applicationJsonString);
            }
            return RequestBody.create(JSON_MEDIA_TYPE, applicationJsonString);
        }else if (stringBody != null) {
            if (customMediaType != null) {
                return RequestBody.create(customMediaType, stringBody);
            }
            return RequestBody.create(MEDIA_TYPE_MARKDOWN, stringBody);
        }else if (file != null) {
            if (customMediaType != null) {
                return RequestBody.create(customMediaType, file);
            }
            return RequestBody.create(MEDIA_TYPE_MARKDOWN, file);
        }else if (bytes != null) {
            if (customMediaType != null) {
                return RequestBody.create(customMediaType, bytes);
            }
            return RequestBody.create(MEDIA_TYPE_MARKDOWN, bytes);
        }else {
            FormBody.Builder builder = new FormBody.Builder();
            try {
                for (HashMap.Entry<String, String> entry: mBodyParameterMap.entrySet()) {
                    builder.add(entry.getKey(), entry.getValue());
                }
                for (HashMap.Entry<String, String> entry: mUrlEncodedFormBodyParameterMap.entrySet()) {
                    builder.addEncoded(entry.getKey(), entry.getValue());
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
            return builder.build();
        }
    }

    public RequestBody getMultiPartRequestBody() {
        MultipartBody.Builder builder = new MultipartBody
                .Builder()
                .setType(customMediaType == null ? MultipartBody.FORM: customMediaType);
        try {
            for (HashMap.Entry<String, String> entry: mMultiPartParameterMap.entrySet()) {
                builder.addPart(Headers.of("Content-Disposition",
                        "form-data; name=\""+ entry.getKey() + "\""),
                        RequestBody.create(null, entry.getValue()));
            }
            for (HashMap.Entry<String, File > entry: mMultiPartFileMap.entrySet()) {
                String fileName = entry.getValue().getName();
                RequestBody fileBody = RequestBody.create(MediaType.parse(CommonUtils.getMimeType(fileName)),
                                        entry.getValue());
                builder.addPart(Headers.of("Content-Disposition",
                        "form-data; name\"" + entry.getKey() + "\"; filename=\"" + fileName + "\""),
                        fileBody);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return builder.build();
    }

    /**
     * generate URL with base url , mPathParameterMap, mQueryParameterMap
     * */
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

    /**
     * parse basic Okhttp Response to target Response
     * */
    public FastResponse parseResponse(Response response) {
        switch (responseType) {
            case ResponseType.JSON_OBJECT:
                try {
                    JSONObject object = new JSONObject(Okio.buffer(response.body().source()).readUtf8());
                    return new FastResponse(object);
                }catch (Exception e) {
                    return new FastResponse(ErrorUtils.getErrorForParse(new FastNetError()));
                }
            case ResponseType.JSON_ARRAY:
                try {
                    JSONArray array = new JSONArray(Okio.buffer(response.body().source()).readUtf8());
                    return new FastResponse(array);
                }catch (Exception e) {
                    return new FastResponse(ErrorUtils.getErrorForParse(new FastNetError()));
                }
            case ResponseType.STRING:
                try {
                    return new FastResponse(Okio.buffer(response.body().source()).readUtf8());
                } catch (IOException e) {
                    return new FastResponse(ErrorUtils.getErrorForParse(new FastNetError()));
                }
            case ResponseType.BITMAP:
                synchronized (sDecodeLock) {
                    try {
                        return CommonUtils.decodeBitmap(response, bitmapMaxWidth, bitmapMaxHeight,
                                decodeConfig, imgScaleType);
                    }catch (Exception e) {
                        return new FastResponse(ErrorUtils.getErrorForParse(new FastNetError()));
                    }
                }
            case ResponseType.PARSED:
                try {
                    return new FastResponse(ParseManager.getParseFactory()
                                .responseBodyParser(mType).convert(response.body()));
                }catch (Exception e) {
                    return new FastResponse(ErrorUtils.getErrorForParse(new FastNetError()));
                }
            case ResponseType.PREFETCH:
                try {
                    Okio.buffer(response.body().source()).skip(Long.MAX_VALUE);
                    return new FastResponse(Const.PREFETCH);
                }catch (Exception e) {
                    return new FastResponse(ErrorUtils.getErrorForParse(new FastNetError()));
                }
        }
        return null;
    }

    /**
     * handle FastResponse
     * */
    public void deliverResponse(final FastResponse<T> response) {
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

    /**
     * handle response for success
     * */
    private void deliverSuccessResponse(FastResponse<T> response) {
        if (commonCallback != null) {
            commonCallback.onResponse(response.getResult());
        }
    }

    /**
     * handle response for Error
     * */
    private void deliverErrorResponse(FastNetError fastNetError) {
        if (commonCallback != null) {
            commonCallback.onError(fastNetError);
        }
    }

    /**
     * shutdown the FastRequest
     * */
    public void finish() {
        destroy();
        FastRequestQueue.getInstance().finish(this);
    }

    public void destroy() {
        this.commonCallback = null;
        this.okhttpResponseCallback = null;
        this.downlaodCompletionCallback = null;
        this.downloadProgressListener = null;
        this.uploadProgressListener = null;
    }

    public void cancel(boolean forceCancel) {
        try {
            if (forceCancel || mPercentageThresholdForCancelling == 0
                    || mProgress < mPercentageThresholdForCancelling) {
                isCancelled = true;
                isRunning = false;
                if (null != call) {
                    call.cancel();
                }
                if (null != future) {
                    future.cancel(true);
                }
                if (!isDelivered) {
                    deliverError(new FastNetError());
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * add ErrorBody for Network Error
     * */
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

    /**
     * update the state of Completion for the Download Task
     * */
    public void updateDownloadCompletion() {
        isDelivered = true;
        if (null != downlaodCompletionCallback) {
            if (!isCancelled) {
                if (mExecutor != null) {
                    mExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            if (null != downlaodCompletionCallback) {
                                downlaodCompletionCallback.onResponse(null);
                            }
                            finish();
                        }
                    });
                }else {
                    Core.getInstance().getExecutorSupplier().executorForMainThreadTask().execute(new Runnable() {
                        @Override
                        public void run() {
                            if (null != downlaodCompletionCallback) {
                                downlaodCompletionCallback.onResponse(null);
                            }
                            finish();
                        }
                    });
                }
            }else {
                deliverError(new FastNetError());
                finish();
            }
        }else {
            finish();
        }
    }

    /**
     * handle the basic Response for Okhttp
     * */
    public void handleOkHttpResponse(final Response response) {
        isDelivered = true;
        if (!isCancelled) {
            if (mExecutor != null) {
                mExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (null != okhttpResponseCallback) {
                            okhttpResponseCallback.onResponse(response);
                        }
                        finish();
                    }
                });
            }else {
                Core.getInstance().getExecutorSupplier().executorForMainThreadTask().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (null != okhttpResponseCallback) {
                            okhttpResponseCallback.onResponse(response);
                        }
                        finish();
                    }
                });
            }
        }else {
            FastNetError error = new FastNetError();
            error.setCancellationMessageInError();
            error.setErrorCode(0);
            if (null != okhttpResponseCallback) {
                okhttpResponseCallback.onError(error);
            }
            finish();
        }
    }

    /**
     * wrap @see FastNetError with request cancel Error
     * */
    public synchronized void deliverError(FastNetError error) {
        if (!isDelivered) {
            if (isCancelled) {
                error.setCancellationMessageInError();
                error.setErrorCode(0);
            }
            deliverErrorResponse(error);
        }
        isDelivered = true;
    }
}
