package com.zj.fastnet.manager;

import com.zj.fastnet.common.builder.GetRequestBuilder;
import com.zj.fastnet.common.callback.FastCallBack;
import com.zj.fastnet.common.consts.Method;
import com.zj.fastnet.common.consts.RequestPriority;
import com.zj.fastnet.process.FastNetWorking;
import com.zj.fastnet.process.FastRequest;
import com.zj.fastnet.process.FastRequestQueue;
import com.zj.fastnet.process.NetWorkRunnable;

import java.util.Map;

/**
 * Created by zhangjun on 2018/1/27.
 */

public class DefaultHttpManager {
    private static DefaultHttpManager mInstance = null;
    private DefaultHttpManager() {
    }

    public static DefaultHttpManager getInstance() {
        if (null == mInstance) {
            synchronized (DefaultHttpManager.class) {
                if (null == mInstance) {
                    mInstance = new DefaultHttpManager();
                }
            }
        }
        return mInstance;
    }

    public void callForStringData(@Method int method, String url, Map<String, String> params, FastCallBack<String>
            fastCallBack) {
        switch (method) {
            case Method.GET:
                GetRequestBuilder builder = new GetRequestBuilder(url);
                if (params != null) {
                    builder.addQueryParameter(params);
                }
                FastRequest request = builder.build(fastCallBack);
                FastRequestQueue.getInstance().addRequest(request);
                break;
        }
    }

}
