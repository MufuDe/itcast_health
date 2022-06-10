package com.itheima.service;

import com.itheima.entity.Result;

import java.util.Map;

public interface OrderService {
    Result order(Map<String, Object> map) throws Exception;

    Map<String, Object> findById(Integer id) throws Exception;
}
