package com.zj.fastnet.common;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.zj.fastnet.common.Method.DELETE;
import static com.zj.fastnet.common.Method.GET;
import static com.zj.fastnet.common.Method.HEAD;
import static com.zj.fastnet.common.Method.OPTIONS;
import static com.zj.fastnet.common.Method.PATCH;
import static com.zj.fastnet.common.Method.POST;
import static com.zj.fastnet.common.Method.PUT;

/**
 * Created by zhangjun on 2018/1/14.
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({GET, POST, PUT, DELETE, HEAD, PATCH, OPTIONS})
public @interface Method {
    int GET = 0;
    int POST = 1;
    int PUT = 2;
    int DELETE = 3;
    int HEAD = 4;
    int PATCH = 5;
    int OPTIONS = 6;
}
