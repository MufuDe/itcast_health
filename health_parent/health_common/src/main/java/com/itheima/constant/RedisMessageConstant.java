package com.itheima.constant;

public class RedisMessageConstant {
    public static final String SENDTYPE_ORDER = "user:order:";//用于缓存体检预约时发送的验证码
    public static final String SENDTYPE_LOGIN = "user:login:";//用于缓存手机号快速登录时发送的验证码
    public static final String SENDTYPE_CHECKED = "user:checked:";//用于缓存手机号快速登录时发送的验证码
    public static final String SENDTYPE_GETPWD = "user:reset:";//用于缓存找回密码时发送的验证码
}