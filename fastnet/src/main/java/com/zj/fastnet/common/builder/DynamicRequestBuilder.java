package com.zj.fastnet.common.builder;

import com.zj.fastnet.common.consts.Method;

/**
 * Created by zhangjun on 2018/1/15.
 */

public class DynamicRequestBuilder extends PostRequestBuilder {

    public DynamicRequestBuilder(String url, @Method int method) {
        super(url, method);
    }
}
