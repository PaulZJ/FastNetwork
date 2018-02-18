package com.zj.fastnet.common.consts;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.zj.fastnet.common.consts.RequestType.DOWNLOAD;
import static com.zj.fastnet.common.consts.RequestType.MULTIPART;
import static com.zj.fastnet.common.consts.RequestType.SIMPLE;

/**
 * Created by zhangjun on 2018/1/14.
 *
 * the interface for RequestType
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({SIMPLE, DOWNLOAD, MULTIPART})
public @interface RequestType {
    int SIMPLE = 0;
    int DOWNLOAD = 1;
    int MULTIPART = 2;
}
