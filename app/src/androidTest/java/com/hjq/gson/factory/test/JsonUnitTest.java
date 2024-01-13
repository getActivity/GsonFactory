package com.hjq.gson.factory.test;

import android.content.Context;
import android.util.Log;
import androidx.test.platform.app.InstrumentationRegistry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonToken;
import com.hjq.gson.factory.GsonFactory;
import com.hjq.gson.factory.ParseExceptionCallback;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/GsonFactory
 *    time   : 2020/11/10
 *    desc   : Gson 解析容错适配器测试用例
 *    doc    : https://developer.android.google.cn/studio/test
 */
public final class JsonUnitTest {

    private static final String TAG = "GsonFactory";

    private Gson mGson;

    /**
     * 测试前
     */
    @Before
    public void onTestBefore() {
        // CrashReport.initCrashReport(InstrumentationRegistry.getInstrumentation().getContext());
        mGson = GsonFactory.getSingletonGson();
        // 设置 Json 解析容错监听
        GsonFactory.setParseExceptionCallback(new ParseExceptionCallback() {

            @Override
            public void onParseObjectException(TypeToken<?> typeToken, String fieldName, JsonToken jsonToken) {
                handlerGsonParseException("解析对象析异常：" + typeToken + "#" + fieldName + "，后台返回的类型为：" + jsonToken);
            }

            @Override
            public void onParseListItemException(TypeToken<?> typeToken, String fieldName, JsonToken listItemJsonToken) {
                handlerGsonParseException("解析 List 异常：" + typeToken + "#" + fieldName + "，后台返回的条目类型为：" + listItemJsonToken);
            }

            @Override
            public void onParseMapItemException(TypeToken<?> typeToken, String fieldName, String mapItemKey, JsonToken mapItemJsonToken) {
                handlerGsonParseException("解析 Map 异常：" + typeToken + "#" + fieldName + "，mapItemKey = " + mapItemKey + "，后台返回的条目类型为：" + mapItemJsonToken);
            }

            private void handlerGsonParseException(String message) {
                Log.e(TAG, message);
                /*
                if (BuildConfig.DEBUG) {
                    throw new IllegalArgumentException(message);
                } else {
                    // 上报到 Bugly 错误列表中
                    CrashReport.postCatchedException(new IllegalArgumentException(message));
                }
                 */
            }
        });
    }

    /**
     * 后台返回正常的 Json 串测试
     */
    @Test
    public void parseNormalJsonTest() {
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        String json = getAssetsString(context, "NormalJson.json");
        //mGson.toJson(mGson.fromJson(json, JsonBean.class));
        JsonBean jsonBean = mGson.fromJson(json, JsonBean.class);
        Log.i(TAG, mGson.toJson(jsonBean));
    }

    /**
     * 后台返回异常的 Json 串测试
     */
    @Test
    public void parseAbnormalJsonTest() {
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        String json = getAssetsString(context, "AbnormalJson.json");
        //mGson.toJson(mGson.fromJson(json, JsonBean.class));
        JsonBean jsonBean = mGson.fromJson(json, JsonBean.class);
        Log.i(TAG, mGson.toJson(jsonBean));
    }

    /**
     * Kotlin DataClass 默认值测试
     */
    @Test
    public void kotlinDataClassDefaultValueTest() {
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        String json = getAssetsString(context, "NullJson.json");
        DataClassBean dataClassBean = mGson.fromJson(json, DataClassBean.class);
        Log.i(TAG, mGson.toJson(dataClassBean));
    }

    /**
     * 测试完成
     */
    @After
    public void onTestAfter() {
        mGson = null;
    }

    /**
     * 获取资产目录下面文件的字符串
     */
    private static String getAssetsString(Context context, String file) {
        try {
            InputStream inputStream = context.getAssets().open(file);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, length);
            }
            outStream.close();
            inputStream.close();
            return outStream.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}