package com.zj.fastnet.kernel;

import android.os.Process;

import java.util.concurrent.ThreadFactory;

/**
 * Created by zhangjun on 2018/1/12.
 *
 * by default, there are three dispatchers:
 *  netExecutor: executor for common net event
 *  immediateExecuror: executor for high priority net event
 *  mainExecutor: executor for UI Event
 */

public class DefaultExecutorSupplier implements ExecutorSupplier {
    public static final int DEFAULT_MAX_NUM_THREADS = 2 * Runtime.getRuntime().availableProcessors() + 1;
    private CommonThreadExecutor netExecutor;
    private CommonThreadExecutor immediateExecutor;
    private MainThreadExecutor mainExecutor;

    public DefaultExecutorSupplier() {
        ThreadFactory backgroundPriorityThreadFactory = new PriorityThreadFactory(Process.THREAD_PRIORITY_BACKGROUND);
        netExecutor = new CommonThreadExecutor(DEFAULT_MAX_NUM_THREADS, backgroundPriorityThreadFactory);
        immediateExecutor = new CommonThreadExecutor(2, backgroundPriorityThreadFactory);
        mainExecutor = new MainThreadExecutor();
    }

    @Override
    public CommonThreadExecutor executorForNetTask() {
        return netExecutor;
    }

    @Override
    public CommonThreadExecutor executorForImmediateNetTask() {
        return immediateExecutor;
    }

    @Override
    public MainThreadExecutor executorForMainThreadTask() {
        return mainExecutor;
    }
}
