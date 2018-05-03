package com.zj.fastnet.rx;

import android.net.TrafficStats;

import com.zj.fastnet.common.body.RequestProgressBody;
import com.zj.fastnet.common.body.ResponseProgressBody;
import com.zj.fastnet.common.consts.Const;
import com.zj.fastnet.common.util.CommonUtils;
import com.zj.fastnet.common.util.ConnectionStateManager;
import com.zj.fastnet.common.util.ErrorUtils;
import com.zj.fastnet.common.util.SourceCloseUtils;
import com.zj.fastnet.error.FastNetError;
import com.zj.fastnet.process.FastNetWorking;
import com.zj.fastnet.process.FastResponse;

import java.io.File;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.plugins.RxJavaPlugins;
import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.zj.fastnet.common.consts.Method.DELETE;
import static com.zj.fastnet.common.consts.Method.GET;
import static com.zj.fastnet.common.consts.Method.HEAD;
import static com.zj.fastnet.common.consts.Method.OPTIONS;
import static com.zj.fastnet.common.consts.Method.PATCH;
import static com.zj.fastnet.common.consts.Method.POST;
import static com.zj.fastnet.common.consts.Method.PUT;

/**
 * Created by zhangjun on 2018/4/21.
 */

public class Rx2InternalNetwork {

    /**
     * generate Observable for Simple HTTP Request
     * @param request RxFastRequest
     * @param <T>
     * @return
     */
    public static <T> Observable<T> generateSimpleObservable(RxFastRequest request) {
        Request okhttpRequest;
        Request.Builder builder = new Request.Builder().url(request.getUrl());
        FastNetWorking.addHeadersToRequestBuilder(builder, request);
        RequestBody requestBody;
        switch (request.getMethod()) {
            case GET:
                builder = builder.get();
                break;
            case POST:
                requestBody = request.getRequestBody();
                builder = builder.post(requestBody);
                break;
            case PUT:
                requestBody = request.getRequestBody();
                builder = builder.put(requestBody);
                break;
            case DELETE:
                requestBody = request.getRequestBody();
                builder = builder.delete(requestBody);
                break;
            case HEAD:
                builder = builder.head();
                break;
            case OPTIONS:
                builder = builder.method(Const.OPTIONS, null);
                break;
            case PATCH:
                requestBody = request.getRequestBody();
                builder = builder.patch(requestBody);
                break;
        }

        if (request.getCacheControl() != null) {
            builder.cacheControl(request.getCacheControl());
        }
        okhttpRequest = builder.build();
        if (request.getOkHttpClient() != null) {
            request.setCall(request.getOkHttpClient().newBuilder().cache(request.getOkHttpClient
                    ().cache()).build().newCall(okhttpRequest));
        } else {
            request.setCall(FastNetWorking.getInstance().getOkHttpClient().newCall(okhttpRequest));
        }

        return new SimpleFastObservable<>(request);
    }

    /**
     * generate Observable for File Download Http Request
     * @param request RxFastRequest
     * @param <T>
     * @return
     */
    public static <T> Observable<T> generateDownloadObservable(final RxFastRequest request) {
        Request okHttpRequest;
        Request.Builder builder = new Request.Builder().url(request.getUrl());
        FastNetWorking.addHeadersToRequestBuilder(builder, request);
        builder = builder.get();
        if (request.getCacheControl() != null) {
            builder.cacheControl(request.getCacheControl());
        }
        okHttpRequest = builder.build();

        OkHttpClient okHttpClient;

        if (request.getOkHttpClient() != null) {
            okHttpClient = request.getOkHttpClient()
                    .newBuilder()
                    .cache(FastNetWorking.getInstance().getOkHttpClient().cache())
                    .addNetworkInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Response originalResponse = chain.proceed(chain.request());
                            return originalResponse.newBuilder().body(new ResponseProgressBody(originalResponse.body(),
                                    request.getDownloadProgressListener())).build();
                        }
                    }).build();
        } else {
            okHttpClient = FastNetWorking.getInstance().getOkHttpClient()
                    .newBuilder()
                    .addNetworkInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Response originalResponse = chain.proceed(chain.request());
                            return originalResponse.newBuilder().body(new ResponseProgressBody(originalResponse.body(),
                                    request.getDownloadProgressListener())).build();
                        }
                    }).build();
        }
        request.setCall(okHttpClient.newCall(okHttpRequest));
        return new DownloadNetObservable<>(request);
    }

    /**
     * generate Observable for Multipart HTTP Request
     * @param request
     * @param <T>
     * @return
     */
    public static <T> Observable<T> generateMultipartObservable(final RxFastRequest request) {
        return new MultipartNetObservable<>(request);
    }

    static final class SimpleFastObservable<T> extends Observable<T> {
        private RxFastRequest request;
        private final Call originalCall;

        SimpleFastObservable(RxFastRequest request) {
            this.request = request;
            this.originalCall = request.getCall();
        }

        @Override
        protected void subscribeActual(Observer<? super T> observer) {
            Call call = originalCall.clone();
            observer.onSubscribe(new FastNetDisposable(call));
            boolean doNOtSwallowError = false;
            Response okHttpResponse = null;
            try {
                final long startTime = System.currentTimeMillis();
                final long startBytes = TrafficStats.getTotalRxBytes();
                okHttpResponse = call.execute();
                final long timeTaken = System.currentTimeMillis() - startTime;
                if (okHttpResponse.cacheResponse() == null) {
                    final long finalBytes = TrafficStats.getTotalRxBytes();
                    final long diffBytes;
                    if (startBytes == TrafficStats.UNSUPPORTED || finalBytes == TrafficStats.UNSUPPORTED) {
                        diffBytes = okHttpResponse.body().contentLength();
                    } else {
                        diffBytes = finalBytes - startBytes;
                    }
                    ConnectionStateManager.getInstance().updateBandWidth(diffBytes, timeTaken);
                    CommonUtils.sendAnalytics(request.getDataAnalyticsListener(), timeTaken, (request.getRequestBody()
                            != null &&
                            request.getRequestBody().contentLength() != 0) ?
                            request.getRequestBody().contentLength() : -1, okHttpResponse.body()
                            .contentLength(), false
                    );
                } else if (request.getDataAnalyticsListener() != null) {
                    if (okHttpResponse.networkResponse() == null) {
                        CommonUtils.sendAnalytics(request.getDataAnalyticsListener(), timeTaken, 0, 0, true);
                    } else {
                        CommonUtils.sendAnalytics(request.getDataAnalyticsListener(), timeTaken,
                                (request.getRequestBody() != null && request.getRequestBody().contentLength() != 0) ?
                                        request.getRequestBody().contentLength() : -1, 0, true);
                    }
                }

                if (okHttpResponse.code() >= 400) {
                    if (!call.isCanceled()) {
                        observer.onError(ErrorUtils.getErrorForServerResponse(new FastNetError(okHttpResponse),
                                request, okHttpResponse.code()));
                    }
                } else {
                    FastResponse<T> response = request.parseResponse(okHttpResponse);
                    if (!response.isSuccess()) {
                        if (!call.isCanceled()) {
                            observer.onError(response.getError());
                        }
                    } else {
                        if (!call.isCanceled()) {
                            observer.onNext(response.getResult());
                        }
                        if (!call.isCanceled()) {
                            doNOtSwallowError = true;
                            observer.onComplete();
                        }
                    }
                }
            } catch (IOException e) {
                if (!call.isCanceled()) {
                    observer.onError(ErrorUtils.getErrorForConnection(new FastNetError(e)));
                }
            } catch (Exception e) {
                Exceptions.throwIfFatal(e);
                if (doNOtSwallowError) {
                    RxJavaPlugins.onError(e);
                } else if (!call.isCanceled()) {
                    try {
                        observer.onError(ErrorUtils.getErrorForConnection(new FastNetError()));
                    } catch (Exception e1) {
                        Exceptions.throwIfFatal(e1);
                        RxJavaPlugins.onError(new CompositeException(e, e1));
                    }
                }
            } finally {
                SourceCloseUtils.close(okHttpResponse, request);
            }
        }
    }

    static final class DownloadNetObservable<T> extends Observable<T> {

        private final RxFastRequest request;
        private final Call originalCall;

        DownloadNetObservable(RxFastRequest request) {
            this.request = request;
            this.originalCall = request.getCall();
        }

        @Override
        protected void subscribeActual(Observer<? super T> observer) {
            Call call = originalCall.clone();
            observer.onSubscribe(new FastNetDisposable(call));
            boolean doNotSwallowError = false;
            Response okHttpResponse = null;
            try {
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
                    } else {
                        diffBytes = finalBytes - startBytes;
                    }
                    ConnectionStateManager.getInstance().updateBandWidth(diffBytes, timeTaken);
                    CommonUtils.sendAnalytics(request.getDataAnalyticsListener(), timeTaken, -1, okHttpResponse.body
                            ().contentLength(), false);
                } else if (request.getDataAnalyticsListener() != null) {
                    CommonUtils.sendAnalytics(request.getDataAnalyticsListener(), timeTaken, -1, 0, true);
                }
                if (okHttpResponse.code() >= 400) {
                    if (!call.isCanceled()) {
                        observer.onError(ErrorUtils.getErrorForServerResponse(new FastNetError(okHttpResponse),
                                request, okHttpResponse.code()));
                    }
                } else {
                    if (!call.isCanceled()) {
                        FastResponse<T> response = (FastResponse<T>) FastResponse.success(Const.SUCCESS);
                        observer.onNext(response.getResult());
                    }
                    if (!call.isCanceled()) {
                        doNotSwallowError = true;
                        observer.onComplete();
                    }
                }
            } catch (IOException e) {
                try {
                    File destinationFile = new File(request.getDownloadFilePath() + File.separator + request
                            .getDownloadFileName());
                    if (destinationFile.exists()) {
                        destinationFile.delete();
                    }
                } catch (Exception e1) {
                    e.printStackTrace();
                }
                if (!call.isCanceled()) {
                    observer.onError(ErrorUtils.getErrorForConnection(new FastNetError(e)));
                }
            } catch (Exception e2) {
                Exceptions.throwIfFatal(e2);
                if (doNotSwallowError) {
                    RxJavaPlugins.onError(e2);
                } else if (!call.isCanceled()) {
                    try {
                        observer.onError(ErrorUtils.getErrorForConnection(new FastNetError(e2)));
                    } catch (Exception e1) {
                        Exceptions.throwIfFatal(e1);
                        RxJavaPlugins.onError(new CompositeException(e2, e1));
                    }
                }
            } finally {
                SourceCloseUtils.close(okHttpResponse, request);
            }
        }
    }

    static final class MultipartNetObservable<T> extends Observable<T> {
        private final RxFastRequest request;

        MultipartNetObservable(RxFastRequest request) {
            this.request = request;
        }

        @Override
        protected void subscribeActual(Observer<? super T> observer) {
            boolean doNotSwallowError = false;
            Response okHttpResponse = null;
            Request okHttpRequest;
            try {
                Request.Builder builder = new Request.Builder().url(request.getUrl());
                FastNetWorking.addHeadersToRequestBuilder(builder, request);
                final RequestBody requestBody = request.getMultiPartRequestBody();
                final long requestBodyLength = requestBody.contentLength();
                builder = builder.post(new RequestProgressBody(requestBody, request.getUploadProgressListener()));
                if (request.getCacheControl() != null) {
                    builder.cacheControl(request.getCacheControl());
                }
                okHttpRequest = builder.build();
                if (request.getOkHttpClient() != null) {
                    request.setCall(request.getOkHttpClient().newBuilder().cache(request
                            .getOkHttpClient().cache()).build().newCall(okHttpRequest));
                } else {
                    request.setCall(FastNetWorking.getInstance().getOkHttpClient().newCall(okHttpRequest));
                }
                observer.onSubscribe(new FastNetDisposable(request.getCall()));
                final long startTime = System.currentTimeMillis();
                okHttpResponse = request.getCall().execute();
                final long timeTaken = System.currentTimeMillis() - startTime;
                if (request.getDataAnalyticsListener() != null) {
                    if (okHttpResponse.cacheResponse() == null) {
                        CommonUtils.sendAnalytics(request.getDataAnalyticsListener(), timeTaken, requestBodyLength,
                                okHttpResponse.body().contentLength(), false);
                    } else {
                        if (okHttpResponse.networkResponse() == null) {
                            CommonUtils.sendAnalytics(request.getDataAnalyticsListener(), timeTaken, 0, 0, true);
                        } else {
                            CommonUtils.sendAnalytics(request.getDataAnalyticsListener(), timeTaken,
                                    requestBodyLength != 0 ? requestBodyLength : -1, 0, true);
                        }
                    }
                }
                if (okHttpResponse.code() >= 400) {
                    if (!request.getCall().isCanceled()) {
                        observer.onError(ErrorUtils.getErrorForServerResponse(new FastNetError(okHttpResponse),
                                request, okHttpResponse.code()));
                    }
                } else {
                    FastResponse<T> response = request.parseResponse(okHttpResponse);
                    if (!response.isSuccess()) {
                        if (!request.getCall().isCanceled()) {
                            observer.onError(response.getError());
                        }
                    } else {
                        if (!request.getCall().isCanceled()) {
                            observer.onNext(response.getResult());
                        }
                        if (!request.getCall().isCanceled()) {
                            doNotSwallowError = true;
                            observer.onComplete();
                        }
                    }
                }
            } catch (IOException e) {
                if (!request.getCall().isCanceled()) {
                    observer.onError(ErrorUtils.getErrorForConnection(new FastNetError(e)));
                }
            } catch (Exception e1) {
                Exceptions.throwIfFatal(e1);
                if (doNotSwallowError) {
                    RxJavaPlugins.onError(e1);
                } else if (!request.getCall().isCanceled()) {
                    try {
                        observer.onError(ErrorUtils.getErrorForConnection(new FastNetError(e1)));
                    } catch (Exception e2) {
                        Exceptions.throwIfFatal(e2);
                        RxJavaPlugins.onError(new CompositeException(e1, e2));
                    }
                }
            } finally {
                SourceCloseUtils.close(okHttpResponse, request);
            }
        }
    }

    private static final class FastNetDisposable implements Disposable {

        private final Call call;

        private FastNetDisposable(Call call) {
            this.call = call;
        }

        @Override
        public void dispose() {
            this.call.cancel();
        }

        @Override
        public boolean isDisposed() {
            return this.call.isCanceled();
        }
    }
}
