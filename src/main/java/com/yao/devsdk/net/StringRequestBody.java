package com.yao.devsdk.net;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * 字符串类型的requestBody
 * Created by huichuan on 16/5/10.
 */
public class StringRequestBody extends RequestBody{

    private RequestBody requestBody;
    private String name;
    private String content;

    public StringRequestBody(RequestBody requestBody,String name,String content){
        this.requestBody = requestBody;
        this.name = name;
        this.content = content;
    }


    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        requestBody.contentType();
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public static StringRequestBody create(MediaType contentType,String name, String content) {
        RequestBody requestBody = RequestBody.create(contentType, content);
        StringRequestBody stringRequestBody = new StringRequestBody(requestBody,name,content);
        return stringRequestBody;
    }

}
