package com.zj.fastnetwork;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.reflect.TypeToken;
import com.zj.fastnet.common.callback.DownloadProgressListener;
import com.zj.fastnet.common.callback.FastCallBack;
import com.zj.fastnet.common.consts.Method;
import com.zj.fastnet.error.FastNetError;
import com.zj.fastnet.manager.DefaultHttpManager;
import com.zj.fastnet.rx.RxNetwork;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zhangjun on 2018/1/4.
 */

public class MainActivity extends Activity {

    private ImageView testImg;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testImg = (ImageView) findViewById(R.id.test_img);
        findViewById(R.id.test_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        /*DefaultHttpManager.getInstance().callForStringData(Method.GET, "https://api.github.com/users/PaulZJ/followers",
            null, new FastCallBack<String>() {
              @Override
              public void onResponse(String response) {
                Log.e("zj test", "data: "+ response);
              }

              @Override
              public void onError(FastNetError error) {
                Log.e("zj test", "error");
              }
            });*/

                /*DefaultHttpManager.getInstance().callForBitmap(Method.GET, "http://img.taopic.com/uploads/allimg/120727/201995-120HG1030762.jpg",
                        null, new FastCallBack<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap response) {
                                Log.e("zj test", "bitmap: "+ response);
                                testImg.setImageBitmap(response);
                            }

                            @Override
                            public void onError(FastNetError error) {
                                Log.e("zj test", "error");
                            }
                        });*/

                /*RxNetwork.getInstance().callForStringData(Method.GET, "https://api.github" +
                        ".com/users/PaulZJ/followers", null)
                        .getStringObservable()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<String>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                Log.e("zj test", "onSubscribe");
                            }

                            @Override
                            public void onNext(String s) {
                                Log.e("zj test", "onNext: "+s);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("zj test", "onError");
                            }

                            @Override
                            public void onComplete() {
                                Log.e("zj test", "onComplete");
                            }
                        });*/

           /*     DefaultHttpManager.getInstance().callForBitmap(Method.GET,
                        "http://www.vilogo.com/wp-content/uploads/64965070201304181125484061603230163_006.jpg"
                        , null,
                        DensityUtil.dp2px(MainActivity.this, 100),
                        DensityUtil.dp2px(MainActivity.this, 100),
                         new FastCallBack<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap response) {
                                testImg.setImageBitmap(response);
                            }

                            @Override
                            public void onError(FastNetError error) {

                            }
                        });*/

                /*RxNetwork.getInstance().callForRxData(Method.GET, "https://api.github.com/users/PaulZJ/followers", null)
                        .getJsonObservable(new TypeToken<ArrayList<GitModel>>(){})
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<ArrayList<GitModel>>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(ArrayList<GitModel> gitModels) {
                                Log.e("zj test", "data size: "+ gitModels.size());
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });*/

                DefaultHttpManager.getInstance().callForFileDownload(Method.GET,
                        "https://nodejs.org/dist/v8.11.1/node-v8.11.1.pkg",null,
                        MainActivity.this.getCacheDir().getAbsolutePath(),"download.zj",
                        new DownloadProgressListener(){
                            @Override
                            public void onProgress(long bytesDownloaded, long totalBytes) {
                                Log.e("zj test", String.format(" done bytes: %d, total bytes: %d", bytesDownloaded,
                                        totalBytes));
                            }
                        }, new FastCallBack<Void>(){
                            @Override
                            public void onResponse(Void response) {
                                Log.e("zj test", "response over");
                            }

                            @Override
                            public void onError(FastNetError error) {
                                Log.e("zj test", "response error");
                            }
                        });
            }
        });
    }
}
