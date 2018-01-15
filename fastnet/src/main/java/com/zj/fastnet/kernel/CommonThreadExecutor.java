package com.zj.fastnet.kernel;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;

import com.zj.fastnet.common.consts.RequestPriority;
import com.zj.fastnet.process.NetWorkRunnable;

import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangjun on 2018/1/10.
 *
 * Common NetWork Request ThreadPool
 */
public class CommonThreadExecutor extends ThreadPoolExecutor {

    private static final int DEFAULT_THREAD_COUNT = 3;

    public CommonThreadExecutor(int maxThreadNum, ThreadFactory factory) {
        super(maxThreadNum, maxThreadNum, 0, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<Runnable>(), factory);
    }

    /**
     * adjust NetWork Request Concurrent num according to network state
     * */
    void adjustThreadCount(NetworkInfo info) {
        if (info == null || !info.isConnectedOrConnecting()) {
            setThreadCount(DEFAULT_THREAD_COUNT);
            return;
        }
        switch (info.getType()) {
            case ConnectivityManager.TYPE_WIFI:
            case ConnectivityManager.TYPE_WIMAX:
            case ConnectivityManager.TYPE_ETHERNET:
                setThreadCount(4);
                break;
            case ConnectivityManager.TYPE_MOBILE:
                switch (info.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_LTE:  // 4G
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                        setThreadCount(3);
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS: // 3G
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        setThreadCount(2);
                        break;
                    case TelephonyManager.NETWORK_TYPE_GPRS: // 2G
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                        setThreadCount(1);
                        break;
                    default:
                        setThreadCount(DEFAULT_THREAD_COUNT);
                }
                break;
            default:
                setThreadCount(DEFAULT_THREAD_COUNT);
        }
    }

    /**
     * resize ThreadPool
     * */
    private void setThreadCount(int threadCount) {
        setCorePoolSize(threadCount);
        setMaximumPoolSize(threadCount);
    }

    @Override
    public Future<?> submit(Runnable task) {
        NetworkingFutureTask futureTask = new NetworkingFutureTask((NetWorkRunnable) task);
        execute(futureTask);
        return futureTask;
    }

    private static final class NetworkingFutureTask extends FutureTask<NetWorkRunnable>
            implements Comparable<NetworkingFutureTask>{
        private NetWorkRunnable runnable;

        public NetworkingFutureTask(NetWorkRunnable runnable) {
            super(runnable, null);
            this.runnable = runnable;
        }

        @Override
        public int compareTo(@NonNull NetworkingFutureTask other) {
            RequestPriority p1 = runnable.getPriority();
            RequestPriority p2 = other.runnable.getPriority();
            return (p1 == p2 ? runnable.sequenceNum - other.runnable.sequenceNum : p2.ordinal() - p1.ordinal());
        }
    }
}
