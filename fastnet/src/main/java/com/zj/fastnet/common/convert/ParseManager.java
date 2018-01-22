package com.zj.fastnet.common.convert;

import com.zj.fastnet.common.convert.gson.GsonParserFactory;

/**
 * Created by zhangjun on 2018/1/22.
 */

public class ParseManager {
    private static Parser.Factory mParseFactory;

    public static Parser.Factory getParseFactory() {
        if (null == mParseFactory) {
            synchronized (ParseManager.class) {
                if (null == mParseFactory) {
                    mParseFactory = new GsonParserFactory();
                }
            }
        }
        return mParseFactory;
    }

    public static void setParseFactory(Parser.Factory parseFactory) {
        if (null != parseFactory) {
            mParseFactory = parseFactory;
        }
    }

    public static void shutDown() {
        mParseFactory = null;
    }
}
