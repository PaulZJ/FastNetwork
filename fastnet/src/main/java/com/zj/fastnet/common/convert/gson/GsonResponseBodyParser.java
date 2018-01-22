package com.zj.fastnet.common.convert.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.zj.fastnet.common.convert.Parser;

import java.io.IOException;

import okhttp3.ResponseBody;

/**
 * Created by zhangjun on 2018/1/22.
 */

final class GsonResponseBodyParser<T> implements Parser<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    GsonResponseBodyParser(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        JsonReader jsonReader = gson.newJsonReader(value.charStream());
        try {
            return adapter.read(jsonReader);
        }finally {
            value.close();
        }
    }
}
