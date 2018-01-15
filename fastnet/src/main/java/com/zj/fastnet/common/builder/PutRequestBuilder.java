package com.zj.fastnet.common.builder;

import com.zj.fastnet.common.consts.Method;

/**
 * Created by zhangjun on 2018/1/15.
 */

public class PutRequestBuilder extends PostRequestBuilder {

    public PutRequestBuilder(String url) {
        super(url, Method.PUT);
    }
}
