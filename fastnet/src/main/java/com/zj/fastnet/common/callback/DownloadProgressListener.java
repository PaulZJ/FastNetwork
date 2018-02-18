package com.zj.fastnet.common.callback;

/**
 * Created by zhangjun on 2018/1/21.
 *
 * the callback to handle download progress
 * <p></p>
 * Note: we can check the download progress when #onProgress called
 */
public interface DownloadProgressListener {
    void onProgress(long bytesDownloaded, long totalBytes);
}
