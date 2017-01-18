package com.yao.devsdk.net.exception;

/**
 * API请求时返回的异常错误
 * Created by huichuan on 16/4/14.
 */
public class ApiException extends RuntimeException {

    //失败/未定义的错误
    public static final int FAIL = 0;
    //正常/确认/无需更新
    public static final int OK = 1;
    //手机号格式错误
    public static final int MOBILE_PHONE_NUMBER_ERROR = 1001;
    //手机号已注册
    public static final int MOBILE_PHONE_NUMBER_REGISTERED = 1002;
    //昵称已经存在
    public static final int SIGNUP_NICKNAME_EXISTS = 1003;
    //用户名/手机号 或 密码错误
    public static final int PASSWORD_ERROR = 1004;
    //session_key错误，未通过登陆验证
    public static final int SESSION_KEY_ERROR = 1005;
    //验证码发送失败
    public static final int SIGNUP_CODE_FAIL = 1006;
    //验证码发送过于频繁
    public static final int SIGNUP_CODE_FREQUENT = 1007;
    //验证码不正确
    public static final int SIGNUP_CODE_WRONG = 1008;
    //用户昵称长度不符合要求
    public static final int SIGNUP_NICKNAME_LENGTH_ERROR = 1009;
    //用户昵称含非法字符
    public static final int SIGNUP_NICKNAME_WORD_ERROR = 1010;
    //注册提交的密码哈希值格式不合法
    public static final int SIGNUP_PASSWORD_ERROR = 1011;
    //注册失败
    public static final int SIGNUP_FAIL = 1012;
    //没有数据
    public static final int NO_DATA = 1052;
    //赞已存在
    public static final int LIKE_EXIST = 1026;
    //赞不存在
    public static final int LIKE_NOT_EXIST = 1027;
    //需要登录
    public static final int LOGIN_REQUIRED = 1028;
    //第三方登录信息未注册
    public static final int LOGIN_FAILED_SSO_INFO_NOT_EXIST = 1057;


    private int status;
    private String message;

    public ApiException(int status, String msg) {
        super(msg);
        this.status = status;
        this.message = msg;
    }


    public int getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }

    /**
     * 是否是没有数据或者没有更多的错误
     * @return
     */
    public boolean isErrorNoData(){
        return (status == NO_DATA);
    }


    /**
     * 错误信息Str
     * @return
     */
    public String getErrorStr(){
        if (status != OK){
            //非请求成功，返回错误信息
            return "("+status+")";
        }else{
            return "ok";
        }
    }

    /**
     * 接口请求获取到的错误信息处理
     * @param e
     * @return
     */
    public static String getExceptionMessage(Throwable e){
        String message;
        if (e instanceof ApiException){
            ApiException ex = (ApiException)e;
            message = ex.getStatus()+","+ex.getMessageFromErrorCode();
        }else{
            message = e.getMessage();
        }

        return message;
    }


    /**
     * 从api错误码获取错误信息
     */
    public String getMessageFromErrorCode(){
        String message;
        switch (status){
            case FAIL:
                message = "失败";
                break;
            case OK:
                message = "请求成功";
                break;
            case MOBILE_PHONE_NUMBER_ERROR:
                message = "手机号格式错误";
                break;
            case MOBILE_PHONE_NUMBER_REGISTERED:
                message = "手机号已注册";
                break;
            case SIGNUP_NICKNAME_EXISTS:
                message = "昵称已经存在";
                break;
            case PASSWORD_ERROR:
                message = "用户名/手机号 或 密码错误";
                break;
            case SESSION_KEY_ERROR:
                message = "session_key错误，未通过登陆验证";
                break;
            case SIGNUP_CODE_FAIL:
                message = "验证码发送失败";
                break;
            case SIGNUP_CODE_FREQUENT:
                message = "验证码发送过于频繁";
                break;
            case SIGNUP_CODE_WRONG:
                message = "验证码不正确";
                break;
            case SIGNUP_NICKNAME_LENGTH_ERROR:
                message = "用户昵称长度不符合要求";
                break;
            case SIGNUP_NICKNAME_WORD_ERROR:
                message = "用户昵称含非法字符";
                break;
            case SIGNUP_PASSWORD_ERROR:
                message = "注册提交的密码哈希值格式不合法";
                break;
            case SIGNUP_FAIL:
                message = "注册失败";
                break;
            case NO_DATA:
                message = "暂时没有数据";
                break;
            case LOGIN_REQUIRED:
                message = "需要登录";
                break;
            default:
                message = "未知错误";
                break;
        }
        return message;

    }


    @Override
    public String toString() {


        String currentString = "ApiException{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
        String parentString = super.toString();

        return currentString +"######"+parentString;
    }
}
