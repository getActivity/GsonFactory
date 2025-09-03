# [中文文档](README.md)

# Gson Fault-Tolerant Parsing Framework

* Project address: [Github](https://github.com/getActivity/GsonFactory)

#### Integration Steps

* If your project's Gradle configuration is `below 7.0`, add the following to your `build.gradle` file

```groovy
allprojects {
    repositories {
        // JitPack remote repository: https://jitpack.io
        maven { url 'https://jitpack.io' }
    }
}
```

* If your Gradle configuration is `7.0 or above`, add the following to your `settings.gradle` file

```groovy
dependencyResolutionManagement {
    repositories {
        // JitPack remote repository: https://jitpack.io
        maven { url 'https://jitpack.io' }
    }
}
```

* After configuring the remote repository, add the remote dependency in the `build.gradle` file of your app module

```groovy
android {
    // Support JDK 1.8
    compileOptions {
        targetCompatibility JavaVersion.VERSION_1_8
        sourceCompatibility JavaVersion.VERSION_1_8
    }
}

dependencies {
    // Gson fault-tolerant parsing: https://github.com/getActivity/GsonFactory
    implementation 'com.github.getActivity:GsonFactory:10.3'
    // Json parsing framework: https://github.com/google/gson
    implementation 'com.google.code.gson:gson:2.13.1'
    // Kotlin reflection library: for reflecting Kotlin data class objects, change 1.5.10 to your project's Kotlin version
    implementation 'org.jetbrains.kotlin:kotlin-reflect:1.5.10'
}
```

* Two points to note:

   * Your project must have a Kotlin environment, otherwise it will not compile

   * Gson framework must use version **2.13.0** or above, otherwise there will be compatibility issues

   * If you encounter [errorprone](https://github.com/google/error-prone) compilation failures after upgrading Gson, you can try to exclude the dependency from Gson to solve this issue

	```groovy
	implementation ('com.google.code.gson:gson:x.x.x') {
	    exclude group: 'com.google.errorprone', module: 'error_prone_annotations'
	}
	```

#### Usage Documentation

* Please use the Gson object returned by the framework instead of the Gson object in your project

```java
// Get the singleton Gson object (with fault tolerance)
Gson gson = GsonFactory.getSingletonGson();
```

* The Gson object in the framework has already handled fault-tolerant parsing rules

#### Other APIs

```java
// Set a custom Gson object
GsonFactory.setSingletonGson(Gson gson);

// Create a Gson builder (with fault tolerance)
GsonBuilder gsonBuilder = GsonFactory.newGsonBuilder();

// Register type adapter factory
GsonFactory.registerTypeAdapterFactory(TypeAdapterFactory factory);

// Register instance creator
GsonFactory.registerInstanceCreator(Type type, InstanceCreator<?> creator);

// Add reflection access filter
GsonFactory.addReflectionAccessFilter(ReflectionAccessFilter filter);

// Set Json parsing fault-tolerant callback object
GsonFactory.setParseExceptionCallback(ParseExceptionCallback callback);
```

#### Proguard Rules for the Framework

* Be careful not to obfuscate the bean classes of kotlin class data, otherwise it will cause ClassNotFoundException when reflecting the constructor of kotlin data class. For details, see [issues/43](https://github.com/getActivity/GsonFactory/issues/43)

```
# This rule must be added, otherwise it may cause ClassNotFoundException when reflecting the constructor of kotlin class data bean classes. Replace xxx with the corresponding package name
-keepnames class xxx.xxx.xxx.** {
    public (...);
}
```

#### Comparison of Different Json Parsing Frameworks

|  Feature/Detail  | [GsonFactory](https://github.com/getActivity/GsonFactory) | [Gson](https://github.com/google/gson)  | [moshi](https://github.com/square/moshi) | [FastJson](https://github.com/alibaba/fastjson) |
| :----: | :------: |  :-----: |  :-----: |  :-----: |
|    Version  |  10.3 |  1.30.6  |  1.5.0  |  2.0.28  |
|    Number of issues   |  [![](https://img.shields.io/github/issues/getActivity/GsonFactory.svg)](https://github.com/getActivity/GsonFactory/issues)  |  [![](https://img.shields.io/github/issues/google/gson.svg)](https://github.com/google/gson/issues)  |  [![](https://img.shields.io/github/issues/square/moshi.svg)](https://github.com/square/moshi/issues)  |  [![](https://img.shields.io/github/issues/alibaba/fastjson.svg)](https://github.com/alibaba/fastjson/issues)  |
|                    Framework size                 | 60 KB + 283 KB | 283 KB | 162 KB | 188 KB |
|                   Maintenance status               | Maintaining | Maintaining | Maintaining | Stopped |
|         Can parse incorrect data types without error          |  ✅  |  ❌  |  ❌  |  ✅  |
|    Can continue parsing other fields after encountering incorrect data type     |  ✅  |  ❌  |  ❌  |  ❌  |
|     Can intelligently convert incorrect data types    |  ✅  |  ❌  |  ❌  |  ❌  |
|      Supports default values for kotlin data class fields    |  ✅  |  ❌  |  ✅  |  ✅  |
|    Supports non-null, no-default-value fields in kotlin data class    |  ✅  |  ❌  |  ❌  |  ❌  |
|  Skips parsing null values in Json (avoids NPE)   |  ✅  |  ❌  |  ❌  |  ✅  |
|    Supports parsing `org.json.JSONObject` type   |  ✅  |  ❌  |  ❌  |  ❌  |
|    Supports parsing `org.json.JSONArray` type    |  ✅  |  ❌  |  ❌  |  ❌  |

#### Introduction to Fault Tolerance for Data Types

* Currently supported fault-tolerant data types:

	* `Bean class`

	* `Array collection`

	* `Map collection`

	* `JSONArray`

	* `JSONObject`

	* `String`

	* `boolean / Boolean`

	* `int / Integer`

	* `long / Long`

	* `float / Float`

	* `double / Double`

	* `BigDecimal`

* **Covers 99.99% of development scenarios**. You can run the **unit test** cases in the Demo to see the effect:

|  Data Type |        Fault-tolerant Range           |            Data Example              |
| :-----: | :--------------------: | :-----------------------: |
|  bean  |  collection, string, boolean, number  |  `[]`, `""`, `false`, `0`  |
|   collection  |  bean, string, boolean, number |  `{}`, `""`, `false`, `0`  |
|  string |   bean, collection, boolean, number  |  `{}`, `[]`, `false`, `0`  |
|  boolean |   bean, collection, string, number  |    `{}`, `[]`, `""`, `0`   |
|   number  |  bean, collection, string, boolean |  `{}`, `[]`, `""`, `false` |

* You may think Gson fault-tolerant parsing is not important, that's because you haven't encountered Gson parsing failure scenarios:

	* Shocking: When there is data, the backend returns **JsonObject** type, when there is no data, it returns `[]`, Gson will throw an exception directly
	
	* Unexpected: If the client defines a **boolean** type, but the backend returns **0** or **1**, Gson will throw an exception directly

	* Caught off guard: If the client defines **int** or **long** type, but the backend returns **float** or **double** type, Gson will throw an exception directly

* The above situations have been handled by the framework with fault tolerance. The specific rules are as follows:

	* If the type returned by the backend does not match the type defined by the client, the framework will **skip parsing this field** without affecting the parsing of other fields

	* If the client defines a **boolean** type, but the backend returns an integer, the framework will assign **true** to non-zero values, otherwise false

	* If the client defines **int** or **long** type, but the backend returns a floating-point number, the framework will **directly take the integer part** and assign it to the field

#### Introduction to Kotlin Null Value Adaptation

* If you define a Bean class in Kotlin as follows

```kotlin
class XxxBean {
    
    val age: Int = 18
}
```

* You might think that when the backend returns `{ "age" : null }`, the value of the `age` field will be `18`? I have tested it for you, it will not be `18`, it will be null.

* Why is this? This is due to the mechanism of Gson parsing. When Gson parses a Bean class, it reflects to create an object, but what you may not know is that Gson will parse the value in the Json string according to the field name of the Bean class, and then simply and rudely assign it by reflection. If the backend returns a null value for the `age` field, then `age` will be assigned null. But you declared the `age` variable as non-null in Kotlin, so when it is called, a `NullPointerException` is triggered, which is expected.

* In addition, for `List` and `Map` type objects, if the backend returns null or incorrect data type, the framework will return a non-null but size 0 `List` or `Map` object, to avoid null pointer exceptions caused by non-null fields in Kotlin when the backend returns null.

* The current solution of the framework is: if the backend does not return the value of this field, or returns null, it will not assign the value to the class field. Because what Gson does is unreasonable, it causes problems when using Gson in Kotlin. If you don't define the variable as nullable, you have to check for null every time you use a basic data type. If you define it as non-null, it will trigger a `NullPointerException` when used. It's a dilemma.

#### Introduction to Default Values for Kotlin Data Class

* If you define a Bean class in Kotlin as follows

```kotlin
data class DataClassBean(val name: String = "Hello")
```

* If you give it to the native Gson for parsing, you will get the following result

```text
name = null
```

* Why is `name` not equal to `Hello`? Why is it equal to `null`? This is because Gson only initializes the empty constructor of the `DataClassBean` class by default, and the framework's solution is very simple and rude, directly introducing the kotlin reflection library, so that it can find the primary constructor of the automatically generated `kotlin data class`, and then reflect to create the kotlin class, so that the default values of the fields will be preserved, which solves the problem that the default values of the fields in the `kotlin data class` do not take effect when using Gson reflection. The framework has already handled this issue internally, and users who use the framework do not need to handle this issue by themselves, just call the framework for parsing.

#### Introduction to Non-null, No-default-value Fields in Kotlin Data Class

* If you define a Bean class in Kotlin as follows

```kotlin
data class DataClassBean(var name: String)
```

* If you give it to the native [Gson](https://github.com/google/gson/) for parsing, no matter what value Json is, you will get an empty `name` field value, while giving it to [moshi](https://github.com/square/moshi) for parsing will throw a null pointer exception, while GsonFactory can parse it into `""`, why do three frameworks have three different results? This is because the three frameworks have different implementation methods for reflecting kotlin data class classes, as shown below.

* Gson: Only reflects the empty constructor, even if a class does not have an empty constructor, it will not fail to reflect, because Gson internally uses a special way to instantiate Class (that is, it uses `sun.misc.Unsafe` class `allocateInstance(Class<?> clazz)` method to create an object, note that this method is `native` modified), so the object is created successfully, but the fields in the class are not initialized, this is `Unsafe` class bypassing the constructor to create an object (how it does this is not the focus of our research), so why are the fields of kotlin data class type empty? The reason is here.

* moshi: The moshi uses a more clever solution, it relies on a kotlin reflection library, when reflecting kotlin data class, it finds the primary constructor of kotlin data class, and then reflects to create, moshi does this because it can preserve the default values of kotlin data class fields, but there is a fatal problem, like `(var name: String)` this field has no default value, but it is not marked as nullable, which will cause kotlin to perform a non-null check on the `name` field (if it is null, it will throw a `NullPointerException` exception) when compiling kotlin data class, so users who use this framework need to modify the code to `(var name: String = "")`, otherwise the compilation process will not report an error, but when using moshi for parsing, it will report an error. I have always believed that the kotlin class definition `(var name: String)` is incorrect, you should assign a value if you do not mark it as nullable, if you do not assign a value, you should mark it as nullable, that is `(var name: String?)`, but strangely, kotlin syntax checks it, still allows it to compile, I hope kotlin will correct this problem in the future.

* GsonFactory: On the basis of moshi, it has been improved, that is, for some fields that are defined as non-null and not assigned values, GsonFactory will assign a default value to these fields. If this field is a basic data type, it will be assigned a default value directly, if it is an object type, it will reflect to create an object, of course, kotlin data class type fields are also reflected to create a kotlin data class type object, which will not cause a `NullPointerException` when parsing.

* **Warning: It is strongly recommended not to define kotlin data class fields in this inaccurate way, because although the framework is compatible with this issue, it is only applicable to `Android Gradle Plugin 8.5.0` and below versions. If you use it in `Android Gradle Plugin 8.6.0` and above versions, the compatibility solution of the framework will be invalid, and there is currently no better solution, please refer to [GsonFactory/issues/51](https://github.com/getActivity/GsonFactory/issues/51) for details.**

## Common Questions and Answers

#### How to replace Gson in Retrofit?

```java
Retrofit retrofit = new Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(GsonFactory.getSingletonGson()))
        .build();
```

#### How to replace the native Gson in your project?

```text
// Replace call
new Gson()
GsonFactory.getSingletonGson()
```

```text
// Replace import
import com.google.gson.Gson
import com.hjq.gson.factory.GsonFactory
```

```text
// Then manually handle some that were not replaced successfully
new GsonBuilder()
```

#### Is it necessary to handle Json parsing fault tolerance?

* I think it is very necessary, because we cannot control the data structure returned by the backend, but one thing is certain, we do not want it to crash, because a failed interface will cause the entire App to crash, which is not worth it. However, Gson is very sensitive, it crashes easily.

#### Do you need to handle fault tolerance if your backend uses Java?

* If your backend uses PHP, I highly recommend you use this framework, because PHP returns very messy data, those who have experienced it know what I mean, saying more is crying, those who have not experienced it will not understand no matter how much I say.

* If your backend uses Java, then you can decide based on the actual situation, you can use it or not, but it is better to use it, as a fallback solution, so that you can prevent the backend from suddenly not adhering to the code, for example, in my current company, all backends are developed using Java, but Bugly still reports about Gson parsing exceptions, the following data is collected through `GsonFactory.setParseExceptionCallback`, you can refer to it:

![](picture/bugly_report_error.jpg)

* Roughly estimated, a total of three million times were reported, affecting three million devices, do you still believe that backends developed in Java do not have data fault tolerance issues? It all depends on people, Java is just a development language, it cannot guarantee data fault tolerance issues, if such a problem really occurs in the future, it is mainly divided into two situations:

    * If iOS does not do data fault tolerance, then the pot is the backend, this is undeniable, the backend wants to throw it away, it cannot.

    * If iOS does data fault tolerance, then the most likely result is that both the backend and Android sides will be pulled out to each receive fifty lashes:

        * CTO: The backend has a problem, why does iOS work, but Android has a problem?

        * Android: iOS side did fault tolerance, but Android side did not.

        * CTO: Why don't you do it? This fallback mechanism should have been there, right?

        * Android: We use the Gson framework, its mechanism is like this.

        * CTO: I don't care what framework you use, if it crashes here, it's your fault, if the backend returns incorrect data structure, you don't parse it, why do you still crash? The same data structure, why does iOS work? This responsibility should have a part of Android.

        * Android: 。。。。。。（Silent, can't say anything）

        * Ps: The above story is purely fictional, please take it lightly, but one thing is true, if you don't want to argue about it later, you'd better leave a hand.

#### How do I know if a Json error has occurred and ensure that the problem is not hidden?

* For this question, the solution is also very simple, use the `GsonFactory.setParseExceptionCallback` API. If the backend returns incorrect data structure, in debug mode, it will directly throw an exception, developers can know it immediately; and in online mode, report this issue to ensure that no issue is missed (can be uploaded to the backend or Bugly error list), the example code is as follows:

```java
// Set Json parsing fault-tolerant listener
GsonFactory.setParseExceptionCallback(new ParseExceptionCallback() {

    @Override
    public void onParseObjectException(TypeToken<?> typeToken, String fieldName, JsonToken jsonToken) {
        handlerGsonParseException("Parsing object exception: " + typeToken + "#" + fieldName + "，backend returned type: " + jsonToken);
    }

    @Override
    public void onParseListItemException(TypeToken<?> typeToken, String fieldName, JsonToken listItemJsonToken) {
        handlerGsonParseException("Parsing List exception: " + typeToken + "#" + fieldName + "，backend returned item type: " + listItemJsonToken);
    }

    @Override
    public void onParseMapItemException(TypeToken<?> typeToken, String fieldName, String mapItemKey, JsonToken mapItemJsonToken) {
        handlerGsonParseException("Parsing Map exception: " + typeToken + "#" + fieldName + "，mapItemKey = " + mapItemKey + "，backend returned item type: " + mapItemJsonToken);
    }

    private void handlerGsonParseException(String message) {
        if (BuildConfig.DEBUG) {
            throw new IllegalArgumentException(message);
        } else {
            // Report to Bugly error list
            CrashReport.postCatchedException(new IllegalArgumentException(message));
        }
    }
});
```

#### Other Open Source Projects by the Author

* Android middle office: [AndroidProject](https://github.com/getActivity/AndroidProject)![](https://img.shields.io/github/stars/getActivity/AndroidProject.svg)![](https://img.shields.io/github/forks/getActivity/AndroidProject.svg)

* Android middle office kt version: [AndroidProject-Kotlin](https://github.com/getActivity/AndroidProject-Kotlin)![](https://img.shields.io/github/stars/getActivity/AndroidProject-Kotlin.svg)![](https://img.shields.io/github/forks/getActivity/AndroidProject-Kotlin.svg)

* Permissions framework: [XXPermissions](https://github.com/getActivity/XXPermissions) ![](https://img.shields.io/github/stars/getActivity/XXPermissions.svg) ![](https://img.shields.io/github/forks/getActivity/XXPermissions.svg)

* Toast framework: [Toaster](https://github.com/getActivity/Toaster)![](https://img.shields.io/github/stars/getActivity/Toaster.svg)![](https://img.shields.io/github/forks/getActivity/Toaster.svg)

* Network framework: [EasyHttp](https://github.com/getActivity/EasyHttp)![](https://img.shields.io/github/stars/getActivity/EasyHttp.svg)![](https://img.shields.io/github/forks/getActivity/EasyHttp.svg)

* Title bar framework: [TitleBar](https://github.com/getActivity/TitleBar)![](https://img.shields.io/github/stars/getActivity/TitleBar.svg)![](https://img.shields.io/github/forks/getActivity/TitleBar.svg)

* Floating window framework: [EasyWindow](https://github.com/getActivity/EasyWindow)![](https://img.shields.io/github/stars/getActivity/EasyWindow.svg)![](https://img.shields.io/github/forks/getActivity/EasyWindow.svg)

* Device compatibility framework：[DeviceCompat](https://github.com/getActivity/DeviceCompat) ![](https://img.shields.io/github/stars/getActivity/DeviceCompat.svg) ![](https://img.shields.io/github/forks/getActivity/DeviceCompat.svg)

* Shape view framework: [ShapeView](https://github.com/getActivity/ShapeView)![](https://img.shields.io/github/stars/getActivity/ShapeView.svg)![](https://img.shields.io/github/forks/getActivity/ShapeView.svg)

* Shape drawable framework: [ShapeDrawable](https://github.com/getActivity/ShapeDrawable)![](https://img.shields.io/github/stars/getActivity/ShapeDrawable.svg)![](https://img.shields.io/github/forks/getActivity/ShapeDrawable.svg)

* Language switching framework: [Multi Languages](https://github.com/getActivity/MultiLanguages)![](https://img.shields.io/github/stars/getActivity/MultiLanguages.svg)![](https://img.shields.io/github/forks/getActivity/MultiLanguages.svg)

* Logcat viewing framework: [Logcat](https://github.com/getActivity/Logcat)![](https://img.shields.io/github/stars/getActivity/Logcat.svg)![](https://img.shields.io/github/forks/getActivity/Logcat.svg)

* Nested scrolling layout framework：[NestedScrollLayout](https://github.com/getActivity/NestedScrollLayout) ![](https://img.shields.io/github/stars/getActivity/NestedScrollLayout.svg) ![](https://img.shields.io/github/forks/getActivity/NestedScrollLayout.svg)

* Android version guide: [AndroidVersionAdapter](https://github.com/getActivity/AndroidVersionAdapter)![](https://img.shields.io/github/stars/getActivity/AndroidVersionAdapter.svg)![](https://img.shields.io/github/forks/getActivity/AndroidVersionAdapter.svg)

* Android code standard: [AndroidCodeStandard](https://github.com/getActivity/AndroidCodeStandard)![](https://img.shields.io/github/stars/getActivity/AndroidCodeStandard.svg)![](https://img.shields.io/github/forks/getActivity/AndroidCodeStandard.svg)

* Android resource summary：[AndroidIndex](https://github.com/getActivity/AndroidIndex) ![](https://img.shields.io/github/stars/getActivity/AndroidIndex.svg) ![](https://img.shields.io/github/forks/getActivity/AndroidIndex.svg)

* Android open source leaderboard: [AndroidGithubBoss](https://github.com/getActivity/AndroidGithubBoss)![](https://img.shields.io/github/stars/getActivity/AndroidGithubBoss.svg)![](https://img.shields.io/github/forks/getActivity/AndroidGithubBoss.svg)

* Studio boutique plugins: [StudioPlugins](https://github.com/getActivity/StudioPlugins)![](https://img.shields.io/github/stars/getActivity/StudioPlugins.svg)![](https://img.shields.io/github/forks/getActivity/StudioPlugins.svg)

* Emoji collection: [EmojiPackage](https://github.com/getActivity/EmojiPackage)![](https://img.shields.io/github/stars/getActivity/EmojiPackage.svg)![](https://img.shields.io/github/forks/getActivity/EmojiPackage.svg)

* China provinces json: [ProvinceJson](https://github.com/getActivity/ProvinceJson)![](https://img.shields.io/github/stars/getActivity/ProvinceJson.svg)![](https://img.shields.io/github/forks/getActivity/ProvinceJson.svg)

* Markdown documentation：[MarkdownDoc](https://github.com/getActivity/MarkdownDoc) ![](https://img.shields.io/github/stars/getActivity/MarkdownDoc.svg) ![](https://img.shields.io/github/forks/getActivity/MarkdownDoc.svg)

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
```