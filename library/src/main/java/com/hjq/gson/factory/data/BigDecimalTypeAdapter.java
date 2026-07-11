package com.hjq.gson.factory.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.math.BigDecimal;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/GsonFactory
 *    time   : 2021/01/01
 *    desc   : BigDecimal 类型解析适配器，参考：{@link com.google.gson.internal.bind.TypeAdapters#BIG_DECIMAL}
 */
public class BigDecimalTypeAdapter extends TypeAdapter<BigDecimal> {

    @Nullable
    @Override
    public BigDecimal read(@NonNull JsonReader in) throws IOException {
        JsonToken jsonToken = in.peek();
        switch (jsonToken) {
            case NUMBER:
            case STRING:
                String result = in.nextString();
                if (result == null || result.isEmpty()) {
                    return new BigDecimal(0);
                }
                return new BigDecimal(result);
            case NULL:
                in.nextNull();
                return null;
            default:
                in.skipValue();
                throw new IllegalArgumentException("The current parser is of type BigDecimal, but the data is of type " + jsonToken);
        }
    }

    @Override
    public void write(@NonNull JsonWriter out, @Nullable BigDecimal value) throws IOException {
        out.value(value);
    }
}