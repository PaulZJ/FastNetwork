package com.zj.fastnet.common.handler;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.zj.fastnet.common.callback.DownloadProgressListener;
import com.zj.fastnet.common.consts.Const;
import com.zj.fastnet.common.model.Progress;

/**
 * Created by zhangjun on 2018/1/21.
 *
 * the Main Thread Handler to notify download progress
 *
 */
public class DownloadProgressHandler extends Handler {
    private final DownloadProgressListener listener;

    public DownloadProgressHandler(DownloadProgressListener listener) {
        super(Looper.getMainLooper());
        this.listener = listener;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case Const.UPDATE:
                if (null != listener) {
                    final Progress progress = (Progress) msg.obj;
                    listener.onProgress(progress.currentBytes, progress.totalBytes);
                }
                break;
             default:
                 super.handleMessage(msg);
        }
    }
}
