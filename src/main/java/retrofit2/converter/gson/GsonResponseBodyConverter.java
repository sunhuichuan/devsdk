/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package retrofit2.converter.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.yao.devsdk.log.LoggerUtil;

import java.io.IOException;
import java.io.Reader;

import okhttp3.ResponseBody;
import retrofit2.Converter;

final class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private static final String TAG = "GsonResponseBodyConverter";
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    GsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        try {
            Reader charStream = value.charStream();

            StringBuilder sb = new StringBuilder();
            char[] buffer = new char[1024];
            int readCount;
            while ((readCount = charStream.read(buffer)) != -1) {
                sb.append(buffer, 0, readCount);
            }
            String response = sb.toString();

            LoggerUtil.i(TAG, "获取到的服务器返回结果：" + response);

            return adapter.fromJson(response);


//        JsonReader jsonReader = gson.newJsonReader(charStream);
//        return adapter.read(jsonReader);

        } catch (Exception e) {
            LoggerUtil.e(TAG,"解析异常",e);
            return null;
        } finally {
            value.close();
        }
    }
}
