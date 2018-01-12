package com.zj.fastnet.kernel;

import lombok.Getter;

/**
 * Created by zhangjun on 2018/1/12.
 *
 * the core central controller
 */

public class Core {
    private static Core mInstance = null;
    @Getter
    private final ExecutorSupplier executorSupplier;

    private Core() {
        executorSupplier = new DefaultExecutorSupplier();
    }

    public static Core getInstance() {
        if (null == mInstance) {
            synchronized (Core.class) {
                if (null == mInstance) {
                    mInstance = new Core();
                }
            }
        }
        return mInstance;
    }

    public static void shutDown() {
        if (null != mInstance)
            mInstance = null;
    }
}
