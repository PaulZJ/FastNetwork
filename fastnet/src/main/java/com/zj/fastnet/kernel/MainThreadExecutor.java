package com.zj.fastnet.kernel;


import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Created by zhangjun on 2018/1/10.
 *
 * the executor for Runnable which runs on UI Thread
 */

public class MainThreadExecutor implements Executor {
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    public void execute(@NonNull Runnable command) {
        mainHandler.post(command);
    }
}
