package com.hjq.gson.factory.data;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/GsonFactory
 *    time   : 2020/05/05
 *    desc   : boolean / Boolean 类型解析适配器，参考：{@link com.google.gson.internal.bind.TypeAdapters#BOOLEAN}
 */
public class BooleanTypeAdapter extends TypeAdapter<Boolean> {

    @Override
    public Boolean read(JsonReader in) throws IOException {
        JsonToken jsonToken = in.peek();
        switch (jsonToken) {
            case BOOLEAN:
                return in.nextBoolean();
            case STRING:
                String result = in.nextString();
                // 如果后台返回 "true" 或者 "TRUE"，则处理为 true，否则为 false
                if (String.valueOf(true).equalsIgnoreCase(result)) {
                    return true;
                } else if (String.valueOf(false).equalsIgnoreCase(result)) {
                    return false;
                }
                // 如果是其他类型，则直接不解析，并且抛给上一层做处理
                throw new IllegalArgumentException("Data parsing failed, unable to convert " + result + " to boolean type");
            case NUMBER:
                // 如果后台返回的是非 0 的数值则处理为 true，否则为 false
                return in.nextInt() != 0;
            case NULL:
                in.nextNull();
                return null;
            default:
                in.skipValue();
                throw new IllegalArgumentException("The current parser is of type Boolean, but the data is of type " + jsonToken);
        }
    }

    @Override
    public void write(JsonWriter out, Boolean value) throws IOException {
        out.value(value);
    }
}