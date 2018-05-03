package com.zj.fastnet.common.util;

import com.zj.fastnet.common.consts.ResponseType;
import com.zj.fastnet.process.FastRequest;

import okhttp3.Response;

/**
 * Created by zhangjun on 2018/1/14.
 */

public class SourceCloseUtils {

    /**
     * close Response body
     * @param response
     * @param request
     */
    public static void close(Response response, FastRequest request) {
        if (request.getResponseType() != ResponseType.OK_HTTP_RESPONSE &&
                response != null && response.body() != null &&
                response.body().source() != null) {
            try {
                response.body().source().close();
            } catch (Exception ignore) {

            }
        }
    }
}
