package com.zj.fastnet.error;

import com.zj.fastnet.common.consts.Const;

import lombok.Getter;
import lombok.Setter;
import okhttp3.Response;

/**
 * Created by zhangjun on 2018/1/14.
 *
 * the common Error for FastNetWork
 */

public class FastNetError extends Exception {
    @Setter @Getter
    private String errorBody;
    @Setter @Getter
    private int errorCode = 0;
    @Setter @Getter
    private String errorDetail;
    @Getter
    private Response response;

    public FastNetError(){}

    public FastNetError(String message) {
        super(message);
    }

    public FastNetError(Response response) {
        this.response = response;
    }

    public FastNetError(String message, Response response) {
        super(message);
        this.response = response;
    }

    public FastNetError(String message, Throwable throwable) {
        super(message, throwable);
    }

    public FastNetError(String message, Response response, Throwable throwable) {
        super(message, throwable);
        this.response = response;
    }

    public FastNetError(Throwable throwable) {
        super(throwable);
    }

    public FastNetError(Response response, Throwable throwable) {
        super(throwable);
        this.response = response;
    }

    public void setCancellationMessageInError() {
        this.errorDetail = Const.REQUEST_CANCELLED_ERROR;
    }


}
