package com.zj.fastnet.common.builder;

import com.zj.fastnet.common.consts.Method;

/**
 * Created by zhangjun on 2018/1/15.
 *
 * the builder for Options HTTP Request
 */

public class OptionsRequestBuilder extends GetRequestBuilder {

    public OptionsRequestBuilder(String url) {
        super(url, Method.OPTIONS);
    }
}
