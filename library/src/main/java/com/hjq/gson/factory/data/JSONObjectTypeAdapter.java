package com.hjq.gson.factory.data;

import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/GsonFactory
 *    time   : 2021/09/30
 *    desc   : JSONObject 类型解析适配器
 */
public class JSONObjectTypeAdapter extends TypeAdapter<JSONObject> {

    public TypeAdapter<JsonElement> mProxy = TypeAdapters.JSON_ELEMENT;

    @Override
    public JSONObject read(JsonReader in) throws IOException {
        JsonElement read = mProxy.read(in);
        if (read.isJsonObject()) {
            try {
                return new JSONObject(read.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void write(JsonWriter out, JSONObject value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        mProxy.write(out, mProxy.fromJson(value.toString()));
    }
}