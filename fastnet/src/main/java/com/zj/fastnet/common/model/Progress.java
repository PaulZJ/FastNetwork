package com.zj.fastnet.common.model;

import java.io.Serializable;

/**
 * Created by zhangjun on 2018/1/21.
 */

public class Progress implements Serializable {

    public long currentBytes;
    public long totalBytes;

    public Progress(long currentBytes, long totalBytes) {
        this.currentBytes = currentBytes;
        this.totalBytes = totalBytes;
    }

}
