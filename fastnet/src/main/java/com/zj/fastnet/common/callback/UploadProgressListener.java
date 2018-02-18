package com.zj.fastnet.common.callback;

/**
 * Created by zhangjun on 2018/1/21.
 *
 * the callback to handle upload progress
 * <p></p>
 * Note: we can check the upload progress
 * when @see UploadProgressListener#onProgress(long, long) called
 */

public interface UploadProgressListener {
    void onProgress(long bytesUploaded, long totalBytes);
}
