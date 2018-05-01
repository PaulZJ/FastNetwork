package com.zj.fastnetwork;

import android.content.Context;

/**
 * Created by zhangjun on 2018/5/1.
 */

public class DensityUtil {

    public static int dp2px(Context context, float dpVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpVal*scale +0.5f);
    }
}
