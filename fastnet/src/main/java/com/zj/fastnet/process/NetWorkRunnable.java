package com.zj.fastnet.process;

import com.zj.fastnet.common.util.CommonUtils;
import com.zj.fastnet.common.util.ErrorUtils;
import com.zj.fastnet.common.consts.RequestPriority;
import com.zj.fastnet.common.consts.RequestType;
import com.zj.fastnet.common.consts.ResponseType;
import com.zj.fastnet.common.util.SourceCloseUtils;
import com.zj.fastnet.error.FastNetError;
import com.zj.fastnet.kernel.Core;

import lombok.Getter;
import okhttp3.Response;

/**
 * Created by zhangjun on 2018/1/10.
 *
 * the Runnable for FastRequest
 * @see RequestPriority priority  the priority for FastRequest
 * @see int sequenceNum the index for FastRequest
 * @see FastRequest the FastRequest
 */
public class NetWorkRunnable implements Runnable {
    @Getter
    private final RequestPriority priority;
    public final int sequenceNum;
    public final FastRequest request;

    public NetWorkRunnable(RequestPriority priority, int sequenceNum, FastRequest request) {
        this.priority = priority;
        this.sequenceNum = sequenceNum;
        this.request = request;
    }

    @Override
    public void run() {
        request.setRunning(true);
        //do request
        switch (request.getRequestType()) {
            case RequestType.SIMPLE:
                doSimpleRequest();
                break;
            case RequestType.DOWNLOAD:
                doDownloadRequest();
                break;
            case RequestType.MULTIPART:
                doUploadRequest();
                break;
        }
        request.setRunning(false);
    }

    private void doSimpleRequest() {
        Response okhttpResponse = null;
        try {
            okhttpResponse = FastNetWorking.getInstance().doSimpleRequest(request);
            if (null == okhttpResponse) {
                deliverError(request, ErrorUtils.getErrorForConnection(new FastNetError()));
                return;
            }

            if (request.getResponseType() == ResponseType.OK_HTTP_RESPONSE) {
                request.handleOkHttpResponse(okhttpResponse);
                return;
            }

            if (okhttpResponse.code() >= 400) {
                deliverError(request, ErrorUtils.getErrorForServerResponse(new FastNetError(okhttpResponse),request,
                        okhttpResponse.code()));
                return;
            }

            FastResponse response = request.parseResponse(okhttpResponse);
            if (!response.isSuccess()) {
                deliverError(request, response.getError());
                return;
            }
            response.setOkHttpResponse(okhttpResponse);
            request.deliverResponse(response);
        } catch (FastNetError fastNetError) {
            fastNetError.printStackTrace();
        }finally {
            SourceCloseUtils.close(okhttpResponse, request);
        }
    }

    private void doDownloadRequest() {
        Response okHttpResponse;
        try {
            okHttpResponse = FastNetWorking.getInstance().doDownloadRequest(request);
            if (null == okHttpResponse) {
                deliverError(request, ErrorUtils.getErrorForConnection(new FastNetError()));
                return;
            }
            if (okHttpResponse.code() >= 400) {
                deliverError(request, ErrorUtils.getErrorForServerResponse(new FastNetError(okHttpResponse),request,
                        okHttpResponse.code()));
                return;
            }
            request.updateDownloadCompletion();
        }catch (Exception e) {
            deliverError(request, ErrorUtils.getErrorForConnection(new FastNetError()));
        }
    }

    private void doUploadRequest() {
        Response okHttpResponse = null;
        try {
            okHttpResponse = FastNetWorking.getInstance().doUploadRequest(request);
            if (null == okHttpResponse) {
                deliverError(request, ErrorUtils.getErrorForConnection(new FastNetError()));
                return;
            }
            if (request.getResponseType() == ResponseType.OK_HTTP_RESPONSE) {
                request.handleOkHttpResponse(okHttpResponse);
            }

            if (okHttpResponse.code() >= 400) {
                deliverError(request, ErrorUtils.getErrorForServerResponse(new FastNetError(okHttpResponse),
                        request, okHttpResponse.code()));
                return;
            }
            FastResponse response = request.parseResponse(okHttpResponse);
            if (!response.isSuccess()) {
                deliverError(request, response.getError());
                return;
            }
            response.setOkHttpResponse(okHttpResponse);
            request.deliverResponse(response);
        }catch (Exception e) {
            deliverError(request, ErrorUtils.getErrorForConnection(new FastNetError()));
        }finally {
            SourceCloseUtils.close(okHttpResponse, request);
        }
    }

    private void deliverError(final FastRequest request, final FastNetError error) {
        Core.getInstance().getExecutorSupplier().executorForMainThreadTask().execute(new Runnable() {
            @Override
            public void run() {
                request.deliverError(error);
                request.finish();
            }
        });
    }

}
