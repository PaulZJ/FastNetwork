package com.zj.fastnet.rx;


import android.graphics.Bitmap;

import com.google.gson.reflect.TypeToken;
import com.zj.fastnet.common.callback.FastCallBack;
import com.zj.fastnet.common.consts.RequestType;
import com.zj.fastnet.common.consts.ResponseType;
import com.zj.fastnet.process.FastRequest;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;


/**
 * Created by zhangjun on 2018/4/21.
 */

public class RxFastRequest  extends FastRequest<RxFastRequest>{

    public RxFastRequest(int method, FastCallBack fastCallBack) {
        super(method, fastCallBack);
    }

    /**
     * get Observable for String
     * @return
     */
    public Observable<String> getStringObservable() {
        this.setResponseType(ResponseType.STRING);
        if (this.getRequestType() == RequestType.SIMPLE) {
            return Rx2InternalNetwork.generateSimpleObservable(this);
        }else if (this.getRequestType() == RequestType.MULTIPART) {
            return Rx2InternalNetwork.generateMultipartObservable(this);
        }else {
            return null;
        }
    }

    public Flowable<String> getStringFlowable() {
        return getStringObservable().toFlowable(BackpressureStrategy.LATEST);
    }

    public Single<String> getStringSingle() {
        return getStringObservable().singleOrError();
    }

    public Maybe<String> getStringMaybe() {
        return getStringObservable().singleElement();
    }

    public Completable getStringCompletable() {
        return getStringObservable().ignoreElements();
    }

    /**
     * get Observable for json model
     * @param typeToken
     * @param <T>
     * @return
     */
    public <T> Observable<T> getJsonObservable(TypeToken<T> typeToken) {
        this.setMType(typeToken.getType());
        this.setResponseType(ResponseType.PARSED);
        if (this.getRequestType() == RequestType.SIMPLE) {
            return Rx2InternalNetwork.generateSimpleObservable(this);
        }else if (this.getRequestType() == RequestType.MULTIPART) {
            return Rx2InternalNetwork.generateMultipartObservable(this);
        }else {
            return null;
        }
    }

    public <T> Flowable<T> getJsonFlowable(TypeToken<T> typeToken) {
        return getJsonObservable(typeToken).toFlowable(BackpressureStrategy.LATEST);
    }

    public <T> Single<T> getJsonSingle(TypeToken<T> typeToken) {
        return getJsonObservable(typeToken).singleOrError();
    }

    public <T> Maybe<T> getJsonMaybe(TypeToken<T> typeToken) {
        return getJsonObservable(typeToken).singleElement();
    }

    public <T> Completable getJsonCompletable(TypeToken<T> typeToken) {
        return getJsonObservable(typeToken).ignoreElements();
    }

    /**
     * get Observable for Bitmap
     * @return
     */
    public Observable<Bitmap> getBitmapObservable() {
        this.setResponseType(ResponseType.BITMAP);
        if (this.getRequestType() == RequestType.SIMPLE) {
            return Rx2InternalNetwork.generateSimpleObservable(this);
        }else if (this.getRequestType() == RequestType.MULTIPART) {
            return Rx2InternalNetwork.generateMultipartObservable(this);
        }else {
            return null;
        }
    }

    public Flowable<Bitmap> getBitmapFlowable() {
        return getBitmapObservable().toFlowable(BackpressureStrategy.LATEST);
    }

    public Single<Bitmap> getBitmapSingle() {
        return getBitmapObservable().singleOrError();
    }

    public Maybe<Bitmap> getBitmapMaybe() {
        return getBitmapObservable().singleElement();
    }

    public Completable getBitmapCompletable() {
        return getBitmapObservable().ignoreElements();
    }

    /**
     * get Observable for download request
     * @return
     */
    public Observable<String> getDownloadObservable() {
        return Rx2InternalNetwork.generateDownloadObservable(this);
    }

    public Flowable<String> getDownloadFlowable() {
        return getDownloadObservable().toFlowable(BackpressureStrategy.LATEST);
    }

    public Single<String> getDownloadSingle() {
        return getDownloadObservable().singleOrError();
    }

    public Maybe<String> getDownloadMaybe() {
        return getDownloadObservable().singleElement();
    }

    public Completable getDownloadCompletable() {
        return getDownloadObservable().ignoreElements();
    }
}
