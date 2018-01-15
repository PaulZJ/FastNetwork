package com.zj.fastnet.process;

import com.zj.fastnet.common.util.ErrorUtils;
import com.zj.fastnet.common.consts.RequestPriority;
import com.zj.fastnet.common.consts.RequestType;
import com.zj.fastnet.common.consts.ResponseType;
import com.zj.fastnet.common.util.SourceCloseUtils;
import com.zj.fastnet.error.FastNetError;

import lombok.Getter;
import okhttp3.Response;

/**
 * Created by zhangjun on 2018/1/10.
 *
 * the Runnable for FastRequest
 * @param RequestPriority priority  the priority for FastRequest
 * @param int sequenceNum the index for FastRequest
 * @param FastRequest the FastRequest
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
                break;
            case RequestType.MULTIPART:
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

    private void deliverError(final FastRequest request, final FastNetError error) {

    }

}
