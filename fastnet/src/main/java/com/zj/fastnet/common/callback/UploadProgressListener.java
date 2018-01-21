package com.zj.fastnet.common.callback;

/**
 * Created by zhangjun on 2018/1/21.
 */

public interface UploadProgressListener {
    void onProgress(long bytesUploaded, long totalBytes);
}
