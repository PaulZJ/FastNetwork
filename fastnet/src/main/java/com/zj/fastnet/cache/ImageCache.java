package com.zj.fastnet.cache;

import android.graphics.Bitmap;

/**
 * Created by zhangjun on 2018/1/10.
 *
 * the interface for Image Cache
 */

public interface ImageCache {
    /**
     * get Bitmap by cache key
     * */
    Bitmap getBitmap(String key);

    /**
     * cache Bitmap with cache key
     * */
    void putBitmap(String key, Bitmap bitmap);

    /**
     * drop cached Bitmap with cache key
     * */
    void evictBitmap(String key);

    /**
     * drop all cached Bitmaps
     * */
    void evictAllBitmap();
}
