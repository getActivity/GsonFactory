package com.hjq.gson.factory.data;

import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONException;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/GsonFactory
 *    time   : 2021/09/30
 *    desc   : JSONObject 类型解析适配器
 */
public class JSONArrayTypeAdapter extends TypeAdapter<JSONArray> {

    private static final TypeAdapter<JsonElement> PROXY = TypeAdapters.JSON_ELEMENT;

    @Override
    public JSONArray read(JsonReader in) throws IOException {
        JsonElement read = PROXY.read(in);
        if (read.isJsonArray()) {
            try {
                return new JSONArray(read.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void write(JsonWriter out, JSONArray value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        PROXY.write(out, PROXY.fromJson(value.toString()));
    }
}