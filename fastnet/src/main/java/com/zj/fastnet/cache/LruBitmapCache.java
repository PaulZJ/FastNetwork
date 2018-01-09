package com.zj.fastnet.cache;

import android.graphics.Bitmap;

/**
 * Created by zhangjun on 2018/1/10.
 */

public class LruBitmapCache extends LruCache<String, Bitmap> implements ImageCache {
    /**
     * the constructor of LruCache
     * init a LinkedHashMap with loadFactor = 0.75 (with the capacity is 0.75*maxCapacity, auto expand)
     *
     * @param maxSize
     */
    public LruBitmapCache(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight();
    }

    @Override
    public Bitmap getBitmap(String key) {
        return get(key);
    }

    @Override
    public void putBitmap(String key, Bitmap bitmap) {
        put(key, bitmap);
    }

    @Override
    public void evictBitmap(String key) {
        evictBitmap(key);
    }

    @Override
    public void evictAllBitmap() {
        evictAll();
    }
}
