package com.zj.fastnet.process;

import com.zj.fastnet.common.RequestPriority;

import lombok.Getter;

/**
 * Created by zhangjun on 2018/1/10.
 */

public class NetWorkRunnable implements Runnable {
    @Getter
    private final RequestPriority priority;
    public final int sequenceNum;
    public final FastRequest request;

    public NetWorkRunnable(RequestPriority priority, int sequenceNum, FastRequest request) {
        this.priority = priority;
        this.sequenceNum = sequenceNum;
        this.request = request;
    }

    @Override
    public void run() {
        request.setRunning(true);
        //do request

        request.setRunning(false);
    }


}
