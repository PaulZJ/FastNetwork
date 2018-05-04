package com.zj.fastnet.process;

import com.zj.fastnet.common.consts.RequestPriority;
import com.zj.fastnet.kernel.Core;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhangjun on 2018/1/22.
 * <p>
 * the Queue handling FastRequests
 */

public class FastRequestQueue {
    private final Set<FastRequest> mCurrentRequests = Collections.newSetFromMap(new ConcurrentHashMap<FastRequest,
            Boolean>());
    private AtomicInteger mSequenceGenerator = new AtomicInteger();
    private static FastRequestQueue mInstance = null;

    public static FastRequestQueue getInstance() {
        if (null == mInstance) {
            synchronized (FastRequestQueue.class) {
                if (null == mInstance) {
                    mInstance = new FastRequestQueue();
                }
            }
        }
        return mInstance;
    }

    public interface RequestFilter {
        boolean apply(FastRequest request);
    }

    /**
     * add a FastRequest to executor
     *
     * @param request
     * @return
     */
    public FastRequest addRequest(FastRequest request) {
        try {
            mCurrentRequests.add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (request.isImmediateNetTask()) {
                request.setFuture(Core.getInstance().getExecutorSupplier()
                        .executorForImmediateNetTask().submit(new NetWorkRunnable(RequestPriority.MEDIUM,
                                getSequenceNumber(), request)));
            } else {
                request.setFuture(Core.getInstance().getExecutorSupplier()
                        .executorForNetTask().submit(new NetWorkRunnable(RequestPriority.MEDIUM, getSequenceNumber(),
                                request)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return request;
    }

    /**
     * cancel a FastRequest specified with RequestFilter
     *
     * @param filter
     * @param forceCancel
     */
    private void cancel(RequestFilter filter, boolean forceCancel) {
        try {
            for (Iterator<FastRequest> iterator = mCurrentRequests.iterator(); iterator.hasNext(); ) {
                FastRequest request = iterator.next();
                if (filter.apply(request)) {
                    request.cancel(forceCancel);
                    if (request.isCancelled()) {
                        request.destroy();
                        iterator.remove();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * cancel all FastRequest
     *
     * @param forceCancel
     */
    public void cancelAll(boolean forceCancel) {
        try {
            for (Iterator<FastRequest> iterator = mCurrentRequests.iterator(); iterator.hasNext(); ) {
                FastRequest request = iterator.next();
                request.cancel(forceCancel);
                if (request.isCancelled()) {
                    request.destroy();
                    iterator.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * cancel FastRequest specified with Tag
     *
     * @param tag
     * @param forceCancel
     */
    public void cancelRequestWithGivenTag(final Object tag, final boolean forceCancel) {
        try {
            if (null == tag) {
                return;
            }
            cancel(new RequestFilter() {
                @Override
                public boolean apply(FastRequest request) {
                    return isRequestWithTheGivenTag(request, tag);
                }
            }, forceCancel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getSequenceNumber() {
        return mSequenceGenerator.incrementAndGet();
    }

    /**
     * whether a FastRequest is with a specific Tag
     *
     * @param request
     * @param tag
     * @return
     */
    private boolean isRequestWithTheGivenTag(FastRequest request, Object tag) {
        if (request.getTag() == null) {
            return false;
        }
        if (request.getTag() instanceof String && tag instanceof String) {
            final String tempRequestTag = (String) request.getTag();
            final String tempTag = (String) tag;
            return tempRequestTag.equals(tempTag);
        }

        return request.getTag().equals(tag);
    }

    /**
     * remove finished FastRequest
     *
     * @param request
     */
    public void finish(FastRequest request) {
        try {
            mCurrentRequests.remove(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
