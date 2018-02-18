package com.zj.fastnet.common.builder;

import com.zj.fastnet.common.consts.Method;

/**
 * Created by zhangjun on 2018/1/15.
 *
 * the builder for Put HTTP Request
 */

public class PutRequestBuilder extends PostRequestBuilder {

    public PutRequestBuilder(String url) {
        super(url, Method.PUT);
    }
}
