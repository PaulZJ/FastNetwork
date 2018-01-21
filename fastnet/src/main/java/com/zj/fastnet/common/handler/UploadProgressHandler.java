package com.zj.fastnet.common.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.zj.fastnet.common.callback.UploadProgressListener;
import com.zj.fastnet.common.consts.Const;
import com.zj.fastnet.common.model.Progress;

/**
 * Created by zhangjun on 2018/1/21.
 */

public class UploadProgressHandler extends Handler {
    private final UploadProgressListener uploadProgressListener;

    public UploadProgressHandler(UploadProgressListener listener) {
        super(Looper.getMainLooper());
        uploadProgressListener = listener;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case Const.UPDATE:
                if (null != uploadProgressListener) {
                    final Progress progress = (Progress) msg.obj;
                    uploadProgressListener.onProgress(progress.currentBytes, progress.totalBytes);
                }
                break;
            default:
                super.handleMessage(msg);
        }
    }
}
