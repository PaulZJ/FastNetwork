package com.zj.fastnet.process;

import com.zj.fastnet.error.FastNetError;

import lombok.Getter;
import lombok.Setter;
import okhttp3.Response;

/**
 * Created by zhangjun on 2018/1/14.
 */

public class FastResponse<T> {
    @Getter
    private final T result;
    @Getter
    private final FastNetError error;
    @Setter @Getter
    private Response okHttpResponse;

    public static <T> FastResponse<T> success(T result) {
        return new FastResponse<>(result);
    }

    public static <T> FastResponse<T> failed(FastNetError error) {
        return new FastResponse<>(error);
    }

    public FastResponse(T result) {
        this.result = result;
        this.error = null;
    }
    public FastResponse(FastNetError error) {
        this.result = null;
        this.error = error;
    }

    public boolean isSuccess() {
        return error == null;
    }

}
