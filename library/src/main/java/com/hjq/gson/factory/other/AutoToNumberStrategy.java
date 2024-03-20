package com.hjq.gson.factory.other;

import com.google.gson.ToNumberStrategy;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import java.math.BigDecimal;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/GsonFactory
 *    time   : 2024/03/11
 *    desc   : 自动转换数值类型的策略
 */
public class AutoToNumberStrategy implements ToNumberStrategy {

    @Override
    public Number readNumber(JsonReader in) throws IOException {
        // Github issue 地址：https://github.com/getActivity/GsonFactory/issues/40
        String numberString = in.nextString();
        BigDecimal bigDecimal = new BigDecimal(numberString);
        // 判断这个数值是浮点数还是整数
        if (bigDecimal.scale() > 0) {
            // 如果是浮点数，则用 double 类型装载
            return bigDecimal.doubleValue();
        }

        // 如果是整数，则用 int 类型或者 long 类型装载
        if (bigDecimal.compareTo(BigDecimal.valueOf(Integer.MAX_VALUE)) > 0) {
            return bigDecimal.longValue();
        } else {
            return bigDecimal.intValue();
        }
    }
}