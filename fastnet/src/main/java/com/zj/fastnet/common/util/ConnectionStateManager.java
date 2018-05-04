package com.zj.fastnet.common.util;

import com.zj.fastnet.common.callback.ConnectionQualityChangeListener;
import com.zj.fastnet.common.consts.ConnectionQuality;
import com.zj.fastnet.kernel.Core;

import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by zhangjun on 2018/1/19.
 *
 * the manager handling Network State
 */

public class ConnectionStateManager {
    private static final int BYTES_TO_BITS = 8;
    private static final int DEFAULT_SAMPLES_TO_QUALITY_CHANGE = 5;
    private static final int MINIMUM_SAMPLES_TO_DECIDE_QUALITY = 2;
    private static final int DEFAULT_POOR_BANDWIDTH = 150;
    private static final int DEFAULT_MODERATE_BANDWIDTH = 550;
    private static final int DEFAULT_GOOD_BANDWIDTH = 2000;
    private static final long BANDWIDTH_LOWER_BOUND = 10;

    private static ConnectionStateManager mInstance;
    @Getter
    private ConnectionQuality currentConnecttionQuality = ConnectionQuality.UNKNOWN;
    private int mCurrentBandWidthForSampling = 0;
    private int mCurrentNumberOfSample = 0;
    @Getter
    private int currentBandWidth = 0;
    @Setter
    private ConnectionQualityChangeListener connectionQualityChangeListener;

    private ConnectionStateManager() {}

    public static ConnectionStateManager getInstance() {
        if (mInstance == null) {
            synchronized (ConnectionStateManager.class) {
                if (null == mInstance) {
                    mInstance = new ConnectionStateManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * upload Current BandWidth with sampling data
     */
    public synchronized void updateBandWidth(long bytes, long timeInMs) {
        if (0 == timeInMs || bytes < 2000 || (bytes) * 1.0 / (timeInMs) * BYTES_TO_BITS < BANDWIDTH_LOWER_BOUND) {
            return;
        }
        double bandWidth = (bytes) * 1.0 / (timeInMs) * BYTES_TO_BITS;
        mCurrentBandWidthForSampling = (int) ((mCurrentBandWidthForSampling * mCurrentNumberOfSample + bandWidth) /
                (mCurrentNumberOfSample + 1));
        mCurrentNumberOfSample ++;
        if (mCurrentNumberOfSample == DEFAULT_SAMPLES_TO_QUALITY_CHANGE ||
        (currentConnecttionQuality == ConnectionQuality.UNKNOWN && mCurrentNumberOfSample ==
                MINIMUM_SAMPLES_TO_DECIDE_QUALITY)) {
            final ConnectionQuality lastConnectionQuality = currentConnecttionQuality;
            currentBandWidth = mCurrentBandWidthForSampling;
            if (mCurrentBandWidthForSampling <= 0) {
                currentConnecttionQuality = ConnectionQuality.UNKNOWN;
            }else if (mCurrentBandWidthForSampling < DEFAULT_POOR_BANDWIDTH) {
                currentConnecttionQuality = ConnectionQuality.POOR;
            }else if (mCurrentBandWidthForSampling < DEFAULT_MODERATE_BANDWIDTH) {
                currentConnecttionQuality = ConnectionQuality.MODERATE;
            }else if (mCurrentBandWidthForSampling < DEFAULT_GOOD_BANDWIDTH) {
                currentConnecttionQuality = ConnectionQuality.GOOD;
            }else if (mCurrentBandWidthForSampling > DEFAULT_GOOD_BANDWIDTH) {
                currentConnecttionQuality = ConnectionQuality.EXCELLENT;
            }

            if (mCurrentNumberOfSample == DEFAULT_SAMPLES_TO_QUALITY_CHANGE){
                mCurrentNumberOfSample = 0;
                mCurrentBandWidthForSampling =0;
            }
            if (currentConnecttionQuality != lastConnectionQuality && connectionQualityChangeListener != null) {
                Core.getInstance().getExecutorSupplier().executorForMainThreadTask().execute(new Runnable() {
                    @Override
                    public void run() {
                        connectionQualityChangeListener.onChange(currentConnecttionQuality, currentBandWidth);
                    }
                });
            }
        }
    }

    public void removeListener() {
        connectionQualityChangeListener = null;
    }

    public static void shutDown() {
        if (null != mInstance) {
            mInstance = null;
        }
    }
}
