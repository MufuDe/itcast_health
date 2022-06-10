package com.itheima.dao;

import com.itheima.pojo.Order;
import org.apache.ibatis.annotations.MapKey;

import java.util.List;
import java.util.Map;

public interface OrderDao {
    List<Order> findByCondition(Order order);

    void add(Order order);

    @MapKey("id")
    Map<String, Object> findById4Detail(Integer id);

    Integer findOrderCountByDate(String today);

    Integer findOrderCountAfterDate(String thisWeekMonday);

    Integer findVisitsCountByDate(String today);

    Integer findVisitsCountAfterDate(String thisWeekMonday);

    @MapKey("id")
    List<Map<String, Object>> findHotSetmeal();
}
