package com.zj.fastnet.common.util;

import com.zj.fastnet.common.Const;
import com.zj.fastnet.error.FastNetError;
import com.zj.fastnet.process.FastRequest;

/**
 * Created by zhangjun on 2018/1/14.
 */

public class ErrorUtils {
    public static FastNetError getErrorForConnection(FastNetError error) {
        error.setErrorDetail(Const.CONNECTION_ERROR);
        error.setErrorCode(0);
        return error;
    }

    public static FastNetError getErrorForServerResponse(FastNetError error, FastRequest request, int code) {
        error = request.parseNetworkError(error);
        error.setErrorCode(code);
        error.setErrorDetail(Const.RESPONSE_FROM_SERVER_ERROR);
        return error;
    }
}
