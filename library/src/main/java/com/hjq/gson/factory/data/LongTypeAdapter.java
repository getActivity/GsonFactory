package com.hjq.gson.factory.data;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.math.BigDecimal;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/GsonFactory
 *    time   : 2020/05/05
 *    desc   : long / Long 类型解析适配器，参考：{@link com.google.gson.internal.bind.TypeAdapters#LONG}
 */
public class LongTypeAdapter extends TypeAdapter<Long> {

    @Override
    public Long read(JsonReader in) throws IOException {
        JsonToken jsonToken = in.peek();
        switch (jsonToken) {
            case NUMBER:
                try {
                    return in.nextLong();
                } catch (NumberFormatException e) {
                    // 如果带小数点则会抛出这个异常
                    return new BigDecimal(in.nextString()).longValue();
                }
            case STRING:
                String result = in.nextString();
                if (result == null || "".equals(result)) {
                    return 0L;
                }
                try {
                    return Long.parseLong(result);
                } catch (NumberFormatException e) {
                    // 如果带小数点则会抛出这个异常
                    return new BigDecimal(result).longValue();
                }
            case NULL:
                in.nextNull();
                return null;
            default:
                in.skipValue();
                throw new IllegalArgumentException("The current parser is of type Long, but the data is of type " + jsonToken);
        }
    }

    @Override
    public void write(JsonWriter out, Long value) throws IOException {
        out.value(value);
    }
}