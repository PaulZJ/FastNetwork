package com.zj.fastnet.kernel;

/**
 * Created by zhangjun on 2018/1/10.
 *
 * support three Executors
 */

public interface ExecutorSupplier {
    CommonThreadExecutor executorForNetTask();
    CommonThreadExecutor executorForImmediateNetTask();
    MainThreadExecutor executorForMainThreadTask();
}
