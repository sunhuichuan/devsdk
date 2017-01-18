package com.yao.devsdk.net.response;

public class HttpResult<T> {

    /**请求失败*/
    public static final int STATUS_ERROR = 0;
    /**请求成功*/
    public static final int STATUS_SUCCESS = 1;

    private int status;
    private String message;

    private T data;

    public int getStatus() {
        return status;
    }


    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

}