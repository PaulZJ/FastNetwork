package com.zj.fastnet.common.builder;

import com.zj.fastnet.common.consts.Method;

/**
 * Created by zhangjun on 2018/1/15.
 *
 * the builder for Delete HTTP Request
 */

public class DeleteRequestBuilder extends PostRequestBuilder {

    public DeleteRequestBuilder(String url) {
        super(url, Method.DELETE);
    }
}
