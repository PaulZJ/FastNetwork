package com.zj.fastnet.rx;


import com.zj.fastnet.common.callback.FastCallBack;
import com.zj.fastnet.process.FastRequest;

/**
 * Created by zhangjun on 2018/4/21.
 */

public class RxFastRequest  extends FastRequest<RxFastRequest>{

    public RxFastRequest(int method, FastCallBack fastCallBack) {
        super(method, fastCallBack);
    }

}
