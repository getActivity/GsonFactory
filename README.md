# Gson 解析容错框架

* 码云地址：[Gitee](https://gitee.com/getActivity/GsonFactory)

#### Gradle 集成

```groovy
dependencies {
    // Gson 解析容错：https://github.com/getActivity/GsonFactory
    implementation 'com.hjq.gson:factory:5.0'
    // Json 解析框架：https://github.com/google/gson
    implementation 'com.google.code.gson:gson:2.8.5'
}
```

#### 使用文档

* 请使用框架返回的 Gson 对象来代替项目中的 Gson 对象

```java
// 获取单例的 Gson 对象（已处理容错）
Gson gson = GsonFactory.getSingletonGson();
```

* 因为框架中的 Gson 对象已经对解析规则进行了容错处理

#### 其他 API

```java
// 设置自定义的 Gson 对象
GsonFactory.setSingletonGson(gson);

// 创建一个 Gson 构建器（已处理容错）
GsonBuilder gsonBuilder = GsonFactory.createGsonBuilder();

// 设置 Json 解析容错监听
GsonFactory.setExceptionListener(new JsonCallback() {

    @Override
    public void onTypeException(TypeToken<?> typeToken, String fieldName, JsonToken jsonToken) {
        Log.e("GsonFactory", "类型解析异常：" + typeToken + "#" + fieldName + "，后台返回的类型为：" + jsonToken);
    }
});
```

#### 容错介绍

* 目前支持容错的数据类型有：

	* `Bean 类`

	* `数组集合`
	
	* `String`（字符串）

	* `boolean / Boolean`（布尔值）

	* `int / Integer`（整数，属于数值类）
	
	* `long /Long`（长整数，属于数值类）
	
	* `float / Float`（单精度浮点数，属于数值类）
	
	* `double / Double`（双精度浮点数，属于数值类）
	
	* `BigDecimal`（精度更高的浮点数，属于数值类）
	
* **基本涵盖 99.99% 的开发场景**，可以运行 Demo 中的**单元测试**用例来查看效果：

|  数据类型  | 容错的范围 |  数据示例  |
| :----: | :------: |  :-----: |
|  bean | 集合、字符串、布尔值、数值 |  `[]`、`""`、`false`、`0`  |
|  集合 | bean、字符串、布尔值、数值 |  `{}`、`""`、`false`、`0`  |
|  字符串 | bean、集合、布尔值、数值 |  `{}`、`[]`、`false`、`0`  |
|  布尔值 | bean、集合、字符串、数值 |  `{}`、`[]`、`""`、`0`  |
|  数值 |  bean、集合、字符串、布尔值 |  `{}`、`[]`、`""`、`false`  |

* 大家可能觉得 Gson 解析容错没什么，那是因为我们对 Gson 解析失败的场景没有了解过：

	* 类型不对：后台有数据时返回 `JsonObject`，没数据返回 `[]`，Gson 会直接抛出异常

	* 措手不及：如果客户端定义的是`整数`，但是后台返回`浮点数`，Gson 会直接抛出异常
	
	* 意想不到：如果客户端定义的是`布尔值`，但是后台返回的是 `0` 或者 `1`，Gson 会直接抛出异常
	
* 以上情况框架已经做了容错处理，具体处理规则如下：

	* 如果后台返回的类型和客户端定义的类型不匹配，框架就`不解析`这个字段

	* 如果客户端定义的是整数，但后台返回浮点数，框架就对数值进行`取整`并赋值给字段

	* 如果客户端定义布尔值，但是后台返回整数，框架则将`非 0 的数值则赋值为 true，否则为 false`
	
#### 常见疑问解答

*  Retrofit + RxJava 怎么替换？

```java
Retrofit retrofit = new Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(GsonFactory.getSingletonGson()))
        .build();
```

* 有没有必要处理 Json 解析容错？

> 我觉得非常有必要，因为后台返回的数据结构是什么样我们把控不了，但是有一点是肯定的，我们都不希望它崩，因为一个接口的失败导致整个 App 崩溃退出实属不值得，但是 Gson 很敏感，动不动就崩。

* 我们后台用的是 Java，有必要处理容错吗？

> 如果你们的后台用的是 PHP，那我十分推荐你使用这个框架，因为 PHP 返回的数据结构很乱，这块经历过的人都懂，没经历过的人怎么说都不懂。

> 如果你们的后台用的是 Java，那么可以根据实际情况而定，例如我现在的公司用的就是 Java 后台，但是 Bugly 有上报一个关于 Gson 解析的 Crash，所以后台的话只能信一半。

#### 作者的其他开源项目

* 安卓技术中台：[AndroidProject](https://github.com/getActivity/AndroidProject)

* 网络框架：[EasyHttp](https://github.com/getActivity/EasyHttp)

* 吐司框架：[ToastUtils](https://github.com/getActivity/ToastUtils)

* 权限框架：[XXPermissions](https://github.com/getActivity/XXPermissions)

* 标题栏框架：[TitleBar](https://github.com/getActivity/TitleBar)

* 悬浮窗框架：[XToast](https://github.com/getActivity/XToast)

* 国际化框架：[MultiLanguages](https://github.com/getActivity/MultiLanguages)

* 日志查看框架：[Logcat](https://github.com/getActivity/Logcat)

#### 微信公众号：Android轮子哥

![](https://raw.githubusercontent.com/getActivity/Donate/master/picture/official_ccount.png)

#### Android 技术分享 QQ 群：78797078

#### 如果您觉得我的开源库帮你节省了大量的开发时间，请扫描下方的二维码随意打赏，要是能打赏个 10.24 :monkey_face:就太:thumbsup:了。您的支持将鼓励我继续创作:octocat:

![](https://raw.githubusercontent.com/getActivity/Donate/master/picture/pay_ali.png) ![](https://raw.githubusercontent.com/getActivity/Donate/master/picture/pay_wechat.png)

#### [点击查看捐赠列表](https://github.com/getActivity/Donate)

## License

```text
Copyright 2020 Huang JinQun

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.