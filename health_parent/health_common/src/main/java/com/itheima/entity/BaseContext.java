package com.itheima.entity;

/**
 * 基于 ThreadLocal 封装工具类，用于保存和获取当前登录用户ID
 */
public class BaseContext {
    private static final ThreadLocal<Object> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Object id){
        threadLocal.set(id);
    }

    public static Object getCurrentId(){
        return threadLocal.get();
    }

    public static void removeCurrentId(){
        threadLocal.remove();
    }
}