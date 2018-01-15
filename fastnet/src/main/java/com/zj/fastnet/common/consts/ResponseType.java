package com.zj.fastnet.common.consts;

/**
 * Created by zhangjun on 2018/1/14.
 */

public @interface ResponseType {
    String STRING = "string";
    String JSON_OBJECT = "json_obj";
    String JSON_ARRAY = "json_arr";
    String OK_HTTP_RESPONSE = "okhttp_response";
    String BITMAP = "bitmap";
    String PREFETCH = "prefetch";
    String PARSED = "parsed";
}
