package com.hjq.gson.factory.test;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import java.io.IOException;
import java.math.BigDecimal;

public class DecimalAdapter extends JsonAdapter<BigDecimal> {

    @Override
    public BigDecimal fromJson(JsonReader reader) throws IOException {
        String value;

        value = reader.nextString();
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            return new BigDecimal(value);
        } catch (NumberFormatException ex) {
            throw new JsonDataException(ex.getMessage(), ex);
        }
    }

    @Override
    public void toJson(JsonWriter writer, BigDecimal value) throws IOException {
        if (value == null) {
            writer.nullValue();
        } else {
            writer.value(value.toPlainString());
        }
    }
}