package com.zj.fastnet.common.body;

import android.support.annotation.Nullable;

import com.zj.fastnet.common.callback.UploadProgressListener;
import com.zj.fastnet.common.consts.Const;
import com.zj.fastnet.common.handler.UploadProgressHandler;
import com.zj.fastnet.common.model.Progress;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by zhangjun on 2018/1/21.
 *
 * the RequestBody recording the bytes that uploaded
 */
public class RequestProgressBody extends RequestBody {
    private final RequestBody requestBody;
    private BufferedSink bufferedSink;
    private UploadProgressHandler uploadProgressHandler;

    public RequestProgressBody(RequestBody requestBody, UploadProgressListener uploadProgressListener) {
        this.requestBody = requestBody;
        if (null != uploadProgressListener) {
            this.uploadProgressHandler = new UploadProgressHandler(uploadProgressListener);
        }
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return requestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if (null == bufferedSink) {
            bufferedSink = Okio.buffer(sink(sink));
        }
        requestBody.writeTo(bufferedSink);
        bufferedSink.flush();
    }

    private Sink sink(Sink sink) {
        return new ForwardingSink(sink) {
            long bytesWritten = 0L;
            long contentLength = 0L;

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (contentLength == 0L) {
                    contentLength = contentLength();
                }
                bytesWritten += byteCount;
                if (null != uploadProgressHandler) {
                    uploadProgressHandler.obtainMessage(Const.UPDATE,
                            new Progress(bytesWritten, contentLength)).sendToTarget();
                }
            }
        };
    }
}
