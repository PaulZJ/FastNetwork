package com.zj.fastnet.common.util;

import com.zj.fastnet.common.callback.DataAnalyticsListener;
import com.zj.fastnet.kernel.Core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;

import okhttp3.Response;

/**
 * Created by zhangjun on 2018/1/20.
 */

public class CommonUtils {
    public static String getMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    public static void sendAnalytics(final DataAnalyticsListener analyticsListener, final long timeTakenInMills,
                                     final long bytesSent, final long bytesReceived, final boolean isFromCache) {
        Core.getInstance().getExecutorSupplier().executorForMainThreadTask().execute(new Runnable() {
            @Override
            public void run() {
                if (null != analyticsListener) {
                    analyticsListener.onReceived(timeTakenInMills, bytesSent, bytesReceived, isFromCache);
                }
            }
        });
    }

    /**
     * save file for Download Task
     * */
    public static void saveFile(Response response, String dirPath,
                                String fileName) throws IOException {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len;
        FileOutputStream fos = null;
        try {
            is = response.body().byteStream();
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, fileName);
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fos != null) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
