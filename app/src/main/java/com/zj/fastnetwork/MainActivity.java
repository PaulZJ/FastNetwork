package com.zj.fastnetwork;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.zj.fastnet.common.callback.FastCallBack;
import com.zj.fastnet.common.consts.Method;
import com.zj.fastnet.error.FastNetError;
import com.zj.fastnet.manager.DefaultHttpManager;

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

                DefaultHttpManager.getInstance().callForBitmap(Method.GET, "http://img.taopic.com/uploads/allimg/120727/201995-120HG1030762.jpg",
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
                        });
            }
        });
    }
}
